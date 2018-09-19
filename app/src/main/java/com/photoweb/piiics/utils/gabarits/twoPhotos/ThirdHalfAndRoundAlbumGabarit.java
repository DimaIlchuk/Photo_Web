package com.photoweb.piiics.utils.gabarits.twoPhotos;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.gabarits.AlbumGabarit;

import java.util.ArrayList;

/**
 * Created by dnizard on 07/08/2017.
 */

public class ThirdHalfAndRoundAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        int margin = 80;

        if(bMap.getHeight() > bMap.getWidth()*2/3)
        {
            height = Math.round(bMap.getWidth()*2/3);
            width = bMap.getWidth();

        }else{
            width = Math.round(bMap.getHeight()*3/2);
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        return Bitmap.createScaledBitmap(crop, Utils.pageWidth - 2*margin, (Utils.pageWidth - 2*margin)*2/3, true);
    }

    public Bitmap generateSecond(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        int margin = 80;

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

        Bitmap resized = Bitmap.createScaledBitmap(crop, (Utils.pageWidth-2*margin)/3, (Utils.pageWidth-2*margin)/3, true);

        crop.recycle();
        //bMap.recycle();

        Bitmap result = Bitmap.createBitmap(resized.getWidth(), resized.getHeight(), resized.getConfig());

        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawCircle(Math.round((Utils.pageWidth-2*margin)/6), Math.round((Utils.pageWidth-2*margin)/6), (Utils.pageWidth-2*margin)/6, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resized, 0, 0, paint);

        resized.recycle();

        return result;
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {
        int margin = 80;

        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, margin, margin, null);

        bMap = images.get(1);

        canvas.drawBitmap(bMap, margin + (Utils.pageWidth - 2*margin)/3, margin + (Utils.pageWidth - 2*margin)*2/3 - (Utils.pageWidth - 2*margin)/6, null);

        return result;
    }

}
