package io.plactal.eoscommander.data.remote.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by swapnibble on 2018-01-10.
 */

public class GetCodeRequest {
    @SerializedName("account_name")
    @Expose
    private String name;

    public GetCodeRequest(String accountName){
        name = accountName;
    }
}
