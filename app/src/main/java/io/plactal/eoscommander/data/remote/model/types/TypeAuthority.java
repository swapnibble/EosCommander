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
package io.plactal.eoscommander.data.remote.model.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swapnibble on 2017-09-12.
 */

public class TypeAuthority implements EosType.Packer {

    private int mThreshold;
    private List<TypeKeyWeight> mKeys;
    private List<TypePermissionLevelWeight> mAccounts;
    private List<TypeWaitWeight> mWaits;

    public TypeAuthority(int threshold, List<TypeKeyWeight> keyWeight,
                         List<TypePermissionLevelWeight> permissionLevelWeight, List<TypeWaitWeight> waitWeight) {
        mThreshold  = threshold;
        mKeys       = keyWeight;

        mAccounts   = permissionLevelWeight;
        mWaits      = waitWeight;
    }

    private static <T> List<T> createList(T item ) {
        ArrayList<T> retList = new ArrayList<>();
        retList.add( item );

        return retList;
    }


    public TypeAuthority(TypeKeyWeight oneKey, long uint32DelaySec) {
        this( 1, createList(oneKey), null, null);

        if ( uint32DelaySec > 0 ) {
            mThreshold = 2;
            mWaits = createList( new TypeWaitWeight(uint32DelaySec, 1));
        }
    }

    public TypeAuthority(int threshold, TypePublicKey pubKey, String permission) {
        this( threshold
                ,( null == pubKey ? null: createList(new TypeKeyWeight( pubKey, (short)1)) )
                ,( null == permission ? null : createList(new TypePermissionLevelWeight(permission))), null );
    }

    @Override
    public void pack(EosType.Writer writer) {

        writer.putIntLE( mThreshold);

        // keys
        writer.putCollection( mKeys );

        // accounts
        writer.putCollection( mAccounts );

        // waits
        writer.putCollection( mWaits );
    }
}
