package io.plactal.eoscommander.data.remote.model.types;

import com.google.gson.annotations.Expose;

/**
 * Created by swapnibble on 2018-04-04.
 */

public class TypeSharedLock {
    @Expose
    private TypeAccountName account;

    @Expose
    private TypeScopeName scope;
}
