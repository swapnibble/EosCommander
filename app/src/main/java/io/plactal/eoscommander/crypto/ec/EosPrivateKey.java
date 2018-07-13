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

import java.math.BigInteger;
import java.security.SecureRandom;

import io.plactal.eoscommander.crypto.digest.Sha256;
import io.plactal.eoscommander.crypto.util.Base58;


/**
 * Created by swapnibble on 2017-09-25.
 *
 * 참고:
 * https://github.com/EOSIO/eosjs-ecc
 * https://github.com/EOSIO/eosjs-ecc/blob/master/src/ecdsa.js
 */

public class EosPrivateKey {

    private static final String PREFIX = "PVT";

    private final BigInteger mPrivateKey;
    private final EosPublicKey mPublicKey;

    private final CurveParam mCurveParam;

    private static final SecureRandom mSecRandom;

    static {
        mSecRandom = new SecureRandom();
    }

    public static SecureRandom getSecuRandom(){
        return mSecRandom;
    }

    public EosPrivateKey(){
        this( CurveParam.SECP256_K1, null);
    }

    public EosPrivateKey(int curveParamType){
        this( curveParamType, null);
    }

    public EosPrivateKey(byte[] keyBytes){
        this( CurveParam.SECP256_K1, keyBytes);
    }

    public EosPrivateKey( int curveParamType, byte[] keyBytes){
        mCurveParam = EcTools.getCurveParam(curveParamType);

        mPrivateKey = getOrCreatePrivKeyBigInteger( keyBytes );
        mPublicKey = new EosPublicKey(findPubKey( mPrivateKey ), mCurveParam);
    }

    public EosPrivateKey( String base58Str ) {

        String[] split = EosEcUtil.safeSplitEosCryptoString( base58Str );
        byte[] keyBytes;

        if ( split.length == 1 ){
            mCurveParam = EcTools.getCurveParam( CurveParam.SECP256_K1);
            keyBytes = EosEcUtil.getBytesIfMatchedSha256( base58Str, null);
        }
        else {
            if ( split.length < 3 ) {
                throw new IllegalArgumentException("Invalid private key format: " + base58Str);
            }

            mCurveParam = EosEcUtil.getCurveParamFrom( split[1]);
            keyBytes = EosEcUtil.getBytesIfMatchedRipemd160( split[2], split[1], null);
        }


        if ( ( null == keyBytes) || (keyBytes.length < 5 )) {
            throw new IllegalArgumentException("Invalid private key length");
        }

        mPrivateKey = getOrCreatePrivKeyBigInteger( keyBytes );
        mPublicKey = new EosPublicKey(findPubKey( mPrivateKey ), mCurveParam);
    }


    public void clear(){
        mPrivateKey.multiply( BigInteger.ZERO );
    }

    private byte[] findPubKey(BigInteger bnum) {
        EcPoint Q = EcTools.multiply( mCurveParam.G(), bnum );// Secp256k1Param.G, bnum);

        // Q를 curve 상에서, compressed point 로 변환하자. ( 압축을 위해 )

        Q = new EcPoint(Q.getCurve(), Q.getX(), Q.getY(), true);

        return Q.getEncoded();
    }

    public EosPublicKey getPublicKey() {
        return mPublicKey;
    }

    public String toWif() {
        byte[] rawPrivKey = getBytes();
        byte[] resultWIFBytes = new byte[ 1 + 32 + 4 ];

        resultWIFBytes[0] = (byte)0x80;
        System.arraycopy( rawPrivKey, rawPrivKey.length > 32 ? 1 : 0, resultWIFBytes, 1 , 32);

        Sha256 hash = Sha256.doubleHash( resultWIFBytes, 0, 33 );

        System.arraycopy( hash.getBytes(), 0, resultWIFBytes, 33, 4 );

        return Base58.encode( resultWIFBytes );
    }

    public CurveParam getCurveParam(){
        return mCurveParam;
    }

    public EcSignature sign( Sha256 digest ) {
        return EcDsa.sign( digest, this);
    }

    @Override
    public String toString() {
        if ( mCurveParam.isType( CurveParam.SECP256_K1 ) ){
            return toWif();
        }

        return EosEcUtil.encodeEosCrypto( PREFIX, mCurveParam , getBytes());
    }

    public BigInteger getAsBigInteger() {
        return mPrivateKey;
    }


    public byte[] getBytes() {
        byte[] result = new byte[32];
        byte[] bytes = mPrivateKey.toByteArray();
        if (bytes.length <= result.length) {
            System.arraycopy(bytes, 0, result, result.length - bytes.length, bytes.length);
        } else {
            assert bytes.length == 33 && bytes[0] == 0;
            System.arraycopy(bytes, 1, result, 0, bytes.length - 1);
        }
        return result;
    }

    public byte[] getBytes(BigInteger value) {
        byte[] result = new byte[32];
        byte[] bytes = value.toByteArray();
        if (bytes.length <= result.length) {
            System.arraycopy(bytes, 0, result, result.length - bytes.length, bytes.length);
        } else {
            // This happens if the most significant bit is set and we have an
            // extra leading zero to avoid a negative BigInteger
            assert bytes.length == 33 && bytes[0] == 0;
            System.arraycopy(bytes, 1, result, 0, bytes.length - 1);
        }
        return result;
    }

    private BigInteger toUnsignedBigInteger(BigInteger value ) {
        if ( value.signum() < 0 ) {
            return new BigInteger( 1, value.toByteArray());
        }

        return value;
    }

    private BigInteger toUnsignedBigInteger(byte[] value ) {
        if ( (( value[0]) & 0x80) != 0 ) {
            return new BigInteger( 1, value);
        }

        return new BigInteger(value);
    }

    private BigInteger getOrCreatePrivKeyBigInteger(byte[] value ) {
        if ( null != value ) {
            if (((value[0]) & 0x80) != 0) {
                return new BigInteger(1, value);
            }

            return new BigInteger(value);
        }


        int nBitLength = mCurveParam.n().bitLength();// Secp256k1Param.n.bitLength();
        BigInteger d;
        do {
            // Make a BigInteger from bytes to ensure that Android and 'classic'
            // java make the same BigIntegers from the same random source with the
            // same seed. Using BigInteger(nBitLength, random)
            // produces different results on Android compared to 'classic' java.
            byte[] bytes = new byte[nBitLength / 8];
            mSecRandom.nextBytes(bytes);
            bytes[0] = (byte) (bytes[0] & 0x7F); // ensure positive number
            d = new BigInteger(bytes);
        }
        while (d.equals(BigInteger.ZERO) || (d.compareTo(mCurveParam.n()) >= 0));// Secp256k1Param.n) >= 0));

        return d;
    }
}
