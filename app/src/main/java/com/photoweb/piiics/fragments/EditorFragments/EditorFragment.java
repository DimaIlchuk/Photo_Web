package com.photoweb.piiics.fragments.EditorFragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class EditorFragment extends Fragment {

    protected abstract int getLayoutResource();

    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResource(), container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null)  unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /*
    The maximal size that is accepted for a bitmap by all the phone (especially by the old ones) are 2048x2048
    If the bitmap is taller than 2048x2048, resize it.
 */
    protected Bitmap resizeBitmapIfNeeded(Bitmap bitmap) {
        if (bitmap.getHeight() > 2048) {
            if (bitmap.getWidth() > bitmap.getHeight()) {
                bitmap = getScaledBitmapFromWitdh(bitmap);
            } else {
                bitmap = getScaledBitmapFromHeight(bitmap);
            }
        } else if (bitmap.getWidth() > 2048) {
            if (bitmap.getWidth() > bitmap.getHeight()) {
                bitmap = getScaledBitmapFromWitdh(bitmap);
            } else {
                bitmap = getScaledBitmapFromHeight(bitmap);
            }
        }
        return bitmap;
    }

    protected Bitmap getScaledBitmapFromWitdh(Bitmap myBitmap) {
        float aspectRatio = myBitmap.getWidth() /
                (float) myBitmap.getHeight();

        int width = 2048;
        int height = Math.round(width / aspectRatio);

        myBitmap = Bitmap.createScaledBitmap(
                myBitmap, width, height, false);
        return myBitmap;
    }

    protected Bitmap getScaledBitmapFromHeight(Bitmap myBitmap) {
        float aspectRatio = myBitmap.getWidth() /
                (float) myBitmap.getHeight();
        int height = 2048;
        int width = Math.round(height / aspectRatio);


        myBitmap = Bitmap.createScaledBitmap(
                myBitmap, width, height, false);
        return myBitmap;
    }
}