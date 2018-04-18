/*
 * Copyright (c) 2017-2018 PLACTAL.
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

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.plactal.eoscommander.R;
import io.plactal.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2017-12-28.
 */

public class AbiStringViewHolder extends AbiViewBaseHolder<String> {

    public AbiStringViewHolder(String key, String type, boolean isArray, LayoutInflater layoutInflater, ViewGroup parentView) {
        super(key, type, isArray, layoutInflater, parentView );
    }

    protected int getItemLayout() { return R.layout.label_with_str; }

    @Override
    protected View getItemView(LayoutInflater layoutInflater, ViewGroup parentView, String label ) {
        ViewGroup container = (ViewGroup)layoutInflater.inflate( getItemLayout(), parentView, false );
        setItemViewLabel( container, label );

        return container;
    }

    @Override
    protected String getItemValue(View itemView) {
        TextInputEditText tie = itemView.findViewById( R.id.et_input );
        if ( tie != null ) {
            return tie.getText().toString();
        }

        return "";
    }

    @Override
    protected void setItemViewLabel( View itemView, String label){
        TextInputLayout til = itemView.findViewById( R.id.til_input_wrapper);
        til.setHint( label );
    }
}
