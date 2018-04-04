package io.mithrilcoin.eoscommander.data.remote.model.chain;

import com.google.gson.annotations.Expose;

import java.util.List;

import io.mithrilcoin.eoscommander.data.remote.model.api.Action;

/**
 * Created by swapnibble on 2018-03-20.
 */

public class ActionTrace {
    @Expose
    public String receiver;

    @Expose
    public boolean context_free;

    @Expose
    public long cpu_usage;

    @Expose
    public Action act;

    @Expose
    public String console;

    @Expose
    public long region_id; // uint32_t

    @Expose
    public long cycle_index;// uint32_t

    @Expose
    public List<DataAccessInfo> data_access;

    @Expose
    public long auths_used; // uint32_t

    @Expose
    public long _profiling_us;
}
