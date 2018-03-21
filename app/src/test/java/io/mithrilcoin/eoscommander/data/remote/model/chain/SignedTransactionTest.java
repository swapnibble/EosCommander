/*
 * Copyright (c) 2017-2018 Mithril coin.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.mithrilcoin.eoscommander.data.remote.model.chain;

import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;

import io.mithrilcoin.eoscommander.crypto.ec.CurveParam;
import io.mithrilcoin.eoscommander.crypto.ec.EcDsa;
import io.mithrilcoin.eoscommander.crypto.ec.EcSignature;
import io.mithrilcoin.eoscommander.crypto.ec.EcTools;
import io.mithrilcoin.eoscommander.crypto.ec.EosPrivateKey;
import io.mithrilcoin.eoscommander.crypto.util.BitUtils;
import io.mithrilcoin.eoscommander.crypto.util.HexUtils;
import io.mithrilcoin.eoscommander.data.remote.model.api.Action;
import io.mithrilcoin.eoscommander.data.remote.model.types.TypeAsset;

import static org.junit.Assert.*;

/**
 * Created by swapnibble on 2017-10-18.
 */
public class SignedTransactionTest {

    @Test
    public void testSymbol(){
        final int precision = 4;
        final String name = "EOS";
        long result = 0;
        for ( int i = 0; i < name.length(); i++) {
            result |= ((byte) name.charAt(i)) << 8*(i+1);
        }

        result |= precision;

        System.out.println( "Symbol name: " + name +", symVal: 0x" + Long.toHexString( result));
    }

    @Test
    public void setRefBlockId_okRefBlockNumAndPrefix() {
        SignedTransaction signedTransaction = new SignedTransaction();
        signedTransaction.setReferenceBlock( "000000044af9aacdb9329e7f875b44907bacb972e0c3ff49cd054660318c707e");

//        assertEquals( "incorrect ref block num", new BigInteger("4"), signedTransaction.getRefBlockNum());
//        assertEquals( "incorrect ref block prefix", new BigInteger(1, HexUtils.toBytes("7F9E32B9")), signedTransaction.getRefBlockPrefix());

        assertEquals( "incorrect ref block num", 4, signedTransaction.getRefBlockNum());
        assertEquals( "incorrect ref block prefix", 0x7F9E32B9, signedTransaction.getRefBlockPrefix());

        assertEquals( "asset symbol parse error", "EOS", new TypeAsset(1).symbolName());

        String assetStr = "12.34567 EOS";
        TypeAsset testAsset = TypeAsset.fromString(assetStr);
        assertNotNull( "fail parse asset from string", testAsset);
        assertEquals( "invalid parsed asset symbol", "EOS", testAsset.symbolName());
        assertEquals( "invalid parsed asset decimals", 5, testAsset.decimals());
        assertEquals( "invalid parsed eos asset", assetStr, testAsset.toString());
    }


    private SignedTransaction createTxn( String contract, String action, String dataAsHex, String[] scopes, String[] permissions ){
        Action msg = new Action();
        msg.setAccount( contract );
        msg.setAuthorization(permissions);
        msg.setName( action );
        msg.setData( dataAsHex );

        SignedTransaction txn = new SignedTransaction();
        // dawn3 does not request scopes.. txn.setScope( scopes );
        txn.addAction( msg );
        // dawn3 does not request scopes.. txn.setReadScopeList(new ArrayList<>(0));
        txn.putSignatures( new ArrayList<>());

        return txn;
    }

    private static final String CREATE_ACCOUNT_DATA_AS_HEX =
            "000000008040934b0000000080fed0430100000001021487ab8aaad86925d1949d9ed17f1da813c3a07e2e576c3e5993b9ad150154e10100000100000001028494b6f3acaf5af03db8e46b8c8d9610fb506f0a51d484cc8f120dccc26cceeb010000010000000001000000008040934b00000000149be8080100010000000000000004454f5300000000";

    private static final String CREATE_ACCOUNT_SIGN_BY_INITA1 =
            "1f40361c656f284cd676d920108d3a63dfcf0779d0296cdb2f9421c0c1fd18244a284db40e8661d75067d45b6559266ba5345e86df0af83343951284121dddd1ec";
    //     545c106dfd35900fab318cc12e208140abba086ab1112c543a808e2248ddb62d
    private static final String CREATE_ACCOUNT_SIGN_BY_INITA2 =
            "20545c106dfd35900fab318cc12e208140abba086ab1112c543a808e2248ddb62d5fe909c18582e792116e418e5491d18a5c98be34e5bdccb577d6fa59806e6a28";


    @Test
    public void signTransactionNew_matchSignature(){
        SignedTransaction txn =createTxn( "eos", "newaccount", CREATE_ACCOUNT_DATA_AS_HEX,
                new String[]{ "inita", "eos"}, new String[]{"inita@active"});

        String head_block_id = "000009907e54bb84c7dc993e613e237f65dfbbc0a26f501b2ac7e1eb7b570df3";
        txn.setReferenceBlock(head_block_id);
        txn.setExpiration("2017-09-22T09:04:25");

        EosPrivateKey key = new EosPrivateKey("5KiA2RDrwb9xq2j6Z3k8qaz58HVhwh7mAxjWPag9dwjpBFNCGYp");

        assertEquals( "key parse failed-1",  "EOS6H6WZR2Nme3Sp4F8Krkdn19EYsTZLEyD8KasQYfa2EcqpZMohV", key.getPublicKey().toString());

        key = new EosPrivateKey("5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3");
        assertEquals( "key parse failed-2",  "EOS6MRyAjQq8ud7hVNYcfnVPJqcVpscN5So8BhtHuGYqET5GDW5CV", key.getPublicKey().toString());

        byte[] data = HexUtils.toBytes("369a790be1b3192fa6eccd0e8e90b39692145d30c75eda7e435df083e30801a3");
        BigInteger r = new BigInteger(HexUtils.toBytes("545c106dfd35900fab318cc12e208140abba086ab1112c543a808e2248ddb62d"));
        BigInteger s = new BigInteger(HexUtils.toBytes("5fe909c18582e792116e418e5491d18a5c98be34e5bdccb577d6fa59806e6a28"));
        CurveParam curveParamK1 = EcTools.getCurveParam( CurveParam.SECP256_K1);
        EcSignature signature = new EcSignature( r,s, curveParamK1,1);
        assertEquals("failed to recover pubKey from sig, (recId 1)", key.getPublicKey(), EcDsa.recoverPubKey(curveParamK1,data, signature, 1) );

        r = new BigInteger(HexUtils.toBytes("40361c656f284cd676d920108d3a63dfcf0779d0296cdb2f9421c0c1fd18244a"));
        s = new BigInteger(HexUtils.toBytes("284db40e8661d75067d45b6559266ba5345e86df0af83343951284121dddd1ec"));
        signature = new EcSignature( r,s, curveParamK1,0);
        assertEquals("failed to recover pubKey from sig, (recId 0)", key.getPublicKey(), EcDsa.recoverPubKey(curveParamK1, data, signature, 0) );
    }
}