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

public class TripleSixteenNinethAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        int margin = 10;

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

        return Bitmap.createScaledBitmap(crop, (Utils.pageHeight-4*margin)*16/27, (Utils.pageHeight-4*margin)/3, true);
    }

    public Bitmap generateSecond(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        int margin = 10;

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

        return Bitmap.createScaledBitmap(crop, (Utils.pageHeight-4*margin)*16/27, (Utils.pageHeight-4*margin)/3, true);
    }

    public Bitmap generateThird(Bitmap bMap)
    {
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        int margin = 10;

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

        return Bitmap.createScaledBitmap(crop, (Utils.pageHeight-4*margin)*16/27, (Utils.pageHeight-4*margin)/3, true);
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {
        int margin = 10;

        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, margin, margin, null);

        bMap = images.get(1);

        canvas.drawBitmap(bMap, margin, (Utils.pageHeight-4*margin)/3 + 2*margin, null);

        bMap = images.get(2);

        canvas.drawBitmap(bMap, margin, (Utils.pageHeight-4*margin)*2/3 + 3*margin, null);

        return result;
    }

}
