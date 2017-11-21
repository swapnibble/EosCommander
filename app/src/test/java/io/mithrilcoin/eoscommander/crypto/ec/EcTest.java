package io.mithrilcoin.eoscommander.crypto.ec;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by swapnibble on 2017-11-21.
 */
public class EcTest {

    @Test
    public void testSampleKeys(){
        // followings are example keys in EOS documents.

        assertEquals( "ec key pair not match! - 1"
                , new EosPrivateKey("5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3").getPublicKey().toString()
                , "EOS6MRyAjQq8ud7hVNYcfnVPJqcVpscN5So8BhtHuGYqET5GDW5CV" );

        assertEquals( "ec key pair not match! - 2"
                , new EosPrivateKey("5JKbLfCXgcafDQVwHMm3shHt6iRWgrr9adcmt6vX3FNjAEtJGaT").getPublicKey().toString()
                , "EOS4toFS3YXEQCkuuw1aqDLrtHim86Gz9u3hBdcBw5KNPZcursVHq" );

        assertEquals( "ec key pair not match! - 3"
                , new EosPrivateKey("5Hv22aPcjnENBv6X9o9nKGdkfrW44En6z4zJUt2PobAvbQXrT9z").getPublicKey().toString()
                , "EOS7d9A3uLe6As66jzN8j44TXJUqJSK3bFjjEEqR4oTvNAB3iM9SA" );
    }

}