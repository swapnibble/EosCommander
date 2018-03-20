package io.mithrilcoin.eoscommander.data.remote.model.chain;

import com.google.gson.annotations.Expose;


/**
 * Created by swapnibble on 2018-03-20.
 */

public class DataAccessInfo {
    //public enum Type { read, write };

    @Expose
    public String type; // access type

    @Expose
    public String code;

    @Expose
    public String scope;

    @Expose
    public long   sequence;
}
