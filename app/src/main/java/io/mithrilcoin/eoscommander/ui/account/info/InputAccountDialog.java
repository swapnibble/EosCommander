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
import android.widget.EditText;
import android.widget.TextView;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.ui.base.BaseDialog;

/**
 * Created by swapnibble on 2017-11-16.
 */

public class InputAccountDialog extends BaseDialog {
    private static final String TAG = InputAccountDialog.class.getSimpleName();
    private static final String ARG_INFO_TYPE = "type.for";

    private AccountInfoType mAccountInfoType = AccountInfoType.REGISTRATION;
    private Callback mCallback;
    private EditText mEtAccount;

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

        return inflater.inflate(R.layout.dialog_input_account, container, false);
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

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG );
    }

    public interface Callback {
        void onAccountEntered( String account, AccountInfoType infoType );
    }
}
