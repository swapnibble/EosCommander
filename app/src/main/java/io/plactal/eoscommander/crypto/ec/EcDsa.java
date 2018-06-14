/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.plactal.eoscommander.crypto.ec;

import com.google.common.base.Preconditions;

import java.math.BigInteger;
import java.util.Arrays;

import io.plactal.eoscommander.data.remote.model.types.EosByteWriter;
import io.plactal.eoscommander.crypto.Hmac;
import io.plactal.eoscommander.crypto.digest.Sha256;

/**
 * Created by swapnibble on 2017-09-29.
 */

public class EcDsa {

    private static class SigChecker {
        BigInteger e;
        BigInteger privKey;

        BigInteger r;
        BigInteger s;

        SigChecker(byte[] hash, BigInteger privKey){
            this.e = new BigInteger(1, hash);
            this.privKey = privKey;
        }

        boolean checkSignature(CurveParam curveParam, BigInteger k) {

            EcPoint Q = EcTools.multiply( curveParam.G(), k);// Secp256k1Param.G, k);
            if ( Q.isInfinity() ) return false;

            r = Q.getX().toBigInteger().mod( curveParam.n());// Secp256k1Param.n );
            if ( r.signum() == 0 ) return false;


            s = k.modInverse( curveParam.n())// Secp256k1Param.n)
                    .multiply(e.add( privKey.multiply(r)))
                    .mod( curveParam.n());// Secp256k1Param.n);

            if (s.signum() == 0) return false;

            return true;
        }

        public boolean isRSEachLength(int length) {
            return (r.toByteArray().length == length) && ( s.toByteArray().length == length) ;
        }
    }


    private static BigInteger deterministicGenerateK(CurveParam curveParam, byte[] hash, BigInteger d, SigChecker checker, int nonce ){
        if ( nonce > 0 ){
            hash = Sha256.from(hash, BigInteger.valueOf(nonce).toByteArray()).getBytes();
        }

        byte[] dBytes = d.toByteArray();

        // Step b
        byte[] v = new byte[32];
        Arrays.fill(v, (byte)0x01);

        // Step c
        byte [] k = new byte[32];
        Arrays.fill(k, (byte)0x00);

        // Step d
        EosByteWriter bwD = new EosByteWriter(32 + 1 + 32 + 32);
        bwD.putBytes(v);
        bwD.put((byte) 0x00 );
        bwD.putBytes( dBytes );
        bwD.putBytes(hash);
        k = Hmac.hmacSha256(k, bwD.toBytes());

        // Step e
        v = Hmac.hmacSha256(k, v);

        // Step f
        EosByteWriter bwF = new EosByteWriter(32 + 1 + 32 + 32);
        bwF.putBytes(v);
        bwF.put((byte) 0x01 );
        bwF.putBytes(dBytes);
        bwF.putBytes(hash);
        k = Hmac.hmacSha256(k, bwF.toBytes());

        // Step g
        v = Hmac.hmacSha256(k, v);

        // Step H2b
        v = Hmac.hmacSha256(k, v);

        BigInteger t = new BigInteger(1, v);

        // Step H3, repeat until T is within the interval [1, Secp256k1Param.n - 1]
        while ((t.signum() <= 0) || (t.compareTo( curveParam.n()) >= 0) || !checker.checkSignature(curveParam, t)) {
            EosByteWriter bwH = new EosByteWriter(32 + 1);
            bwH.putBytes(v);
            bwH.put((byte) 0x00);
            k = Hmac.hmacSha256(k, bwH.toBytes());
            v = Hmac.hmacSha256(k, v);

            // Step H1/H2a, again, ignored as tlen === qlen (256 bit)
            // Step H2b again
            v = Hmac.hmacSha256(k, v);

            t = new BigInteger(v);
        }
        return t;
    }

    public static EcSignature sign(Sha256 hash, EosPrivateKey key ) {
        BigInteger privAsBI = key.getAsBigInteger();
        SigChecker checker = new SigChecker(hash.getBytes(), privAsBI);

        CurveParam curveParam = key.getCurveParam();

        int nonce = 0;
        while ( true ) {
            deterministicGenerateK(curveParam, hash.getBytes(), privAsBI, checker, nonce++);

            if (checker.s.compareTo( curveParam.halfCurveOrder() ) > 0) {//  Secp256k1Param.HALF_CURVE_ORDER) > 0) {
                checker.s = curveParam.n().subtract(checker.s);//   Secp256k1Param.n.subtract(checker.s);
            }

            if ( checker.isRSEachLength(32)) {
                break;
            }
        }

        EcSignature signature = new EcSignature( checker.r, checker.s, curveParam );

        byte[] data = hash.getBytes();

        EosPublicKey pubKey = key.getPublicKey();

        for (int i = 0; i < 4; i++) {
            EosPublicKey recovered = recoverPubKey(curveParam, data, signature, i);
            if ( pubKey.equals( recovered)) {
                signature.setRecid( i );
                break;
            }
        }

        if ( signature.recId < 0 ) {
            throw new IllegalStateException("could not find recid. Was this data signed with this key?");
        }

        return signature;
    }

    public static EosPublicKey recoverPubKey(byte[] messageSigned, EcSignature signature ) {
        return recoverPubKey( signature.curveParam, messageSigned, signature, signature.recId);
    }

    public static EosPublicKey recoverPubKey(CurveParam curveParam, byte[] messageSigned, EcSignature signature, int recId ) {

        Preconditions.checkArgument(recId >= 0, "recId must be positive");
        Preconditions.checkArgument(signature.r.compareTo(BigInteger.ZERO) >= 0, "r must be positive");
        Preconditions.checkArgument(signature.s.compareTo(BigInteger.ZERO) >= 0, "s must be positive");
        Preconditions.checkNotNull(messageSigned);
        // 1.0 For j from 0 to h (h == recId here and the loop is outside this
        // function)
        // 1.1 Let x = r + jn

        BigInteger n = curveParam.n();// Secp256k1Param.n; // EcCurve order.
        BigInteger i = BigInteger.valueOf((long) recId / 2);
        BigInteger x = signature.r.add(i.multiply(n));
        // 1.2. Convert the integer x to an octet string X of length mlen using
        // the conversion routine
        // specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or mlen =
        // ⌈m/8⌉.
        // 1.3. Convert the octet string (16 set binary digits)||X to an elliptic
        // curve point R using the
        // conversion routine specified in Section 2.3.4. If this conversion
        // routine outputs "invalid", then
        // do another iteration of Step 1.
        //
        // More concisely, what these points mean is to use X as a compressed
        // public key.

        EcCurve curve = curveParam.getCurve();// Secp256k1Param.curve;
        BigInteger prime = curve.getQ(); // Bouncy Castle is not consistent about
        // the letter it uses for the prime.
        if (x.compareTo(prime) >= 0) {
            // Cannot have point co-ordinates larger than this as everything takes
            // place modulo Q.
            return null;
        }
        // Compressed keys require you to know an extra bit of data about the
        // y-coord as there are two possibilities.
        // So it's encoded in the recId.
        EcPoint R = EcTools.decompressKey(curveParam, x, (recId & 1) == 1);
        // 1.4. If nR != point at infinity, then do another iteration of Step 1
        // (callers responsibility).
        if (!R.multiply(n).isInfinity())
            return null;
        // 1.5. Compute e from M using Steps 2 and 3 of ECDSA signature
        // verification.
        BigInteger e = new BigInteger(1, messageSigned);
        // 1.6. For k from 1 to 2 do the following. (loop is outside this function
        // via iterating recId)
        // 1.6.1. Compute a candidate public key as:
        // Q = mi(r) * (sR - eG)
        //
        // Where mi(x) is the modular multiplicative inverse. We transform this
        // into the following:
        // Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
        // Where -e is the modular additive inverse of e, that is z such that z +
        // e = 0 (mod n). In the above equation
        // ** is point multiplication and + is point addition (the EC group
        // operator).
        //
        // We can find the additive inverse by subtracting e from zero then taking
        // the mod. For example the additive
        // inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and -3 mod 11 =
        // 8.
        BigInteger eInv = BigInteger.ZERO.subtract(e).mod(n);
        BigInteger rInv = signature.r.modInverse(n);
        BigInteger srInv = rInv.multiply(signature.s).mod(n);
        BigInteger eInvrInv = rInv.multiply(eInv).mod(n);
        EcPoint q = EcTools.sumOfTwoMultiplies( curveParam.G(), eInvrInv, R, srInv); //  Secp256k1Param.G, eInvrInv, R, srInv);


        // We have to manually recompress the point as the compressed-ness gets
        // lost when multiply() is used.
        q = new EcPoint(curve, q.getX(), q.getY(), true);

        return new EosPublicKey( q.getEncoded() );
    }


    private static boolean isSignerOf(CurveParam curveParam, byte[] messageSigned, int recId, EcSignature sig, byte[] pubKeyBytes ) {
        Preconditions.checkArgument(recId >= 0, "recId must be positive");
        Preconditions.checkArgument(sig.r.compareTo(BigInteger.ZERO) >= 0, "r must be positive");
        Preconditions.checkArgument(sig.s.compareTo(BigInteger.ZERO) >= 0, "s must be positive");
        Preconditions.checkNotNull(messageSigned);
        // 1.0 For j from 0 to h (h == recId here and the loop is outside this
        // function)
        // 1.1 Let x = r + jn

        BigInteger n = curveParam.n();//Secp256k1Param.n; // EcCurve order.
        BigInteger i = BigInteger.valueOf((long) recId / 2);
        BigInteger x = sig.r.add(i.multiply(n));
        // 1.2. Convert the integer x to an octet string X of length mlen using
        // the conversion routine
        // specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or mlen =
        // ⌈m/8⌉.
        // 1.3. Convert the octet string (16 set binary digits)||X to an elliptic
        // curve point R using the
        // conversion routine specified in Section 2.3.4. If this conversion
        // routine outputs "invalid", then
        // do another iteration of Step 1.
        //
        // More concisely, what these points mean is to use X as a compressed
        // public key.

        EcCurve curve = curveParam.getCurve();// Secp256k1Param.curve;
        BigInteger prime = curve.getQ(); // Bouncy Castle is not consistent about
        // the letter it uses for the prime.
        if (x.compareTo(prime) >= 0) {
            // Cannot have point co-ordinates larger than this as everything takes
            // place modulo Q.
            return false;
        }
        // Compressed keys require you to know an extra bit of data about the
        // y-coord as there are two possibilities.
        // So it's encoded in the recId.
        EcPoint R = EcTools.decompressKey(curveParam, x, (recId & 1) == 1);
        // 1.4. If nR != point at infinity, then do another iteration of Step 1
        // (callers responsibility).
        if (!R.multiply(n).isInfinity())
            return false;
        // 1.5. Compute e from M using Steps 2 and 3 of ECDSA signature
        // verification.
        BigInteger e = new BigInteger(1, messageSigned);
        // 1.6. For k from 1 to 2 do the following. (loop is outside this function
        // via iterating recId)
        // 1.6.1. Compute a candidate public key as:
        // Q = mi(r) * (sR - eG)
        //
        // Where mi(x) is the modular multiplicative inverse. We transform this
        // into the following:
        // Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
        // Where -e is the modular additive inverse of e, that is z such that z +
        // e = 0 (mod n). In the above equation
        // ** is point multiplication and + is point addition (the EC group
        // operator).
        //
        // We can find the additive inverse by subtracting e from zero then taking
        // the mod. For example the additive
        // inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and -3 mod 11 =
        // 8.
        BigInteger eInv = BigInteger.ZERO.subtract(e).mod(n);
        BigInteger rInv = sig.r.modInverse(n);
        BigInteger srInv = rInv.multiply(sig.s).mod(n);
        BigInteger eInvrInv = rInv.multiply(eInv).mod(n);
        EcPoint q = EcTools.sumOfTwoMultiplies( curveParam.G(), eInvrInv, R, srInv); //Secp256k1Param.G, eInvrInv, R, srInv);


        // We have to manually recompress the point as the compressed-ness gets
        // lost when multiply() is used.
        q = new EcPoint(curve, q.getX(), q.getY(), true);

        byte[] recoveredPub = q.getEncoded();

        return Arrays.equals(recoveredPub, pubKeyBytes );
    }
}
