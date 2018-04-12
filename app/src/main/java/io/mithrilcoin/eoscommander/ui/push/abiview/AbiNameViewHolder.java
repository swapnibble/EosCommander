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
package io.mithrilcoin.eoscommander.ui.push.abiview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.ui.widget.TextInputAutoCompleteTextView;
import io.mithrilcoin.eoscommander.util.UiUtils;

/**
 * Created by swapnibble on 2018-01-09.
 */

public class AbiNameViewHolder extends AbiStringViewHolder {
    public AbiNameViewHolder(String key, String type, boolean isArray, LayoutInflater layoutInflater, ViewGroup parentView) {
        super(key, type, isArray, layoutInflater, parentView);
    }

    @Override
    protected int getItemLayout() { return R.layout.label_with_name; }

    @Override
    protected View getItemView(LayoutInflater layoutInflater, ViewGroup parentView, String label ) {
        View view = super.getItemView( layoutInflater, parentView, label );

        UiUtils.setupAccountHistory( view.findViewById( R.id.et_input ) );

        return view;
    }

    @Override
    protected String getItemValue(View itemView) {
        TextInputAutoCompleteTextView tiauto = itemView.findViewById( R.id.et_input );
        if ( tiauto != null ) {
            return tiauto.getText().toString();
        }

        return "";
    }
}
