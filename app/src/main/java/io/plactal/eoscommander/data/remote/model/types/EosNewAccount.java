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

import io.plactal.eoscommander.crypto.util.HexUtils;

/**
 * Created by swapnibble on 2017-09-12.
 */

public class EosNewAccount implements EosType.Packer {
    private TypeAccountName mCreator;
    private TypeAccountName mNewName;
    private TypeAuthority mOwner;
    private TypeAuthority mActive;

    public String getActionName() {
        return "newaccount";
    }

    public EosNewAccount(String creator, String newName,
                         TypeAuthority owner, TypeAuthority active ) {

        this( new TypeAccountName(creator), new TypeAccountName(newName), owner, active);
    }

    public EosNewAccount( String creator, String newName,
                          TypePublicKey ownerPubKey, TypePublicKey activePubKey) {

        this( new TypeAccountName(creator), new TypeAccountName(newName)
                , new TypeAuthority(1, ownerPubKey, null)
                , new TypeAuthority(1, activePubKey, null)  );
    }

    public EosNewAccount(TypeAccountName creator, TypeAccountName newName,
                         TypeAuthority owner, TypeAuthority active) {

        mCreator = creator;
        mNewName = newName;
        mOwner = owner;
        mActive = active;
    }

    public String getCreatorName(){
        return mCreator.toString();
    }

    @Override
    public void pack(EosType.Writer writer) {

        mCreator.pack(writer);
        mNewName.pack(writer);
        mOwner.pack(writer);
        mActive.pack(writer);
    }

    public String getAsHex() {
        EosType.Writer writer = new EosByteWriter(256);

        pack(writer);
        return HexUtils.toHex( writer.toBytes() );
    }
}
