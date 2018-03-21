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
package io.mithrilcoin.eoscommander.ui.push.abiview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.data.remote.model.abi.EosAbiAction;
import io.mithrilcoin.eoscommander.data.remote.model.abi.EosAbiField;
import io.mithrilcoin.eoscommander.data.remote.model.abi.EosAbiMain;
import io.mithrilcoin.eoscommander.data.remote.model.abi.EosAbiStruct;
import io.mithrilcoin.eoscommander.data.remote.model.abi.EosAbiTypeDef;
import io.mithrilcoin.eoscommander.util.RefValue;
import io.mithrilcoin.eoscommander.util.StringUtils;


/**
 * Created by swapnibble on 2017-12-26.
 */

public class AbiViewBuilder implements AbiViewCallback {
    private Map<String, Class<? extends AbiViewBaseHolder>> mBuiltinTypeToVH;
    private LayoutInflater mLayoutInflater;

    private Map<String, EosAbiStruct>   mStructs;
    private Map<String, String>         mTypeDefs;
    private Map<String, EosAbiAction>   mActions;

    public void setAbiJson( String abiJson ){
        setAbiMain( new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create().fromJson(abiJson, EosAbiMain.class) );
    }

    public void setAbiMain( EosAbiMain abi ) {
        initTypeToViewMap();

        // type
        if ( null != abi.types ) {
            mTypeDefs = new HashMap<>( abi.types.size() );
            for( EosAbiTypeDef typeDef : abi.types ) {
                mTypeDefs.put( typeDef.new_type_name, typeDef.type);
            }
        }
        else {
            mTypeDefs = new HashMap<>(0);
        }

        // struct
        if ( null != abi.structs ) {
            mStructs = new HashMap<>( abi.structs.size() );
            for ( EosAbiStruct struct : abi.structs ) {
                mStructs.put( struct.name, struct );
            }
        }
        else {
            mStructs = new HashMap<>( 0 );
        }

        // action list
        if ( null != abi.actions) {
            mActions = new TreeMap<>();
            for ( EosAbiAction action : abi.actions ) {
                mActions.put( action.action_name, action );
            }
        }
        else {
            mActions = new HashMap<>(0);
        }
    }

    public List<String> getActionNames() {
        ArrayList<String > actionNameList = new ArrayList<>( mActions.size() );

        Set<String> actionNames = mActions.keySet();
        for ( String name : actionNames ){
            actionNameList.add( name );
        }

        return actionNameList;
    }

    private void initTypeToViewMap() {
        // libraries/abi_generator.cpp 를 봐...

        if ( ( mBuiltinTypeToVH != null) && (mBuiltinTypeToVH.size() > 0)) {
            return;
        }

        mBuiltinTypeToVH = new HashMap<>();
        mBuiltinTypeToVH.put( "string", AbiStringViewHolder.class );

        mBuiltinTypeToVH.put( "name",  AbiNameViewHolder.class );
        mBuiltinTypeToVH.put( "account_name",  AbiNameViewHolder.class );
        mBuiltinTypeToVH.put( "uint8", AbiIntegerViewHolder.class );
        mBuiltinTypeToVH.put( "uint16", AbiIntegerViewHolder.class );
        mBuiltinTypeToVH.put( "uint32", AbiIntegerViewHolder.class );
        mBuiltinTypeToVH.put( "uint64", AbiBigIntegerViewHolder.class );
        mBuiltinTypeToVH.put( "uint128", AbiBigIntegerViewHolder.class );
        mBuiltinTypeToVH.put( "uint256", AbiBigIntegerViewHolder.class );

        mBuiltinTypeToVH.put( "int8", AbiIntegerViewHolder.class );
        mBuiltinTypeToVH.put( "int16",  AbiIntegerViewHolder.class );
        mBuiltinTypeToVH.put( "int32", AbiIntegerViewHolder.class );
        mBuiltinTypeToVH.put( "int64", AbiIntegerViewHolder.class );

        // time :
        mBuiltinTypeToVH.put( "time", AbiTimeInputViewHolder.class );

        // bytes : hex string
        mBuiltinTypeToVH.put( "bytes", AbiHexStrViewHolder.class);

        mBuiltinTypeToVH.put( "asset", AbiAssetViewHolder.class);

        // signature : hex string len 65 제한!
        mBuiltinTypeToVH.put( "signature", AbiStringViewHolder.class);

        // checksum : hex string, len 32 제한
        mBuiltinTypeToVH.put( "checksum", AbiHexStrViewHolder.class);

        // public key : hex string, len 33 제한
        mBuiltinTypeToVH.put( "public_key", AbiStringViewHolder.class);
    }


    private String resolveType( String typeName, RefValue<Boolean> isArrayTypeRef ) {
        if ( StringUtils.isEmpty( typeName )) {
            return "";
        }

        // is array ?
        if ( typeName.endsWith("[]") ) {
            typeName = typeName.substring( 0, typeName.length() - 2);

            if ( isArrayTypeRef != null ) {
                isArrayTypeRef.data = true;
            }
        }

        // 1. lookup built-in type
        if ( mBuiltinTypeToVH.get( typeName) != null ) {
            return typeName;
        }

        // 2. lookup typedefs
        String resolved = mTypeDefs.get( typeName);
        if ( null != resolved ){
            return resolveType( resolved, isArrayTypeRef );
        }

        return typeName;
    }

    private TextView getErrorMsgTextView(ViewGroup parentView, String msg) {
        TextView tv = (TextView) mLayoutInflater.inflate( R.layout.single_text, parentView, false);
        tv.setText( msg );

        return tv;
    }


    public View getViewForAction(ViewGroup parentView, String actionName ) {
        mLayoutInflater = LayoutInflater.from( parentView.getContext() );

        EosAbiAction abiAction = mActions.get( actionName );
        if ( null == abiAction ){
            return getErrorMsgTextView( parentView, "Error: No action defined: " + actionName);
        }

        AbiViewBaseHolder.setDynViewCallback( this);

        return onRequestAbiViewHolder( abiAction.type, parentView);
    }

    @Override
    public View onRequestAbiViewHolder(String typeName, ViewGroup parentView) {

        RefValue<Boolean> isArray = new RefValue<>(false);

        String resolvedType = resolveType( typeName, isArray ) ;
        if ( StringUtils.isEmpty( resolvedType )) {
            return getErrorMsgTextView( parentView, "Unknown type:" + typeName);
        }

        EosAbiStruct actionStruct = mStructs.get( resolvedType );
        ViewGroup abiView;

        if (null != actionStruct) {
            // action 의 root
            // holder create
            AbiStructViewHolder structViewHolder = new AbiStructViewHolder(null, resolvedType, isArray.data, this, mLayoutInflater, parentView);
            abiView = structViewHolder.getContainerView();
        }
        else {
            abiView = getViewHolderForBuiltinType( null, resolvedType, isArray.data, parentView).getContainerView();
        }

        if ( abiView == null ) {
            return getErrorMsgTextView( parentView, "Unknown type " + resolvedType );
        }

        return abiView;
    }

    @Override
    public void onRequestViewForStruct( String structName, ViewGroup parentView){
        EosAbiStruct abiStruct = mStructs.get( resolveType(structName, null) );
        if ( abiStruct != null ) {
            buildViewForStruct( abiStruct, parentView);
        }
    }

    private void buildViewForStruct( EosAbiStruct abiStruct, ViewGroup parentView) {
        // base 부터..
        if (!StringUtils.isEmpty(abiStruct.base) ) {

            EosAbiStruct baseStruct = mStructs.get( resolveType( abiStruct.base, null) );
            if (baseStruct != null) {
                buildViewForStruct( baseStruct, parentView); // recursive call
            }
            else {
                parentView.addView( getErrorMsgTextView( parentView, "Unknown base struct: " + abiStruct.base + " for " + abiStruct.name) );
            }
        }

        // 필드 view 붙이기..
        buildViewForFields( abiStruct.fields, parentView );
    }

    private String getArrayTypeIfNeeded(String type, boolean isArrayType ) {
        if ( isArrayType ) {
            return type + "[]";
        }

        return type;
    }

    private void buildViewForFields(List<EosAbiField> fields, ViewGroup parentView ) {
        RefValue<Boolean> isArray = new RefValue<>(false);

        for ( EosAbiField abiFiled : fields ) {

            isArray.data = false; // initialize

            String rType = resolveType( abiFiled.type, isArray );
            if ( StringUtils.isEmpty( rType ) ) {
                parentView.addView( getErrorMsgTextView( parentView, "UnknownTypeFor: " + abiFiled.name) );
                continue;
            }

            String typeForView = getArrayTypeIfNeeded( rType, isArray.data);

            // struct 를 받고
            EosAbiStruct abiStruct = mStructs.get( rType );
            AbiViewBaseHolder<?> abiViewHolder;
            if ( abiStruct != null){
                // nested layout 을 inflate 한다.
                abiViewHolder = new AbiStructViewHolder( abiFiled.name, typeForView, isArray.data, this, mLayoutInflater, parentView);
            }
            else {
                // maybe built-in type
                abiViewHolder = getViewHolderForBuiltinType( abiFiled.name, rType, isArray.data, parentView);
            }

            if ( abiViewHolder != null) {
                abiViewHolder.attachContainerToParent(parentView);
            }
            else {
                parentView.addView( getErrorMsgTextView( parentView, "Error creating view: "+ abiFiled.name));
            }
        }
    }

    private void buildViewForFields(Map<String,String> fields, ViewGroup parentView ) {
        Set<String> keys = fields.keySet();

        RefValue<Boolean> isArray = new RefValue<>(false);

        for ( String keyName : keys ) {

            isArray.data = false; // initialize

            String rType = resolveType( fields.get(keyName), isArray );
            if ( StringUtils.isEmpty( rType ) ) {
                parentView.addView( getErrorMsgTextView( parentView, "UnknownTypeFor: " + keyName) );
                continue;
            }

            String typeForView = getArrayTypeIfNeeded( rType, isArray.data);

            // struct 를 받고
            EosAbiStruct abiStruct = mStructs.get( rType );
            AbiViewBaseHolder<?> abiViewHolder;
            if ( abiStruct != null){
                // nested layout 을 inflate 한다.
                abiViewHolder = new AbiStructViewHolder( keyName, typeForView, isArray.data, this, mLayoutInflater, parentView);
            }
            else {
                // maybe built-in type
                abiViewHolder = getViewHolderForBuiltinType( keyName, rType, isArray.data, parentView);
            }

            if ( abiViewHolder != null) {
                abiViewHolder.attachContainerToParent(parentView);
            }
            else {
                parentView.addView( getErrorMsgTextView( parentView, "Error creating view: "+ keyName));
            }
        }
    }

    private AbiViewBaseHolder getViewHolderForBuiltinType(String key, String type, boolean isArray, ViewGroup parentView ){


        Class< ? extends AbiViewBaseHolder> viewHolderClazz = mBuiltinTypeToVH.get( type );

        if ( null == viewHolderClazz ) {
            return null;
        }

        AbiViewBaseHolder result = null;
        try {

            Constructor< ? extends AbiViewBaseHolder> constructor
                    = viewHolderClazz.getConstructor( String.class, String.class, Boolean.TYPE, LayoutInflater.class, ViewGroup.class);

            if ( null != constructor ) {
                result = constructor.newInstance(new Object[]{key, type, isArray, mLayoutInflater, parentView});
            }

        }
        catch (NoSuchMethodException e){
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getJson( View view ){
        return AbiViewBaseHolder.dumpToJson( view );
    }
}
