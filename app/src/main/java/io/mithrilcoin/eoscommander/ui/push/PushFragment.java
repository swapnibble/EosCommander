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
package io.mithrilcoin.eoscommander.ui.push;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.di.component.ActivityComponent;
import io.mithrilcoin.eoscommander.ui.base.BaseFragment;
import io.mithrilcoin.eoscommander.ui.file.FileChooserActivity;

public class PushFragment extends BaseFragment implements PushMvpView{
    private static final int REQ_SELECT_MSG_FILE = 10;

    @Inject
    PushPresenter mPresenter;

    private EditText mEtContract;
    private EditText mEtAction;
    private EditText mEtScopes;
    private EditText mEtPermissionAccount;
    private EditText mEtPermissionName;
    private EditText mEtMsg;


    public static PushFragment newInstance() {
        PushFragment fragment = new PushFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_push, container, false);
        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            mPresenter.attachView( this );
        }

        return view;
    }


    @Override
    protected void setUpView(View view) {
        mEtContract     = view.findViewById( R.id.et_contract_account );
        mEtAction       = view.findViewById( R.id.et_contract_action );
        mEtMsg          = view.findViewById(R.id.et_message);
        mEtScopes = view.findViewById( R.id.et_scopes );
        mEtPermissionAccount= view.findViewById( R.id.et_permission_account );
        mEtPermissionName   = view.findViewById( R.id.et_permission_name );


        view.findViewById(R.id.btn_import).setOnClickListener( v -> mPresenter.onImportFileClicked());

        view.findViewById(R.id.btn_ok).setOnClickListener( v ->
                mPresenter.pushMessage(mEtContract.getText().toString(), mEtAction.getText().toString()      // contract, action
                        , mEtMsg.getText().toString() // message,
                        , mEtScopes.getText().toString()   // scope
                        , mEtPermissionAccount.getText().toString()     // account for permission
                        , mEtPermissionName.getText().toString()     // permission name
                        ));
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQ_SELECT_MSG_FILE == requestCode) {
            mPresenter.onImportMessageFile( FileChooserActivity.getResultPath( resultCode, data) );
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void openFileManager() {
        startActivityForResult( new Intent(getContext(), FileChooserActivity.class), REQ_SELECT_MSG_FILE );
    }

    @Override
    public void showContractMessage( String msg) {
        mEtMsg.setText( msg );
    }
}