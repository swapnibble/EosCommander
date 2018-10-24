package io.plactal.eoscommander.data.remote.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.plactal.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2017-09-15.
 */

public class GetTableRequest {
    private static final int DEFAULT_FETCH_LIMIT = 10;

    @Expose
    private boolean json = true;

    @Expose
    private String code;

    @Expose
    private String scope;

    @Expose
    private String table;

    @Expose
    private String key_type = "";

    @Expose
    private int index_position = 0;

    @Expose
    private String encode_type = "";

    @Expose
    private String lower_bound= "";

    @Expose
    private String upper_bound= "";

    @Expose
    private int limit ;



    public GetTableRequest( String scope, String code, String table,
                            int indexPos, String keyType, String encodeType, String lowerBound, String upperBound, int limit ) {
        this.scope = scope;
        this.code = code;
        this.table = table;

        this.key_type = keyType;
        this.encode_type = encodeType;
        this.index_position = indexPos < 0 ? 0 : indexPos;

        this.lower_bound = StringUtils.isEmpty( lowerBound) ? "" : lowerBound;
        this.upper_bound = StringUtils.isEmpty( upperBound) ? "" : upperBound;
        this.limit = limit <= 0 ? DEFAULT_FETCH_LIMIT : limit;
    }
}
