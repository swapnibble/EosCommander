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
package io.mithrilcoin.eoscommander.ui.wallet;

import java.util.ArrayList;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.data.EoscDataManager;
import io.mithrilcoin.eoscommander.ui.base.BasePresenter;
import io.mithrilcoin.eoscommander.ui.base.RxCallbackWrapper;
import io.mithrilcoin.eoscommander.util.Consts;
import io.mithrilcoin.eoscommander.util.StringUtils;
import io.reactivex.Observable;

/**
 * Created by swapnibble on 2017-11-08.
 */

public class WalletPresenter extends BasePresenter<WalletMvpView> {

    @Inject
    EoscDataManager mDataManager;

    @Inject
    public WalletPresenter(){
    }

    public void createNewWallet(String name ) {
        getMvpView().showLoading( true );

        addDisposable(
                Observable.fromCallable( () -> mDataManager.getWalletManager().create( name ))
                        .subscribeOn( getSchedulerProvider().io())
                        .observeOn( getSchedulerProvider().ui())
                        .subscribeWith( new RxCallbackWrapper<String>(this){
                            @Override
                            public void onNext(String pw) {
                                if ( ! isViewAttached() ) return;

                                getMvpView().showLoading( false );
                                getMvpView().showCreatedPassword( name, pw
                                        , mDataManager.getPreferenceHelper().getSavePasswordOption(), true );
                            }
                        })
        );
    }

    public void onFinishedSetupView(){
        getMvpView().showCreateDefaultWalletButton( ! mDataManager.getWalletManager().defaultWalletExists() );

        loadWallets();
    }

    public void loadWallets() {
        getMvpView().showWalletList( mDataManager.getWalletManager().listWallets( null ) );
    }

    public void createDefaultWallet() {
        addDisposable(
                Observable.fromCallable( () -> mDataManager.getWalletManager().createTestingDefaultWallet())
                        .subscribeOn( getSchedulerProvider().io())
                        .observeOn( getSchedulerProvider().ui())
                        .subscribeWith( new RxCallbackWrapper<String>(this){
                            @Override
                            public void onNext(String pw) {
                                if ( ! isViewAttached() ) return;

                                getMvpView().showCreatedPassword( Consts.DEFAULT_WALLET_NAME, pw
                                        , mDataManager.getPreferenceHelper().getSavePasswordOption(), false );
                                getMvpView().showCreateDefaultWalletButton( false );
                            }
                        })
        );
    }

    public void onRequestShowKeys() {
        // show keys of unlocked wallets
        ArrayList<String> keyPairs = mDataManager.getWalletManager().listKeysAsPairString();
        if ( keyPairs.size() <= 0 ) {
            getMvpView().onError(R.string.no_keys);
            return;
        }

        StringBuilder outputBuilder = new StringBuilder( 128 * keyPairs.size());
        outputBuilder.append('[');

        for ( String pair : keyPairs ) {
            outputBuilder.append("[\n  ").append( pair).append("\n  ],\n");
        }

        outputBuilder.append(']');

        getMvpView().showKeys( outputBuilder.toString() );
    }


    public void changeLockStatus(String walletName ) {
        if ( mDataManager.getWalletManager().isLocked(walletName) ) {
            getMvpView().getPassword( walletName, mDataManager.getPreferenceHelper().getWalletPassword( walletName ) );
        }
        else {
            mDataManager.getWalletManager().lock( walletName );
            loadWallets();

            if ( mDataManager.getWalletManager().isLocked( walletName )) {
                getMvpView().showToast(R.string.locked);
            }
        }
    }

    public void unlockWallet(String walletName, String password){
        mDataManager.getWalletManager().unlock(walletName, password);

        // 바뀐 상태를 update 하도록..
        loadWallets();

        if (! mDataManager.getWalletManager().isLocked( walletName )) {
            getMvpView().showToast(R.string.unlocked);
        }
    }

    public void onRequestImportKey(String walletName){
        if ( mDataManager.getWalletManager().isLocked( walletName )) {
            getMvpView().onError( R.string.should_unlock_to_import_key);
            return;
        }

        getMvpView().getKeyToImport( walletName );
    }

    public void importKey( String walletName, String key ) {
        if ( mDataManager.getWalletManager().isLocked( walletName )) {
            getMvpView().onError( R.string.should_unlock_to_import_key);
            return;
        }

        try {
            mDataManager.getWalletManager().importKey(walletName, key);

            mDataManager.getWalletManager().saveFile(walletName);
        }
        catch (Exception e){
            getMvpView().onError( e.getMessage() );
        }
    }

    public void onShownPassword( String walletName, String password ) {
        // save default account when wallet is "default" and password is not empty( user allowed "save password" )
        if ( Consts.DEFAULT_WALLET_NAME.equals( walletName) && !StringUtils.isEmpty( password )) {
            mDataManager.getPreferenceHelper().putDefaultAccountCreator( Consts.DEFAULT_SERVANT_ACCOUNT);

            mDataManager.getWalletManager().saveFile( Consts.DEFAULT_WALLET_NAME );
        }

        mDataManager.getPreferenceHelper().putWalletPassword( walletName, password );

        loadWallets();
    }
}
