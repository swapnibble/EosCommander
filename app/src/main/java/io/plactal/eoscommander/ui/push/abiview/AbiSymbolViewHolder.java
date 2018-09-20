package io.plactal.eoscommander.ui.push.abiview;

import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.plactal.eoscommander.R;
import io.plactal.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2018-09-20.
 */
public class AbiSymbolViewHolder extends AbiStringViewHolder {
    public AbiSymbolViewHolder(String key, String type, boolean isArray, LayoutInflater layoutInflater, ViewGroup parentView) {
        super(key, type, isArray, layoutInflater, parentView );
    }

    protected int getItemLayout() { return R.layout.symbol_info; }

    @Override
    protected View getItemView(LayoutInflater layoutInflater, ViewGroup parentView, String label ) {
        ViewGroup container = (ViewGroup)layoutInflater.inflate( getItemLayout(), parentView, false );

        // set default decimal 4
        ((EditText)container.findViewById( R.id.et_decimal_input)).setText( "4");

        return container;
    }

    @Override
    protected String getItemValue(View itemView) {
        String symbolRep= "";

        TextInputEditText tie = itemView.findViewById( R.id.et_decimal_input);
        if ( tie != null ) {
            if (! StringUtils.isEmpty( tie.getText())) {
                symbolRep = tie.getText().toString();
            }
        }

        symbolRep += ",";

        tie = itemView.findViewById( R.id.et_name_input );
        if ( tie != null ) {
            if (! StringUtils.isEmpty( tie.getText())) {
                symbolRep += tie.getText().toString();
            }
        }

        return symbolRep;
    }

    @Override
    protected void setItemViewLabel( View itemView, String label){
        // nothing to do
    }
}
