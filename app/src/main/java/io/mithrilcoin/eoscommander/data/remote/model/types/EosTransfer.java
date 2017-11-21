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
package io.mithrilcoin.eoscommander.data.remote.model.types;


import io.mithrilcoin.eoscommander.crypto.util.HexUtils;

/**
 * Created by swapnibble on 2017-09-15.
 */

public class EosTransfer implements EosType.Packer {
    private TypeAccountName mFrom;
    private TypeAccountName mTo;
    private long mAmount;
    private String mMemo;

    public EosTransfer(String from, String to, long amount, String memo ) {
        this( new TypeAccountName(from), new TypeAccountName(to), amount, memo );
    }

    public EosTransfer(TypeAccountName from, TypeAccountName to, long amount, String memo ) {
        mFrom = from;
        mTo = to;
        mAmount = amount;
        mMemo = memo;
    }

    public String getTypeName() {
        return "transfer";
    }


    @Override
    public void pack(EosType.Writer writer) {

        mFrom.pack(writer);
        mTo.pack(writer);

        writer.putLongLE( mAmount);

        writer.putString( mMemo );
    }

    public String getAsHex() {
        EosType.Writer writer = new EosByteWriter(128);
        pack(writer);

        return HexUtils.toHex( writer.toBytes() );
    }
}
