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
package io.mithrilcoin.eoscommander.ui.base;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import io.mithrilcoin.eoscommander.R;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Created by swapnibble on 2017-11-10.
 */

public class RxCallbackWrapper<T> extends DisposableObserver<T> {

    private WeakReference<MvpPresenter<? extends MvpView>> mPresenterRef;

    public RxCallbackWrapper( MvpPresenter<? extends MvpView> presenter ) {
        mPresenterRef = new WeakReference<>(presenter);
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {

        MvpPresenter<? extends MvpView> presenter = mPresenterRef.get();
        if ( (null == presenter ) || ! presenter.isViewAttached()) {
            return;
        }

        presenter.getMvpView().showLoading( false );

        if (e instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) e).response().errorBody();

            presenter.getMvpView().onError( String.format( "HttpCode:%d\n\n%s", ((HttpException) e).code(), getErrorMessage(responseBody)));
        } else if (e instanceof SocketTimeoutException) {
            presenter.getMvpView().onError(R.string.timeout);
        } else if (e instanceof IOException) {
            presenter.getMvpView().onError(R.string.network_err);
        } else {
            e.printStackTrace();
            presenter.getMvpView().onError(e.getMessage());
        }
    }

    @Override
    public void onComplete() {
        MvpPresenter<? extends MvpView> presenter = mPresenterRef.get();
        if ( (null == presenter ) || ! presenter.isViewAttached()) {
            return;
        }

        presenter.getMvpView().showLoading( false );
    }

    private String getErrorMessage(ResponseBody responseBody) {
        try {
            return responseBody.string();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
