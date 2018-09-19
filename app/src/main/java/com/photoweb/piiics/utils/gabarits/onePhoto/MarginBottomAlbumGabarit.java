package com.photoweb.piiics.utils.gabarits.onePhoto;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.gabarits.AlbumGabarit;

import java.util.ArrayList;

/**
 * Created by dnizard on 21/07/2017.
 */

public class MarginBottomAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
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

        return Bitmap.createScaledBitmap(crop, Utils.pageWidth, Utils.pageWidth*3/4, true);
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {
        Log.d("ALBUM", "doing generateAlbumPage of MarginBottom");
        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, 0, 0, null);

        return result;
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images, Bitmap bg)
    {
        Log.d("ALBUM", "doing generateAlbumPage of MarginBottom");
        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createScaledBitmap(bg, Utils.pageWidth, Utils.pageHeight, false);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, 0, 0, null);

        return result;
    }
}
