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
package io.plactal.eoscommander.data.wallet;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.plactal.eoscommander.crypto.digest.Sha512;
import io.plactal.eoscommander.crypto.ec.EosPrivateKey;
import io.plactal.eoscommander.crypto.ec.EosPublicKey;
import io.plactal.eoscommander.crypto.util.CryptUtil;
import io.plactal.eoscommander.crypto.util.HexUtils;
import io.plactal.eoscommander.data.remote.model.types.EosByteWriter;
import io.plactal.eoscommander.data.remote.model.types.EosType;
import io.plactal.eoscommander.data.remote.model.types.EosByteReader;
import io.plactal.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2017-09-25.
 */

public class EosWallet implements EosType.Packer, EosType.Unpacker {

    public static class Status {
        public String   walletName;
        public boolean  locked;

        public Status( String name, boolean locked ) {
            this.walletName = name;
            this.locked = locked;
        }
    }

    private static int ENCRYPT_KEY_LEN = 32;
    private static String WALLET_DATA_JSON_KEY = "cipher_keys";


    private String  mFilePath;
    private byte[] mWalletData;

    /**
     * pubKey -> wif map. String 을 사용하지 않고, builder 를 사용한 이유는,
     * 민감한 private key 라서, 더이상 사용하지 않더라도 mem dump 해서도 그 정보가 나타나지 말아야 한다.
     *
     * 그래서 lock 을 걸때 WIF 가 mem 상에서 유지되지 않도록 하려면 String data 를 clear 해야 하는데,
     * 단순히 객체를 교체할 게 아니라, 기존 객체의 data를 overwrite 해야함. String 은 이게 불가능. StringBuilder 는 setLength(0) 하면, 기존 버퍼를 0 으로 채움.
     */
    private Map<EosPublicKey, String> mKeys;
    private Sha512 mChecksum;

    public EosWallet( ) {
        this(null);
    }

    public EosWallet(byte[] initialData ) {
        mWalletData = initialData;
        mKeys = new HashMap<>();

        mChecksum = ( null == initialData ) ? Sha512.ZERO_HASH : Sha512.from( initialData );
    }

    private String ensureFilePathEndsWithSeparator( String filePath ) {

        // 1 글자 비교할 건데, endsWith 는 general purpose 라 넘 복잡하게 계산함.
        if ( filePath.charAt( filePath.length() - 1) == File.separatorChar) {
            return filePath;
        }

        return filePath.concat(File.separator);
    }

    public void setWalletFilePath( String filePath ) {
        this.mFilePath = filePath;
    }

    public String getWalletFilePath() {
        return mFilePath;
    }

    public boolean isLocked(){
        return Sha512.ZERO_HASH.equals( mChecksum );
    }

    public Map<EosPublicKey,String> listKeys(){
        return mKeys;
    }


    /**
     * Get the private key corresponding to a public key or nothing.
     */
    public EosPrivateKey tryGetPrivateKey( EosPublicKey pubKey ) {
        String wif = mKeys.get( pubKey );
        if ( null != wif ) {
            return new EosPrivateKey( wif );
        }

        return null;
    }

    /**
     * Get the WIF private key corresponding to a public key.  The
     * private key must already be in the wallet.
     */
    public EosPrivateKey getPrivateKey( EosPublicKey pubKey ) {
        return new EosPrivateKey( mKeys.get( pubKey ) );
    }

    public boolean importKey( String wif ) {
        if ( isLocked() ) {
            return false;
        }

        EosPrivateKey key = new EosPrivateKey(wif); // 잘못된 key 면 exception throw 됨.

        mKeys.put( key.getPublicKey(), wif );

        return true;
    }

    public boolean importKey(EosPrivateKey[] keys ) {
        if ( isLocked() ) {
            return false;
        }

        for ( EosPrivateKey privKey : keys ){
            mKeys.put(privKey.getPublicKey(), privKey.toString() );
        }

        return true;
    }

    private boolean loadReader(Reader contentReader ){
        JsonReader reader = null;
        try {
            reader = new JsonReader( contentReader );
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse( reader );

            JsonElement itemElement = element.getAsJsonObject().get(WALLET_DATA_JSON_KEY);
            if ( null == itemElement ){
                return false;
            }

            String hexData = itemElement.getAsString();
            if ( StringUtils.isEmpty( hexData )) {
                return false;
            }

            mWalletData = HexUtils.toBytes( hexData );
            return true;

        }
        catch ( IllegalStateException | JsonIOException |JsonSyntaxException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if ( null != reader ) {
                try {
                    reader.close();
                } catch (Throwable t) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }
            }
        }
    }

    public boolean loadString( String jsonString ){
        return loadReader( new StringReader( jsonString ));
    }

    public boolean loadFile( File jsonFile ){
        if ( ! jsonFile.exists() ) {
            return false;
        }

        try {
            return loadReader(new FileReader(jsonFile));
        }
        catch ( FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean loadFile( String filePath ) {
        if ( StringUtils.isEmpty( filePath )) {
            if ( StringUtils.isEmpty( mFilePath )) {
                return false;
            }

            filePath = mFilePath;
        }

        return loadFile( new File( filePath ) );
    }

    public boolean saveFile( String filePath ) {
        if ( StringUtils.isEmpty( filePath )) {
            if ( StringUtils.isEmpty( mFilePath )) {
                return false;
            }

            filePath = mFilePath;
        }

        // encrypt 부터해야지!!!
        encryptKeys();

        FileOutputStream fos = null;
        try {
            Gson gson = new Gson();

            JsonObject object = new JsonObject();
            object.addProperty( WALLET_DATA_JSON_KEY, HexUtils.toHex(mWalletData) );

            String json = gson.toJson( object );
            if ( StringUtils.isEmpty( json)) {
                return false;
            }

            fos = new FileOutputStream( filePath);
            fos.write( json.getBytes());
            fos.flush();

            return true;
        }
        catch ( FileNotFoundException fne){
            fne.printStackTrace();
            return false;
        }
        catch (SecurityException se){
            se.printStackTrace();
            return false;
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }
        finally {
            if ( null != fos ) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private byte[] getIv(Sha512 hash) {
        return Arrays.copyOfRange(hash.getBytes(), ENCRYPT_KEY_LEN, ENCRYPT_KEY_LEN + 16 );
    }

    private void encryptKeys() {
        if ( isLocked() ) {
            return;
        }

        // mKeys, mChecksum 을 serialize(pack) 한뒤 그 데이터를 check 을 key 로 해서 encrypt 한다.
        EosByteWriter writer = new EosByteWriter(256) ;
        this.pack( writer );

        mWalletData = CryptUtil.aesEncrypt( Arrays.copyOf( mChecksum.getBytes(), ENCRYPT_KEY_LEN )
                        , writer.toBytes(), getIv(mChecksum ) );
    }

    public void lock() {
        if ( isLocked() ) {
            return ;
        }

        encryptKeys();


        // clear data
        mKeys.clear();
        mChecksum = Sha512.ZERO_HASH;
    }

    public void unlock( String password ) {
        Preconditions.checkArgument( (password != null ) && ( password.length() > 0) );

        Sha512 pw = Sha512.from( password.getBytes() );

        // hash 의 앞 ENCRYPT_KEY_LEN(32) 는 key로, 뒤의 32 바이트중 16 바이트는 iv 로 사용한다.
        byte[] decrypted = CryptUtil.aesDecrypt( Arrays.copyOf( pw.getBytes(), ENCRYPT_KEY_LEN ),
                mWalletData, getIv(pw) );

        if ( null == decrypted ) { // not match!
            return;
        }



        // save data to recover when error occurs.
        Sha512 oldChecksum = mChecksum;
        Map<EosPublicKey,String> oldKeys = mKeys;

        try {
            // 아래의 함수 안에서 mChecksum, mKeys 가 load 됨.
            this.unpack( new EosByteReader(decrypted) );

            if ( mChecksum.compareTo( pw) != 0 ) {
                mChecksum = oldChecksum;
                mKeys = oldKeys;
            }
        }
        catch ( EosType.InsufficientBytesException e) {
            e.printStackTrace();

            mChecksum = oldChecksum;
            mKeys = oldKeys;
        }
    }

    public boolean isNew() {
        return mKeys.size() == 0;
    }

    public void setPassword( String password ){
        if ( ! isNew() ) {
            if (isLocked()) {
                throw new WalletLockedException("The wallet must be unlocked before the password can be set");
            }
        }

        mChecksum = Sha512.from(password.getBytes());
        lock();
    }

    @Override
    public void pack(EosType.Writer writer) {
        writer.putBytes( mChecksum.getBytes() );


        writer.putVariableUInt(mKeys.size());
        for (Map.Entry<EosPublicKey, String> entry : mKeys.entrySet()) {

            // key
            writer.putString( entry.getKey().toString() );

            // value
            writer.putString( entry.getValue().toString() );
        }
    }

    @Override
    public void unpack(EosType.Reader reader) throws EosType.InsufficientBytesException {
        mChecksum = new Sha512( reader.getBytes( Sha512.HASH_LENGTH ) );

        int keyCount = (int)reader.getVariableUint();

        HashMap<EosPublicKey,String> keys= new HashMap<>( keyCount );

        for ( int i = 0; i < keyCount; i++) {
            keys.put( new EosPublicKey( reader.getString()), reader.getString() );
        }

        mKeys = keys;
    }

    public static class WalletLockedException extends IllegalStateException {
        public WalletLockedException(String msg ) {
            super( msg );
        }
    }
}
