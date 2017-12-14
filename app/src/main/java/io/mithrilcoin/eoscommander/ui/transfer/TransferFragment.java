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
package io.mithrilcoin.eoscommander.ui.transfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.List;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.di.component.ActivityComponent;
import io.mithrilcoin.eoscommander.ui.base.BaseFragment;
import io.mithrilcoin.eoscommander.util.UiUtils;

public class TransferFragment extends BaseFragment implements TransferMvpView{

    @Inject
    TransferPresenter mPresenter;

    private AutoCompleteTextView mEtFrom;
    private AutoCompleteTextView mEtTo;
    private EditText mEtAmount;


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
        mEtFrom = view.findViewById( R.id.et_from);
        mEtTo   = view.findViewById( R.id.et_to);
        mEtAmount = view.findViewById( R.id.et_amount );

        // click handler
        view.findViewById( R.id.btn_transfer).setOnClickListener( v ->
                mPresenter.transfer( mEtFrom.getText().toString(), mEtTo.getText().toString(), mEtAmount.getText().toString()) );
    }

    @Override
    public void onStart() {
        super.onStart();

        // notify to presenter
        mPresenter.onStart();
    }

    @Override
    public void setupAccountHistory(List<String> recentAccounts){
        UiUtils.setupRecentAccountSuggest( mEtFrom, recentAccounts );
        UiUtils.setupRecentAccountSuggest( mEtTo, recentAccounts );
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }
}