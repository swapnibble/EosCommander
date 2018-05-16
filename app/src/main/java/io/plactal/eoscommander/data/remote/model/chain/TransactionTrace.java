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
package io.plactal.eoscommander.data.remote.model.chain;

/**
 * Created by swapnibble on 2017-09-11.
 */

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import java.util.List;

import io.plactal.eoscommander.data.remote.model.chain.ActionTrace;
import io.plactal.eoscommander.data.remote.model.chain.TransactionReceipt;
import io.plactal.eoscommander.data.remote.model.types.TypeSharedLock;
import io.plactal.eoscommander.util.StringUtils;

public class TransactionTrace extends TransactionReceipt {

    @Expose
    private List<ActionTrace> action_traces;

    @Expose
    private JsonElement deferred_transaction_requests;

    @Expose
    private List<TypeSharedLock> read_locks;

    @Expose
    private List<TypeSharedLock> write_locks;

    @Expose
    private long cpu_usage; // uint64_t

    @Expose
    private long net_usage; // uint64_t

    @Expose
    private String packed_trx_digest;

    @Expose
    private long region_id; // uint64_t

    @Expose
    private long cycle_index; // uint64_t

    @Expose
    private long shard_index; // uint64_t

    @Expose
    private long _profiling_us; // fc::microseconds

    @Expose
    private long _setup_profiling_us; // fc::microseconds

    @Override
    public String toString(){
        if (StringUtils.isEmpty( status)) {
            return "empty status";
        }

        return status + " " + kcpu_usage + " bytes " + net_usage_words + " cycles";
    }
}
