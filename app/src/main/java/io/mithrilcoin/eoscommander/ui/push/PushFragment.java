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
package io.mithrilcoin.eoscommander.ui.push;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.jraska.console.Console;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.data.remote.model.abi.EosAbiMain;
import io.mithrilcoin.eoscommander.di.component.ActivityComponent;
import io.mithrilcoin.eoscommander.ui.base.BaseFragment;
import io.mithrilcoin.eoscommander.ui.file.FileChooserActivity;
import io.mithrilcoin.eoscommander.ui.push.abiview.MsgInputActivity;
import io.mithrilcoin.eoscommander.ui.push.abiview.AbiViewBuilder;
import io.mithrilcoin.eoscommander.util.StringUtils;
import io.mithrilcoin.eoscommander.util.UiUtils;

import static android.app.Activity.RESULT_OK;

public class PushFragment extends BaseFragment
        implements PushMvpView, EditText.OnEditorActionListener {
    private static final int REQ_SELECT_MSG_FILE = 10;
    private static final int REQ_ABI_VIEW = 20;

    @Inject
    PushPresenter mPresenter;

    private AutoCompleteTextView mEtContract;
    private AppCompatSpinner mActionSpinner;
    private ArrayAdapter<String> mActionNameAdapter;

    private AbiViewBuilder mAbiViewBuilder;
    private MultiAutoCompleteTextView mEtScopes;
    private AutoCompleteTextView mEtPermissionAccount;
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

        mAbiViewBuilder = new AbiViewBuilder();

        return view;
    }


    @Override
    protected void setUpView(View view) {
        mEtContract     = view.findViewById( R.id.et_contract_account );
        mEtContract.setOnEditorActionListener( this);
        mActionSpinner  = view.findViewById( R.id.sp_action_name );
        mEtMsg          = view.findViewById(R.id.et_message);
        mEtScopes = view.findViewById( R.id.et_scopes );
        mEtPermissionAccount= view.findViewById( R.id.et_permission_account );
        mEtPermissionAccount.setOnEditorActionListener( this);

        mEtPermissionName   = view.findViewById( R.id.et_permission_name );
        mEtPermissionName.setOnEditorActionListener( this);

        view.findViewById(R.id.btn_get_abi).setOnClickListener( v -> onContractEntered( mEtContract.getText().toString()) );

        view.findViewById(R.id.btn_ui_input).setOnClickListener( v -> mPresenter.onRequestInputUi((String)mActionSpinner.getSelectedItem()) );
        view.findViewById(R.id.btn_import).setOnClickListener( v -> mPresenter.onImportFileClicked() );

        view.findViewById(R.id.btn_ok).setOnClickListener( v -> onPushAction() );

        // NEW_ACCOUNT_SUGGEST
        setupAccountHistory();

        Console.writeLine("Hello Console!");
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_SEND == actionId) {
            onPushAction();
            return true;
        }
        else
        if ( (EditorInfo.IME_ACTION_SEARCH == actionId ) && ( textView.getId() == R.id.et_contract_account) ){
            onContractEntered( mEtContract.getText().toString());
        }

        return false;
    }

    private void onPushAction() {
        if ( mActionSpinner.getSelectedItem() == null ){
            showToast(R.string.select_action_after_abi);
            return;
        }

        mPresenter.pushAction(mEtContract.getText().toString(),mActionSpinner.getSelectedItem().toString()// mEtAction.getText().toString()      // contract, action
                , mEtMsg.getText().toString() // message,
                , mEtScopes.getText().toString()   // scope
                , mEtPermissionAccount.getText().toString()     // account for permission
                , mEtPermissionName.getText().toString()     // permission name
        );
    }

    private void onContractEntered(String name){
        if ( StringUtils.isEmpty(name)) {
            return;
        }

        hideKeyboard();

        mPresenter.onGetAbiClicked( name );
    }

    private void setupAccountHistory(){

        // get abi when contract name item selected
        mEtContract.setOnItemClickListener( (adapterView, view, position, id) -> {
            ListAdapter adapter = mEtContract.getAdapter();
            if ( (null != adapter ) && !StringUtils.isEmpty( (String) adapter.getItem(position)) ){
                onContractEntered( (String) adapter.getItem(position) );
            }
        });

        UiUtils.setupAccountHistory( mEtContract, mEtScopes, mEtPermissionAccount );
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQ_SELECT_MSG_FILE == requestCode) {
            mPresenter.onImportMessageFile( FileChooserActivity.getResultPath( resultCode, data) );
        }
        else
        if ( REQ_ABI_VIEW == requestCode ) {
            if ( RESULT_OK == resultCode) {
                String json = MsgInputActivity.getResultString(data);
                if ( !StringUtils.isEmpty( json)) {
                    mEtMsg.setText( json);
                }
            }
            else {
                showToast( R.string.err_create_msg_input);
            }
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

    @Override
    public void buildContractView(EosAbiMain abi) {
        if ( mActionSpinner == null ){
            return;
        }

        this.showToast(R.string.action_loaded);

        mAbiViewBuilder.setAbiMain( abi);

        mActionNameAdapter = new ArrayAdapter<>( getContext(), android.R.layout.simple_spinner_item, mAbiViewBuilder.getActionNames());
        mActionNameAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        mActionSpinner.setAdapter( mActionNameAdapter);
    }

    @Override
    public void openDynInputFromAbi(String key, String actionName ) {

        Intent inputActivity = MsgInputActivity.getLaunchIntent( getContext(), key, actionName );
        this.startActivityForResult( inputActivity, REQ_ABI_VIEW );
    }
}