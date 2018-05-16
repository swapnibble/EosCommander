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
import java.util.Arrays;

import io.plactal.eoscommander.crypto.util.HexUtils;
import io.plactal.eoscommander.util.RefValue;
import io.plactal.eoscommander.util.StringUtils;


/**
 * Created by swapnibble on 2017-09-20.
 */

public class EcSignature {
    private static final String PREFIX = "SIG";

    public int recId = -1;

    public final BigInteger r;
    public final BigInteger s;
    public final CurveParam curveParam;

    public EcSignature(BigInteger r, BigInteger s, CurveParam curveParam) {
        this.r = r;
        this.s = s;
        this.curveParam = curveParam;
    }

    public EcSignature(BigInteger r, BigInteger s, CurveParam curveParam, int recId) {
        this(r, s,curveParam);

        setRecid( recId );
    }

    public EcSignature( String base58Str ){
        String[] parts = EosEcUtil.safeSplitEosCryptoString( base58Str );
        if ( parts.length < 3 ) {
            throw new IllegalArgumentException("Invalid private key format: " + base58Str);
        }

        if ( PREFIX.equals( parts[0]) == false ) {
            throw new IllegalArgumentException("Signature Key has invalid prefix: " + base58Str);
        }

        if (StringUtils.isEmpty( parts[2])) {
            throw new IllegalArgumentException("Signature has no data: " + base58Str);
        }

        this.curveParam = EosEcUtil.getCurveParamFrom( parts[1]);
        byte[] rawBytes = EosEcUtil.getBytesIfMatchedRipemd160( parts[2], parts[1], null);

        if ( null == rawBytes ) {
            // TODO handle error!
        }

        setRecid( rawBytes[0] - 27 - 4 ); // recId encoding 이 recId + 27 + (compressed ? 4 : 0) 이므로

        this.r = new BigInteger(Arrays.copyOfRange(rawBytes, 1, 33) );
        this.s = new BigInteger(Arrays.copyOfRange(rawBytes, 33, 65) );
    }

    public void setRecid(int recid ) {
        this.recId = recid;
    }


    /**
     * Returns true if the S component is "low", that means it is below HALF_CURVE_ORDER. See <a
     * href="https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#Low_S_values_in_signatures">BIP62</a>.
     */

    public boolean isCanonical(){
        return s.compareTo( curveParam.halfCurveOrder()) <= 0 ; // Secp256k1Param.HALF_CURVE_ORDER) <= 0;
    }

    /**
     * Will automatically adjust the S component to be less than or equal to half the curve order, if necessary.
     * This is required because for every signature (r,s) the signature (r, -s (mod N)) is a valid signature of
     * the same message. However, we dislike the ability to modify the bits of a Bitcoin transaction after it's
     * been signed, as that violates various assumed invariants. Thus in future only one of those forms will be
     * considered legal and the other will be banned.
     */
    public EcSignature toCanonicalised() {
        if (!isCanonical()) {
            // The order of the curve is the number of valid points that exist on that curve. If S is in the upper
            // half of the number of valid points, then bring it back to the lower half. Otherwise, imagine that
            //    N = 10
            //    s = 8, so (-8 % 10 == 2) thus both (r, 8) and (r, 2) are valid solutions.
            //    10 - 8 == 2, giving us always the latter solution, which is canonical.
            return new EcSignature(r, curveParam.n().subtract(s), curveParam); //Secp256k1Param.n.subtract(s));
        } else {
            return this;
        }
    }

    @Override
    public boolean equals( Object other) {
        if (this == other)
            return true;

        if ( null == other || getClass() != other.getClass())
            return false;

        EcSignature otherSig = (EcSignature) other;
        return r.equals(otherSig.r) && s.equals(otherSig.s);
    }

    public boolean isRSEachLength(int length) {
        return (r.toByteArray().length == length) && ( s.toByteArray().length == length) ;
    }


    public String eosEncodingHex( boolean compressed ) {
        if ( recId < 0 || recId > 3) {
            throw new IllegalStateException("signature has invalid recid.");
        }

        int headerByte = recId + 27 + (compressed ? 4 : 0);
        byte[] sigData = new byte[65]; // 1 header + 32 bytes for R + 32 bytes for S
        sigData[0] = (byte) headerByte;
        System.arraycopy(EcTools.integerToBytes( this.r, 32), 0, sigData, 1, 32);
        System.arraycopy(EcTools.integerToBytes( this.s, 32), 0, sigData, 33, 32);

        return EosEcUtil.encodeEosCrypto( PREFIX, curveParam , sigData);
    }

    @Override
    public String toString(){
        if ( recId < 0 || recId > 3) {
            return "no recovery sig: "+ HexUtils.toHex(this.r.toByteArray()) + HexUtils.toHex(this.s.toByteArray());
        }

        return eosEncodingHex( true );
    }
}
