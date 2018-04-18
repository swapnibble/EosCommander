package io.plactal.eoscommander.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.plactal.eoscommander.data.local.db.AppDatabase;
import io.plactal.eoscommander.data.local.repository.EosAccountRepository;
import io.plactal.eoscommander.data.local.repository.EosAccountRepositoryImpl;
import io.plactal.eoscommander.data.prefs.PreferencesHelper;
import io.plactal.eoscommander.data.remote.NodeosApi;
import io.plactal.eoscommander.data.remote.HostInterceptor;
import io.plactal.eoscommander.data.util.GsonEosTypeAdapterFactory;
import io.plactal.eoscommander.data.wallet.EosWalletManager;
import io.plactal.eoscommander.di.ApplicationContext;
import io.plactal.eoscommander.util.RefValue;
import io.plactal.eoscommander.util.StringUtils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by swapnibble on 2017-08-24.
 */
@Module
public class AppModule {

    private final Application mApp;

    public AppModule( Application application) { mApp = application;}

    @Provides
    Application provideApp() { return mApp; }

    @Provides
    @ApplicationContext
    Context provideAppContext() { return mApp; }


    @Provides
    @Singleton
    HostInterceptor providesHostInterceptor() {
        return new HostInterceptor();
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(HostInterceptor interceptor) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(logging)
                .build();
    }


    @Provides
    @Singleton
    Gson providesGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new GsonEosTypeAdapterFactory())
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation().create();
    }


    private static final String ENDPOINT = "http://testnet1.eos.io";

    @Provides
    @Singleton
    NodeosApi providesEosService(Gson gson, OkHttpClient okHttpClient, PreferencesHelper preferencesHelper) {
        RefValue<Integer> portRef = new RefValue<>(0);
        String addr = preferencesHelper.getNodeosConnInfo( portRef );

        String url = StringUtils.isEmpty( addr ) ? ENDPOINT : ( "http://"+addr+":"+portRef.data);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( url )
                .addConverterFactory( GsonConverterFactory.create( gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // retrofit 용 rxjava2 adapter
                .client( okHttpClient )
                .build();

        return retrofit.create( NodeosApi.class);
    }

    @Provides
    @Singleton
    EosWalletManager providesWalletManager() {
        return new EosWalletManager();
    }

    @Provides
    @Singleton
    AppDatabase provideAppDatabase( @ApplicationContext  Context context){
        return Room.databaseBuilder( context.getApplicationContext(), AppDatabase.class, "eosc.db")
                .build();
    }

    @Provides
    @Singleton
    EosAccountRepository provideAccountRepository(AppDatabase database ) {
        return new EosAccountRepositoryImpl(database);
    }
}
