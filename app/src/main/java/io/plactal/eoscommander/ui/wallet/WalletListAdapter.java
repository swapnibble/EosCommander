/*
 * Copyright (c) 2017-2018 PlayerOne.
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
package io.plactal.eoscommander.ui.wallet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.plactal.eoscommander.R;
import io.plactal.eoscommander.data.wallet.EosWallet;
import io.plactal.eoscommander.util.Consts;

/**
 * Created by swapnibble on 2017-11-13.
 */

public class WalletListAdapter extends RecyclerView.Adapter<WalletListAdapter.WalletVH>{

    private ArrayList<EosWallet.Status> mWalletList;
    private ItemCallback mCallback;

    @Override
    public WalletVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.list_item_wallet, parent, false);


        return new WalletVH( view );
    }

    @Override
    public void onBindViewHolder(WalletVH holder, int position) {
        if ( getItemCount() <= position ){
            return;
        }

        EosWallet.Status walletStatus = mWalletList.get( position );

        holder.ivDelete.setVisibility( Consts.DEFAULT_WALLET_NAME.equals( walletStatus.walletName ) ? View.GONE : View.VISIBLE );

        holder.tvWalletName.setText( walletStatus.walletName );
        holder.tvLockStatus.setText( walletStatus.locked ? R.string.locked : R.string.unlocked );
        holder.tvLockStatus.setTag( walletStatus.locked );
    }

    @Override
    public int getItemCount() {
        if ( null != mWalletList ) {
            return mWalletList.size();
        }

        return 0;
    }

    public void setWalletList(ArrayList<EosWallet.Status> list ) {
        mWalletList = list;
    }

    public void setCallback(ItemCallback callback ){
        mCallback = callback;
    }

    class WalletVH extends RecyclerView.ViewHolder {
        TextView tvWalletName;
        TextView tvLockStatus;
        ImageView ivDelete;

        public WalletVH( View itemView) {
            super(itemView);

            // R.layout.list_item_wallet

            tvWalletName = itemView.findViewById( R.id.tv_wallet_name);
            tvLockStatus = itemView.findViewById( R.id.tv_lock_status );
            ivDelete     = itemView.findViewById( R.id.iv_delete );

            // import
            itemView.findViewById( R.id.btn_import)
                    .setOnClickListener( v -> {
                        if ( null != mCallback ) mCallback.onClickImportKey( tvWalletName.getText().toString());
                    });

            // lock/unlock
            tvLockStatus.setOnClickListener( v -> {
                        if ( null != mCallback) {
                            mCallback.onClickChangeLockStatus( tvWalletName.getText().toString());
                        }
                    });

            // delete
            ivDelete.setOnClickListener( v -> {
                        if ( null != mCallback) {
                            mCallback.onClickDelete( tvWalletName.getText().toString());
                        }
                    });
        }
    }


    interface ItemCallback {
        void onClickImportKey(String walletName);
        void onClickChangeLockStatus(String walletName);
        void onClickDelete( String walletName );
    }
}
