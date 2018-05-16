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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.plactal.eoscommander.crypto.ec.EosPrivateKey;
import io.plactal.eoscommander.crypto.ec.EosPublicKey;
import io.plactal.eoscommander.data.remote.model.chain.PackedTransaction;
import io.plactal.eoscommander.data.remote.model.chain.SignedTransaction;
import io.plactal.eoscommander.data.remote.model.types.TypeChainId;
import io.plactal.eoscommander.util.Consts;

/**
 * Created by swapnibble on 2017-09-19.
 */

public class EosWalletManager {
    private static final String EOS_WALLET_PASSWD_PREFIX = "PW";
    private static final String EOS_WALLET_FILE_EXT = ".wallet";

    private File mDir;
    private HashMap<String, EosWallet> mWallets;
    private boolean mDefaultWalletExists;

    public EosWalletManager() {
        mWallets = new HashMap<>();
    }

    public void setDir(File dir ) {
        mDir = dir;
    }

    public int openExistingsInDir(){
        if ( null == mDir ){
            return 0;
        }

        int count =0;
        File[] files = mDir.listFiles();

        for ( File walletFile : files ) {

            try {
                open( walletFile);
                count++;
            }
            catch (RuntimeException e){
                // ignore..
            }
        }

        return count;
    }

    public boolean defaultWalletExists(){
        return mDefaultWalletExists;
    }


    private String genPassword() {
        EosPrivateKey key = new EosPrivateKey();
        return EOS_WALLET_PASSWD_PREFIX + key.toWif();
    }

    public String createTestingDefaultWallet() throws IOException {
        String pw = create( Consts.DEFAULT_WALLET_NAME);

        importKey( Consts.DEFAULT_WALLET_NAME, Consts.SAMPLE_PRIV_KEY_FOR_TEST);

        saveFile( Consts.DEFAULT_WALLET_NAME );

        return pw;
    }

    /**
     * Create a new wallet.
     * A new wallet is created in file dir/{name}.wallet see set_dir.
     * The new wallet is unlocked after creation.
     * @param name  name of the wallet and name of the file without ext .wallet.
     * @return Plaintext password that is needed to unlock wallet. Caller is responsible for saving password otherwise
     *          they will not be able to unlock their wallet. Note user supplied passwords are not supported.
     */
    public String create( String name) throws IOException{
        String password = genPassword();
        File walletFile = new File( mDir, name + EOS_WALLET_FILE_EXT);

        if ( walletFile.exists() ) {
            throw new IllegalStateException( String.format("Wallet with name: '%1$s' already exists", name ));
        }

        walletFile.createNewFile();

        EosWallet eosWallet = new EosWallet();
        eosWallet.setPassword( password );
        eosWallet.setWalletFilePath( walletFile.getAbsolutePath());
        eosWallet.unlock( password );
        eosWallet.saveFile( walletFile.getAbsolutePath() );

        eosWallet.lock();
        eosWallet.unlock( password );

        // put 은 이미 있으면 replace 한다.
        mWallets.put(name, eosWallet );

        checkDefaultWallet( name );

        return password;
    }

    /**
     * check given fileNameWithExt is the name of default wallet.
     * @param fileNameWithoutExt
     */
    private void checkDefaultWallet( String fileNameWithoutExt ) {
        if ( ! mDefaultWalletExists && fileNameWithoutExt.equals(Consts.DEFAULT_WALLET_NAME)) {
            mDefaultWalletExists = true;
        }
    }

    public boolean walletExists(String name ) {
        return new File( mDir, name + EOS_WALLET_FILE_EXT).exists();
    }

    public void open( String name){
        open( new File( mDir, name ) );
    }

    private void open( File walletFile ) {
        EosWallet eosWallet = new EosWallet();
        eosWallet.setWalletFilePath( walletFile.getAbsolutePath() );

        if (! eosWallet.loadFile( "" )){
            throw new RuntimeException("Unable to open file: " + walletFile.getName());
        }


        String fileName = walletFile.getName();
        String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf(EOS_WALLET_FILE_EXT));


        // put 은 이미 있으면 replace 한다.
        mWallets.put( nameWithoutExt, eosWallet );

        checkDefaultWallet(nameWithoutExt );
    }


    /**
     * list wallets.
     * @param listLocked    pass true when you want to list locked wallets.
     *                      pass false when you want to list unlocked wallets.
     *                      pass null when you want to list all wallets. ( don't care lock status)
     * @return
     */
    public ArrayList<EosWallet.Status> listWallets( Boolean listLocked ) {
        ArrayList<EosWallet.Status> result = new ArrayList<>( mWallets.size() );

        if ( mWallets.size() <= 0 ){
            return result;
        }

        for ( Map.Entry<String, EosWallet> entry : mWallets.entrySet() ){

            if ( ( null == listLocked) || ( listLocked == entry.getValue().isLocked())) {
                result.add(new EosWallet.Status(entry.getKey(), entry.getValue().isLocked()));
            }
        }

        return result;
    }

    public Map<EosPublicKey, String> listKeys(){
        HashMap<EosPublicKey, String> result = new HashMap<>();

        for ( Map.Entry<String, EosWallet> walletEntry : mWallets.entrySet() ){

            if (! walletEntry.getValue().isLocked() ) {
                result.putAll( walletEntry.getValue().listKeys());
            }
        }

        return result;
    }

    public ArrayList<String> listPubKeys() {
        Map<EosPublicKey, String> map = listKeys();

        ArrayList<String> result = new ArrayList<>( map.size());

        for ( EosPublicKey publicKey : map.keySet() ){
           result.add( publicKey.toString());
        }

        return result;
    }

    public ArrayList<String> listKeysAsPairString(){
        Map<EosPublicKey, String> keyMap = listKeys();

        ArrayList<String> retList = new ArrayList<>( keyMap.size());

        StringBuilder pairBuilder = new StringBuilder(128);
        for ( Map.Entry<EosPublicKey, String> walletEntry : keyMap.entrySet() ){
            pairBuilder.append("\"")
                    .append( walletEntry.getKey().toString())
                    .append("\", \"")
                    .append( walletEntry.getValue())
                    .append("\"");

            retList.add( pairBuilder.toString());
            pairBuilder.setLength(0);
        }

        return retList;
    }




    public boolean isLocked(String name) {
        if (! mWallets.containsKey(name) ) {
            throw new IllegalStateException("Wallet not found: " + name);
        }

        return mWallets.get( name ).isLocked();
    }


    public void lockAll() {
        for (Map.Entry<String, EosWallet> walletEntry : mWallets.entrySet()) {
            if ( ! walletEntry.getValue().isLocked() ) {
                walletEntry.getValue().lock();
            }
        }
    }

    public void lock( String name) throws IllegalStateException {
        if (! mWallets.containsKey(name) ) {
            throw new IllegalStateException("Wallet not found: " + name);
        }

        EosWallet eosWallet = mWallets.get( name );
        if ( !eosWallet.isLocked()){
            eosWallet.lock();
        }
    }

    public void unlock( String name, String password){
        if ( ! mWallets.containsKey( name)) {
            open( name );
        }

        EosWallet eosWallet = mWallets.get( name );
        if ( ! eosWallet.isLocked() ) {
            return;
        }

        eosWallet.unlock( password);
    }

    public void importKey( String name, String wif) throws IllegalStateException {
        if (! mWallets.containsKey(name) ) {
            throw new IllegalStateException("Wallet not found: " + name);
        }

        EosWallet eosWallet = mWallets.get( name );
        if ( eosWallet.isLocked() ) {
            throw new IllegalStateException("Wallet is locked: " + name);
        }

        eosWallet.importKey( wif );
    }

    public void importKeys( String walletName, EosPrivateKey[] privateKeys) throws IllegalStateException {
        if (! mWallets.containsKey(walletName) ) {
            throw new IllegalStateException("Wallet not found: " + walletName);
        }

        EosWallet eosWallet = mWallets.get( walletName );
        if ( eosWallet.isLocked() ) {
            throw new IllegalStateException("Wallet is locked: " + walletName);
        }

        eosWallet.importKey( privateKeys );
    }

    public void saveFile( String walletName ) {
        if (! mWallets.containsKey(walletName) ) {
            throw new IllegalStateException("Wallet not found: " + walletName);
        }

        EosWallet eosWallet = mWallets.get( walletName );
        if ( eosWallet.isLocked() ) {
            throw new IllegalStateException("Wallet is locked: " + walletName);
        }

        eosWallet.saveFile(null);
    }

    public SignedTransaction signTransaction(final SignedTransaction txn,
               final List<EosPublicKey> keys, final TypeChainId id) throws IllegalStateException{

        SignedTransaction stxn = new SignedTransaction( txn );

        for ( EosPublicKey pubKey : keys ){
            boolean found = false;

            for (Map.Entry<String, EosWallet> walletEntry : mWallets.entrySet()) {
                if (! walletEntry.getValue().isLocked() ) {
                    EosPrivateKey privKey = walletEntry.getValue().tryGetPrivateKey( pubKey );
                    if ( null != privKey ){
                        stxn.sign( privKey, id);
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                throw new IllegalStateException("Public key not found in unlocked wallets " + pubKey);
            }
        }

        return stxn;
    }
}
