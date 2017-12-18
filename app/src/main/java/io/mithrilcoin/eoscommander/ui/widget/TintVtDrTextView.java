package io.mithrilcoin.eoscommander.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import io.mithrilcoin.eoscommander.R;

/**
 * Created by swapnibble on 2017-12-18.
 */

public class TintVtDrTextView extends AppCompatTextView {
    public TintVtDrTextView(Context context) {
        super(context);
    }

    public TintVtDrTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TintVtDrTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if ( null == attrs ) {
            return;
        }

        TypedArray attributeArray = context.obtainStyledAttributes( attrs, R.styleable.TintVtDrTextView);
        Drawable[] drawables = new Drawable[4];
        int[] styleableIds = { R.styleable.TintVtDrTextView_vectorLeft, R.styleable.TintVtDrTextView_vectorTop,
                R.styleable.TintVtDrTextView_vectorRight, R.styleable.TintVtDrTextView_vectorBottom} ;

        int colorId = attributeArray.getResourceId(R.styleable.TintVtDrTextView_vectorTint, -1);
        int tintColor = Color.BLACK;
        if ( -1 != colorId ) {
            tintColor = context.getResources().getColor( colorId );
        }


        for ( int i = 0; i < drawables.length ;i++ ) {
            int rscId = attributeArray.getResourceId(styleableIds[i], -1);
            if ( -1 != rscId ){
                drawables[i] = AppCompatResources.getDrawable(context, rscId) ;

                if ( -1 != tintColor ) {
                    DrawableCompat.setTint( drawables[i], tintColor);
                }
            }
        }

        // left, top, right, bottom
        setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);
        attributeArray.recycle();
    }
}
