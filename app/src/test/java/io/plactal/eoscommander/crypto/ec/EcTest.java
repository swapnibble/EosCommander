package io.plactal.eoscommander.crypto.ec;

import org.junit.Test;

import io.plactal.eoscommander.crypto.digest.Sha256;
import io.plactal.eoscommander.crypto.util.HexUtils;

import static org.junit.Assert.assertEquals;

/**
 * Created by swapnibble on 2017-11-21.
 */
public class EcTest {

    @Test
    public void testSampleK1Keys(){

        // followings are example keys in EOS documents.

        assertEquals( "ec(secp256k1) key pair not match! - 1"
                , new EosPrivateKey("5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3").getPublicKey().toString()
                , "EOS6MRyAjQq8ud7hVNYcfnVPJqcVpscN5So8BhtHuGYqET5GDW5CV" );

        assertEquals( "ec(secp256k1) key pair not match! - 2"
                , new EosPrivateKey("5JKbLfCXgcafDQVwHMm3shHt6iRWgrr9adcmt6vX3FNjAEtJGaT").getPublicKey().toString()
                , "EOS4toFS3YXEQCkuuw1aqDLrtHim86Gz9u3hBdcBw5KNPZcursVHq" );

        assertEquals( "ec(secp256k1) key pair not match! - 3"
                , new EosPrivateKey("5Hv22aPcjnENBv6X9o9nKGdkfrW44En6z4zJUt2PobAvbQXrT9z").getPublicKey().toString()
                , "EOS7d9A3uLe6As66jzN8j44TXJUqJSK3bFjjEEqR4oTvNAB3iM9SA" );

    }

    @Test
    public void testSampleR1Keys() {
        assertEquals( "ec(secp256r1) key pair not match! - 1"
                , new EosPrivateKey("PVT_R1_iyQmnyPEGvFd8uffnk152WC2WryBjgTrg22fXQryuGL9mU6qW").getPublicKey().toString()
                , "PUB_R1_6EPHFSKVYHBjQgxVGQPrwCxTg7BbZ69H9i4gztN9deKTEXYne4" );
    }

    private void recover_by_paramType( int curveParamType, String msgOnFailure ) {
        String payload = "PLACTAL";
        Sha256 digest = Sha256.from( payload.getBytes());
        EosPrivateKey privateKey = new EosPrivateKey(curveParamType);
        EosPublicKey  publicKey  = privateKey.getPublicKey();
        EcSignature signature = EcDsa.sign( digest, privateKey);

        EosPublicKey recoveredKey = EcDsa.recoverPubKey(digest.getBytes(), signature);

        assertEquals(msgOnFailure, publicKey, recoveredKey);
    }


    @Test
    public void test_recovery(){
        recover_by_paramType( CurveParam.SECP256_K1, "sig recovery(k1) failed");
        recover_by_paramType( CurveParam.SECP256_R1, "sig recovery(r1) failed");
    }

    private void recycleKeys(int curveParamType, String msgOnFailure){
        EosPrivateKey privateKey = new EosPrivateKey( curveParamType );

        String oriPubKeyString = privateKey.getPublicKey().toString();
        EosPublicKey recycledPubKey = new EosPublicKey( oriPubKeyString );

        assertEquals(msgOnFailure//"test key recycle curve: " + ( curveParamType == CurveParam.SECP256_R1 ? EosEcUtil.PREFIX_R1 : EosEcUtil.PREFIX_K1 )
                , oriPubKeyString, recycledPubKey.toString()) ;
    }

    @Test
    public void test_recycleKeys(){
        recycleKeys( CurveParam.SECP256_K1, "key recycle failed(k1)" );
        recycleKeys( CurveParam.SECP256_R1, "key recycle failed(r1)" );
    }
}