package io.mithrilcoin.eoscommander.data.remote.model.chain;

import com.google.gson.annotations.Expose;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.mithrilcoin.eoscommander.crypto.util.BitUtils;
import io.mithrilcoin.eoscommander.crypto.util.HexUtils;
import io.mithrilcoin.eoscommander.data.remote.model.types.EosType;

/**
 * Created by swapnibble on 2018-03-19.
 */

public class TransactionHeader implements EosType.Packer {
    @Expose
    private String expiration;

    @Expose
    private int region = 0; // uint16_t

    @Expose
    private int ref_block_num = 0; // uint16_t

    @Expose
    private long ref_block_prefix= 0;// uint32_t

    @Expose
    private int packed_bandwidth_words; // uint16_t

    @Expose
    private int context_free_cpu_bandwdith; // uint16_t

    public TransactionHeader(){
    }

    public TransactionHeader( TransactionHeader other ){
        this.expiration = other.expiration;
        this.region = other.region;
        this.ref_block_num = other.ref_block_num;
        this.ref_block_prefix = other.ref_block_prefix;
        this.packed_bandwidth_words = other.packed_bandwidth_words;
        this.context_free_cpu_bandwdith = other.context_free_cpu_bandwdith;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public void setReferenceBlock( String refBlockIdAsSha256 ) {
        ref_block_num = new BigInteger( 1, HexUtils.toBytes(refBlockIdAsSha256.substring(0,8))).intValue();

        ref_block_prefix = //new BigInteger( 1, HexUtils.toBytesReversed( refBlockIdAsSha256.substring(16,24))).longValue();
                BitUtils.uint32ToLong(HexUtils.toBytes(refBlockIdAsSha256.substring(16,24)), 0); // BitUtils treats bytes in little endian.
                                                                                                        // so, no need to reverse bytes.
    }

    public int getRefBlockNum() {
        return ref_block_num;
    }
    public long getRefBlockPrefix() {
        return ref_block_prefix;
    }


    private Date getExpirationAsDate(String dateStr) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse( dateStr);

        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    @Override
    public void pack(EosType.Writer writer) {
        writer.putIntLE( (int)(getExpirationAsDate(expiration).getTime() / 1000) ); // ms -> sec

        writer.putShortLE( (short)(region  & 0xFFFF) );         // uint16
        writer.putShortLE( (short)(ref_block_num  & 0xFFFF) );  // uint16
        writer.putIntLE( (int)( ref_block_prefix & 0xFFFFFFFF) );// uint32

        writer.putShortLE( (short)(packed_bandwidth_words  & 0xFFFF) );     // uint16
        writer.putShortLE( (short)(context_free_cpu_bandwdith  & 0xFFFF) ); // uint16
    }
}
