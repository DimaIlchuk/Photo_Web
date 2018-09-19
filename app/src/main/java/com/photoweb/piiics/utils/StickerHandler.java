package com.photoweb.piiics.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by dnizard on 11/07/2017.
 */

public class StickerHandler {

    private static StickerHandler instance;

    public static StickerHandler get() {
        if (instance == null) instance = new StickerHandler();
        return instance;
    }

    public static Bitmap drawSticker(Bitmap bMap, Bitmap sticker, float x, float y, float width, float height, float arg)
    {
        if(bMap == null){
            return bMap;
        }

        //float max = Math.max(width, height);

        Bitmap finale = Bitmap.createScaledBitmap(sticker, Math.round(width), Math.round(height), false);

        // create a matrix object
        Matrix matrix = new Matrix();
        matrix.postRotate(arg); // anti-clockwise by 90 degrees

        // create a new bitmap from the original using the matrix to transform the result
        Bitmap rotatedBitmap = Bitmap.createBitmap(finale , 0, 0, finale.getWidth(), finale.getHeight(), matrix, true);

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, 0, 0, null);
        canvas.drawBitmap(rotatedBitmap, x - rotatedBitmap.getWidth()/2, y - rotatedBitmap.getHeight()/2, null);

        /*Matrix matrix = new Matrix();
        matrix.setRotate(arg, x, y);
        canvas.drawBitmap(finale, matrix, null);*/


        return result;
    }

    public static Bitmap generateBitmapFromText(String text, float textSize, int textColor, Typeface tf, float ht) {

        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTypeface(tf);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);

        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);

        return Bitmap.createScaledBitmap(image, Math.round((image.getWidth()*ht)/image.getHeight()), Math.round(ht), false);
    }
}
