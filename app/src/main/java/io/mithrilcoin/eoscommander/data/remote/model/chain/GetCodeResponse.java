package io.mithrilcoin.eoscommander.data.remote.model.chain;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.mithrilcoin.eoscommander.crypto.digest.Sha256;
import io.mithrilcoin.eoscommander.util.StringUtils;

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

    public boolean isValidCode() {
        return ! ( StringUtils.isEmpty(code_hash) || Sha256.ZERO_HASH.toString().equals( code_hash ));
    }
}
