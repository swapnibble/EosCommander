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
package io.mithrilcoin.eoscommander.data.remote.model.chain;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import io.mithrilcoin.eoscommander.data.remote.model.types.EosByteWriter;
import io.mithrilcoin.eoscommander.crypto.digest.Sha256;
import io.mithrilcoin.eoscommander.crypto.ec.EcDsa;
import io.mithrilcoin.eoscommander.crypto.ec.EcSignature;
import io.mithrilcoin.eoscommander.crypto.ec.EosPrivateKey;
import io.mithrilcoin.eoscommander.data.remote.model.types.TypeChainId;

/**
 * Created by swapnibble on 2017-09-11.
 */

public class SignedTransaction extends Transaction {

    @Expose
    private List<String> signatures = null;

    @Expose
    private List<String> context_free_data = new ArrayList<>();


    public SignedTransaction(){
        super();
    }

    public SignedTransaction( SignedTransaction anotherTxn){
        super(anotherTxn);
        this.signatures = deepCopyOnlyContainer( anotherTxn.signatures );
    }

    public List<String> getSignatures() {
        return signatures;
    }

    public void putSignatures(List<String> signatures) {
        this.signatures = signatures;
    }

    public int getCtxFreeDataCount() {
        return ( context_free_data == null ) ? 0 : context_free_data.size();
    }

    public List<String> getCtxFreeData() {
        return context_free_data;
    }


    private Sha256 getDigestForSignature(TypeChainId chainId) {
        EosByteWriter writer = new EosByteWriter(255);

        // data layout to sign :
        // [ {chainId}, {Transaction( parent class )} ]

        writer.putBytes(chainId.getBytes());
        pack( writer); // don't include members of current class!

        return Sha256.from(writer.toBytes());
    }

    public void sign(EosPrivateKey privateKey, TypeChainId chainId) {
        if ( null == this.signatures){
            this.signatures = new ArrayList<>();
        }

        EcSignature signature = EcDsa.sign(getDigestForSignature( chainId ), privateKey);
        this.signatures.add( signature.toString());
    }
}
