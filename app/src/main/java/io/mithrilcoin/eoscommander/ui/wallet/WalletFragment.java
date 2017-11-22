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
package io.mithrilcoin.eoscommander.ui.wallet;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.data.wallet.EosWallet;
import io.mithrilcoin.eoscommander.di.component.ActivityComponent;
import io.mithrilcoin.eoscommander.ui.base.BaseFragment;
import io.mithrilcoin.eoscommander.ui.result.ShowResultDialog;
import io.mithrilcoin.eoscommander.ui.wallet.dlg.CreateWalletDialog;
import io.mithrilcoin.eoscommander.ui.wallet.dlg.InputDataDialog;
import io.mithrilcoin.eoscommander.ui.wallet.dlg.ShowWalletPwDialog;

public class WalletFragment extends BaseFragment
        implements WalletMvpView, WalletListAdapter.ItemCallback {

    @Inject
    WalletPresenter mPresenter;

    private View mCreateDefaultWalletBtn;
    private RecyclerView mWalletRV;
    private WalletListAdapter mAdapter;

    public static WalletFragment newInstance() {
        WalletFragment fragment = new WalletFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            mPresenter.attachView( this );
        }

        return view;
    }


    @Override
    protected void setUpView(View view) {

        mCreateDefaultWalletBtn = view.findViewById(R.id.btn_create_default);
        mCreateDefaultWalletBtn.setOnClickListener( v -> onClickCreateDefaultWallet() );

        // create wallet
        view.findViewById(R.id.btn_create).setOnClickListener( v ->
                CreateWalletDialog.newInstance()
                        .setCallback( name -> mPresenter.createNewWallet( name ))
                        .show( getChildFragmentManager())
        );

        // view keys
        view.findViewById(R.id.btn_view_keys).setOnClickListener( v -> mPresenter.onRequestShowKeys());

        setupListView( view );

        mPresenter.onFinishedSetupView();
    }

    private void setupListView(View view){
        RecyclerView rv = view.findViewById(R.id.rv_wallet);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setItemAnimator(new DefaultItemAnimator());

        rv.addItemDecoration( new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mAdapter = new WalletListAdapter();
        mAdapter.setCallback( this );
        rv.setAdapter( mAdapter );

        mWalletRV = rv;
    }

    private void onClickCreateDefaultWallet() {
        mPresenter.createDefaultWallet();
    }

    @Override
    public void showCreatedPassword(String walletName, String password
            , boolean savePassword, boolean enableSavePassword) {
        ShowWalletPwDialog
                .newInstance( walletName, password, savePassword, enableSavePassword )
                .setCallback( ( name, pass) -> mPresenter.onShownPassword( name, pass) )
                .show( getChildFragmentManager());
    }


    @Override
    public void showWalletList(ArrayList<EosWallet.Status> wallets){
        mAdapter.setWalletList( wallets );
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void getKeyToImport( String walletName) {
        InputDataDialog.newInstance( walletName, null, InputDataDialog.Type.Key)
                .setCallback( ( name, key) -> mPresenter.importKey( name, key) )
                .show( getChildFragmentManager() );
    }


    @Override
    public void onClickImportKey(String walletName){
        mPresenter.onRequestImportKey( walletName );
    }

    @Override
    public void onClickChangeLockStatus(String walletName){
        mPresenter.changeLockStatus( walletName );
    }


    @Override
    public void showKeys( String keysString) {
        ShowResultDialog.newInstance( getString(R.string.view_wallet_keys), keysString).show( getChildFragmentManager());
    }

    @Override
    public void getPassword( String walletName, String initialData ) {
        InputDataDialog.newInstance( walletName, initialData, InputDataDialog.Type.Password)
                .setCallback( ( name, password) -> mPresenter.unlockWallet( name, password) )
                .show( getChildFragmentManager() );
    }

    @Override
    public void showCreateDefaultWalletButton(boolean show) {
        mCreateDefaultWalletBtn.setVisibility( show ? View.VISIBLE : View.GONE );
    }
}