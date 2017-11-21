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
package io.mithrilcoin.eoscommander.ui.wallet.dlg;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.ui.base.BaseDialog;

/**
 * Created by swapnibble on 2017-11-13.
 */

public class ShowWalletPwDialog extends BaseDialog {
    private static final String TAG = ShowWalletPwDialog.class.getSimpleName();

    private static final String ARG_WALLET_NAME     = "wallet.name";
    private static final String ARG_WALLET_PASSWORD = "wallet.pass";
    private static final String ARG_SAVE_PASSWORD_FOR_TESTING = "save.pass";
    private static final String ARG_ENABLE_CHECKER_FOR_SAVE_PASSWORD = "enable.checker.savepw";


    private String   mWalletName;
    private CheckBox mSavePassword;
    private Callback mCallback;


    public static ShowWalletPwDialog newInstance(String walletName, String password
            , boolean savePassword, boolean enableChecker4SavePassword ) {
        ShowWalletPwDialog fragment = new ShowWalletPwDialog();
        Bundle bundle = new Bundle();
        bundle.putString( ARG_WALLET_NAME, walletName);
        bundle.putString( ARG_WALLET_PASSWORD, password);
        bundle.putBoolean(ARG_SAVE_PASSWORD_FOR_TESTING, savePassword );
        bundle.putBoolean(ARG_ENABLE_CHECKER_FOR_SAVE_PASSWORD, enableChecker4SavePassword );
        fragment.setArguments(bundle);
        return fragment;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_wallet_create_password, container, false);
    }

    public ShowWalletPwDialog setCallback( Callback callback ) {
        mCallback = callback;

        return this;
    }

    @Override
    protected void setUpView(View view) {
        Bundle args = getArguments();
        mWalletName = args.getString( ARG_WALLET_NAME );

        ((TextView)view.findViewById( R.id.tv_password)).setText( args.getString( ARG_WALLET_PASSWORD ));
        mSavePassword = view.findViewById( R.id.cb_save_password);
        mSavePassword.setChecked( args.getBoolean( ARG_SAVE_PASSWORD_FOR_TESTING ));

        if ( ! args.getBoolean( ARG_ENABLE_CHECKER_FOR_SAVE_PASSWORD)) {
            mSavePassword.setEnabled( false );
            mSavePassword.setTextColor(getResources().getColor(R.color.colorGray) );
        }

        view.findViewById( R.id.btn_close).setOnClickListener( v -> dismiss());
    }

    @Override
    public void dismiss() {
        super.dismiss();

        if ( null != mCallback ) {
            // if "save password" is not checked, set password as empty string.
            mCallback.onFinishedShowingPassword( mWalletName, mSavePassword.isChecked() ?
                    ( (TextView)(getView().findViewById(R.id.tv_password))).getText().toString() : "" );
        }
    }

    public interface Callback {
        void onFinishedShowingPassword(String walletName, String password );
    }
}
