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
 * Created by swapnibble on 2017-09-12.
 */

public class EosNewAccount implements EosType.Packer {
    private TypeAccountName mCreator;
    private TypeAccountName mNewName;
    private TypeAuthority mOwner;
    private TypeAuthority mActive;
    private TypeAuthority mRecovery;
    private TypeAsset mDeposit;

//    public static class Deserializer implements JsonDeserializer<EosNewAccount> {
//
//        @Override
//        public EosNewAccount deserialize(JsonElement json, Packer typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            final JsonObject jsonObject = json.getAsJsonObject();
//
//            return null;
//        }
//    }

    public String getTypeName() {
        return "newaccount";
    }

    public EosNewAccount(String creator, String newName,
                         TypeAuthority owner, TypeAuthority active, TypeAuthority recovery, TypeAsset deposit) {

        this( new TypeAccountName(creator), new TypeAccountName(newName), owner, active, recovery, deposit );
    }

    public EosNewAccount( String creator, String newName,
                          String ownerPubKeyInHex, String activePubKeyInHex, String recoveryAccountWithOneWeight) {

        this( new TypeAccountName(creator), new TypeAccountName(newName)
                , new TypeAuthority(1, ownerPubKeyInHex, null)
                , new TypeAuthority(1, activePubKeyInHex, null)
                , new TypeAuthority(1, null, recoveryAccountWithOneWeight)
                , new TypeAsset(1) );
    }

    public EosNewAccount(TypeAccountName creator, TypeAccountName newName,
                         TypeAuthority owner, TypeAuthority active, TypeAuthority recovery, TypeAsset deposit) {

        mCreator = creator;
        mNewName = newName;
        mOwner = owner;
        mActive = active;
        mRecovery = recovery;
        mDeposit = deposit;
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
        mRecovery.pack(writer);
        mDeposit.pack(writer);
    }

    public String getAsHex() {
        EosType.Writer writer = new EosByteWriter(256);

        pack(writer);
        return HexUtils.toHex( writer.toBytes() );
    }
}
