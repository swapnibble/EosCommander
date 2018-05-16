package io.plactal.eoscommander.data.local.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by swapnibble on 2017-12-11.
 */
@Database( entities = {EosAccount.class}, version = AppDatabase.VERSION)
public abstract class AppDatabase extends RoomDatabase {

    static final int VERSION = 1;

    public abstract EosAccountDao eosAccountDao();
}
