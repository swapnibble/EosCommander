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
package io.mithrilcoin.eoscommander.data;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mithrilcoin.eoscommander.crypto.ec.EosPrivateKey;
import io.mithrilcoin.eoscommander.data.local.repository.EosAccountRepository;
import io.mithrilcoin.eoscommander.data.prefs.PreferencesHelper;
import io.mithrilcoin.eoscommander.data.remote.EosdApi;
import io.mithrilcoin.eoscommander.data.remote.model.api.AccountInfoRequest;
import io.mithrilcoin.eoscommander.data.remote.model.api.EosChainInfo;
import io.mithrilcoin.eoscommander.data.remote.model.api.GetTableRequest;
import io.mithrilcoin.eoscommander.data.remote.model.api.JsonToBinRequest;
import io.mithrilcoin.eoscommander.data.remote.model.api.Message;
import io.mithrilcoin.eoscommander.data.remote.model.api.PushTxnResponse;
import io.mithrilcoin.eoscommander.data.remote.model.chain.GetRequiredKeys;
import io.mithrilcoin.eoscommander.data.remote.model.chain.SignedTransaction;
import io.mithrilcoin.eoscommander.data.remote.model.types.EosNewAccount;
import io.mithrilcoin.eoscommander.data.remote.model.types.EosTransfer;
import io.mithrilcoin.eoscommander.data.remote.model.types.TypeChainId;
import io.mithrilcoin.eoscommander.data.wallet.EosWalletManager;
import io.mithrilcoin.eoscommander.util.Consts;
import io.mithrilcoin.eoscommander.util.Utils;
import io.reactivex.Observable;

import static io.mithrilcoin.eoscommander.util.Consts.TX_EXPIRATION_IN_MILSEC;

/**
 * Created by swapnibble on 2017-11-03.
 */
@Singleton
public class EoscDataManager {

    private final EosdApi mEosdApi;
    private final PreferencesHelper mPrefHelper;
    private final EosWalletManager  mWalletMgr;
    private final EosAccountRepository mAccountRepository;

    @Inject
    public EoscDataManager(EosdApi eosdApi, EosWalletManager walletManager, EosAccountRepository accountRepository, PreferencesHelper prefHelper) {
        mEosdApi = eosdApi;
        mWalletMgr  = walletManager;
        mAccountRepository = accountRepository;
        mPrefHelper = prefHelper;

        mWalletMgr.setDir( mPrefHelper.getWalletDirFile() );
        mWalletMgr.openExistingsInDir();
    }

    public EosWalletManager getWalletManager() { return mWalletMgr; }

    public PreferencesHelper getPreferenceHelper() { return mPrefHelper; }


    public void addAccountHistory(String... accountNames){
        mAccountRepository.addAll(accountNames);
    }

    public void addAccountHistory(List<String> accountNames){
        mAccountRepository.addAll(accountNames);
    }

    public void deleteAccountHistory( String accountName ) {
        mAccountRepository.delete( accountName );
    }

    public List<String> getAllAccountHistory( boolean getFromCacheIfPossible) {
        return mAccountRepository.getAll(getFromCacheIfPossible);
    }

    public Observable<EosChainInfo> getChainInfo(){

        return mEosdApi.readInfo("get_info");
    }

    public Observable<String> getTable( String accountName, String code, String table ){
        return mEosdApi.getTable( new GetTableRequest(accountName, code, table))
                .map( tableResult -> Utils.prettyPrintJson(tableResult));
    }

    public Observable<EosPrivateKey[]> createKey( int count ) {
        return Observable.fromCallable( () -> {
            EosPrivateKey[] retKeys = new EosPrivateKey[ count ];
            for ( int i = 0; i < count; i++) {
                retKeys[i] = new EosPrivateKey();
            }

            return retKeys;
        } );
    }

    private SignedTransaction createTransaction( String contract, String action, String dataAsHex,
                                String[] scopes, String[] permissions, EosChainInfo chainInfo ){
        Message msg = new Message();
        msg.setCode( contract );
        msg.setAuthorization(permissions);
        msg.setType( action );
        msg.setData( dataAsHex );

        SignedTransaction txn = new SignedTransaction();
        txn.setScope( scopes );
        txn.addMessage( msg );
        txn.setReadScopeList(new ArrayList<>(0));
        txn.setSignatures( new ArrayList<>());


        if ( null != chainInfo ) {
            txn.setReferenceBlock(chainInfo.getHeadBlockId());
            txn.setExpiration(chainInfo.getTimeAfterHeadBlockTime(TX_EXPIRATION_IN_MILSEC));
        }

        return txn;
    }

    private Observable<SignedTransaction> signTransaction( SignedTransaction txnBeforeSign ) {
        if ( mPrefHelper.shouldSkipSigning() ) {
            return Observable.just( txnBeforeSign );
        }

        return mEosdApi.getRequiredKeys( new GetRequiredKeys( txnBeforeSign, mWalletMgr.listPubKeys() ))
                .map( keys -> mWalletMgr.signTransaction( txnBeforeSign, keys.getKeys(), new TypeChainId() ));
    }


    private String[] getActivePermission(String accountName ) {
        return new String[] { accountName + "@active" };
    }


    public Observable<JsonObject> readAccountInfo(String accountName ) {
        return mEosdApi.getAccountInfo(new AccountInfoRequest(accountName));
    }

    public Observable<JsonObject> transferEos( String from, String to, long amount, String memo ) {

        return getChainInfo()
                .map( info -> {
                    EosTransfer transfer = new EosTransfer(from, to, amount, memo);

                    return createTransaction( Consts.EOS_CONTRACT_NAME, transfer.getTypeName(), transfer.getAsHex(),
                            new String[]{ from, to }, getActivePermission(from), info);
                })
                .flatMap( txn -> signTransaction( txn ) )
                .flatMap( signedTxn -> mEosdApi.pushTransactionRetJson( signedTxn )) ;
    }

    public Observable<PushTxnResponse> createAccount(EosNewAccount newAccountData) {

        return getChainInfo()
                .map( info -> createTransaction( Consts.EOS_CONTRACT_NAME, newAccountData.getTypeName(), newAccountData.getAsHex()
                                    , new String[]{ newAccountData.getCreatorName(),Consts.EOS_CONTRACT_NAME }
                                    , getActivePermission( newAccountData.getCreatorName() ), info ))
                .flatMap( txn -> signTransaction( txn))
                .flatMap( signedTxn -> mEosdApi.pushTransaction( signedTxn ));
    }

    public Observable<JsonObject> getTransactions(String accountName ) {

        JsonObject gsonObject = new JsonObject();
        gsonObject.addProperty( EosdApi.GET_TRANSACTIONS_KEY, accountName);

        return mEosdApi.getAccountHistory( EosdApi.ACCOUNT_HISTORY_GET_TRANSACTIONS, gsonObject);
    }

    public Observable<JsonObject> getServants( String accountName ) {
        // controlling_account

        JsonObject gsonObject = new JsonObject();
        gsonObject.addProperty( EosdApi.GET_SERVANTS_KEY, accountName);

        return mEosdApi.getAccountHistory( EosdApi.ACCOUNT_HISTORY_GET_SERVANTS, gsonObject);
    }

    public Observable<JsonObject> pushMessage(String contract, String action, String message, String [] scopes, String[] permissions) {

        return mEosdApi.jsonToBin( new JsonToBinRequest( contract, action, message ))
                .flatMap( jsonToBinResp -> getChainInfo()
                                            .map( info -> createTransaction( contract, action, jsonToBinResp.getBinargs(), scopes, permissions, info )) )
                .flatMap( txn -> signTransaction( txn))
                .flatMap( signedTxn -> mEosdApi.pushTransactionRetJson( signedTxn));
    }
}
