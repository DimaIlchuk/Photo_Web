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

public class SixteenNinethAndTwoFourAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        if(bMap.getWidth() > bMap.getHeight()*9/16)
        {
            height = bMap.getHeight();
            width = bMap.getHeight()*9/16;

        }else{
            width = bMap.getWidth();
            height = bMap.getWidth()*16/9;

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        return Bitmap.createScaledBitmap(crop, Utils.pageHeight*9/16, Utils.pageHeight, true);
    }

    public Bitmap generateSecond(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

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

        return Bitmap.createScaledBitmap(crop, (Utils.pageHeight - 2*80 - 20)*2/3, (Utils.pageHeight - 2*80 - 20)/2, true);
    }

    public Bitmap generateThird(Bitmap bMap)
    {
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

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

        return Bitmap.createScaledBitmap(crop, (Utils.pageHeight - 2*80 - 20)*2/3, (Utils.pageHeight - 2*80 - 20)/2, true);
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {

        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, 0, 0, null);

        bMap = images.get(1);

        canvas.drawBitmap(bMap, (Utils.pageHeight)*9/16 - 80, 80, null);

        bMap = images.get(2);

        canvas.drawBitmap(bMap, (Utils.pageHeight)*9/16 - 80, 100 + (Utils.pageHeight - 2*80 - 20)/2, null);

        return result;
    }

}
