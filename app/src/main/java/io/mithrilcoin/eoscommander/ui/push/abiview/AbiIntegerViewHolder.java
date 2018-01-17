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
package io.mithrilcoin.eoscommander.ui.push.abiview;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.util.Utils;

import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;

/**
 * Created by swapnibble on 2018-01-09.
 */

public class AbiIntegerViewHolder extends AbiViewBaseHolder<Long> {
    private static final String TYPE_PREFIX_FOR_SIGNED = "int";

    public AbiIntegerViewHolder(String key, String type, boolean isArray, LayoutInflater layoutInflater, ViewGroup parentView) {
        super(key, type, isArray, layoutInflater, parentView);
    }

    @Override
    protected View getItemView(LayoutInflater layoutInflater, ViewGroup parentView, String label ) {
        ViewGroup vg = (ViewGroup)layoutInflater.inflate( R.layout.label_with_int, parentView, false );
        setItemViewLabel( vg, label );

        // add TYPE_NUMBER_FLAG_SIGNED when type is "signed int".
        TextInputEditText tie = vg.findViewById( R.id.et_input);
        tie.setInputType( getTypeName().startsWith( TYPE_PREFIX_FOR_SIGNED ) ?
                ( InputType.TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED) : InputType.TYPE_CLASS_NUMBER );

        return vg;
    }

    @Override
    protected Long getItemValue(View itemView) {
        TextInputEditText tie = itemView.findViewById( R.id.et_input );
        if ( tie == null ){
            return 0L;
        }

        return Utils.parseLongSafely( tie.getText().toString(), 0);
    }

    @Override
    protected void setItemViewLabel( View itemView, String label){
        TextInputLayout til = itemView.findViewById( R.id.til_input_wrapper);
        til.setHint( label );
    }
}
