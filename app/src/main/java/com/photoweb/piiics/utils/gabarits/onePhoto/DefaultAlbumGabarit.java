package com.photoweb.piiics.utils.gabarits.onePhoto;

import android.graphics.Bitmap;
import android.util.Log;

import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.gabarits.AlbumGabarit;

/**
 * Created by dnizard on 21/07/2017.
 */

public class DefaultAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
        int width;
        int height;

        Log.d("PRESET", "values : " + bMap.getWidth() + " " + bMap.getHeight());

        if(bMap.getWidth() < bMap.getHeight())
        {
            height = (int)Math.round(Utils.pageHeight*0.9);
            width = Math.round(bMap.getWidth() * height / bMap.getHeight());

            if(width > Utils.pageWidth){
                width = (int)Math.round(Utils.pageWidth*0.9);
                height = Math.round(bMap.getHeight() * width / bMap.getWidth());
            }
        }else if(bMap.getWidth() > bMap.getHeight()){
            width = (int)Math.round(Utils.pageWidth*0.9);
            height = Math.round(bMap.getHeight() * width / bMap.getWidth());

            if(height > Utils.pageHeight){
                height = (int)Math.round(Utils.pageHeight*0.9);
                width = Math.round(bMap.getWidth() * height / bMap.getHeight());
            }
        }else{
            width = (int)Math.round(Utils.pageHeight*0.9);
            height = (int)Math.round(Utils.pageHeight*0.9);
        }

        Log.d("DEFAULT", "values : " + width + " " + height);

        return Bitmap.createScaledBitmap(bMap, width, height, true);
    }

}
