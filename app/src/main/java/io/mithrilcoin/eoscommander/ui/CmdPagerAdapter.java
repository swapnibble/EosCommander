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
package io.mithrilcoin.eoscommander.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.ViewGroup;

import io.mithrilcoin.eoscommander.ui.account.AccountMainFragment;
import io.mithrilcoin.eoscommander.ui.base.BaseActivity;
import io.mithrilcoin.eoscommander.ui.base.BaseFragment;
import io.mithrilcoin.eoscommander.ui.gettable.GetTableFragment;
import io.mithrilcoin.eoscommander.ui.push.PushFragment;
import io.mithrilcoin.eoscommander.ui.transfer.TransferFragment;
import io.mithrilcoin.eoscommander.ui.wallet.WalletFragment;

public class CmdPagerAdapter extends FragmentPagerAdapter {
    private static final int TAB_IDX_WALLET     = 0;
    private static final int TAB_IDX_ACCOUNT    = 1;
    private static final int TAB_IDX_TRANSFER   = 2;
    private static final int TAB_IDX_PUSH       = 3;
    private static final int TAB_IDX_GET_TABLE  = 4;

    private int mTabCount;

    private AppCompatActivity mActivity;

    private SparseArray<BaseFragment> mFragmentInstances = new SparseArray<>();

    public CmdPagerAdapter(AppCompatActivity activity) {
        super(activity.getSupportFragmentManager());
        mActivity = activity;
    }

    public void setTabCount( int tabCount ){
        this.mTabCount = tabCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BaseFragment fragment = (BaseFragment) super.instantiateItem( container, position );
        mFragmentInstances.put( position, fragment);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mFragmentInstances.remove(position);
        super.destroyItem(container, position, object);
    }

    public BaseFragment getFragment(int position) {
        return mFragmentInstances.get(position);
    }


    @Override
    public Fragment getItem(int position) {
        if ( mActivity instanceof BaseActivity ){
            ((BaseActivity)mActivity).hideKeyboard();
        }

        BaseFragment fragment;

        switch (position) {
            case TAB_IDX_ACCOUNT:
                fragment = AccountMainFragment.newInstance(); break;

            case TAB_IDX_WALLET:
                fragment = WalletFragment.newInstance(); break;

            case TAB_IDX_TRANSFER:
                fragment = TransferFragment.newInstance(); break;

            case TAB_IDX_PUSH:
                fragment = PushFragment.newInstance(); break;

            case TAB_IDX_GET_TABLE:
                fragment = GetTableFragment.newInstance(); break;

            default:
                return null;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return mTabCount;
    }
}