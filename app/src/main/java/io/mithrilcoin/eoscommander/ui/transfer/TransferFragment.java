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
package io.mithrilcoin.eoscommander.ui.transfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;


import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.di.component.ActivityComponent;
import io.mithrilcoin.eoscommander.ui.base.BaseFragment;
import io.mithrilcoin.eoscommander.ui.widget.TextInputAutoCompleteTextView;
import io.mithrilcoin.eoscommander.util.UiUtils;

public class TransferFragment extends BaseFragment implements TransferMvpView{

    @Inject
    TransferPresenter mPresenter;

    private View mRootView;


    public static TransferFragment newInstance() {
        TransferFragment fragment = new TransferFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);
        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            mPresenter.attachView( this );
        }

        return view;
    }

    @Override
    protected void setUpView(View view) {

        //  from, to, amount edit text
        AutoCompleteTextView etFrom = view.findViewById(R.id.et_from);
        AutoCompleteTextView etTo = view.findViewById(R.id.et_to);
        EditText etAmount = view.findViewById(R.id.et_amount);

        // click handler
        view.findViewById(R.id.btn_transfer).setOnClickListener(v -> onSend() );

        etAmount.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (EditorInfo.IME_ACTION_SEND == actionId) {
                onSend();
                return true;
            }

            return false;
        });


        // account history
        UiUtils.setupAccountHistory( view.findViewById(R.id.input_token_contract).findViewById(R.id.et_input), etFrom, etTo );
    }

    private void onSend() {
        mPresenter.transfer(getTextInputValue(R.id.input_token_contract),
                getTextFromAutoComplete(R.id.et_from), getTextFromAutoComplete(R.id.et_to)
                , getTextFromAutoComplete(R.id.et_amount), getTextInputValue(R.id.input_memo));
    }

    private String getTextFromAutoComplete( int viewId ) {
        AutoCompleteTextView itemView = mRootView.findViewById( viewId );
        if ( itemView == null ) {
            return "";
        }

        return itemView.getText().toString();
    }

    private String getTextInputValue(int viewId) {
        View itemView = mRootView.findViewById( viewId );
        if ( itemView == null ) {
            return "";
        }

        TextInputAutoCompleteTextView tiauto = itemView.findViewById( R.id.et_input );
        if ( tiauto != null ) {
            return tiauto.getText().toString();
        }

        return "";
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }
}