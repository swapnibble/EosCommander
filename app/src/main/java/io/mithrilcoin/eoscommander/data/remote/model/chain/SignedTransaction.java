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
package io.mithrilcoin.eoscommander.data.remote.model.chain;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.mithrilcoin.eoscommander.data.remote.model.types.EosByteWriter;
import io.mithrilcoin.eoscommander.crypto.digest.Sha256;
import io.mithrilcoin.eoscommander.crypto.ec.EcDsa;
import io.mithrilcoin.eoscommander.crypto.ec.EcSignature;
import io.mithrilcoin.eoscommander.crypto.ec.EosPrivateKey;
import io.mithrilcoin.eoscommander.crypto.util.HexUtils;
import io.mithrilcoin.eoscommander.data.remote.model.api.EoscGsonTypeAdapterFactory;
import io.mithrilcoin.eoscommander.data.remote.model.api.Message;
import io.mithrilcoin.eoscommander.data.remote.model.types.EosType;
import io.mithrilcoin.eoscommander.data.remote.model.types.TypeAccountName;
import io.mithrilcoin.eoscommander.data.remote.model.types.TypeChainId;

/**
 * Created by swapnibble on 2017-09-11.
 */

public class SignedTransaction implements EosType.Packer {
    @SerializedName("ref_block_num")
    @Expose
    private BigInteger refBlockNum;

    @SerializedName("ref_block_prefix")
    @Expose
    private BigInteger refBlockPrefix;

    @SerializedName("expiration")
    @Expose
    private String expiration;

    private List<TypeAccountName> scopeList = null;

    private List<TypeAccountName> readScopeList = new ArrayList<>();

    @SerializedName("messages")
    @Expose
    private List<Message> messages = null;


    @SerializedName("signatures")
    @Expose
    private List<String> signatures = null;

    public SignedTransaction(){
    }

    public SignedTransaction( SignedTransaction anotherTxn){
        // belows are shallow copying
        this.refBlockNum = anotherTxn.refBlockNum;
        this.refBlockPrefix = anotherTxn.refBlockPrefix;
        this.expiration = anotherTxn.expiration;

        // belows are only container should be deep copying
        this.scopeList = deepCopyOnlyContainer(anotherTxn.scopeList);

        this.messages = deepCopyOnlyContainer( anotherTxn.messages );

        this.signatures = deepCopyOnlyContainer( anotherTxn.signatures );
    }

    private <T> List<T> deepCopyOnlyContainer(List<T> srcList){
        if ( null == srcList ){
            return null;
        }

        List<T> newList = new ArrayList<>( srcList.size() );
        newList.addAll( srcList);

        return newList;
    }


    public BigInteger getRefBlockNum() {
        return refBlockNum;
    }

    public void setRefBlockNum(BigInteger refBlockNum) {
        this.refBlockNum = refBlockNum;
    }

    public BigInteger getRefBlockPrefix() {
        return refBlockPrefix;
    }

    public void setRefBlockPrefix(BigInteger refBlockPrefix) {
        this.refBlockPrefix = refBlockPrefix;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public void setReferenceBlock( String refBlockIdAsSha256 ) {
        setRefBlockNum( new BigInteger( 1, HexUtils.toBytes(refBlockIdAsSha256.substring(0,8))) );

        // 주의 아래의 toBytesReversed() 를 사용한 것은 의도적인거다!
        setRefBlockPrefix( new BigInteger( 1, HexUtils.toBytesReversed( refBlockIdAsSha256.substring(16,24)) ) );
    }


    public List<TypeAccountName> getScopeList() {
        return scopeList;
    }

    public void setScopeList(List<TypeAccountName> scopeList) {
        this.scopeList = scopeList;
    }

    public void setScope( String... scopeArray) {
        Arrays.sort( scopeArray, String.CASE_INSENSITIVE_ORDER);

        ArrayList<TypeAccountName> scopeList = new ArrayList<>( scopeArray.length );
        for ( String scope : scopeArray ) {
            scopeList.add( new TypeAccountName(scope));
        }

        this.setScopeList( scopeList );
    }

    public void addMessage( Message msg ){
        if ( null == messages ) {
            messages = new ArrayList<>(1);
        }

        messages.add( msg);
    }

    public List<TypeAccountName> getReadScopeList() {
        return readScopeList;
    }

    public void setReadScopeList(List<TypeAccountName> readScopeList) {
        this.readScopeList = readScopeList;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }


    public static Date getExpirationAsDate(String dateStr) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse( dateStr);

        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public List<String> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<String> signatures) {
        this.signatures = signatures;
    }


    private Sha256 getDigestForSignature(TypeChainId chainId) {
        EosByteWriter writer = new EosByteWriter(255);
        writer.putBytes(chainId.getBytes());

        this.pack( writer);

        return Sha256.from(writer.toBytes());
    }

    public void sign(EosPrivateKey privateKey, TypeChainId chainId) {
        if ( null == this.signatures){
            this.signatures = new ArrayList<>();
        }


        EcSignature signature = EcDsa.sign(getDigestForSignature( chainId ), privateKey);
        this.signatures.add( signature.toString());
    }


    @Override
    public void pack(EosType.Writer writer) {

        writer.putShortLE( (short)(refBlockNum.intValue() & 0xFFFF) );// uint16
        writer.putIntLE(refBlockPrefix.intValue());

        writer.putIntLE( (int)(getExpirationAsDate(expiration).getTime() / 1000) ); // ms -> sec

        writer.putCollection( scopeList );
        writer.putCollection( readScopeList );
        writer.putCollection( messages );


    }

    public static class GsonTypeAdapterFactory extends EoscGsonTypeAdapterFactory<SignedTransaction> {
        public GsonTypeAdapterFactory(){
            super(SignedTransaction.class);
        }

        private JsonArray toJsonArray(List<TypeAccountName> typeAccountNames){

            JsonArray jsonArray = new JsonArray( typeAccountNames.size() );
            for (TypeAccountName scope : typeAccountNames ) {
                jsonArray.add( scope.toString());
            }

            return jsonArray;
        }

        private List<TypeAccountName> toAccountNameList(JsonArray jsonArray){

            ArrayList<TypeAccountName> accountNames = new ArrayList<>( jsonArray.size() );

            for ( JsonElement element : jsonArray ) {
                accountNames.add( new TypeAccountName(element.getAsString()));
            }

            return accountNames;
        }

        @Override
        protected void beforeWrite(SignedTransaction source, JsonElement toSerialize) {
            JsonObject jsonObject = toSerialize.getAsJsonObject();

            // scopeList
            List<TypeAccountName> list = source.getScopeList();
            if ( null != list ) {
                jsonObject.add("scope", toJsonArray( list ) );
            }

            // readScopeList
            list = source.getReadScopeList();
            if ( null != list ) {
                jsonObject.add("read_scope", toJsonArray( list ) );
            }
        }

        @Override
        protected void afterRead(SignedTransaction source, JsonElement deserialized) {
            JsonObject jsonObject = deserialized.getAsJsonObject();

            JsonArray jsonArray = jsonObject.getAsJsonArray("scope");
            if ( null != jsonArray ){
                source.setScopeList( toAccountNameList( jsonArray ));
            }

            jsonArray = jsonObject.getAsJsonArray("read_scope");
            if ( null != jsonArray ){
                source.setReadScopeList( toAccountNameList( jsonArray ));
            }
        }
    }
}
