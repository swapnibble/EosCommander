package io.mithrilcoin.eoscommander.data.remote.model.chain;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by swapnibble on 2018-01-10.
 */

public class GetCodeResponse {
    @SerializedName("account_name")
    @Expose
    private String name;

    @SerializedName("wast")
    @Expose
    private String wast;

    @SerializedName("code_hash")
    @Expose
    private String code_hash;

    @SerializedName("abi")
    @Expose
    private JsonObject abi;

    public JsonObject getAbi() { return abi; }
}
