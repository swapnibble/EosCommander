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

import io.plactal.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2017-09-12.
 */

public class TypeAccountName extends TypeName {
    private static final int MAX_ACCOUNT_NAME_LEN = 12;

    private static final char CHAR_NOT_ALLOWED = '.';

    public TypeAccountName(String name) {
        super(name);

        if (! StringUtils.isEmpty( name )) {
            if (name.length() > MAX_ACCOUNT_NAME_LEN ) {
                throw new IllegalArgumentException("account name can only be 12 chars long: " + name) ; // changed from dawn3
            }

            if ( (name.indexOf( CHAR_NOT_ALLOWED) >= 0) && ! name.startsWith("eosio.") ){
                throw new IllegalArgumentException("account name must not contain '.': " + name);
            }
        }
    }
}
