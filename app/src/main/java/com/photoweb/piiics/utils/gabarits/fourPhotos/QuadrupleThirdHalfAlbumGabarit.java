package com.photoweb.piiics.utils.gabarits.fourPhotos;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.gabarits.AlbumGabarit;

import java.util.ArrayList;

/**
 * Created by dnizard on 08/08/2017.
 */

public class QuadrupleThirdHalfAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

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

        return Bitmap.createScaledBitmap(crop, Utils.pageWidth, Utils.pageWidth*2/3, true);
    }

    public Bitmap generateSecond(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

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

        return Bitmap.createScaledBitmap(crop, (Utils.pageWidth-20)/3, (Utils.pageWidth-20)*2/9, true);
    }

    public Bitmap generateThird(Bitmap bMap)
    {
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

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

        return Bitmap.createScaledBitmap(crop, (Utils.pageWidth-20)/3, (Utils.pageWidth-20)*2/9, true);
    }

    public Bitmap generateFourth(Bitmap bMap)
    {
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

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

        return Bitmap.createScaledBitmap(crop, (Utils.pageWidth-20)/3, (Utils.pageWidth-20)*2/9, true);
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {
        int margin = 80;

        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, 0, 0, null);

        bMap = images.get(1);

        canvas.drawBitmap(bMap, 0, Utils.pageWidth*3/4 + 20, null);

        bMap = images.get(2);

        canvas.drawBitmap(bMap, (Utils.pageWidth-20)/3 + 10, Utils.pageWidth*3/4 + 20, null);

        bMap = images.get(3);

        canvas.drawBitmap(bMap, 2*(Utils.pageWidth-20)/3 + 20, Utils.pageWidth*3/4 + 20, null);

        return result;
    }

}
