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
package io.mithrilcoin.eoscommander.ui.wallet.dlg;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.data.EoscDataManager;
import io.mithrilcoin.eoscommander.di.component.ActivityComponent;
import io.mithrilcoin.eoscommander.ui.base.BaseDialog;
import io.mithrilcoin.eoscommander.ui.base.MvpView;
import io.mithrilcoin.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2017-11-08.
 */

public class CreateWalletDialog extends BaseDialog implements MvpView {

    private static final String TAG = CreateWalletDialog.class.getSimpleName();


    @Inject
    EoscDataManager mDataManager;


    private Callback mCallback;
    private EditText mEtWalletName;

    public static CreateWalletDialog newInstance() {
        CreateWalletDialog fragment = new CreateWalletDialog();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_wallet_create, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
        }
        return view;
    }

    public CreateWalletDialog setCallback( Callback callback ){
        mCallback = callback;
        return this;
    }

    @Override
    protected void setUpView(View view) {
        mEtWalletName = view.findViewById( R.id.et_wallet_name);

        view.findViewById( R.id.btn_create).setOnClickListener( v -> {
            String name = mEtWalletName.getText().toString();

            if ( (null != mCallback ) && !StringUtils.isEmpty(name) ) {
                if ( mDataManager.getWalletManager().walletExists( name )) {
                    onError( String.format( getString(R.string.wallet_already_exists), name));
                    return;
                }

                mCallback.onNewWalletNameEntered( name );
            }

            dismiss();
        });

        view.findViewById( R.id.btn_cancel).setOnClickListener( v -> dismiss());
    }


    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    public interface Callback {
        void onNewWalletNameEntered(String name );
    }
}
