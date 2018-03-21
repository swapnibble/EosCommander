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
package io.mithrilcoin.eoscommander.ui.result;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.ui.base.BaseDialog;

/**
 * Created by swapnibble on 2017-11-13.
 */

public class ShowResultDialog extends BaseDialog {
    private static final String TAG = ShowResultDialog.class.getSimpleName();
    private static final String ARG_TITLE       = "show.data.title";
    private static final String ARG_RESULT_DATA = "show.data.result";


    public static ShowResultDialog newInstance(String title, String info ) {
        ShowResultDialog fragment = new ShowResultDialog();
        Bundle bundle = new Bundle();
        bundle.putString( ARG_TITLE, title );
        bundle.putString( ARG_RESULT_DATA, info );
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_get_response, container, false);
    }


    @Override
    protected void setUpView(View view) {
        Bundle args = getArguments();

        // title
        ((TextView) view.findViewById(R.id.tv_dialog_title)).setText( args.getString( ARG_TITLE));

        // info
        ((TextView) view.findViewById(R.id.tv_get_response)).setText( args.getString(ARG_RESULT_DATA) );

        view.findViewById(R.id.btn_close).setOnClickListener( v -> dismiss() );

    }


    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }
}
