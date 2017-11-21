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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.mithrilcoin.eoscommander.data.remote.model.api.EoscGsonTypeAdapterFactory;


/**
 * Created by swapnibble on 2017-09-12.
 */

public class TypeAccountPermission implements EosType.Packer {

    private TypeAccountName mAccount;
    private TypePermissionName mPermission;

    public TypeAccountPermission(String accountName, String permissionName) {
        mAccount = new TypeAccountName(accountName);
        mPermission = new TypePermissionName( permissionName);
    }

    public String getAccount(){
        return mAccount.toString();
    }

    public void setAccount(String accountName ){
        mAccount = new TypeAccountName( accountName );
    }

    public String getPermission(){
        return mPermission.toString();
    }

    public void setPermission(String permissionName ){
        mPermission = new TypePermissionName( permissionName);
    }

    @Override
    public void pack(EosType.Writer writer) {

        mAccount.pack(writer);
        mPermission.pack(writer);
    }

    public static class GsonTypeAdapterFactory extends EoscGsonTypeAdapterFactory<TypeAccountPermission> {
        public GsonTypeAdapterFactory(){
            super(TypeAccountPermission.class);
        }

        @Override
        protected void beforeWrite(TypeAccountPermission source, JsonElement toSerialize) {
            JsonObject jsonObject = toSerialize.getAsJsonObject();
            jsonObject.addProperty("account", source.getAccount());
            jsonObject.addProperty("permission", source.getPermission());
        }

        @Override
        protected void afterRead(TypeAccountPermission source, JsonElement deserialized) {
            JsonObject jsonObject = deserialized.getAsJsonObject();
            source.setAccount( jsonObject.get("account").getAsString() );
            source.setPermission( jsonObject.get("permission").getAsString() );
        }
    }
}
