package io.plactal.eoscommander.data.remote.model.types;

/**
 * Created by swapnibble on 2018-05-17.
 */
public class TypeWaitWeight implements EosType.Packer {
    private int    mWaitSec; // uint32_t
    private short  mWeight;

    public TypeWaitWeight( long uint32WaitSec, int uint16Weight){
        mWaitSec= (int)( uint32WaitSec & 0xFFFFFFFF );
        mWeight = (short)( uint16Weight & 0xFFFF );
    }

    @Override
    public void pack(EosType.Writer writer) {
        writer.putIntLE( mWaitSec );
        writer.putShortLE( mWeight);
    }
}
