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
package io.plactal.eoscommander.ui.base;

import java.io.IOException;
import java.net.SocketTimeoutException;

import io.plactal.eoscommander.R;
import io.plactal.eoscommander.util.RefValue;
import io.plactal.eoscommander.util.rx.EoscSchedulerProvider;
import io.plactal.eoscommander.util.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Created by swapnibble on 2017-08-24.
 */

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    private V mMvpView;

    private SchedulerProvider mSchedulerProvider;
    private CompositeDisposable mCompositeDisposable;

    protected RefValue<Long> mAccountHistoryVersion = new RefValue<>(0L);

    @Override
    public void attachView(V mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void detachView() {
        if ( null != mMvpView ) {
            mMvpView.showLoading(false);
        }

        mMvpView = null;

        if ( null != mCompositeDisposable) {
            mCompositeDisposable.clear();
        }
    }



    protected SchedulerProvider getSchedulerProvider() {
        if ( null == mSchedulerProvider ) {
            mSchedulerProvider = new EoscSchedulerProvider();
        }

        return mSchedulerProvider;
    }

    protected void addDisposable(Disposable d) {
        if ( null == mCompositeDisposable ) {
            mCompositeDisposable = new CompositeDisposable();
        }

        if ( ! mCompositeDisposable.isDisposed() ) {
            mCompositeDisposable.add(d);
        }
    }

    @Override
    public V getMvpView() {
        return mMvpView;
    }

    @Override
    public boolean isViewAttached() {
        return mMvpView != null;
    }

    public void checkViewAttached() throws MvpViewNotAttachedException{
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    protected void safeTurnOffLoading(){
        if ( ! isViewAttached() ) return;

        getMvpView().showLoading( false );
    }

    protected void notifyErrorToMvpView( Throwable e){
        getMvpView().showLoading(false);

        if (e instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) e).response().errorBody();

            getMvpView().onError( String.format( "HttpCode:%d\n\n%s", ((HttpException) e).code(), getErrorMessage(responseBody)));
        } else if (e instanceof SocketTimeoutException) {
            getMvpView().onError(R.string.timeout);
        } else if (e instanceof IOException) {
            getMvpView().onError(R.string.network_err);
        } else {
            getMvpView().onError(e.getMessage());
        }
    }

    private String getErrorMessage(ResponseBody responseBody) {
        try {
            return responseBody.string();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Call Presenter.attachView(MvpView) before requesting data to the Presenter");
        }
    }
}
