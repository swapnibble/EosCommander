package io.plactal.eoscommander.data.local.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by swapnibble on 2017-12-11.
 */
@Dao
public interface EosAccountDao {
    @Query("SELECT account_name FROM eos_account ORDER BY account_name")
    List<String> getAll();

    @Query("SELECT account_name FROM eos_account WHERE account_name like :nameStarts ORDER BY account_name")
    List<String> getAll(String nameStarts);

    @Query("SELECT account_name FROM eos_account WHERE account_name like :nameStarts AND type=:account_type ORDER BY account_name")
    List<String> getAllWithType(String nameStarts, Integer account_type);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertAll( List<EosAccount> accounts);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertAll( EosAccount... accounts);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert( EosAccount account);

    @Delete
    void delete( EosAccount account);

    @Query("DELETE FROM eos_account")
    void deleteAll();
}
