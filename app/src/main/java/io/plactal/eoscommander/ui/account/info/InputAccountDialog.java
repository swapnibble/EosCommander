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
package io.plactal.eoscommander.ui.account.info;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import javax.inject.Inject;

import io.plactal.eoscommander.R;
import io.plactal.eoscommander.data.EoscDataManager;
import io.plactal.eoscommander.di.component.ActivityComponent;
import io.plactal.eoscommander.ui.base.BaseDialog;
import io.plactal.eoscommander.util.RefValue;
import io.plactal.eoscommander.util.UiUtils;
import io.plactal.eoscommander.util.Utils;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by swapnibble on 2017-11-16.
 */

public class InputAccountDialog extends BaseDialog {
    private static final String TAG = InputAccountDialog.class.getSimpleName();
    private static final String ARG_INFO_TYPE = "type.for";

    private AccountInfoType mAccountInfoType = AccountInfoType.REGISTRATION;
    private Callback mCallback;
    private AutoCompleteTextView mEtAccount;

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

        View view = inflater.inflate(R.layout.dialog_input_account, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
        }

        return view;
    }

    @Override
    protected void setUpView(View view) {
        if ( AccountInfoType.ACTIONS.equals( mAccountInfoType ) ) {
            ((AutoCompleteTextView)view.findViewById(R.id.et_pos)).setText("-1");
            ((AutoCompleteTextView)view.findViewById(R.id.et_offset)).setText("-20");
        }
        else {
            view.findViewById(R.id.et_pos).setVisibility( View.GONE );
            view.findViewById(R.id.et_offset).setVisibility( View.GONE );
        }

        // title
        ((TextView)view.findViewById( R.id.tv_title)).setText( mAccountInfoType.getTitleId());

        // edit view
        mEtAccount = view.findViewById( R.id.et_account );

        UiUtils.setupAccountHistory(mEtAccount);

        // ok
        view.findViewById( R.id.btn_ok ).setOnClickListener( v -> {

            if ( null != mCallback) {
                if ( AccountInfoType.ACTIONS.equals( mAccountInfoType ) ) {
                    mCallback.onAccountEntered(mEtAccount.getText().toString()
                            , Utils.parseIntSafely( ((AutoCompleteTextView)view.findViewById(R.id.et_pos)).getText().toString(), -1 )
                            , Utils.parseIntSafely( ((AutoCompleteTextView)view.findViewById(R.id.et_offset)).getText().toString(), -20 )
                            , mAccountInfoType);
                }
                else {
                    mCallback.onAccountEntered(mEtAccount.getText().toString(), -1, -1, mAccountInfoType);
                }
            }

            dismiss();
        });

        // cancel
        view.findViewById( R.id.btn_cancel ).setOnClickListener( v -> dismiss());
    }



    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface Callback {
        void onAccountEntered( String account, int position, int offset, AccountInfoType infoType );
    }
}
