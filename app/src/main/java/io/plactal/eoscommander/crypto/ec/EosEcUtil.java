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

import java.util.Arrays;
import java.util.regex.PatternSyntaxException;

import io.plactal.eoscommander.crypto.digest.Ripemd160;
import io.plactal.eoscommander.crypto.digest.Sha256;
import io.plactal.eoscommander.crypto.util.Base58;
import io.plactal.eoscommander.crypto.util.BitUtils;
import io.plactal.eoscommander.util.RefValue;
import io.plactal.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2018-02-02.
 */

public class EosEcUtil {

    public static final String PREFIX_K1 = "K1";
    public static final String PREFIX_R1 = "R1";

//    public static byte[] decodeEosCrypto(String base58Data, RefValue<CurveParam> curveParamRef, RefValue<Long> checksumRef ){
//
//        final byte[] retKeyData;
//
//        final String typePrefix;
//        if ( base58Data.startsWith( EOS_PREFIX ) ) {
//
//            if ( base58Data.startsWith( PREFIX_K1, EOS_PREFIX.length())) {
//                typePrefix = PREFIX_K1;
//            }
//            else
//            if ( base58Data.startsWith( PREFIX_R1, EOS_PREFIX.length())) {
//                typePrefix = PREFIX_R1;
//            }
//            else {
//                typePrefix = null;
//            }
//
//            retKeyData = getBytesIfMatchedRipemd160( base58Data.substring( EOS_PREFIX.length() ), typePrefix, checksumRef);
//        }
//        else{
//            typePrefix = null;
//            retKeyData = getBytesIfMatchedSha256( base58Data, checksumRef );
//        }
//
//        if ( curveParamRef != null) {
//            curveParamRef.data = EcTools.getCurveParam( PREFIX_R1.equals( typePrefix ) ? CurveParam.SECP256_R1 : CurveParam.SECP256_K1);
//        }
//
//        return retKeyData;
//    }

    public static byte[] extractFromRipemd160( String base58Data ) {
        byte[] data= Base58.decode( base58Data );
        if ( data[0] == data.length) {
            return Arrays.copyOfRange(data, 2, data.length );
        }

        return null;
    }

//    public static byte[] getBytesIfMatchedRipemd160(String base58Data, String prefix, RefValue<Long> checksumRef ){
//        byte[] prefixBytes = StringUtils.isEmpty(prefix) ? new byte[0] : prefix.getBytes();
//
//        byte[] data= Base58.decode( base58Data.substring( prefixBytes.length));
//
//        byte[] toHashData = new byte[data.length - 4 + prefixBytes.length];
//        System.arraycopy( data, 0, toHashData, 0, data.length - 4); // key data
//
//        System.arraycopy( prefixBytes, 0, toHashData, data.length - 4, prefixBytes.length);
//
//        Ripemd160 ripemd160 = Ripemd160.from( toHashData); //byte[] data, int startOffset, int length
//        long checksumByCal = BitUtils.uint32ToLong( ripemd160.bytes(), 0);
//        long checksumFromData= BitUtils.uint32ToLong(data, data.length - 4 );
//        if ( checksumByCal != checksumFromData ) {
//            throw new IllegalArgumentException("Invalid format, checksum mismatch");
//        }
//
//        if ( checksumRef != null ){
//            checksumRef.data = checksumFromData;
//        }
//
//        return Arrays.copyOfRange(data, 0, data.length - 4);
//    }

    public static byte[] getBytesIfMatchedRipemd160(String base58Data, String prefix, RefValue<Long> checksumRef ){
        byte[] prefixBytes = StringUtils.isEmpty(prefix) ? new byte[0] : prefix.getBytes();

        byte[] data= Base58.decode( base58Data );

        byte[] toHashData = new byte[data.length - 4 + prefixBytes.length];
        System.arraycopy( data, 0, toHashData, 0, data.length - 4); // key data

        System.arraycopy( prefixBytes, 0, toHashData, data.length - 4, prefixBytes.length);

        Ripemd160 ripemd160 = Ripemd160.from( toHashData); //byte[] data, int startOffset, int length
        long checksumByCal = BitUtils.uint32ToLong( ripemd160.bytes(), 0);
        long checksumFromData= BitUtils.uint32ToLong(data, data.length - 4 );
        if ( checksumByCal != checksumFromData ) {
            throw new IllegalArgumentException("Invalid format, checksum mismatch");
        }

        if ( checksumRef != null ){
            checksumRef.data = checksumFromData;
        }

        return Arrays.copyOfRange(data, 0, data.length - 4);
    }

    public static byte[] getBytesIfMatchedSha256(String base58Data,RefValue<Long> checksumRef ){
        byte[] data= Base58.decode( base58Data );

        // offset 0은 제외, 뒤의 4바이트 제외하고, private key 를 뽑자
        Sha256 checkOne = Sha256.from( data, 0, data.length - 4 );
        Sha256 checkTwo = Sha256.from( checkOne.getBytes() );
        if ( checkTwo.equalsFromOffset( data, data.length - 4, 4)
                || checkOne.equalsFromOffset( data, data.length - 4, 4) ){

            if ( checksumRef != null ){
                checksumRef.data = BitUtils.uint32ToLong( data, data.length - 4);
            }

            return Arrays.copyOfRange(data, 1, data.length - 4);
        }

        throw new IllegalArgumentException("Invalid format, checksum mismatch");
    }

//    public static String encodeEosCrypto(byte[] data, CurveParam curveParam ) {
//        boolean isR1 = ( null != curveParam ) && curveParam.isType( CurveParam.SECP256_R1);
//
//        byte[] toHashData = new byte[ data.length + (isR1 ? PREFIX_R1.length() : 0) ];
//        System.arraycopy( data, 0, toHashData, 0, data.length);
//        if ( isR1 ) {
//            System.arraycopy( PREFIX_R1.getBytes(), 0, toHashData, data.length, PREFIX_R1.length());
//        }
//
//        byte[] result = new byte[ data.length + 4 ];
//
//        Ripemd160 ripemd160 = Ripemd160.from( toHashData); //byte[] data, int startOffset, int length
//        byte[] checksumBytes = ripemd160.bytes();
//
//        System.arraycopy( data, 0, result, 0, data.length); // copy source data
//        System.arraycopy( checksumBytes, 0, result, data.length, 4); // copy checksum data
//
//        return EOS_PREFIX + ( isR1 ? PREFIX_R1 : "") + Base58.encode( result );
//    }

    public static String encodeEosCrypto(String prefix, CurveParam curveParam, byte[] data ) {
        String typePart = "";
        if ( curveParam != null ) {
            if ( curveParam.isType( CurveParam.SECP256_K1)) {
                typePart = PREFIX_K1;
            }
            else
            if ( curveParam.isType( CurveParam.SECP256_R1)){
                typePart = PREFIX_R1;
            }
        }

        byte[] toHashData = new byte[ data.length + typePart.length() ];
        System.arraycopy( data, 0, toHashData, 0, data.length);
        if ( typePart.length() > 0 ) {
            System.arraycopy( typePart.getBytes(), 0, toHashData, data.length, typePart.length());
        }

        byte[] dataToEncodeBase58 = new byte[ data.length + 4 ];

        Ripemd160 ripemd160 = Ripemd160.from( toHashData);
        byte[] checksumBytes = ripemd160.bytes();

        System.arraycopy( data, 0, dataToEncodeBase58, 0, data.length); // copy source data
        System.arraycopy( checksumBytes, 0, dataToEncodeBase58, data.length, 4); // copy checksum data


        String result;
        if ( StringUtils.isEmpty( typePart)) {
            result = prefix;
        }
        else {
            result = prefix + EOS_CRYPTO_STR_SPLITTER + typePart + EOS_CRYPTO_STR_SPLITTER;
        }

        return result + Base58.encode( dataToEncodeBase58 );
    }




    private static final String EOS_CRYPTO_STR_SPLITTER = "_";
    public static String[] safeSplitEosCryptoString( String cryptoStr ) {
        if ( StringUtils.isEmpty( cryptoStr)) {
            return new String[]{ cryptoStr };
        }

        try {
            return cryptoStr.split( EOS_CRYPTO_STR_SPLITTER );
        }
        catch (PatternSyntaxException e){
            e.printStackTrace();
            return new String[]{ cryptoStr };
        }
    }

    public static String concatEosCryptoStr( String... strData ) {

        String result="";

        for ( int i = 0; i < strData.length; i++) {
            result += strData[i] + ( i < strData.length -1 ? EOS_CRYPTO_STR_SPLITTER : "");
        }
        return result;
    }

    public static CurveParam getCurveParamFrom( String curveType ) {
        return EcTools.getCurveParam( PREFIX_R1.equals( curveType ) ? CurveParam.SECP256_R1 : CurveParam.SECP256_K1);
    }


//    public static EosCryptoProperty getEosCryptoProperty( String cryptoStr ) {
//        if ( StringUtils.isEmpty( cryptoStr)) {
//            return new EosCryptoProperty( cryptoStr );
//        }
//
//        String[] splitted = null;
//        try {
//            splitted = cryptoStr.split(EOS_CRYPTO_STR_SPLITTER);
//
//            if ( splitted == null || splitted.length <= 1) {
//                return new EosCryptoProperty( cryptoStr );
//            }
//
//            return new EosCryptoProperty( splitted[0], null, splitted[1]);
//        }
//    }
}
