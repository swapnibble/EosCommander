package io.mithrilcoin.eoscommander.ui;

import android.os.Bundle;
import android.widget.TextView;

import io.mithrilcoin.eoscommander.BuildConfig;
import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.ui.base.BaseActivity;

public class InfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);

        ((TextView)findViewById(R.id.tv_desc_under_logo)).setText(
                String.format( getString(R.string.app_title_with_ver_fmt), BuildConfig.VERSION_NAME)); // version name
    }
}
