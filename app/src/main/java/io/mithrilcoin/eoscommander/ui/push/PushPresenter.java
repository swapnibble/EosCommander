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
package io.mithrilcoin.eoscommander.ui.push;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.data.EoscDataManager;
import io.mithrilcoin.eoscommander.data.remote.model.abi.EosAbiMain;
import io.mithrilcoin.eoscommander.data.remote.model.api.PushTxnResponse;
import io.mithrilcoin.eoscommander.ui.base.BasePresenter;
import io.mithrilcoin.eoscommander.ui.base.RxCallbackWrapper;
import io.mithrilcoin.eoscommander.util.StringUtils;
import io.mithrilcoin.eoscommander.util.Utils;


/**
 * Created by swapnibble on 2017-11-08.
 */

public class PushPresenter extends BasePresenter<PushMvpView> {
    private static final String KEY_FOR_ABI_OBJECT = "push_action_abi";

    private EosAbiMain mContractAbi;

    @Inject
    EoscDataManager mDataManager;

    @Inject
    public PushPresenter(){
    }

    public void onGetAbiClicked( String contract){
        getMvpView().showLoading( true );
        addDisposable( mDataManager.getCodeAbi( contract )
                        .doOnNext( abi -> mDataManager.addAccountHistory( contract))
                        .subscribeOn( getSchedulerProvider().io())
                        .observeOn( getSchedulerProvider().ui())
                        .subscribeWith(new RxCallbackWrapper<EosAbiMain>( this) {
                                   @Override
                                   public void onNext(EosAbiMain result) {
                                       if (!isViewAttached()) return;

                                       getMvpView().showLoading(false);

                                       mContractAbi = result;
                                       getMvpView().buildContractView( result);
                                   }
                               }
                        )
        );
    }

    // for test
    public void testAbiByString( String jsonString ) {
        addDisposable( mDataManager.getAbiMainFromJson( jsonString )
                .subscribeOn( getSchedulerProvider().io())
                .observeOn( getSchedulerProvider().ui())
                .subscribeWith(new RxCallbackWrapper<EosAbiMain>( this) {
                                   @Override
                                   public void onNext(EosAbiMain result) {
                                       if (!isViewAttached()) return;

                                       getMvpView().showLoading(false);

                                       mContractAbi = result;
                                       getMvpView().buildContractView( result);
                                   }
                               }
                )
        );
    }

    public void onRequestInputUi( String actionName ) {
        if ( StringUtils.isEmpty( actionName) ) {
            getMvpView().showToast(R.string.select_action_after_abi);
            return;
        }

        // save abi object to datamanager to pass it to input dialog
        mDataManager.pushAbiObject( KEY_FOR_ABI_OBJECT, mContractAbi);

        getMvpView().openDynInputFromAbi(KEY_FOR_ABI_OBJECT, actionName);
    }


    public void onImportFileClicked(){
        getMvpView().openFileManager();
    }

    // parse scopes into array
    private String[] getArrayFromSeparator(String csv) {
        csv = csv.trim();

        if (StringUtils.isEmpty(csv)) {
            return new String []{""};
        }

        StringTokenizer tokenizer = new StringTokenizer( csv, ", "); // added ' '. 2017.12.04.
        int tokenCount = tokenizer.countTokens();
        ArrayList<String> parsed = new ArrayList<>( tokenCount);

        for ( int i = 0; i < tokenCount; i++ ){
            String token = tokenizer.nextToken();
            if ( ! StringUtils.isEmpty( token)) {
                parsed.add( token );
            }
        }

        return parsed.toArray( new String[ parsed.size()]);
    }

    private ArrayList<String> getAccountListForHistory( String contract, String permissionAccount ){
        ArrayList<String> historyAccounts = new ArrayList<>(2);
        historyAccounts.add( contract );
        historyAccounts.add( permissionAccount );

        return historyAccounts;
    }

    public void pushAction(String contract, String action, String message, String scopes, String permissionAccount, String permissionName ){

        getMvpView().showLoading( true );

        // can make
        String[] permissions = ( StringUtils.isEmpty(permissionAccount) || StringUtils.isEmpty( permissionName))
                            ? null : new String[]{permissionAccount + "@" + permissionName };


        addDisposable(
                mDataManager.pushAction(contract, action, message.replaceAll("\\r|\\n","")
                                , permissions)
                .mergeWith( jsonObject -> mDataManager.addAccountHistory( getAccountListForHistory( contract, permissionAccount) ))
                .subscribeOn( getSchedulerProvider().io())
                .observeOn( getSchedulerProvider().ui())
                .subscribeWith(new RxCallbackWrapper<PushTxnResponse>( this) {
                       @Override
                       public void onNext(PushTxnResponse result) {
                           if (!isViewAttached()) return;

                            getMvpView().showLoading(false);

                            getMvpView().showResult( Utils.prettyPrintJson( result ), result.toString() );
                       }
                   }
                )
        );
    }

    public void onImportMessageFile( String filePath ) {
        if( StringUtils.isEmpty( filePath )) {
            return;
        }

        BufferedReader reader = null;
        StringBuilder  builder = new StringBuilder();
        char[] buffer = new char[1024];
        int read;

        try {
            // read file to string
            reader = new BufferedReader(new FileReader (filePath));
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }

            // pretty print
            JSONObject json = new JSONObject(builder.toString()); // Convert text to object
            getMvpView().showContractMessage(json.toString(2)); // Print it with specified indentation
        }
        catch (FileNotFoundException fne) {
            getMvpView().onError( fne.getMessage() );
        }
        catch (IOException e){
            getMvpView().onError( e.getMessage() );
        }
        catch (JSONException jsonE){
            getMvpView().onError( jsonE.getMessage() );
        }
        finally {
            Utils.closeSilently( reader );
        }
    }


}
