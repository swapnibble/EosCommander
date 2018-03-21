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
package io.mithrilcoin.eoscommander.ui.account.info;

import android.support.annotation.StringRes;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2017-11-13.
 */

public enum AccountInfoType {
    REGISTRATION( R.string.get_account), // eosc get account <account>
    TRANSACTIONS(R.string.get_transactions), // eosc get transaction <account>
    SERVANTS(R.string.get_servants) ; // eosc get servants <account>

    private final int mTitleRscId;

    AccountInfoType(@StringRes int strRsc) {
        mTitleRscId = strRsc;
    }

    public int getTitleId() {
        return mTitleRscId;
    }

    public static AccountInfoType safeValueOf(String name ) {
        try {
            if (StringUtils.isEmpty(name)) {
                return REGISTRATION;
            }

            return AccountInfoType.valueOf( name);
        }
        catch (IllegalArgumentException e) {
            return REGISTRATION;
        }
    }
}
