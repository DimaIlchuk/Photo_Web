package com.photoweb.piiics.utils.gabarits.twoPhotos;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.gabarits.AlbumGabarit;

import java.util.ArrayList;

/**
 * Created by dnizard on 07/08/2017.
 */

public class BigAndSmallFourTierAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        if(bMap.getWidth() > bMap.getHeight()*3/4)
        {
            height = bMap.getHeight();
            width = bMap.getHeight()*3/4;

        }else{
            width = bMap.getWidth();
            height = bMap.getWidth()*4/3;

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        return Bitmap.createScaledBitmap(crop, Utils.pageHeight*3/4, Utils.pageHeight, true);
    }

    public Bitmap generateSecond(Bitmap bMap){
        Log.d("ALBUM", "doing generateFirtst of MarginBottom");

        int width;
        int height;

        if(bMap.getWidth() > bMap.getHeight()*3/4)
        {
            height = bMap.getHeight();
            width = bMap.getHeight()*3/4;

        }else{
            width = bMap.getWidth();
            height = bMap.getWidth()*4/3;

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        return Bitmap.createScaledBitmap(crop, Utils.pageHeight/4, Utils.pageHeight/3, true);
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {
        int margin = 80;

        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, 0, 0, null);

        bMap = images.get(1);

        canvas.drawBitmap(bMap, Utils.pageHeight*3/4-Utils.pageHeight/8, Utils.pageHeight*2/3 - margin, null);

        return result;
    }

}
