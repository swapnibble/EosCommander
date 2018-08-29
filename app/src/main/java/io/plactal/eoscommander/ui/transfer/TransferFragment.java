/*
 * Copyright (c) 2017-2018 PlayerOne.
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
package io.plactal.eoscommander.ui.transfer;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;


import javax.inject.Inject;

import io.plactal.eoscommander.R;
import io.plactal.eoscommander.di.component.ActivityComponent;
import io.plactal.eoscommander.ui.base.BaseFragment;
import io.plactal.eoscommander.ui.widget.TextInputAutoCompleteTextView;
import io.plactal.eoscommander.util.UiUtils;

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

        mRootView = view;

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
        UiUtils.setupAccountHistory( etFrom, etTo );
    }

    private void onSend() {
        mPresenter.transfer( getTextFromEt(R.id.et_from), getTextFromEt(R.id.et_to)
                , getTextFromEt(R.id.et_amount), getTextFromEt(R.id.et_memo));
    }

    private String getTextFromEt( int textEditId ) {
        EditText et = mRootView.findViewById( textEditId );
        if ( et == null ){
            return "";
        }

        return et.getText().toString();
    }

    private TextInputAutoCompleteTextView getTiAutoTv( int viewId ) {
        View itemView = mRootView.findViewById( viewId );
        if ( itemView == null ) {
            return null;
        }

        return itemView.findViewById( R.id.et_input );
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }
}