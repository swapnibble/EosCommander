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
package io.plactal.eoscommander.ui.settings;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import io.plactal.eoscommander.R;
import io.plactal.eoscommander.ui.base.BaseActivity;
import io.plactal.eoscommander.util.StringUtils;
import io.plactal.eoscommander.util.UiUtils;
import timber.log.Timber;

public class SettingsActivity extends BaseActivity implements SettingsMvpView {
    private static final int COLOR_ID_CONNECTION_OK = R.color.colorPlactal;
    private static final int COLOR_ID_CONNECTION_NA = R.color.colorRed;

    @Inject
    SettingsPresenter mPresenter;

    private CheckBox mChkSkipSigning;
    private TextView mTvConnStatus;
    private TextView mTvConnMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // di
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_settings);
        setToolbarConfig(R.id.toolbar, true);

        //btn_connect 누르면 get info 를 보여준다.
        findViewById(R.id.btn_connect).setOnClickListener( v ->
                mPresenter.tryConnectNodeos(
                        ((AppCompatSpinner)findViewById( R.id.sp_scheme)).getSelectedItem().toString()
                        , ((TextView)findViewById(R.id.et_host)).getText()
                        , ((TextView)findViewById(R.id.et_port)).getText())
        );

        mTvConnStatus = findViewById( R.id.tv_conn_status);
        mTvConnMsg = findViewById( R.id.tv_conn_msg);

        mChkSkipSigning = findViewById(R.id.cb_skip_signature);
        mChkSkipSigning.setOnCheckedChangeListener( ( v, checked) -> mPresenter.onChangeIgnoreSignature( checked ));

        mPresenter.attachView( this );
    }



    @Override
    public void addConnScheme(List<String> schemes, int curSchemePosition){
        addSpinnerData( R.id.sp_scheme, schemes, curSchemePosition, null );
    }

    @Override
    public void addCoreSymbols( List<String> coreSymbolStrs, int curSymbolPosition) {
        AppCompatSpinner spinner = addSpinnerData(R.id.sp_core_symbol, coreSymbolStrs, curSymbolPosition,
                        new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.changeCoreSymbol( parent.getItemAtPosition( position).toString() );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if ( ( spinner != null ) && ( curSymbolPosition >= 0) ){
            spinner.setSelection( curSymbolPosition );
        }
    }

    private AppCompatSpinner addSpinnerData(int spinnerViewId, List<String> list, int curPosition,
                                            AdapterView.OnItemSelectedListener itemSelectedListener) {
        if ( list == null ) return null;

        ArrayAdapter<String> adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        AppCompatSpinner spinner = findViewById( spinnerViewId);
        spinner.setAdapter( adapter );

        if ( (curPosition >= 0) && ( curPosition < list.size()) ){
            spinner.setSelection(curPosition);
        }

        spinner.setOnItemSelectedListener( itemSelectedListener );

        return spinner;
    }

    @Override
    public void showConnInfo(String host, int port) {

        EditText editText = findViewById( R.id.et_host);
        if (( null != editText) && !StringUtils.isEmpty(host)) {
            UiUtils.setTextAndMoveCursorToEnd( editText, host );
        }

        editText =  findViewById( R.id.et_port);
        if ( null != editText ) {
            UiUtils.setTextAndMoveCursorToEnd( editText, String.valueOf( port ) );
        }
    }

    @Override
    public void showConnStatus(boolean connected, String message) {

        // conn status
        mTvConnStatus.setTextColor( getResources().getColor( connected ? COLOR_ID_CONNECTION_OK : COLOR_ID_CONNECTION_NA) );
        mTvConnStatus.setText( connected ? R.string.connected : R.string.disconnected);

        // conn message
        mTvConnMsg.setText(message);
    }

    @Override
    public void exitWithResult( boolean success ) {
        setResult( success ? RESULT_OK : RESULT_CANCELED);
        finish();
    }

    @Override
    public void showCheckOptions( boolean skipSigning ) {
        mChkSkipSigning.setChecked( skipSigning );
    }


    @Override
    public void onBackPressed() {
        mPresenter.onBackButton();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();

        super.onDestroy();
    }
}
