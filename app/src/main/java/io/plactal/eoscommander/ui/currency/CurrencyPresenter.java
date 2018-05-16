package io.plactal.eoscommander.ui.currency;

import com.google.gson.JsonObject;

import javax.inject.Inject;

import io.plactal.eoscommander.data.EoscDataManager;
import io.plactal.eoscommander.ui.base.BasePresenter;
import io.plactal.eoscommander.ui.base.RxCallbackWrapper;
import io.plactal.eoscommander.util.StringUtils;
import io.plactal.eoscommander.util.Utils;

/**
 * Created by swapnibble on 2018-04-16.
 */
public class CurrencyPresenter extends BasePresenter<CurrencyMvpView> {
    @Inject
    EoscDataManager mDataManager;

    @Inject
    public CurrencyPresenter(){
    }

    public void onGetBalance(String contract, String account, String symbol){
        getMvpView().showLoading( true );

        addDisposable(
            mDataManager.getCurrencyBalance( contract, account, symbol )
                .doOnNext( balanceResult -> mDataManager.addAccountHistory( contract, account ))
                .subscribeOn( getSchedulerProvider().io())
                .observeOn( getSchedulerProvider().ui())
                .subscribeWith( new RxCallbackWrapper<String>( this){
                    @Override
                    public void onNext(String result) {

                        if ( ! isViewAttached() ) return;

                        getMvpView().showLoading( false );

                        getMvpView().showResult( result, null);
                    }
                })
        );
    }

    public void onGetStats(String contract, String symbol){
        addDisposable(
            mDataManager.getCurrencyStats( contract, symbol )
                .doOnNext( balanceResult -> mDataManager.addAccountHistory( contract ))
                .subscribeOn( getSchedulerProvider().io())
                .observeOn( getSchedulerProvider().ui())
                .subscribeWith( new RxCallbackWrapper<String>( this){
                    @Override
                    public void onNext(String result) {

                        if ( ! isViewAttached() ) return;

                        getMvpView().showLoading( false );

                        getMvpView().showResult( result, null);
                    }
                })
        );
    }
}
