package io.plactal.eoscommander.data.local.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by swapnibble on 2017-12-08.
 */
@Entity( tableName = "eos_account", indices={@Index(value = "account_name", unique = true)})
public class EosAccount {
    public static final int TYPE_ACCOUNT_ALL = 0;
    public static final int TYPE_ACCOUNT_USER = 1;
    public static final int TYPE_ACCOUNT_CONTRACT = 2;

    @PrimaryKey
    @ColumnInfo( name = "account_name")
    @NonNull
    public String   name;

    @ColumnInfo( name = "type")
    public Integer  type;

    public static EosAccount from( String name){
        return new EosAccount(name, TYPE_ACCOUNT_ALL );
    }

    public EosAccount( String name, Integer type){
        this.name = name;
        this.type = type;
    }

    @Override
    public int hashCode(){
        int result = 0;

        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type : 0);

        return result;
    }

    @Override
    public String toString(){
        return name;
    }
}
