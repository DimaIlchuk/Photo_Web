package com.photoweb.piiics.utils.gabarits.onePhoto;

import android.graphics.Bitmap;

import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.gabarits.AlbumGabarit;

/**
 * Created by dnizard on 21/07/2017.
 */

public class FullScreenAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap)
    {
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
}
