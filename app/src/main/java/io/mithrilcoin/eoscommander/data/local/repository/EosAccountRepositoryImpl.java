package io.mithrilcoin.eoscommander.data.local.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.mithrilcoin.eoscommander.data.local.db.AppDatabase;
import io.mithrilcoin.eoscommander.data.local.db.EosAccount;
import io.mithrilcoin.eoscommander.util.RefValue;

/**
 * Created by swapnibble on 2017-12-14.
 */

public class EosAccountRepositoryImpl implements EosAccountRepository {

    private AppDatabase mAppDatabase;

    private Set<String> mAccountCache = new TreeSet<>();

    private long        mDataVersion;
    private boolean     mShouldSyncWithDb;

    public EosAccountRepositoryImpl( AppDatabase appDatabase ) {
        mAppDatabase = appDatabase;
        mDataVersion = 1L;
        mShouldSyncWithDb = true;
    }


    @Override
    public void addAll(String... accountNames) {

        ArrayList<EosAccount> eosAccounts = new ArrayList<>(accountNames.length);

        for ( String name : accountNames ) {
            eosAccounts.add( EosAccount.from(name ) );
        }

        mAppDatabase.eosAccountDao().insertAll(eosAccounts);

        mDataVersion++;
        Collections.addAll(mAccountCache, accountNames);
    }

    @Override
    public void addAll(List<String> accountNames){
        ArrayList<EosAccount> eosAccounts = new ArrayList<>(accountNames.size());

        for ( String name : accountNames ) {
            eosAccounts.add( EosAccount.from(name ) );
        }

        mAppDatabase.eosAccountDao().insertAll(eosAccounts);

        mDataVersion++;
        mAccountCache.addAll( accountNames);
    }

    @Override
    public void addAccount(String accountName) {
        mAppDatabase.eosAccountDao().insert( EosAccount.from(accountName) );

        mDataVersion++;
        mAccountCache.add(accountName);
    }

    @Override
    public void deleteAll() {
        mAppDatabase.eosAccountDao().deleteAll();

        mDataVersion++;
        mAccountCache.clear();
    }

    @Override
    public void delete(String accountName) {
        mAppDatabase.eosAccountDao().delete( EosAccount.from(accountName));

        mDataVersion++;
        mAccountCache.remove( accountName );
    }

    @Override
    public List<String> getAll(boolean getFromCacheIfPossible, RefValue<Long> dataVersion) {

        if ( mShouldSyncWithDb ){
            getFromCacheIfPossible = false;
            mShouldSyncWithDb = false;
        }

        if ( null != dataVersion ){
            dataVersion.data = mDataVersion;
        }

        if ( getFromCacheIfPossible ) {
            return new ArrayList<>(mAccountCache); // get accounts from cache
        }
        else {
            List<String> retList = mAppDatabase.eosAccountDao().getAll(); // get accounts from db
            mAccountCache.addAll(retList); //  then cache..

            return retList;
        }
    }

    @Override
    public long getDataVersion() {
        return mDataVersion;
    }
}
