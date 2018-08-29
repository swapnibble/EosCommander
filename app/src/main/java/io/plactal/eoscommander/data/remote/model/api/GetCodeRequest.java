package io.plactal.eoscommander.data.remote.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by swapnibble on 2018-01-10.
 */

public class GetCodeRequest {
    @Expose
    private String account_name;

    @Expose
    private boolean code_as_wasm ;

    public GetCodeRequest(String accountName){
        this(accountName, true);
    }

    public GetCodeRequest(String accountName, boolean code_as_wasm){
        this.account_name = accountName;
        this.code_as_wasm = code_as_wasm;
    }
}
