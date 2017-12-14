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
package io.mithrilcoin.eoscommander.ui.base;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.app.EosCommanderApp;
import io.mithrilcoin.eoscommander.di.component.ActivityComponent;
import io.mithrilcoin.eoscommander.di.component.DaggerActivityComponent;
import io.mithrilcoin.eoscommander.di.module.ActivityModule;
import io.mithrilcoin.eoscommander.ui.result.ShowResultDialog;
import io.mithrilcoin.eoscommander.util.UiUtils;


/**
 * Created by swapnibble on 2017-08-24.
 */

public class BaseActivity extends AppCompatActivity implements MvpView{
    private ActivityComponent mActivityComponent;

    private ProgressDialog  mProgressDlg;

    protected Toolbar setToolbarConfig( int toolbarId, boolean showUpIcon ) {
        Toolbar toolbar = findViewById(toolbarId);

        if ( null != toolbar ) {
            setSupportActionBar(toolbar);
        }

        if ( showUpIcon ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if ( null != upIntent ) {
                    upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                        // This activity is NOT part of this app's task, so create a new task
                        // when navigating up, with a synthesized back stack.
                        TaskStackBuilder.create(this)
                                // Add all of this activity's parents to the back stack
                                .addNextIntentWithParentStack(upIntent)
                                // Navigate up to the closest parent
                                .startActivities();
                    }
                    else {
                        // This activity is part of this app's task, so simply
                        // navigate up to the logical parent activity.
                        NavUtils.navigateUpTo(this, upIntent);
                    }
                }
                else {
                    onBackPressed();
                }


                return true;
        }

        return super.onOptionsItemSelected( item );
    }


    public ActivityComponent getActivityComponent() {
        if ( null == mActivityComponent) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule( new ActivityModule(this))
                    .appComponent( EosCommanderApp.get(this).getAppComponent() )
                    .build();
        }

        return mActivityComponent;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void showLoading(boolean show) {

        if ( (null != mProgressDlg) && mProgressDlg.isShowing() ) {
            mProgressDlg.cancel();
        }

        if ( show ) {
            mProgressDlg = UiUtils.showLoadingDialog( this );
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        onError( getString( resId));
    }

    @Override
    public void onError(String message) {
        ShowResultDialog.newInstance( getString(R.string.error), message)
                .show( getSupportFragmentManager());
    }

    @Override
    public void showToast(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(@StringRes int resId) {
        Toast.makeText( this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showResult( String resultMsg ) {
        ShowResultDialog.newInstance( getString(R.string.result), resultMsg)
                .show( getSupportFragmentManager());
    }

    @Override
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
