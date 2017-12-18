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
package io.mithrilcoin.eoscommander.ui.account.info;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.data.EoscDataManager;
import io.mithrilcoin.eoscommander.di.component.ActivityComponent;
import io.mithrilcoin.eoscommander.ui.suggestion.AccountSuggestAdapter;
import io.mithrilcoin.eoscommander.ui.base.BaseDialog;
import io.mithrilcoin.eoscommander.util.RefValue;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by swapnibble on 2017-11-16.
 */

public class InputAccountDialog extends BaseDialog {
    private static final String TAG = InputAccountDialog.class.getSimpleName();
    private static final String ARG_INFO_TYPE = "type.for";

    @Inject
    EoscDataManager mDataManager;

    private AccountInfoType mAccountInfoType = AccountInfoType.REGISTRATION;
    private Callback mCallback;
    private AutoCompleteTextView mEtAccount;
    private CompositeDisposable mCompositeDisposable;

    protected RefValue<Long> mAccountHistoryVersion = new RefValue<>(0L);

    public static InputAccountDialog newInstance(AccountInfoType infoType ) {
        InputAccountDialog fragment = new InputAccountDialog();
        Bundle bundle = new Bundle();
        bundle.putString( ARG_INFO_TYPE, infoType.name() );
        fragment.setArguments(bundle);
        return fragment;
    }

    public InputAccountDialog setCallback( Callback callback ) {
        mCallback = callback;
        return this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        mAccountInfoType = AccountInfoType.safeValueOf( args.getString( ARG_INFO_TYPE));

        mCompositeDisposable = new CompositeDisposable();

        View view = inflater.inflate(R.layout.dialog_input_account, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
        }

        return view;
    }

    @Override
    protected void setUpView(View view) {
        // title
        ((TextView)view.findViewById( R.id.tv_title)).setText( mAccountInfoType.getTitleId());

        // edit view
        mEtAccount = view.findViewById( R.id.et_account );

        // ok
        view.findViewById( R.id.btn_ok ).setOnClickListener( v -> {
            if ( null != mCallback) mCallback.onAccountEntered( mEtAccount.getText().toString(), mAccountInfoType);

            dismiss();
        });

        // cancel
        view.findViewById( R.id.btn_cancel ).setOnClickListener( v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();

        setupRecentAccountSuggest( mEtAccount);
    }

    private void setupRecentAccountSuggest( AutoCompleteTextView autoTextView) {
        if (! mDataManager.shouldUpdateAccountHistory( mAccountHistoryVersion.data)){
            return;
        }

        AccountSuggestAdapter adapter = new AccountSuggestAdapter(autoTextView.getContext(), R.layout.account_suggestion, R.id.eos_account);

        mCompositeDisposable.add(
                Observable.fromCallable( () -> mDataManager.getAllAccountHistory( true, mAccountHistoryVersion ) )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe( list -> adapter.addAll( list ), e -> onError(e.getMessage()))
        );


        autoTextView.setThreshold(1);
        autoTextView.setAdapter( adapter );
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG );
    }

    @Override
    public void onDestroyView() {
        mCompositeDisposable.clear();
        super.onDestroyView();
    }

    public interface Callback {
        void onAccountEntered( String account, AccountInfoType infoType );
    }
}
