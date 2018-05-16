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
package io.plactal.eoscommander.data.remote.model.api;

/**
 * Created by swapnibble on 2017-09-11.
 */

import com.google.gson.annotations.Expose;

import io.plactal.eoscommander.data.remote.model.chain.TransactionTrace;
import io.plactal.eoscommander.util.StringUtils;

public class PushTxnResponse {

    @Expose
    private String transaction_id;

    @Expose
    private TransactionTrace processed;

    public String getTransactionId() {
        return transaction_id;
    }

    public void setTransactionId(String transactionId) {
        this.transaction_id = transactionId;
    }

    public TransactionTrace getProcessed() {
        return processed;
    }

    public void setProcessed(TransactionTrace processed) {
        this.processed = processed;
    }

    @Override
    public String toString() {
        if (StringUtils.isEmpty(transaction_id) || (processed == null )) return "";

        return "transaction: " + transaction_id + "\n" + processed.toString();
    }
}
