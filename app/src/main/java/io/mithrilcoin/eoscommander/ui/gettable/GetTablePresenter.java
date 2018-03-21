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
package io.mithrilcoin.eoscommander.ui.gettable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.data.EoscDataManager;
import io.mithrilcoin.eoscommander.data.remote.model.abi.EosAbiMain;
import io.mithrilcoin.eoscommander.data.remote.model.abi.EosAbiTable;
import io.mithrilcoin.eoscommander.ui.base.BasePresenter;
import io.mithrilcoin.eoscommander.ui.base.RxCallbackWrapper;
import io.mithrilcoin.eoscommander.util.StringUtils;
import io.reactivex.Single;

/**
 * Created by swapnibble on 2017-11-17.
 */

public class GetTablePresenter extends BasePresenter<GetTableMvpView> {
    @Inject
    EoscDataManager mDataManager;

    @Inject
    public GetTablePresenter(){
    }

    private List<String> getTableNames( List<EosAbiTable> abiTables ) {
        if ( null == abiTables ){
            return new ArrayList<>();
        }

        ArrayList<String> names = new ArrayList<>( abiTables.size() );
        for ( EosAbiTable table : abiTables ){
            names.add( table.table_name );
        }

        Collections.sort( names);

        return names;
    }

    public void onGetTableListClicked( String contract ){
        if ( StringUtils.isEmpty(contract) ){
            return;
        }

        getMvpView().showLoading( true );

        addDisposable( mDataManager.getCodeAbi( contract )
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

    public void getTable(String accountName, String contract, String table ) {
        addDisposable(
            mDataManager.getTable( accountName, contract, table)
                    .doOnNext( result -> mDataManager.addAccountHistory( accountName, contract) )
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribeWith( new RxCallbackWrapper<String>( this ) {
                        @Override
                        public void onNext(String result) {

                            if ( ! isViewAttached() ) return;

                            getMvpView().showLoading( false );

                            if ( !StringUtils.isEmpty( result)) {
                                getMvpView().showTableResult( result );
                            }
                        }
                    })
        );

    }
}
