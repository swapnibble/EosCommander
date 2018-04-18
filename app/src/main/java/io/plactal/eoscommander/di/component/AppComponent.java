package io.plactal.eoscommander.di.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import io.plactal.eoscommander.app.EosCommanderApp;
import io.plactal.eoscommander.data.EoscDataManager;
import io.plactal.eoscommander.data.remote.HostInterceptor;
import io.plactal.eoscommander.di.ApplicationContext;
import io.plactal.eoscommander.di.module.AppModule;

/**
 * Created by swapnibble on 2017-08-24.
 */
@Singleton
@Component( modules = AppModule.class)
public interface AppComponent {

    void inject(EosCommanderApp eosCommanderApp);

    @ApplicationContext
    Context context();

    Application application();
    EoscDataManager dataManager();
    HostInterceptor hostInterceptor();
}
