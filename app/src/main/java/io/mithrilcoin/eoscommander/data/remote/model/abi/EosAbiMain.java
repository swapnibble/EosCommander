package io.mithrilcoin.eoscommander.data.remote.model.abi;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by swapnibble on 2017-12-22.
 */

public class EosAbiMain {

    @Expose
    public List<EosAbiTypeDef> types;

    @Expose
    public List<EosAbiAction> actions;

    @Expose
    public List<EosAbiStruct> structs;

    @Expose
    public List<EosAbiTable> tables;


    /*
    public int getActionCount() {
        return ( null == actions ) ? 0 : actions.size();
    }

    public List<String> getActionNames() {
        if ( null == actions ) {
            return new ArrayList<>();
        }

        ArrayList<String> names = new ArrayList<>( actions.size() );

        for ( EosAbiAction action : actions ) {
            names.add( action.action_name );
        }

        return names;
    }

    public List<String> getTableNames() {
        if ( null == tables ) {
            return new ArrayList<>();
        }

        ArrayList<String> names = new ArrayList<>( tables.size() );

        for ( EosAbiTable table : tables ) {
            names.add( table.table_name );
        }

        return names;
    }

    public Map<String,String> getTypeMap() {
        if ( null == types) {
            return new HashMap<>(0);
        }

        HashMap<String,String> typeMap = new HashMap<>( types.size() );
        for( EosAbiTypeDef typeDef : types ) {
            typeDef.addToMap( typeMap );
        }

        return typeMap;
    }
    */
}
