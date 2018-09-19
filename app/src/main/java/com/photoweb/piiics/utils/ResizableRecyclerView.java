package com.photoweb.piiics.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class ResizableRecyclerView extends RecyclerView {
    public ResizableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableRecyclerView(Context context) {
        super(context);
    }

    public ResizableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}