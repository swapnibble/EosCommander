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

import java.util.Arrays;

/**
 * Created by swapnibble on 2017-09-12.
 */

public class TypeName implements EosType.Packer {
    private static final String CHAR_MAP = ".12345abcdefghijklmnopqrstuvwxyz";

    private static final int MAX_NAME_IDX = 12;
    private long mValue;

    static byte char_to_symbol( char c ){
        if( c >= 'a' && c <= 'z' )
            return (byte)((c - 'a') + 6);
        if( c >= '1' && c <= '5' )
            return (byte)((c - '1') + 1);

        return (byte)0;
    }

    static public long string_to_name( String str ) {
        if ( null == str ) {
            return 0L;
        }

        int len = str.length();
        long value = 0;

        for (int i = 0; i <= MAX_NAME_IDX; i++) {
            long c = 0;

            if( i < len && i <= MAX_NAME_IDX) c = char_to_symbol( str.charAt(i) );

            if( i < MAX_NAME_IDX) {
                c &= 0x1f;
                c <<= 64-5*(i+1);
            }
            else {
                c &= 0x0f;
            }

            value |= c;
        }

        return value;
    }

    static public String name_to_string( long nameAsLong ) {
        long tmp = nameAsLong;

        char[] result = new char[MAX_NAME_IDX + 1];
        Arrays.fill( result, ' ');

        for( int i = 0; i <= MAX_NAME_IDX; ++i ) {
            char c = CHAR_MAP.charAt( (int)(tmp & (i == 0 ? 0x0f : 0x1f)) );
            result[MAX_NAME_IDX-i] = c;
            tmp >>= (i == 0 ? 4 : 5);
        }

        return String.valueOf( result ).replaceAll("[.]+$", ""); // remove trailing dot
    }

    public TypeName(long nameAsLong) {
        mValue = nameAsLong;
    }

    public TypeName(String name) {
        mValue = string_to_name( name);
    }

    @Override
    public void pack(EosType.Writer writer) {
        writer.putLongLE( mValue);
    }

    @Override
    public String toString(){
        return name_to_string( mValue );
    }

}
