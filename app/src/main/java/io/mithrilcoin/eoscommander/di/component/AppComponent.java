package io.mithrilcoin.eoscommander.di.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import io.mithrilcoin.eoscommander.app.EosCommanderApp;
import io.mithrilcoin.eoscommander.data.EoscDataManager;
import io.mithrilcoin.eoscommander.data.remote.EosdApi;
import io.mithrilcoin.eoscommander.data.remote.HostInterceptor;
import io.mithrilcoin.eoscommander.di.ApplicationContext;
import io.mithrilcoin.eoscommander.di.module.AppModule;

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

    EosdApi eosService();
}
