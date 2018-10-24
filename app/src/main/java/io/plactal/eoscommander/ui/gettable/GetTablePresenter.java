/*
 * Copyright (c) 2017-2018 PlayerOne.
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
package io.plactal.eoscommander.ui.gettable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.plactal.eoscommander.data.EoscDataManager;
import io.plactal.eoscommander.data.remote.model.abi.EosAbiMain;
import io.plactal.eoscommander.data.remote.model.abi.EosAbiTable;
import io.plactal.eoscommander.ui.base.BasePresenter;
import io.plactal.eoscommander.ui.base.RxCallbackWrapper;
import io.plactal.eoscommander.util.StringUtils;
import io.plactal.eoscommander.util.Utils;
import io.reactivex.Single;

/**
 * Created by swapnibble on 2017-11-17.
 */

public class GetTablePresenter extends BasePresenter<GetTableMvpView> {
    @Inject
    EoscDataManager mDataManager;

    private static final String[] KEY_INDEX_POSITION = { "primary (first)", "2nd", "3rd", "4th", "5th"
                                  , "6th", "7th", "8th", "9th", "10th"};
    private static final int FIRST_POS_INDEX_VAL = 1;


    private static final String[] KEY_INDEX_TYPES = { "name(account_name)", "i64", "i128", "i256", "float64", "float128", "ripemd160", "sha256" };
    private static final int KEY_INDEX_TYPES_POSITION_I256 = 3;
    private static final int KEY_INDEX_TYPES_POSITION_RIPEMD160 = 6;


    private static final String[] KEY_INDEX_ENCODINGS = { "dec", "hex" };
    private static final int KEY_INDEX_ENCODINGS_POS_DEC = 0;
    private static final int KEY_INDEX_ENCODINGS_POS_HEX = 1;

    @Inject
    public GetTablePresenter(){
    }

    public void onFinishedSetupView(){

        getMvpView().populateKeyInfo( Arrays.asList( KEY_INDEX_POSITION )
                , Arrays.asList( KEY_INDEX_TYPES ), Arrays.asList( KEY_INDEX_ENCODINGS )  );

    }

    public void onIndexTypeOrEncodingSelected(int typePosSelected, int encodingPosSelected ){
        // match type and encoding!

        // KEY_INDEX_TYPES = { "name(account_name)", "i64", "i128", "i256", "float64", "float128", "ripemd160", "sha256" }

        // (i64 , i128 , float64, float128) only support "dec" encoding ( encodePos 0 )
        // i256 - supports both 'dec' and 'hex'
        // ripemd160 and sha256 is 'hex' only

        if ( typePosSelected >= KEY_INDEX_TYPES_POSITION_RIPEMD160 ) { // ripemd160, sha256
            // only hex!
            if ( KEY_INDEX_ENCODINGS_POS_HEX != encodingPosSelected ) {
                getMvpView().setTypeEncodingSelection( KEY_INDEX_ENCODINGS_POS_HEX );
            }

            return;
        }

        if ( KEY_INDEX_TYPES_POSITION_I256 != typePosSelected ) {
            // only dec !
            if ( KEY_INDEX_ENCODINGS_POS_DEC != encodingPosSelected ) {
                getMvpView().setTypeEncodingSelection( KEY_INDEX_ENCODINGS_POS_DEC );
            }
        }
    }

    private List<String> getTableNames( List<EosAbiTable> abiTables ) {
        if ( null == abiTables ){
            return new ArrayList<>();
        }

        ArrayList<String> names = new ArrayList<>( abiTables.size() );
        for ( EosAbiTable table : abiTables ){
            names.add( table.name );
        }

        Collections.sort( names);

        return names;
    }

    public void onGetTableListClicked( String contract ){
        if ( StringUtils.isEmpty(contract) ){
            return;
        }

        getMvpView().showLoading( true );

        addDisposable( mDataManager.getAbi( contract )
                .map( abi -> {
                    mDataManager.addAccountHistory( contract);
                    return getTableNames( abi.tables);
                })
                .subscribeOn( getSchedulerProvider().io())
                .observeOn( getSchedulerProvider().ui())
                .subscribeWith(new RxCallbackWrapper<List<String>>( this) {
                                   @Override
                                   public void onNext(List<String> result) {
                                       if (!isViewAttached()) return;

                                       getMvpView().showLoading(false);
                                       getMvpView().showTableList( result);
                                   }
                               }
                )
        );
    }

    private int getIndexPosValue( int position) {
        return position + FIRST_POS_INDEX_VAL;
    }

    public void getTable(String accountName, String contract, String table,
                         int keyPosition, int keyType, int keyEncoding, String lowerBound, String upperBound, String limit ) {

        addDisposable(
            mDataManager.getTable( accountName, contract, table
                    , getIndexPosValue( keyPosition ), KEY_INDEX_TYPES[ keyType ], KEY_INDEX_ENCODINGS[ keyEncoding ],
                                        lowerBound, upperBound, Utils.parseIntSafely(limit, 0))
                    .doOnNext( result -> mDataManager.addAccountHistory( accountName, contract) )
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribeWith( new RxCallbackWrapper<String>( this ) {
                        @Override
                        public void onNext(String result) {

                            if ( ! isViewAttached() ) return;

                            getMvpView().showLoading( false );

                            if ( !StringUtils.isEmpty( result)) {
                                getMvpView().showTableResult( result, null );
                            }
                        }
                    })
        );

    }
}
