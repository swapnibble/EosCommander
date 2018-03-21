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
package io.mithrilcoin.eoscommander.ui.settings;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.ui.base.BaseActivity;
import io.mithrilcoin.eoscommander.util.StringUtils;
import io.mithrilcoin.eoscommander.util.UiUtils;

public class SettingsActivity extends BaseActivity implements SettingsMvpView {
    private static final int COLOR_ID_CONNECTION_OK = R.color.colorMithril;
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
        findViewById(R.id.btn_connect).setOnClickListener(
                v -> mPresenter.tryConnectEosd(((TextView)findViewById(R.id.et_host)).getText()
                        , ((TextView)findViewById(R.id.et_port)).getText())
        );

        mTvConnStatus = findViewById( R.id.tv_conn_status);
        mTvConnMsg = findViewById( R.id.tv_conn_msg);

        mChkSkipSigning = findViewById(R.id.cb_skip_signature);
        mChkSkipSigning.setOnCheckedChangeListener( ( v, checked) -> mPresenter.onChangeIgnoreSignature( checked ));


        mPresenter.attachView( this );
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
