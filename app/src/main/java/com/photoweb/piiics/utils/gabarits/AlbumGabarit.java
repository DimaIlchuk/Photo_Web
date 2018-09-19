package com.photoweb.piiics.utils.gabarits;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.Log;

import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PicAlbum;
import com.photoweb.piiics.utils.PhotoExifHandler;
import com.photoweb.piiics.utils.Utils;

import java.util.ArrayList;

/**
 * Created by dnizard on 21/07/2017.
 */

public class AlbumGabarit {

    public Bitmap applyGabarit(EditorPic pic)
    {
        ArrayList<Bitmap> finals = new ArrayList<>();

        for (PicAlbum subpic:pic.picAlbums) {
            subpic.saveCropImage(generateCrop(PhotoExifHandler.rotateViaMatrix(BitmapFactory.decodeFile(subpic.getOriginalBitmapPath()), PhotoExifHandler.getImageOrientation(subpic.getOriginalBitmapPath())), subpic.index+1));
            finals.add(BitmapFactory.decodeFile(subpic.getFinalBitmapPath()));
        }

        if(pic.getFinalBitmapPath().endsWith(".jpg"))
            return generateAlbumPage(finals, BitmapFactory.decodeFile(pic.getBackgroundReference().getBackgroundFile().getAbsolutePath()));

        return generateAlbumPage(finals);
    }

    public Bitmap drawOnPage(EditorPic pic)
    {
        ArrayList<Bitmap> finals = new ArrayList<>();

        for (PicAlbum subpic:pic.picAlbums) {
            finals.add(BitmapFactory.decodeFile(subpic.getFinalBitmapPath()));
        }

        if(pic.getFinalBitmapPath().endsWith(".jpg"))
            return generateAlbumPage(finals, BitmapFactory.decodeFile(pic.getBackgroundReference().getBackgroundFile().getAbsolutePath()));

        return generateAlbumPage(finals);
    }

    public Bitmap applyThumbnailGabarit(ArrayList<Bitmap> images)
    {
        ArrayList<Bitmap> finals = new ArrayList<>();

        int i = 1;
        for (Bitmap image:images) {
            Log.d("ALBUM", "init image at index " + i);
            finals.add(generateCrop(image, i));
            i++;
        }

        return generateAlbumPage(finals);
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images)
    {
        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, (result.getWidth() - bMap.getWidth())/2, (result.getHeight() - bMap.getHeight())/2, null);

        return result;
    }

    public Bitmap generateAlbumPage(ArrayList<Bitmap> images, Bitmap bg)
    {
        Bitmap bMap = images.get(0);

        Bitmap result = Bitmap.createScaledBitmap(bg, Utils.pageWidth, Utils.pageHeight, false);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bMap, (result.getWidth() - bMap.getWidth())/2, (result.getHeight() - bMap.getHeight())/2, null);

        return result;
    }

    public Bitmap generateCrop(Bitmap image, int index){
        switch (index){
            case 1:
                return generateFirst(image);
            case 2:
                return generateSecond(image);
            case 3:
                return generateThird(image);
            case 4:
                return generateFourth(image);
            default:
                return null;
        }
    }

    public Bitmap generateFirst(Bitmap bMap)
    {
        return bMap;
    }

    public Bitmap generateSecond(Bitmap bMap)
    {
        return bMap;
    }

    public Bitmap generateThird(Bitmap bMap)
    {
        return bMap;
    }

    public Bitmap generateFourth(Bitmap bMap)
    {
        return bMap;
    }

    public int isInside(PointF point){
        return 1;
    }

}
