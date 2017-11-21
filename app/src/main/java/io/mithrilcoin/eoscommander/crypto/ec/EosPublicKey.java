/*
 * Copyright (c) 2017 Mithril coin.
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

import com.google.common.base.Preconditions;

import java.util.Arrays;

import io.mithrilcoin.eoscommander.crypto.digest.Ripemd160;
import io.mithrilcoin.eoscommander.crypto.util.Base58;
import io.mithrilcoin.eoscommander.crypto.util.BitUtils;
import io.mithrilcoin.eoscommander.crypto.util.HexUtils;


/**
 * Created by swapnibble on 2017-09-25.
 */

public class EosPublicKey {
    private static final String PUBKEY_PREFIX = "EOS";
    private static final int CHECK_BYTE_LEN = 4;

    private long mCheck;
    private final byte[] mData;

    public static class IllegalEosPubkeyFormatException extends IllegalArgumentException {
        public IllegalEosPubkeyFormatException(String pubkeyStr) {
            super("invalid eos public key : " + pubkeyStr);
        }
    }

    public EosPublicKey( byte[] data ){
        mData = Arrays.copyOf(data, 33);

        mCheck= BitUtils.uint32ToLong( Ripemd160.from( mData, 0, mData.length).bytes(), 0 );
    }

    public EosPublicKey(String base58Str) {
        Preconditions.checkArgument( base58Str.length() > PUBKEY_PREFIX.length());
        Preconditions.checkArgument( PUBKEY_PREFIX.equals(base58Str.substring(0, PUBKEY_PREFIX.length())) );

        byte[] decodedBytes = Base58.decode( base58Str.substring( PUBKEY_PREFIX.length()) );

        long check_fromDecoded = BitUtils.uint32ToLong(decodedBytes, decodedBytes.length - CHECK_BYTE_LEN);

        long check_fromCalculated = BitUtils.uint32ToLong( Ripemd160.from( decodedBytes, 0, decodedBytes.length - CHECK_BYTE_LEN ).bytes(), 0 );



        if (check_fromDecoded != check_fromCalculated) {
            throw new IllegalEosPubkeyFormatException(base58Str);
        }

        mCheck= check_fromDecoded;
        mData = Arrays.copyOf(decodedBytes, decodedBytes.length - CHECK_BYTE_LEN);
    }

    public byte[] getBytes() {
        return mData;
    }

    public String getBytesAsHexStr() {
        return HexUtils.toHex( mData );
    }

    @Override
    public String toString() {
        byte[] digest = Ripemd160.from( mData ).bytes();
        byte[] result = new byte[ CHECK_BYTE_LEN + mData.length];

        System.arraycopy( mData, 0, result, 0, mData.length);
        System.arraycopy( digest, 0, result, mData.length, CHECK_BYTE_LEN);

        return PUBKEY_PREFIX + Base58.encode( result ) ;
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
}
