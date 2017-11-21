/*
 * Copyright (c) 2017 Mithril coin.
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
package io.mithrilcoin.eoscommander.data.wallet;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;

import io.mithrilcoin.eoscommander.util.StringUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by swapnibble on 2017-10-19.
 */
@FixMethodOrder( MethodSorters.NAME_ASCENDING )
public class EosWalletManagerTest {

    private static final String KEY1 = "5JktVNHnRX48BUdtewU7N1CyL4Z886c42x7wYW7XhNWkDQRhdcS";
    private static final String KEY2 = "5Ju5RTcVDo35ndtzHioPMgebvBM6LkJ6tvuU6LTNQv8yaz3ggZr";
    private static final String KEY3 = "5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3";

    private EosWalletManager mWalletMgr;

    @Before
    public void setUp(){
        checkExistsAndDelFile("test.wallet");
        checkExistsAndDelFile("test2.wallet");
        mWalletMgr = new EosWalletManager();
        mWalletMgr.setDir( new File(".") );
    }

    private boolean checkExistsAndDelFile(String name){
        File file = new File(name);
        if ( file.exists()) {
            file.delete();
            return true;
        }

        return false;
    }

    @Test
    public void t1_noWallet()  {
        assertEquals( 0, mWalletMgr.listWallets(null).size());
        assertEquals( 0, mWalletMgr.listKeys().size());
        assertEquals( 0, mWalletMgr.listWallets(null).size());
    }

    @Test(expected = IllegalStateException.class)
    public void t2_expectExceptions() {

        mWalletMgr.lock("test");
        mWalletMgr.unlock("test", "pw");
        mWalletMgr.importKey("test", "pw");
    }

    @Test
    public void t3_createWallet() throws IOException {

        String pw = mWalletMgr.create("test");
        assertTrue( !StringUtils.isEmpty(pw));
        assertTrue( pw.startsWith("PW"));

        assertEquals( 1, mWalletMgr.listWallets(null).size());
        assertEquals( 0, mWalletMgr.listKeys().size()); // no keys

        assertTrue( ! mWalletMgr.listWallets(null).get(0).locked);
        mWalletMgr.lock("test");
        assertTrue( mWalletMgr.listWallets(null).get(0).locked);

        mWalletMgr.unlock("test", pw);
        assertTrue( !mWalletMgr.listWallets(null).get(0).locked);

        mWalletMgr.importKey( "test", KEY1 );
        assertEquals(1, mWalletMgr.listKeys().size());

        mWalletMgr.importKey( "test", KEY2 );
        //Map<EosPublicKey, String> keys = mWalletMgr.listKeys();
        mWalletMgr.lock("test");
        assertEquals(0, mWalletMgr.listKeys().size());

        mWalletMgr.unlock("test", pw);
        assertEquals(2, mWalletMgr.listKeys().size());

        mWalletMgr.lockAll();
        assertEquals(0, mWalletMgr.listKeys().size());
        assertTrue( mWalletMgr.listWallets(null).get(0).locked);// * 없어야 함. locked 이어야

        String pw2 = mWalletMgr.create("test2");
        assertEquals( 2, mWalletMgr.listWallets(null).size());
        assertEquals( 0, mWalletMgr.listKeys().size());

        mWalletMgr.importKey("test2", KEY3);

        assertTrue( checkExistsAndDelFile( "test.wallet" ) );
        assertTrue( checkExistsAndDelFile( "test2.wallet" ) );
    }
}