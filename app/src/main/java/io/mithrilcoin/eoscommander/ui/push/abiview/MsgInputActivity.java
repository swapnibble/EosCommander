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

package io.mithrilcoin.eoscommander.ui.push.abiview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.data.remote.model.abi.EosAbiMain;
import io.mithrilcoin.eoscommander.ui.base.BaseActivity;
import io.mithrilcoin.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2018-01-15.
 */

public class MsgInputActivity extends BaseActivity implements MsgInputMvpView{
    private static final String EXTRA_ABI_KEY = "abi.obj";
    private static final String EXTRA_ACTION_NAME = "abi.action";

    private static final String RESULT_JSON = "result.json";

    @Inject
    MsgInputPresenter mPresenter;

    private String mActionName;
    private ViewGroup mActionUiRoot;
    private AbiViewBuilder mAbiViewBuilder;

    public static Intent getLaunchIntent(Context context, String abiObjKey, String actionName) {
        Intent intent = new Intent(context, MsgInputActivity.class);
        intent.putExtra( EXTRA_ABI_KEY, abiObjKey);
        intent.putExtra( EXTRA_ACTION_NAME, actionName);

        return intent;
    }

    public static String getResultString( Intent intent ){
        if ( ( intent == null) || ( StringUtils.isEmpty( intent.getStringExtra( RESULT_JSON))) ) {
            return "";
        }

        return intent.getStringExtra( RESULT_JSON);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // di
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_msg_input_ui);

        mActionName     = getIntent().getStringExtra(EXTRA_ACTION_NAME);
        mActionUiRoot   = findViewById( R.id.action_ui_root );

        ((TextView)findViewById(R.id.tv_title)).setText( getString(R.string.action) + ( StringUtils.isEmpty(mActionName) ? "" : mActionName) );

        findViewById( R.id.btn_generate).setOnClickListener( v -> exitWithDumpToJson());


        mPresenter.attachView( this );

        mPresenter.onSetupViewFinished( getIntent().getStringExtra(EXTRA_ABI_KEY) );
    }

    private void exitWithDumpToJson() {

        Intent result = new Intent();

        try {
            String resultJson = mAbiViewBuilder.getJson(mActionUiRoot.getChildAt(0));
            result.putExtra( RESULT_JSON, resultJson );
            setResult( RESULT_OK, result);
        }
        catch (Exception e){
            e.printStackTrace();
            setResult(Activity.RESULT_CANCELED);
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }


    @Override
    public void setupAbiView(EosAbiMain abiMain) {
        if (StringUtils.isEmpty( mActionName )) {
            onError( R.string.err_empty_action_name);
            return;
        }

        if ( mAbiViewBuilder == null ) {
            mAbiViewBuilder = new AbiViewBuilder();
        }

        mAbiViewBuilder.setAbiMain( abiMain);
        View actionView = mAbiViewBuilder.getViewForAction( mActionUiRoot, mActionName);
        if ( actionView == null ){
            onError( R.string.err_create_msg_input);
            return;
        }

        mActionUiRoot.addView(actionView);
    }
}
