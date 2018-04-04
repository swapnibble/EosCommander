package io.mithrilcoin.eoscommander.data.remote.model.api;

import com.google.gson.annotations.Expose;

/**
 * Created by swapnibble on 2018-04-04.
 */

public class TransactionReceipt {
    @Expose
    public String status;

    @Expose
    public String id;
}
