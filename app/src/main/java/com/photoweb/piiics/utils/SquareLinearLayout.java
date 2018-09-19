package com.photoweb.piiics.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

/**
 * Created by dnizard on 09/02/2018.
 */

public class SquareLinearLayout extends LinearLayout {
    public SquareLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLinearLayout(Context context) {
        super(context);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = 40 * (metrics.densityDpi / 160f);

        super.onMeasure(widthMeasureSpec, widthMeasureSpec + Math.round(40));
    }
}
