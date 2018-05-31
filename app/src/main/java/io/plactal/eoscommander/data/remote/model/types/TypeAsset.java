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
package io.plactal.eoscommander.data.remote.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by swapnibble on 2017-09-12.
 */

public class TypeAsset implements EosType.Packer {

    private static final String CORE_SYMBOL_NAME = "SYS";
    private static final long CORE_SYMBOL = 0x0000000053595304L; // (int64_t(4) | (uint64_t('S') << 8) | (uint64_t('Y') << 16) | (uint64_t('S') << 24))

    // 아래 table 은, eos/libraries/types/Asset.cpp 에서 가져옴.
    private static final long[] PRECISION_TABLE = {
            1, 10, 100, 1000, 10000,
            100000, 1000000, 10000000, 100000000,
            1000000000, 10000000000L,
            100000000000L, 1000000000000L,
            10000000000000L, 100000000000000L
    };

    private long mAmount;
    private final long mAssetSymbol;
    private final String mSymbolName;

    public TypeAsset(String value) {

        value = value.trim();

        Pattern pattern = Pattern.compile("^([0-9]+)\\.?([0-9]*)[ ]([a-zA-Z0-9]{1,7})$");//\\s(\\w)$");
        Matcher matcher = pattern.matcher(value);

        if ( matcher.find()) {
            String beforeDotVal = matcher.group(1), afterDotVal = matcher.group(2) ;

            mAmount = Long.valueOf( beforeDotVal + afterDotVal);

            int decimals = afterDotVal.length();
            this.mSymbolName = matcher.group(3);

            this.mAssetSymbol = makeAssetSymbol( mSymbolName, decimals);
        }
        else {
            this.mAmount = 0;
            this.mSymbolName = CORE_SYMBOL_NAME;
            this.mAssetSymbol = CORE_SYMBOL;
        }
    }

    public TypeAsset(long amount) {
        this( amount, CORE_SYMBOL );
    }


    public TypeAsset(long amount, long symbol) {
        mAmount = amount;
        mAssetSymbol = symbol;

        final int byteLen = Long.SIZE / Byte.SIZE;
        int symbolLen = 0;
        char[] sym = new char[byteLen];
        for ( int i = 1; i < byteLen; i++) {
            char oneChar = (char)( (symbol >> (8*i)) & 0xFF );
            if ( oneChar != 0 ) {
                sym[i] = oneChar;
                symbolLen++;
            }
            else {
                break;
            }
        }

        mSymbolName = new String( sym, 1, symbolLen);
    }


    private long makeAssetSymbol(String symbolName, int decimals ) {
        long symbol = 0;
        int nameLen = symbolName.length();
        for (int i = 0; (i < nameLen) && ( i < 7); i++ ) {
            symbol |= (symbolName.charAt( i) <<  ( (i+1) * 8));
        }

        symbol |= decimals;

        return symbol;
    }

    public byte decimals(){
        return (byte)( mAssetSymbol & 0xFF );
    }

    public long precision() {
        int decimal = decimals();
        if ( decimal >= PRECISION_TABLE.length ) {
            decimal = 0;
        }

        return PRECISION_TABLE[ decimal ];
    }

    public double toDouble() {
        return mAmount / precision();
    }

    public String symbolName() {
        return mSymbolName;
    }

    public long assetSymbol(){ return mAssetSymbol;}

    public long getAmount(){ return mAmount;}

    @Override
    public String toString() {
        long precisionVal = precision();
        String result = String.valueOf(  mAmount / precisionVal);

        if ( decimals() > 0 ) {
            long fract = mAmount % precisionVal;
            result += "." + String.valueOf( precisionVal + fract).substring(1);
        }

        return result + " "+ mSymbolName;
    }

    @Override
    public void pack(EosType.Writer writer) {

        writer.putLongLE(mAmount);

        writer.putLongLE( mAssetSymbol);
    }

    public void add( TypeAsset other) {
        //mAmount
    }
}
