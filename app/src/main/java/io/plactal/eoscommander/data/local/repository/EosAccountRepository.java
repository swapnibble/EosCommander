package io.plactal.eoscommander.data.local.repository;

import java.util.List;

import io.plactal.eoscommander.util.RefValue;

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
     * @return
     */
    List<String> getAll( );

    List<String> searchName( String nameStarts);
}
