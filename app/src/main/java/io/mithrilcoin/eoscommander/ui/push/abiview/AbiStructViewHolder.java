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

import org.json.JSONException;
import org.json.JSONObject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2017-12-28.
 */

public class AbiStructViewHolder extends AbiViewBaseHolder<JSONObject> {

    private AbiViewCallback mDynViewCallback;

    public AbiStructViewHolder(String key, String type, boolean isArray, AbiViewCallback callback, LayoutInflater layoutInflater, ViewGroup parentView) {
        super(key, type, isArray);
        mDynViewCallback = callback;

        initView( layoutInflater, parentView);
    }

    @Override
    protected View getItemView(LayoutInflater layoutInflater, ViewGroup parentView, String label) {
        // 배열일 경우, 아래의 view 를 계속해서 붙여나간다. 그후, 해당 child view holder 를 만든다.( child view 들을 붙여야 한다. )
        ViewGroup container = (ViewGroup)layoutInflater.inflate( R.layout.label_with_struct, parentView, false);

        setItemViewLabel( container, label );

        // field view 들을 만들고, view holder 를 붙여서 받자.
        if ( null != mDynViewCallback ) {
            mDynViewCallback.onRequestViewForStruct(getTypeName(), container);
        }

        return container;
    }

    @Override
    protected void setItemViewLabel( View itemView, String label){
        if ( itemView == null ) {
            return;
        }

        TextView tvLabel = itemView.findViewById( R.id.tv_label);

        if (StringUtils.isEmpty( label )){
            tvLabel.setVisibility( View.GONE );
        }
        else {
            tvLabel.setVisibility( View.VISIBLE );
            tvLabel.setText( label );
        }
    }

    @Override
    protected JSONObject getItemValue(View itemView) {
        ViewGroup container = getContainerView();
        int childCount = container.getChildCount();

        JSONObject jsonObject = new JSONObject();

        for ( int i = 0; i < childCount; i++ ) {
            if ( container.getChildAt(i).getTag() instanceof AbiViewBaseHolder) {
                ((AbiViewBaseHolder)container.getChildAt(i).getTag()).writeJson( jsonObject );
            }
        }

        return jsonObject;
    }


    @Override
    public void writeJson(JSONObject json) {
        try {
            if ( isArray() ) {
                super.writeJson( json );
            }
            else {
                JSONObject jsonToWrite = isRoot() ? json : new JSONObject() ;

                ViewGroup container = getContainerView();
                int childCount = container.getChildCount();
                for ( int i = 0; i < childCount; i++ ){
                    if ( container.getChildAt(i).getTag() instanceof AbiViewBaseHolder){
                        ((AbiViewBaseHolder)container.getChildAt(i).getTag()).writeJson( jsonToWrite);
                    }
                }

                if (! isRoot()) {
                    json.put(getKeyName(), jsonToWrite);
                }
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
