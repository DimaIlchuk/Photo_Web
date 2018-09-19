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

public class LeftThirdHalfAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap) {
        int width;
        int height;

        if(bMap.getWidth() > bMap.getHeight()*2/3)
        {
            width = Math.round(bMap.getHeight()*2/3);
            height = bMap.getHeight();

        }else{
            height = Math.round(bMap.getWidth()*3/2);
            width = bMap.getWidth();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        return Bitmap.createScaledBitmap(crop, (int)Math.round(Utils.pageHeight*2/3), Utils.pageHeight, true);
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
