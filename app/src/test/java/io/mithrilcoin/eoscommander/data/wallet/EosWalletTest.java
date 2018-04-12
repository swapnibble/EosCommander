/*
 * Copyright (c) 2017-2018 PLACTAL.
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

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import io.mithrilcoin.eoscommander.crypto.ec.EosPrivateKey;
import io.mithrilcoin.eoscommander.crypto.ec.EosPublicKey;

import static org.junit.Assert.*;

/**
 * Created by swapnibble on 2017-10-18.
 */
public class EosWalletTest {
    @Test
    public void testWallet_noError() throws NoSuchAlgorithmException {
        // Don't forget to install or configure "Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy" !
        // ported from : wallet_test.cpp

        assertEquals("You should install or configure JCE Unlimited Strength Jurisdiction Policy!",
                Cipher.getMaxAllowedKeyLength("AES"), Integer.MAX_VALUE);

        EosWallet wallet = new EosWallet();
        assertTrue( wallet.isLocked());

        wallet.setPassword("pass");
        assertTrue(wallet.isLocked());

        wallet.unlock("pass");
        assertTrue(!wallet.isLocked());

        wallet.setWalletFilePath("test");
        assertEquals("test", wallet.getWalletFilePath());
        assertEquals(0, wallet.listKeys().size());

        EosPrivateKey priv = new EosPrivateKey();
        EosPublicKey pub = priv.getPublicKey() ;
        String wif = priv.toString();
        wallet.importKey(wif);
        assertEquals(1, wallet.listKeys().size());

        EosPrivateKey privCopy = wallet.getPrivateKey(pub) ;
        assertEquals(wif, privCopy.toString());

        wallet.lock();
        assertTrue(wallet.isLocked());
        wallet.unlock("pass");
        assertEquals(1, wallet.listKeys().size());

        assertTrue( wallet.saveFile("wallet_test.json"));


        EosWallet wallet2 = new EosWallet();

        assertTrue(wallet2.isLocked());
        wallet2.loadFile("wallet_test.json");
        assertTrue(wallet2.isLocked());

        wallet2.unlock("pass");
        assertEquals(1, wallet2.listKeys().size());

        EosPrivateKey privCopy2 = wallet2.getPrivateKey(pub) ;
        assertEquals(wif, privCopy2.toString());
    }
}