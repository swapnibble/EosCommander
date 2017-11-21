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
package io.mithrilcoin.eoscommander.data.remote.model.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.mithrilcoin.eoscommander.crypto.util.HexUtils;
import io.mithrilcoin.eoscommander.data.remote.model.types.EosType;
import io.mithrilcoin.eoscommander.data.remote.model.types.TypeAccountName;
import io.mithrilcoin.eoscommander.data.remote.model.types.TypeAccountPermission;
import io.mithrilcoin.eoscommander.data.remote.model.types.TypeFuncName;
import timber.log.Timber;

/**
 * Created by swapnibble on 2017-09-11.
 */

public class Message implements EosType.Packer {
    private TypeAccountName code;

    private TypeFuncName type;

    @SerializedName("authorization")
    @Expose
    private List<TypeAccountPermission> authorization = null;

    @SerializedName("data")
    @Expose
    private JsonElement data;

    @SerializedName("hex_data")
    @Expose
    private String hexData = null;

    public Message(String code, String type, TypeAccountPermission authorization, String data){
        this.code = new TypeAccountName(code);
        this.type = new TypeFuncName(type);
        this.authorization = new ArrayList<>();
        if ( null != authorization ) {
            this.authorization.add(authorization);
        }

        if ( null != data ) {
            this.data = new JsonPrimitive(data);
        }
    }

    public Message(String code, String type) {
        this ( code, type, null, null);
    }

    public Message(){
        this ( null, null, null, null);
    }

    public String getCode() {
        return code.toString();
    }

    public void setCode( String code) {
        this.code = new TypeAccountName(code);
    }

    public String getType() {
        return type.toString();
    }

    public void setType( String type) {
        this.type = new TypeFuncName(type) ;
    }

    public List<TypeAccountPermission> getAuthorization() {
        return authorization;
    }

    public void setAuthorization(List<TypeAccountPermission> authorization) {
        this.authorization = authorization;
    }

    public void setAuthorization(TypeAccountPermission[] authorization) {
        this.authorization.addAll( Arrays.asList( authorization) );
    }

    public void setAuthorization(String[] accountWithPermLevel) {
        if ( null == accountWithPermLevel){
            return;
        }

        for ( String permissionStr : accountWithPermLevel ) {
            String[] splited = permissionStr.split("@", 2);
            authorization.add( new TypeAccountPermission(splited[0], splited[1]) );
        }
    }

    public String getData() {
        return ( null != data) ? data.getAsString() : null;
    }

    public void setData(String data) {
        this.data = ( null != data) ? new JsonPrimitive(data) : null;
    }

    public String getHexData() { return hexData; }
    public void setHexData(String h){ this.hexData = h;}

    @Override
    public void pack(EosType.Writer writer) {
        code.pack(writer);
        type.pack(writer);

        writer.putCollection( authorization );

        if ( null != data ) {
            byte[] dataAsBytes = HexUtils.toBytes( data.getAsString() );
            writer.putVariableUInt(dataAsBytes.length);
            writer.putBytes( dataAsBytes );
        }
        else {
            writer.putVariableUInt(0);
        }
    }

    public static class GsonTypeAdapterFactory extends EoscGsonTypeAdapterFactory<Message> {
        public GsonTypeAdapterFactory(){
            super(Message.class);
        }

        @Override
        protected void beforeWrite(Message source, JsonElement toSerialize) {
            JsonObject jsonObject = toSerialize.getAsJsonObject();
            jsonObject.addProperty("code", source.getCode());
            jsonObject.addProperty("type", source.getType());
        }

        @Override
        protected void afterRead(Message source, JsonElement deserialized) {
            JsonObject jsonObject = deserialized.getAsJsonObject();
            source.setCode( jsonObject.get("code").getAsString() );
            source.setType( jsonObject.get("type").getAsString() );
        }
    }
}
