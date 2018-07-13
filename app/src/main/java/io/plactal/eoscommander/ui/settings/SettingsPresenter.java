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
package io.plactal.eoscommander.ui.settings;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.plactal.eoscommander.R;
import io.plactal.eoscommander.data.EoscDataManager;
import io.plactal.eoscommander.data.remote.HostInterceptor;
import io.plactal.eoscommander.data.remote.model.api.EosChainInfo;
import io.plactal.eoscommander.ui.base.BasePresenter;
import io.plactal.eoscommander.ui.base.RxCallbackWrapper;
import io.plactal.eoscommander.util.Consts;
import io.plactal.eoscommander.util.RefValue;
import io.plactal.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2017-11-03.
 */

public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private final EoscDataManager mDataManager;
    private final HostInterceptor mHostInterceptor;

    private boolean mConnected;

    @Inject
    public SettingsPresenter(EoscDataManager dataManager, HostInterceptor hostInterceptor ){
        mDataManager = dataManager;
        mHostInterceptor = hostInterceptor;

        mConnected = false;
    }

    @Override
    public void attachView(SettingsMvpView mvpView) {
        super.attachView( mvpView );

        List<String> symbolList = Arrays.asList( Consts.DEFAULT_SYMBOL_STRING, Consts.EOS_SYMBOL_STRING);
        mvpView.addCoreSymbols(symbolList
                    , symbolList.indexOf( mDataManager.getPreferenceHelper().getCoreSymbolString()) );


        RefValue<Integer> portRef = new RefValue<>(0);
        RefValue<String> schemeRef = new RefValue<>();

        // fetch connection info from preference
        String host = mDataManager.getPreferenceHelper().getNodeosConnInfo( portRef, schemeRef );
        String port = portRef.data != 0 ? String.valueOf(portRef.data) : "";
        boolean connInfoOk = false;
        if ( validateConnInfo( host, port ) > 0) {
            getMvpView().showConnInfo(host, portRef.data);
            connInfoOk = true;
        }

        List<String> schemeList = Arrays.asList( "http", "https");
        mvpView.addConnScheme(  schemeList, schemeList.indexOf( schemeRef.data ) );

        // get skip signing option
        boolean skipSigning = mDataManager.getPreferenceHelper().shouldSkipSigning();
        getMvpView().showCheckOptions( skipSigning );

        // connect
        if ( connInfoOk ) {
            tryConnectNodeos( schemeRef.data, host, port );
        }
    }

    public void changeCoreSymbol( String symbolStr ) {
        if ( StringUtils.isEmpty( symbolStr )) return;

        mDataManager.updateCoreSymbol( symbolStr, Consts.DEFAULT_SYMBOL_PRECISION );
    }


    private int validateConnInfo( CharSequence host, CharSequence port ) {

        if (StringUtils.isEmpty( host ) ){
            return 0;
        }

        int parsedPort ;
        try {
            parsedPort = Integer.valueOf(port.toString());
            if ( (parsedPort < 0 ) || ( parsedPort >= 65536) ) {
                return -1;
            }

            return parsedPort;
        }
        catch ( NumberFormatException e){
            //e.printStackTrace();
            return -2;
        }
    }

    private void processConnResult(EosChainInfo info, String scheme, String host, int port){

        try {
            if (info != null) {
                mConnected = true;
                mDataManager.getPreferenceHelper().putNodeosConnInfo(scheme, host, port);
                getMvpView().showConnStatus( true, info.getBrief());
            }
            else {
                mConnected = false;
                getMvpView().showConnStatus( false, "");
                getMvpView().onError( R.string.conn_failed);
            }
        }
        catch (MvpViewNotAttachedException e){
            e.printStackTrace();
        }
    }

    public void tryConnectNodeos(String scheme, CharSequence host, CharSequence port){
        String hostAddress = host.toString();
        int nodeosPort = validateConnInfo( hostAddress, port );
        if ( nodeosPort <= 0 ) {
            getMvpView().onError( R.string.invalid_connection_info);
            getMvpView().showConnStatus(false, null);
            return;
        }

        getMvpView().hideKeyboard();

        // change host info
        mHostInterceptor.setInterceptor(scheme, hostAddress , nodeosPort); // "http"

        getMvpView().showLoading( true );

        addDisposable (
            mDataManager.getChainInfo()
                .subscribeOn( getSchedulerProvider().io())
                .observeOn( getSchedulerProvider().ui())
                .subscribeWith( new RxCallbackWrapper<EosChainInfo>(this) {
                        @Override
                        public void onNext(EosChainInfo info) {
                            if ( !isViewAttached() ) return;

                            getMvpView().showLoading( false );
                            processConnResult( info, scheme, hostAddress, nodeosPort );
                        }
                    }
                )
        );
    }

    public void onBackButton(){
        getMvpView().exitWithResult( mConnected );
    }

    public void onChangeIgnoreSignature(boolean skipSigning) {
        mDataManager.getPreferenceHelper().putSkipSigning( skipSigning );
    }
}
