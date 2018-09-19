package com.photoweb.piiics.utils.gabarits.onePhoto;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.gabarits.AlbumGabarit;

import java.util.ArrayList;

/**
 * Created by dnizard on 21/07/2017.
 */

public class RotateAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
        int width;
        int height;

        if(bMap.getHeight() > bMap.getWidth()*9/16)
        {
            height = Math.round(bMap.getWidth()*9/16);
            width = bMap.getWidth();

        }else{
            width = Math.round(bMap.getHeight()*16/9);
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        return Bitmap.createScaledBitmap(crop, Utils.pageWidth, (int)Math.round(Utils.pageWidth*9/16), true);
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {
        Log.d("ALBUM", "doing generateAlbumPage of MarginBottom");
        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Matrix matrix = new Matrix();
        matrix.setTranslate((Utils.pageWidth - bMap.getWidth()) / 2, (Utils.pageHeight - bMap.getHeight()) / 2);
        matrix.postRotate(-10, bMap.getWidth() / 2, bMap.getHeight() / 2);

        canvas.drawBitmap(bMap, matrix, new Paint());

        return result;
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images, Bitmap bg)
    {
        Log.d("ALBUM", "doing generateAlbumPage of MarginBottom");
        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createScaledBitmap(bg, Utils.pageWidth, Utils.pageHeight, false);
        Canvas canvas = new Canvas(result);

        Matrix matrix = new Matrix();
        matrix.setTranslate((Utils.pageWidth - bMap.getWidth()) / 2, (Utils.pageHeight - bMap.getHeight()) / 2);
        matrix.postRotate(-10, bMap.getWidth() / 2, bMap.getHeight() / 2);

        canvas.drawBitmap(bMap, matrix, new Paint());

        return result;
    }

}
