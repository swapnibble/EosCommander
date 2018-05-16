package io.plactal.eoscommander.data.remote.model.abi;

import com.google.gson.annotations.Expose;

/**
 * Created by swapnibble on 2017-12-22.
 */

public class EosAbiAction {
    @Expose
    public String name;

    @Expose
    public String type;

    @Override
    public String toString(){
        return "EosAction: " + name + ", type: "+ type ;
    }
}
