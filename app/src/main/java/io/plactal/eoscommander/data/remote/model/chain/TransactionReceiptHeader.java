package io.plactal.eoscommander.data.remote.model.chain;

import com.google.gson.annotations.Expose;

import io.plactal.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2018-04-04.
 */

public class TransactionReceiptHeader {

//    enum status_enum {
//        executed  = 0, ///< succeed, no error handler executed
//        soft_fail = 1, ///< objectively failed (not executed), error handler executed
//        hard_fail = 2, ///< objectively failed and error handler objectively failed thus no state change
//        delayed   = 3  ///< transaction delayed
//    };

    @Expose
    public String status ;

    @Expose
    public long cpu_usage_us;   ///< total billed CPU usage (microseconds)

    @Expose
    public long net_usage_words;///<  total billed NET usage, so we can reconstruct resource state when skipping context free data... hard failures...
}
