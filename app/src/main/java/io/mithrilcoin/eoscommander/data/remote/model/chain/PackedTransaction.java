package io.mithrilcoin.eoscommander.data.remote.model.chain;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import io.mithrilcoin.eoscommander.crypto.util.HexUtils;
import io.mithrilcoin.eoscommander.data.remote.model.types.EosByteWriter;

/**
 * Created by swapnibble on 2018-03-19.
 */

public class PackedTransaction {
    public enum CompressType{ NONE, ZLIB }

    @Expose
    List<String> signatures ;

    @Expose
    String compression = "none";

    @Expose
    String data;

    public PackedTransaction(SignedTransaction stxn){
        EosByteWriter byteWriter = new EosByteWriter(512);
        stxn.pack(byteWriter);

        data = HexUtils.toHex( byteWriter.toBytes() );
        signatures = stxn.getSignatures();
    }
}
