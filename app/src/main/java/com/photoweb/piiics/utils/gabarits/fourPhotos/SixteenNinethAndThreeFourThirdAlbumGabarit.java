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

public class SixteenNinethAndThreeFourThirdAlbumGabarit extends AlbumGabarit {

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

        int margin = 80;

        if(bMap.getHeight() > bMap.getWidth()*3/4)
        {
            width = bMap.getWidth();
            height = bMap.getWidth()*3/4;

        }else{
            height = bMap.getHeight();
            width = bMap.getHeight()*4/3;

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        return Bitmap.createScaledBitmap(crop, (Utils.pageHeight - 4*margin)*4/9, (Utils.pageHeight - 4*margin)/3, true);
    }

    public Bitmap generateThird(Bitmap bMap)
    {
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        int margin = 80;

        if(bMap.getHeight() > bMap.getWidth()*3/4)
        {
            width = bMap.getWidth();
            height = bMap.getWidth()*3/4;

        }else{
            height = bMap.getHeight();
            width = bMap.getHeight()*4/3;

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        return Bitmap.createScaledBitmap(crop, (Utils.pageHeight - 4*margin)*4/9, (Utils.pageHeight - 4*margin)/3, true);
    }

    public Bitmap generateFourth(Bitmap bMap)
    {
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        int margin = 80;

        if(bMap.getHeight() > bMap.getWidth()*3/4)
        {
            width = bMap.getWidth();
            height = bMap.getWidth()*3/4;

        }else{
            height = bMap.getHeight();
            width = bMap.getHeight()*4/3;

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        return Bitmap.createScaledBitmap(crop, (Utils.pageHeight - 4*margin)*4/9, (Utils.pageHeight - 4*margin)/3, true);
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {

        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, 0, 0, null);

        bMap = images.get(1);

        canvas.drawBitmap(bMap, Utils.pageHeight*9/16 + 80, 80, null);

        bMap = images.get(2);

        canvas.drawBitmap(bMap, Utils.pageHeight*9/16 + 80, (Utils.pageHeight - 4*80)/3 + 2*80, null);

        bMap = images.get(3);

        canvas.drawBitmap(bMap, Utils.pageHeight*9/16 + 80, 2*(Utils.pageHeight - 4*80)/3 + 3*80, null);

        return result;
    }

}
