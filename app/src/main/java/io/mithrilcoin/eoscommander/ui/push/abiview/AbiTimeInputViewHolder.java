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

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.mithrilcoin.eoscommander.R;


/**
 * Created by swapnibble on 2017-12-29.
 */

public class AbiTimeInputViewHolder extends AbiViewBaseHolder<String> {

    public AbiTimeInputViewHolder(String key, String type, boolean isArray, LayoutInflater layoutInflater, ViewGroup parentView) {
        super(key, type, isArray, layoutInflater, parentView);
    }

    @Override
    protected View getItemView(LayoutInflater layoutInflater, ViewGroup parentView, String label ) {
        ViewGroup vg = (ViewGroup)layoutInflater.inflate( R.layout.label_with_time, parentView, false );

        setItemViewLabel( vg, label );

        View button = vg.findViewById( R.id.btn_time_picker );
        button.setOnClickListener( this::openTimePicker);
        button.setTag( vg.findViewById( R.id.et_input)); // save edit text view as tag ( set "date" text after picking date )

        return vg;
    }

    @Override
    protected String getItemValue(View itemView) {
        return ((TextInputEditText)itemView.findViewById( R.id.et_input)).getText().toString();
    }

    @Override
    protected void setItemViewLabel( View itemView, String label){
        TextInputLayout til = itemView.findViewById( R.id.til_input_wrapper);
        til.setHint( label );
    }


    private void openTimePicker( View clickedBtn){
        new SingleDateAndTimePickerDialog.Builder( clickedBtn.getContext() )
                .title( getKeyName())
                .minutesStep(1)
                .setDayFormatter( new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "MM/dd/yyyy") ))
                .listener( date -> dateSelected( clickedBtn, date) )
                .display();
    }

    private void dateSelected( View buttonView, Date date) {
        if ( null == date ) {
            return;
        }

        if ( buttonView.getTag() instanceof TextInputEditText ) {
            // http://en.wikipedia.org/wiki/ISO_860
            ((TextInputEditText)buttonView.getTag()).setText( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss").format( date));
        }
    }
}
