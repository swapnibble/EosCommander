package io.mithrilcoin.eoscommander.data.local.repository;

import java.util.List;

/**
 * Created by swapnibble on 2017-12-14.
 */

public interface EosAccountRepository {
    void addAll(String... accountNames);
    void addAll(List<String> accountNames);
    void addAccount(String accountName);
    void deleteAll();
    void delete(String accountName);

    /**
     * get account list
     * @param getFromCacheIfPossible    get list from cache if possible
     * @return
     */
    List<String> getAll( boolean getFromCacheIfPossible );
}
