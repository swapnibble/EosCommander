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
package io.mithrilcoin.eoscommander.ui.gettable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.di.component.ActivityComponent;
import io.mithrilcoin.eoscommander.ui.base.BaseFragment;
import io.mithrilcoin.eoscommander.ui.result.ShowResultDialog;
import io.mithrilcoin.eoscommander.util.UiUtils;

public class GetTableFragment extends BaseFragment
        implements GetTableMvpView {

    @Inject
    GetTablePresenter mPresenter;

    private AutoCompleteTextView mTvAccountName;
    private AutoCompleteTextView mTvCode;
    private TextView mTvTable;


    public static GetTableFragment newInstance() {
        GetTableFragment fragment = new GetTableFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_table, container, false);
        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            mPresenter.attachView( this );
        }

        return view;
    }


    @Override
    protected void setUpView(View view) {
        mTvAccountName = view.findViewById( R.id.et_account);
        mTvCode = view.findViewById( R.id.et_code);
        mTvTable= view.findViewById( R.id.et_table);

        mTvTable.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                onGetTable();
                return true;
            }

            return false;
        });

        view.findViewById( R.id.btn_get).setOnClickListener( v -> onGetTable() );
    }

    private void onGetTable() {
        mPresenter.getTable ( mTvAccountName.getText().toString(), mTvCode.getText().toString(), mTvTable.getText().toString());
    }

    @Override
    public void onStart() {
        super.onStart();

        // notify to presenter
        mPresenter.onMvpViewShown();
    }

    @Override
    public void onSelected() {
        if ( null != mPresenter ) {
            mPresenter.onMvpViewShown();
        }
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }


    @Override
    public void showTableResult(String result) {
        String title = String.format("%s: %s", getString(R.string.get_table), mTvTable.getText() );

        ShowResultDialog.newInstance( title, result).show( getChildFragmentManager());
    }

    @Override
    public void setupAccountHistory(List<String> recentAccounts){
        UiUtils.setupRecentAccountSuggest( mTvAccountName, recentAccounts );
        UiUtils.setupRecentAccountSuggest( mTvCode, recentAccounts );
    }
}