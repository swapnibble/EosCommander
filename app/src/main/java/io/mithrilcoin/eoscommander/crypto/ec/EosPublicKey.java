/*
 * Copyright (c) 2017-2018 Mithril coin.
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
package io.mithrilcoin.eoscommander.crypto.ec;

import java.util.Arrays;

import io.mithrilcoin.eoscommander.crypto.digest.Ripemd160;
import io.mithrilcoin.eoscommander.crypto.util.Base58;
import io.mithrilcoin.eoscommander.crypto.util.BitUtils;
import io.mithrilcoin.eoscommander.crypto.util.HexUtils;
import io.mithrilcoin.eoscommander.util.RefValue;


/**
 * Created by swapnibble on 2017-09-25.
 */

public class EosPublicKey {
    private static final int CHECK_BYTE_LEN = 4;

    private final long mCheck;
    private final CurveParam mCurveParam;
    private final byte[] mData;

    public static class IllegalEosPubkeyFormatException extends IllegalArgumentException {
        public IllegalEosPubkeyFormatException(String pubkeyStr) {
            super("invalid eos public key : " + pubkeyStr);
        }
    }

    public EosPublicKey( byte[] data ){
        this( data, EcTools.getCurveParam( CurveParam.SECP256_K1));
    }

    public EosPublicKey( byte[] data, CurveParam curveParam ){
        mData = Arrays.copyOf(data, 33);
        mCurveParam = curveParam;

        mCheck= BitUtils.uint32ToLong( Ripemd160.from( mData, 0, mData.length).bytes(), 0 );
    }

    public EosPublicKey(String base58Str) {

        RefValue<CurveParam> curveParamRefValue = new RefValue<>();
        RefValue<Long> checksumRef = new RefValue<>();
        mData = EosEcUtil.parseKeyBase58( base58Str, curveParamRefValue, checksumRef );
        mCurveParam = curveParamRefValue.data;
        mCheck = checksumRef.data;
    }

    public byte[] getBytes() {
        return mData;
    }

    public String getBytesAsHexStr() {
        return HexUtils.toHex( mData );
    }



    @Override
    public String toString() {

        byte[] postfixBytes = CurveParam.SECP256_R1 == mCurveParam.getCurveParamType() ? EosEcUtil.PREFIX_R1.getBytes() : new byte[0] ;
        byte[] toDigest = new byte[mData.length + postfixBytes.length];
        System.arraycopy( mData, 0, toDigest, 0, mData.length);

        if ( postfixBytes.length > 0) {
            System.arraycopy(postfixBytes, 0, toDigest, mData.length, postfixBytes.length);
        }

        byte[] digest = Ripemd160.from( toDigest ).bytes();
        byte[] result = new byte[ CHECK_BYTE_LEN + mData.length];

        System.arraycopy( mData, 0, result, 0, mData.length);
        System.arraycopy( digest, 0, result, mData.length, CHECK_BYTE_LEN);

        return EosEcUtil.EOS_PREFIX + Base58.encode( result ) ;
    }

    @Override
    public int hashCode(){
        return (int)(mCheck & 0xFFFFFFFFL );
    }

    @Override
    public boolean equals(Object other) {
        if ( this == other ) return true;

        if ( null == other || getClass() != other.getClass())
            return false;

        return BitUtils.areEqual( this.mData, ((EosPublicKey)other).mData);
    }

    public boolean isCurveParamK1() {
        return ( mCurveParam == null || CurveParam.SECP256_K1 == mCurveParam.getCurveParamType() );
    }
}
