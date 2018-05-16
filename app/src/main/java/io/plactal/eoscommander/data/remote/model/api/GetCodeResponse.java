package io.plactal.eoscommander.data.remote.model.api;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.plactal.eoscommander.crypto.digest.Sha256;
import io.plactal.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2018-01-10.
 */

public class GetCodeResponse {
    @Expose
    private String account_name;

    @Expose
    private String wast;

    @Expose
    private String code_hash;

    @Expose
    private JsonObject abi;

    public JsonObject getAbi() { return abi; }

    public boolean isValidCode() {
        return ! ( StringUtils.isEmpty(code_hash) || Sha256.ZERO_HASH.toString().equals( code_hash ));
    }
}
