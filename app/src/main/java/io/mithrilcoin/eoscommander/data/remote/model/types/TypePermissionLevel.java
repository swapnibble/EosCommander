/*
 * Copyright (c) 2017-2018 Mithril coin.
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
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by swapnibble on 2017-09-12.
 */

public class TypePermissionLevel implements EosType.Packer {

    @SerializedName("actor")
    @Expose
    private TypeName mActor;

    @SerializedName("permission")
    @Expose
    private TypeName mPermission;

    public TypePermissionLevel(String accountName, String permissionName) {
        mActor = new TypeName(accountName);
        mPermission = new TypeName(permissionName);
    }

    public String getAccount(){
        return mActor.toString();
    }

    public void setAccount(String accountName ){
        mActor = new TypeName(accountName);
    }

    public String getPermission(){
        return mPermission.toString();
    }

    public void setPermission(String permissionName ){
        mPermission = new TypeName(permissionName);
    }

    @Override
    public void pack(EosType.Writer writer) {

        mActor.pack(writer);
        mPermission.pack(writer);
    }
}
