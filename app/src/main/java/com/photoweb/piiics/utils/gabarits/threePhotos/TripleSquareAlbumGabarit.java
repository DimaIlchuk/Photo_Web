package com.photoweb.piiics.utils.gabarits.threePhotos;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.gabarits.AlbumGabarit;

import java.util.ArrayList;

/**
 * Created by dnizard on 08/08/2017.
 */

public class TripleSquareAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

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
    }

    public Bitmap generateSecond(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int margin = 80;

        int width;
        int height;

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

        return Bitmap.createScaledBitmap(crop, (Utils.pageHeight-2*margin)/2, (Utils.pageHeight-2*margin)/2, true);
    }

    public Bitmap generateThird(Bitmap bMap)
    {
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int margin = 80;

        int width;
        int height;

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

        return Bitmap.createScaledBitmap(crop, (Utils.pageHeight-2*margin)/2, (Utils.pageHeight-2*margin)/2, true);
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {
        int margin = 80;

        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, 0, (Utils.pageHeight - Utils.pageWidth)/2, null);

        bMap = images.get(1);

        canvas.drawBitmap(bMap, margin, margin, null);

        bMap = images.get(2);

        canvas.drawBitmap(bMap, margin, margin + (Utils.pageHeight-2*margin)/2, null);

        return result;
    }

}
