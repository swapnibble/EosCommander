package io.plactal.eoscommander.data.remote.model.abi;

import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * Created by swapnibble on 2018-01-03.
 */

public class EosAbiTypeDef {
    @Expose
    public String new_type_name; // fixed_string32

    @Expose
    public String type;
}
