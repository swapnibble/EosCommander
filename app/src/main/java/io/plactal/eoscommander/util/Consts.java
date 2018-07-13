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
package io.plactal.eoscommander.util;


/**
 * Created by swapnibble on 2017-11-10.
 */

public final class Consts {
    public static final boolean DEFAULT_SKIP_SIGNING = true;

    public static final String DEFAULT_SERVANT_ACCOUNT = "eosio";

    public static final String  DEFAULT_WALLET_NAME = "default";
    public static final boolean DEFAULT_SAVE_PASSWORD= true;

    public static final String SAMPLE_PRIV_KEY_FOR_TEST = "5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3";

    public static final String EOSIO_SYSTEM_ACCOUNT = "eosio";
    public static final String EOSIO_TOKEN_CONTRACT = "eosio.token";
    public static final int TX_EXPIRATION_IN_MILSEC = 30000;

    public static final String DEFAULT_SYMBOL_STRING = "SYS";
    public static final int DEFAULT_SYMBOL_PRECISION = 4;

    public static final String EOS_SYMBOL_STRING = "EOS";


    // constants from /libraries/include/eosio/chain/config.hpp
    public static final int DEFAULT_BASE_PER_TRANSACTION_NET_USAGE  = 100;
    public static final int DEFAULT_BASE_PER_TRANSACTION_CPU_USAGE  = 500;
    public static final int DEFAULT_BASE_PER_ACTION_CPU_USAGE       = 1000;
}
