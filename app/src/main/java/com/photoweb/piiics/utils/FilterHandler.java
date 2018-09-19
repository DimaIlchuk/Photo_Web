package com.photoweb.piiics.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;

/**
 * Created by dnizard on 03/07/2017.
 */

public class FilterHandler {
    private static final String TAG = "FilterHandler";

    private static FilterHandler instance;
    private static Context context;

    public static FilterHandler get() {
        if (instance == null) instance = new FilterHandler();
        return instance;
    }

    //Initialization
    public void init(Context _context) {
        context = _context;
    }

    public static Bitmap applyFilter(String filter, Bitmap org){
        try {
            Method m = FilterHandler.class.getMethod(filter, Bitmap.class);

            return (Bitmap) m.invoke(FilterHandler.get(), org);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap DefaultFilter(Bitmap org){
        return org;
    }

    public static Bitmap SepiaFilter(Bitmap org){
        GPUImage mGPU = new GPUImage(context);
        mGPU.setImage(org);

        mGPU.setFilter(new GPUImageSepiaFilter());

        return mGPU.getBitmapWithFilterApplied();
    }

    public static Bitmap ContrastFilter(Bitmap org){
        GPUImage mGPU = new GPUImage(context);
        mGPU.setImage(org);

        mGPU.setFilter(new GPUImageContrastFilter(2.0f));

        return mGPU.getBitmapWithFilterApplied();
    }

    public static Bitmap InvertFilter(Bitmap org){
        GPUImage mGPU = new GPUImage(context);
        mGPU.setImage(org);

        mGPU.setFilter(new GPUImageColorInvertFilter());

        return mGPU.getBitmapWithFilterApplied();
    }

    public static Bitmap HueFilter(Bitmap org){
        GPUImage mGPU = new GPUImage(context);
        mGPU.setImage(org);

        mGPU.setFilter(new GPUImageHueFilter(90.0f));

        return mGPU.getBitmapWithFilterApplied();
    }

    public static Bitmap GrayscaleFilter(Bitmap org){
        GPUImage mGPU = new GPUImage(context);
        mGPU.setImage(org);

        mGPU.setFilter(new GPUImageGrayscaleFilter());

        return mGPU.getBitmapWithFilterApplied();
    }

    public static Bitmap SobelFilter(Bitmap org){
        GPUImage mGPU = new GPUImage(context);
        mGPU.setImage(org);

        mGPU.setFilter(new GPUImageSobelEdgeDetection());

        return mGPU.getBitmapWithFilterApplied();
    }

    public static Bitmap EmbossFilter(Bitmap org){
        GPUImage mGPU = new GPUImage(context);
        mGPU.setImage(org);

        mGPU.setFilter(new GPUImageEmbossFilter());

        return mGPU.getBitmapWithFilterApplied();
    }

    public static Bitmap MonoFilter(Bitmap org){
        GPUImage mGPU = new GPUImage(context);
        mGPU.setImage(org);

        mGPU.setFilter(new GPUImageMonochromeFilter());

        return mGPU.getBitmapWithFilterApplied();
    }

    public static Bitmap VignetteFilter(Bitmap org){
        GPUImage mGPU = new GPUImage(context);
        mGPU.setImage(org);

        PointF centerPoint = new PointF();
        centerPoint.x = 0.5f;
        centerPoint.y = 0.5f;

        mGPU.setFilter(new GPUImageVignetteFilter(centerPoint, new float[] {0.0f, 0.0f, 0.0f}, 0.3f, 0.75f));

        return mGPU.getBitmapWithFilterApplied();
    }
}
