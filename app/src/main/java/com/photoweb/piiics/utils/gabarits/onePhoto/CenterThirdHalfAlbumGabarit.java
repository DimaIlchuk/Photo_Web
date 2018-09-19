package com.photoweb.piiics.utils.gabarits.onePhoto;

import android.graphics.Bitmap;

import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.gabarits.AlbumGabarit;

/**
 * Created by dnizard on 21/07/2017.
 */

public class CenterThirdHalfAlbumGabarit extends AlbumGabarit {

    public Bitmap generateFirst(Bitmap bMap){
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

        return Bitmap.createScaledBitmap(crop, (int)Math.round(Utils.pageWidth*0.9), (int)Math.round(Utils.pageWidth*0.9*2/3), true);
    }

}
