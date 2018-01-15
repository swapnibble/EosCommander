package io.mithrilcoin.eoscommander.data.remote.model.abi;

import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * Created by swapnibble on 2017-12-22.
 */

public class EosAbiStruct {

    @Expose
    public String name;

    @Expose
    public String base;

    @Expose
    public Map<String,String> fields; // key: name, value: type( built-in or another struct )

    @Override
    public String toString() {
        return "Struct name: " + name + ", base: " + base ;
    }
}
