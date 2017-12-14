package io.mithrilcoin.eoscommander.ui.suggestion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.app.EosCommanderApp;
import io.mithrilcoin.eoscommander.data.EoscDataManager;
import io.mithrilcoin.eoscommander.util.rx.EoscSchedulerProvider;
import io.mithrilcoin.eoscommander.util.rx.SchedulerProvider;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by swapnibble on 2017-12-12.
 */

public class AccountSuggestAdapter extends ArrayAdapter<String> implements View.OnClickListener{

    public AccountSuggestAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if ( null == convertView ) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =  inflater.inflate( R.layout.account_suggestion, parent, false );

            holder = new ViewHolder(convertView);
            convertView.setTag( holder );

            holder.delete.setTag( position );
            holder.delete.setOnClickListener( this );
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        String eosAccountName = getItem( position );
        if ( null == eosAccountName ) {
            holder.account_name.setText("");
            holder.delete.setOnClickListener( null );
            return convertView;
        }

        holder.account_name.setText( eosAccountName );

        return convertView;
    }

    @Override
    public void onClick(View view) {
        if ( ( null != view) && (view.getId() == R.id.del_history) && ( null != view.getTag())) {
            String accountName = getItem( (Integer)view.getTag());


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                .setTitle( R.string.delete_confirm)
                .setMessage( String.format( getContext().getString(R.string.ru_sure_delete), accountName))
                .setPositiveButton(android.R.string.yes, ( dlg, btnId) -> deleteAccountHistory( accountName))
                .setNegativeButton(android.R.string.no, null);

            alertDialog.create().show();
        }
    }

    private EoscDataManager getDataMgr() {
        return ((EosCommanderApp) getContext().getApplicationContext())
                .getAppComponent().dataManager();
    }

    private void deleteAccountHistory(String accountName) {

        SchedulerProvider schedulerProvider = new EoscSchedulerProvider();

        Completable.fromAction (() -> getDataMgr().deleteAccountHistory(accountName))
                .subscribeOn( schedulerProvider.io())
                .observeOn( schedulerProvider.ui())
                .subscribe(() -> {
                            reloadHistory();
                            Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        }
                        , e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void reloadHistory() {
        this.clear();

        this.addAll( getDataMgr().getAllAccountHistory(true) );
    }

    private class ViewHolder {
        private TextView account_name;
        private View delete;
        public ViewHolder(View itemView) {
            account_name = itemView.findViewById(R.id.eos_account);
            delete = itemView.findViewById(R.id.del_history);
        }
    }
}
