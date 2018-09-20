package io.plactal.eoscommander.data.remote.model.api;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

/**
 * Created by swapnibble on 2018-09-20.
 */
public class GetAbiResponse {
    @Expose
    private String account_name;

    @Expose
    private JsonObject abi;

    public JsonObject getAbi() { return abi; }
}
