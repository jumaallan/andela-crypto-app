package com.androidstudy.andelatrackchallenge.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.androidstudy.andelatrackchallenge.R;

/**
 * Created by anonymous on 11/2/17.
 */

public class ColorIconView extends AppCompatImageView {
    public ColorIconView(Context context) {
        super(context);
    }

    public ColorIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColorIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorIconView);
        int color = typedArray.getColor(R.styleable.ColorIconView_civ_color, 0);

        setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        typedArray.recycle();
    }

    public void setImageFilterColor(int color) {
        if (color == -1) {
            setColorFilter(null);
        } else {
            setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }
}
