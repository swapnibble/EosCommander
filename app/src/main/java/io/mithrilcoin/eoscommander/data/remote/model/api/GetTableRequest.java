package io.mithrilcoin.eoscommander.data.remote.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by swapnibble on 2017-09-15.
 */

public class GetTableRequest {
    @SerializedName("json")
    @Expose
    private boolean isJson = true;

    @SerializedName("scope")
    @Expose
    private String scope;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("table")
    @Expose
    private String table;

    public GetTableRequest( String scope, String code, String table ) {
        this.scope = scope;
        this.code = code;
        this.table = table;
    }
}
