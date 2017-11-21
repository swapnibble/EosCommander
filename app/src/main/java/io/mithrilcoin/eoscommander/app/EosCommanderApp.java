package io.mithrilcoin.eoscommander.app;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.BuildConfig;
import io.mithrilcoin.eoscommander.data.EoscDataManager;
import io.mithrilcoin.eoscommander.di.component.AppComponent;
import io.mithrilcoin.eoscommander.di.component.DaggerAppComponent;
import io.mithrilcoin.eoscommander.di.module.AppModule;
import timber.log.Timber;

/**
 * Created by swapnibble on 2017-11-03.
 */

public class EosCommanderApp extends Application {
    private AppComponent mAppComponent;

    @Inject
    EoscDataManager mDataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // https://android-developers.googleblog.com/2013/08/some-securerandom-thoughts.html
        PRNGFixes.apply();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mAppComponent = DaggerAppComponent.builder()
                .appModule( new AppModule(this))
                .build();

        mAppComponent.inject( this );
    }

    public static EosCommanderApp get( Context context ){
        return (EosCommanderApp) context.getApplicationContext();
    }

    public AppComponent getAppComponent() { return mAppComponent; }
}
