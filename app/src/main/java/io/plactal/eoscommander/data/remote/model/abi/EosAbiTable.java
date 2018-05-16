package io.plactal.eoscommander.data.remote.model.abi;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by swapnibble on 2017-12-26.
 */

public class EosAbiTable {

    @Expose
    public String name;

    @Expose
    public String type;

    @Expose
    public String index_type;

    @Expose
    public List<String> key_names;

    @Expose
    public List<String> key_types;
}
