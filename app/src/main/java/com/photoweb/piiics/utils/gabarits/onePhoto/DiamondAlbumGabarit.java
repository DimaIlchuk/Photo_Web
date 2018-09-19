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
 * Created by dnizard on 22/07/2017.
 */

public class DiamondAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap)
    {
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

        return Bitmap.createScaledBitmap(crop, Utils.pageWidth, Utils.pageWidth, true);

        /*crop.recycle();
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
        final Rect rect = new Rect(0, 0, result.getWidth(), result.getHeight());
        final RectF rectF = new RectF(new Rect(result.getWidth() - (int)Math.round(result.getWidth()/Math.sqrt(2)), result.getHeight() - (int)Math.round(result.getHeight()/Math.sqrt(2)), (int)Math.round(result.getWidth()/Math.sqrt(2)), (int)Math.round(result.getHeight()/Math.sqrt(2))));

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.save();
        canvas.translate(bMap.getWidth()/2, bMap.getHeight()/2);
        canvas.rotate(45);
        canvas.translate(-bMap.getWidth()/2, -bMap.getHeight()/2);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        canvas.restore();

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
        final Rect rect = new Rect(0, 0, result.getWidth(), result.getHeight());
        final RectF rectF = new RectF(new Rect(result.getWidth() - (int)Math.round(result.getWidth()/Math.sqrt(2)), result.getHeight() - (int)Math.round(result.getHeight()/Math.sqrt(2)), (int)Math.round(result.getWidth()/Math.sqrt(2)), (int)Math.round(result.getHeight()/Math.sqrt(2))));

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.save();
        canvas.translate(bMap.getWidth()/2, bMap.getHeight()/2);
        canvas.rotate(45);
        canvas.translate(-bMap.getWidth()/2, -bMap.getHeight()/2);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        canvas.restore();

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bMap, rect, rect, paint);

        Bitmap finale = Bitmap.createScaledBitmap(bg, Utils.pageWidth, Utils.pageHeight, false);
        Canvas canvasPage = new Canvas(finale);

        canvasPage.drawBitmap(result, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, null);

        return finale;
    }
}
