package com.photoweb.piiics.utils.gabarits.onePhoto;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.gabarits.AlbumGabarit;

import java.util.ArrayList;

/**
 * Created by dnizard on 21/07/2017.
 */

public class RoundedRectAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
        int width;
        int height;

        float radius = 100;

        if(bMap.getHeight() > bMap.getWidth()*3/4)
        {
            height = Math.round(bMap.getWidth()*3/4);
            width = bMap.getWidth();

        }else{
            width = Math.round(bMap.getHeight()*4/3);
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        return Bitmap.createScaledBitmap(crop, (int)Math.round(Utils.pageWidth*0.9), (int)Math.round(Utils.pageWidth*0.9*3/4), true);
        /*crop.recycle();

        Bitmap result = Bitmap.createBitmap(dstBmp.getWidth(), dstBmp.getHeight(), dstBmp.getConfig());

        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, dstBmp.getWidth(), dstBmp.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(dstBmp, rect, rect, paint);

        dstBmp.recycle();

        return result;*/
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {
        float radius = 100;

        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());
        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bMap.getWidth(), bMap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bMap, rect, rect, paint);

        Bitmap finale = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvasPage = new Canvas(finale);

        canvasPage.drawBitmap(result, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, null);

        return finale;
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images, Bitmap bg)
    {
        float radius = 100;

        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());
        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bMap.getWidth(), bMap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bMap, rect, rect, paint);

        Bitmap finale = Bitmap.createScaledBitmap(bg, Utils.pageWidth, Utils.pageHeight, false);
        Canvas canvasPage = new Canvas(finale);

        canvasPage.drawBitmap(result, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, null);

        return finale;
    }

}
