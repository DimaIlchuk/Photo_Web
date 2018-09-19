package com.photoweb.piiics;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by thomas on 25/09/2017.
 */

public class ResizableGridView extends GridView {

    public ResizableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableGridView(Context context) {
        super(context);
    }

    public ResizableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
