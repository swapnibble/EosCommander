package io.plactal.eoscommander.data.remote.model.chain;

import com.google.gson.annotations.Expose;

import java.util.List;

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
    public List<DataAccessInfo> data_access;

    @Expose
    public long auths_used; // uint32_t
}
