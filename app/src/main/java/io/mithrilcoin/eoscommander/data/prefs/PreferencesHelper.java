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
package io.mithrilcoin.eoscommander.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mithrilcoin.eoscommander.di.ApplicationContext;
import io.mithrilcoin.eoscommander.util.Consts;
import io.mithrilcoin.eoscommander.util.RefValue;
import io.mithrilcoin.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2017-08-21.
 */
@Singleton
public class PreferencesHelper {

    public static final String PREF_FILE_NAME = "eosc_pref";

    private static final String PREF_WALLET_DIR_NAME= "wallets";

    private static final String PREF_EOSD_HOST = "eosd.host";
    private static final String PREF_EOSD_PORT = "eosd.port";

    private static final String PREF_PREFIX_WALLET_PW = "wallet.pw.";
    private static final String PREF_SAVE_PASS_FOR_TESTING = "wallet.save.pw";
    private static final String PREF_DEFAULT_ACCOUNT_CREATOR= "account.default.creator";


    private static final String PREF_SKIP_SIGNING = "signing.skip";

    private final SharedPreferences mPrefs;
    private final File mWalletDirFile;

    // cache values
    private Boolean mSkipSigning;

    @Inject
    public PreferencesHelper(@ApplicationContext Context context ) {
        mPrefs = context.getSharedPreferences( PREF_FILE_NAME, Context.MODE_PRIVATE);

        mWalletDirFile = new File( context.getFilesDir(), PREF_WALLET_DIR_NAME);
        mWalletDirFile.mkdirs();
    }

    public void clear() {
        mPrefs.edit().clear().apply();
    }



    public File getWalletDirFile() {
        return mWalletDirFile;
    }


    public void putEosdConnInfo(String host, Integer port) {
        SharedPreferences.Editor editor = mPrefs.edit();

        editor.putString(PREF_EOSD_HOST, host );
        editor.putInt( PREF_EOSD_PORT, port );

        editor.apply();
    }

    public String getEosdConnInfo( RefValue<Integer> portRef) {
        if ( null != portRef ) {
            portRef.data = mPrefs.getInt(PREF_EOSD_PORT, 0);
        }

        return mPrefs.getString(PREF_EOSD_HOST, "");
    }

    public void putDefaultAccountCreator( String account ){
        mPrefs.edit().putString( PREF_DEFAULT_ACCOUNT_CREATOR, account ).commit();
    }

    public String getDefaultAccountCreator( ) {
        return mPrefs.getString( PREF_DEFAULT_ACCOUNT_CREATOR, "" );
    }

    public void putSavePasswordOption( boolean save ) {
        mPrefs.edit().putBoolean( PREF_SAVE_PASS_FOR_TESTING, save ).apply();
    }

    public boolean getSavePasswordOption(){
        return mPrefs.getBoolean( PREF_SAVE_PASS_FOR_TESTING, Consts.DEFAULT_SAVE_PASSWORD );
    }

    private String getSavedPasswdKeyFor( String walletName ) {
        return PREF_PREFIX_WALLET_PW + walletName;
    }

    public void putWalletPassword(String walletName, String password ) {
        String key = getSavedPasswdKeyFor( walletName );

        // critical data! commit immediately
        if (StringUtils.isEmpty( password ))  {
            mPrefs.edit().remove( key ).commit();
        }
        else {
            mPrefs.edit().putString(key, password).commit();
        }
    }

    public String getWalletPassword( String walletName) {
        return mPrefs.getString( getSavedPasswdKeyFor( walletName ), "" );
    }

    public boolean shouldSkipSigning() {
        if ( null != mSkipSigning ) {
            return mSkipSigning;
        }

        mSkipSigning = mPrefs.getBoolean( PREF_SKIP_SIGNING, Consts.DEFAULT_SKIP_SIGNING );
        return mSkipSigning;
    }


    public void putSkipSigning(boolean skipSigning ){
        mPrefs.edit().putBoolean(PREF_SKIP_SIGNING, skipSigning ).apply();
        mSkipSigning = skipSigning;
    }
}
