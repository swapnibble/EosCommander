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
package io.mithrilcoin.eoscommander.ui.wallet.dlg;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.ui.base.BaseDialog;
import io.mithrilcoin.eoscommander.util.StringUtils;
import io.mithrilcoin.eoscommander.util.UiUtils;

/**
 * Created by swapnibble on 2017-11-15.
 * input wallet password ({@link Type#Password}) or key ( {@link Type#Key} )
 */

public class InputDataDialog extends BaseDialog {

    private static final String TAG = InputDataDialog.class.getSimpleName();
    private static final String ARG_WALLET_NAME         = "wallet.name";
    private static final String ARG_INITIAL_INPUT_DATA  = "input.init.data";
    private static final String ARG_INPUT_TYPE          = "input.type";

    /**
     * describes data type to input
     */
    public enum Type {
        Password(R.string.unlock_wallet, R.string.unlock, R.string.input_wallet_password)
        , Key(R.string.import_key, R.string.import_label, R.string.input_private_key) ;

        final int titleStrId;   // provides title string resource
        final int okButtonStrId;// provides ok button string resource
        final int inputHintStrId;//provides input hint string resource

        Type( int titleStrId, int okButtonStrId, int inputHintStrId ){
            this.titleStrId     = titleStrId;
            this.okButtonStrId  = okButtonStrId;
            this.inputHintStrId = inputHintStrId;
        }

        static Type safeValueOf(String name ) {
            try {
                if (StringUtils.isEmpty(name)) {
                    return Password;
                }

                return valueOf( name);
            }
            catch (IllegalArgumentException e) {
                return Password;
            }
        }
    }

    private String mWalletName;
    private EditText mEtData;
    private Callback mCallback;
    private Type mInputType ;


    public static InputDataDialog newInstance( String walletName, String initData, InputDataDialog.Type inputType ) {
        InputDataDialog fragment = new InputDataDialog();
        Bundle bundle = new Bundle();
        bundle.putString( ARG_WALLET_NAME, walletName);
        bundle.putString( ARG_INITIAL_INPUT_DATA, initData);
        bundle.putString( ARG_INPUT_TYPE, inputType.name());
        fragment.setArguments(bundle);
        return fragment;
    }

    public InputDataDialog setCallback( Callback callback ) {
        mCallback = callback;
        return this;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_wallet_input_data, container, false);
    }


    @Override
    protected void setUpView(View view) {
        TextView tv;
        String initialData;
        Bundle args = getArguments();
        mWalletName = args.getString( ARG_WALLET_NAME );
        mInputType  = Type.safeValueOf( args.getString( ARG_INPUT_TYPE));
        initialData = args.getString( ARG_INITIAL_INPUT_DATA );

        // title
        tv = view.findViewById( R.id.tv_title);
        tv.setText( getString(mInputType.titleStrId) + ": " + mWalletName);

        // input data
        mEtData = view.findViewById(R.id.et_input_data);

        // input data hint
        mEtData.setHint( mInputType.inputHintStrId );

        // initial data
        if ( ! StringUtils.isEmpty( initialData )) {
            UiUtils.setTextAndMoveCursorToEnd( mEtData, initialData);
        }

        // ok button
        tv = view.findViewById( R.id.btn_ok);
        tv.setText( mInputType.okButtonStrId);
        tv.setOnClickListener( v -> {
            if ( null != mCallback) mCallback.onDataEntered( mWalletName, mEtData.getText().toString());

            dismiss();
        });

        // cancel button
        view.findViewById( R.id.btn_cancel).setOnClickListener( v -> dismiss());
    }

    public interface Callback {
        void onDataEntered( String walletName, String data);
    }
}
