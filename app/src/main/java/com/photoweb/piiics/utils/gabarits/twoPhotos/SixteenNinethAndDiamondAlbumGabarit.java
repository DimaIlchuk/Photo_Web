package com.photoweb.piiics.utils.gabarits.twoPhotos;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.gabarits.AlbumGabarit;

import java.util.ArrayList;

/**
 * Created by dnizard on 08/08/2017.
 */

public class SixteenNinethAndDiamondAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        if(bMap.getHeight() > bMap.getWidth()*9/16)
        {
            height = bMap.getWidth()*9/16;
            width = bMap.getWidth();

        }else{
            width = bMap.getHeight()*16/9;
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        return Bitmap.createScaledBitmap(crop, Utils.pageWidth, Utils.pageWidth*9/16, true);
    }

    public Bitmap generateSecond(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        float radius = 100;

        if(bMap.getHeight() > bMap.getWidth())
        {
            height = bMap.getWidth();
            width = bMap.getWidth();

        }else{
            width = bMap.getHeight();
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        Bitmap resized = Bitmap.createScaledBitmap(crop, Utils.pageWidth/3, Utils.pageWidth/3, true);

        crop.recycle();
        //bMap.recycle();

        Bitmap result = Bitmap.createBitmap(resized.getWidth(), resized.getHeight(), resized.getConfig());

        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, result.getWidth(), result.getHeight());
        final RectF rectF = new RectF(new Rect(result.getWidth() - (int)Math.round(result.getWidth()/Math.sqrt(2)), result.getHeight() - (int)Math.round(result.getHeight()/Math.sqrt(2)), (int)Math.round(result.getWidth()/Math.sqrt(2)), (int)Math.round(result.getHeight()/Math.sqrt(2))));

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.save();
        canvas.translate(resized.getWidth()/2, resized.getHeight()/2);
        canvas.rotate(45);
        canvas.translate(-resized.getWidth()/2, -resized.getHeight()/2);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        canvas.restore();

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resized, rect, rect, paint);

        resized.recycle();

        return result;
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {
        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, 0, 0, null);

        bMap = images.get(1);

        canvas.drawBitmap(bMap, Utils.pageWidth/3, Utils.pageHeight - Utils.pageWidth*9/16 - Utils.pageWidth/6, null);

        return result;
    }

}
