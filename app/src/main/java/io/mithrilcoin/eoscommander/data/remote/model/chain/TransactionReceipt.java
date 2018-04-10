package io.mithrilcoin.eoscommander.data.remote.model.chain;

import com.google.gson.annotations.Expose;

import io.mithrilcoin.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2018-04-04.
 */

public class TransactionReceipt {

//    enum status_enum {
//        executed  = 0, ///< succeed, no error handler executed
//        soft_fail = 1, ///< objectively failed (not executed), error handler executed
//        hard_fail = 2, ///< objectively failed and error handler objectively failed thus no state change
//        delayed   = 3  ///< transaction delayed
//    };

    @Expose
    public String status ;

    @Expose
    public String id; // sha256

    @Expose
    public long kcpu_usage;

    @Expose
    public long net_usage_words;
}
