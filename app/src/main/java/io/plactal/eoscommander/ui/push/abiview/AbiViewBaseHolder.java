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

package io.plactal.eoscommander.ui.push.abiview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import io.plactal.eoscommander.R;
import io.plactal.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2018-01-08.
 */

public abstract class AbiViewBaseHolder<T> {
    private static final int MAX_ARRAY_SIZE = 32;

    private ViewGroup mContainer;
    private String mKeyName;
    private String mTypeName;
    private boolean mIsRoot;
    private boolean mIsArray;

    private int mCurArraySize;


    private static WeakReference<AbiViewCallback> mDynViewCallbackRef;

    public static void setDynViewCallback( AbiViewCallback callback ){
        mDynViewCallbackRef = new WeakReference<>(callback);
    }

    protected View getDynView( String typeName, ViewGroup parentView ) {
        if ( mDynViewCallbackRef.get() == null ){
            return null;
        }

        return mDynViewCallbackRef.get().onRequestAbiViewHolder( typeName, parentView);
    }

    public static String dumpToJson( View view ) {
        if ( (view != null ) && (view.getTag() instanceof AbiViewBaseHolder) ){
            JSONObject json = new JSONObject();
            ((AbiViewBaseHolder)view.getTag()).writeJson( json );

            try {
                return json.toString(4 );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "";
    }


    public AbiViewBaseHolder(String key, String type, boolean isArray) {
        // key, root
        mKeyName    = key;
        mIsRoot     = StringUtils.isEmpty(key);

        mIsArray    = isArray;
        mTypeName   = type;
    }

    public AbiViewBaseHolder(String key, String type, boolean isArray,  LayoutInflater layoutInflater, ViewGroup parentView) {
        this( key, type, isArray);
        initView( layoutInflater, parentView);
    }

    protected void initView( LayoutInflater layoutInflater, ViewGroup parentView ) {
        // container
        if ( mIsArray ){
            // create array container view
            mContainer = (ViewGroup) layoutInflater.inflate( R.layout.container_for_array, parentView, false );

            // spinner for array size
            initSpinner( layoutInflater.getContext(), MAX_ARRAY_SIZE );
        }
        else {
            mContainer = (ViewGroup)getItemView( layoutInflater, parentView, generateLabel(-1) );
        }

        mContainer.setTag( this );
    }

    private void initSpinner(Context context, int size){

        TextView arrSizeLabel = mContainer.findViewById( R.id.tv_label );
        if ( arrSizeLabel != null ){
            arrSizeLabel.setText( mKeyName + "[] size: ");
        }

        ArrSizeAdapter adapter = new ArrSizeAdapter( context, size );
        AppCompatSpinner spinner = mContainer.findViewById( R.id.sp_array_size );

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                onChangeArraySize( adapterView.getContext(), position );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner.setAdapter( adapter);
    }

    private void onChangeArraySize( Context context, int newSize ) {
        if ( mCurArraySize < newSize ) {
            // add new item views
            // first child is size spinner!
            String typeName = mTypeName.endsWith("[]") ? mTypeName.substring(0, mTypeName.length() - 2) : mTypeName;

            for ( int arrayIndex = mCurArraySize ; arrayIndex < newSize; arrayIndex++ ) {
                View itemView = getDynView( typeName, mContainer );
                if ( itemView != null ){
                    setItemViewLabel( itemView, generateLabel( arrayIndex ));
                    mContainer.addView( itemView);
                }
                else {
                    // TODO add error textview
                }
            }
        }
        else if ( mCurArraySize > newSize ) {
            // remove item views
            // first child is size spinner!
            mContainer.removeViews( newSize + 1 , mCurArraySize - newSize );
        }

        mCurArraySize = newSize;
    }

    private String generateLabel( int arrIndex ) {
        if ( StringUtils.isEmpty( mKeyName)) {
            return "";
        }

        String label;
        if ( arrIndex >= 0 ){
            label = mKeyName +"[" + arrIndex + "]";
        }
        else {
            label = mKeyName;
        }

        String typeNameWithoutBracket = mTypeName.endsWith("[]") ? mTypeName.substring( 0, mTypeName.length() - 2) : mTypeName;

        return label + " (" + typeNameWithoutBracket + ")";
    }

    protected abstract View getItemView( LayoutInflater layoutInflater, ViewGroup parentView, String label );
    protected abstract T getItemValue(View itemView);

    protected abstract void setItemViewLabel( View itemView, String label);

    public String getKeyName() { return mKeyName; }

    public String getTypeName() { return mTypeName; }

    public ViewGroup getContainerView() { return mContainer; }

    protected boolean isRoot() { return mIsRoot; }

    public void attachContainerToParent(ViewGroup parentView){
        parentView.addView( mContainer );
    }


    protected boolean isArray(){
        return mIsArray;
    }


    public void writeJson(JSONObject json) {
        try {
            String jsonKey = isRoot() ? "" : mKeyName;
            if ( mIsArray ) {
                JSONArray jsonArray = new JSONArray();
                for ( int index = 1; index <= mCurArraySize; index++) {
                    jsonArray.put( getItemValue(mContainer.getChildAt(index) ) );
                }

                json.put( jsonKey,jsonArray );
            }
            else {
                json.put(jsonKey, getItemValue(mContainer));

            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private static class ArrSizeAdapter extends ArrayAdapter<Integer> {

        private final int mMaxSize;

        public ArrSizeAdapter(@NonNull Context context, int maxSize) {
            super(context, R.layout.spinner_item_centered);

            mMaxSize = ( maxSize > 0 ) ? maxSize : 0;

            setDropDownViewResource(R.layout.spinner_item_centered);
        }
        public int getCount() { return mMaxSize + 1;}

        @Override
        public Integer getItem( int position ){
            return position;
        }
    }
}
