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
package io.plactal.eoscommander.ui.gettable;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListAdapter;

import java.util.List;

import javax.inject.Inject;

import io.plactal.eoscommander.R;
import io.plactal.eoscommander.di.component.ActivityComponent;
import io.plactal.eoscommander.ui.base.BaseFragment;
import io.plactal.eoscommander.ui.result.ShowResultDialog;
import io.plactal.eoscommander.util.StringUtils;
import io.plactal.eoscommander.util.UiUtils;

public class GetTableFragment extends BaseFragment implements GetTableMvpView {

    @Inject
    GetTablePresenter mPresenter;

    private AutoCompleteTextView mScope;
    private AutoCompleteTextView mTvCode;

    private AppCompatSpinner        mTableNameSpinner;
    private ArrayAdapter<String>    mTableNameAdapter;

    private AppCompatSpinner    mIndexPosSpinner;
    private AppCompatSpinner    mIndexTypeSpinner;
    private AppCompatSpinner    mIndexEncodingSpinner;

    private View mRootView;


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
        mRootView = view;

        mScope = view.findViewById( R.id.et_scope);
        mTvCode = view.findViewById( R.id.et_code);

        view.findViewById( R.id.btn_get).setOnClickListener( v -> onGetTable() );
        view.findViewById( R.id.btn_query_table_list).setOnClickListener( v -> mPresenter.onGetTableListClicked( mTvCode.getText().toString()) );

        mTableNameSpinner = view.findViewById( R.id.sp_table_list);

        setupAccountHistory();

        mPresenter.onFinishedSetupView();
    }

    @Override
    public void populateKeyInfo( List<String> posNames, List<String> types, List<String> typeEncodings) {

        // index position
        mIndexPosSpinner = setDropDownList( R.id.sp_key_pos_list, posNames, null );


        AdapterView.OnItemSelectedListener typeOrEncodingSelectionListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.onIndexTypeOrEncodingSelected( mIndexTypeSpinner.getSelectedItemPosition(), mIndexEncodingSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        // index type
        mIndexTypeSpinner = setDropDownList(R.id.sp_key_type_list, types, typeOrEncodingSelectionListener );

        // index encoding
        mIndexEncodingSpinner = setDropDownList( R.id.sp_key_type_encode_list, typeEncodings, typeOrEncodingSelectionListener );


        // initialize spinner selection.
        if ( mIndexPosSpinner != null ) mIndexPosSpinner.setSelection(0);

        if ( mIndexTypeSpinner != null ) mIndexTypeSpinner.setSelection(0);


        if ( mIndexEncodingSpinner != null ) mIndexEncodingSpinner.setSelection(0);
    }

    private AppCompatSpinner setDropDownList(int dropDownListId, List<String> data, AdapterView.OnItemSelectedListener itemSelectedListener) {
        AppCompatSpinner spinner = mRootView.findViewById( dropDownListId );
        if ( spinner == null ) {
            return null;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner.setAdapter( adapter );

        if ( itemSelectedListener != null ) {
            spinner.setOnItemSelectedListener(itemSelectedListener);
        }

        return spinner;
    }

    public void setTypeEncodingSelection( int position ) {
        if ( mIndexEncodingSpinner != null ) mIndexEncodingSpinner.setSelection( position );
    }

    private void onGetTable() {
        if ( StringUtils.isEmpty(mTvCode.getText().toString()) || (mTableNameSpinner.getSelectedItem() == null ) ){
            showToast(R.string.select_action_after_tables);
            return;
        }

        mPresenter.getTable ( mScope.getText().toString()
                , mTvCode.getText().toString(), mTableNameSpinner.getSelectedItem().toString()
                , mIndexPosSpinner.getSelectedItemPosition()
                , mIndexTypeSpinner.getSelectedItemPosition()
                , mIndexEncodingSpinner.getSelectedItemPosition()
                , getEditString(R.id.et_lower_bound), getEditString(R.id.et_upper_bound), getEditString(R.id.et_limit));

    }

    private String getEditString( int id ) {
        EditText et = mRootView.findViewById( id );
        if ( et == null ) return "";

        return et.getText().toString();
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }


    @Override
    public void showTableResult(String result, String statusInfo) {
        String title = String.format("%s: %s", getString(R.string.get_table), mTableNameSpinner.getSelectedItem() );

        ShowResultDialog.newInstance( title, result, statusInfo).show( getChildFragmentManager());
    }

    private void setupAccountHistory(){
        UiUtils.setupAccountHistory(mScope, mTvCode);

        // when contract selected
        mTvCode.setOnItemClickListener( (adapterView, view, position, id) -> {
            ListAdapter adapter = mTvCode.getAdapter();
            if ( (null != adapter ) && !StringUtils.isEmpty( (String) adapter.getItem(position)) ){
                hideKeyboard();
                mPresenter.onGetTableListClicked( (String) adapter.getItem(position) );
            }
        });
    }

    @Override
    public void showTableList( List<String> tables ) {
        mTableNameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tables);
        mTableNameAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        mTableNameSpinner.setAdapter( mTableNameAdapter );

        showToast( R.string.table_loaded );

    }

}