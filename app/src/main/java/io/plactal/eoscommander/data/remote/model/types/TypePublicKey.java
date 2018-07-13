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

import io.plactal.eoscommander.crypto.ec.EosPublicKey;

/**
 * Created by swapnibble on 2018-03-20.
 */

public class TypePublicKey implements EosType.Packer {
    private static final byte PACK_VAL_CURVE_PARAM_TYPE_K1 = 0;
    private static final byte PACK_VAL_CURVE_PARAM_TYPE_R1 = 1;

    private final EosPublicKey mPubKey;

    public static TypePublicKey from(EosPublicKey publicKey) {
        return new TypePublicKey(publicKey);
    }

    public TypePublicKey( EosPublicKey publicKey ) {
        mPubKey = publicKey;
    }


    @Override
    public void pack(EosType.Writer writer) {
        writer.putVariableUInt( mPubKey.isCurveParamK1() ? PACK_VAL_CURVE_PARAM_TYPE_K1 : PACK_VAL_CURVE_PARAM_TYPE_R1 );

        writer.putBytes( mPubKey.getBytes());
    }
}
