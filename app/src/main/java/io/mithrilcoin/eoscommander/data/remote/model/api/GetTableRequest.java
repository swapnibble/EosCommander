package io.mithrilcoin.eoscommander.data.remote.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by swapnibble on 2017-09-15.
 */

public class GetTableRequest {
    @Expose
    private boolean json = true;

    @Expose
    private String scope;

    @Expose
    private String code;

    @Expose
    private String table;

    public GetTableRequest( String scope, String code, String table ) {
        this.scope = scope;
        this.code = code;
        this.table = table;
    }
}
