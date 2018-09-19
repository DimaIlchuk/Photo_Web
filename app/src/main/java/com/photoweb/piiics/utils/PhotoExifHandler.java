package com.photoweb.piiics.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.media.ExifInterface;

import java.io.IOException;

public class PhotoExifHandler {

    public static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            return orientation;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static Bitmap rotateViaMatrix(Bitmap original, int orientation) {
        /*Matrix matrix=new Matrix();

        matrix.setRotate(degreesForRotation(orientation));

        return(Bitmap.createBitmap(original, 0, 0, original.getWidth(),
                original.getHeight(), matrix, true));*/

        return original;
    }

    private static int degreesForRotation(int orientation) {
        int result;

        switch (orientation) {
            case 8:
                result=270;
                break;

            case 3:
                result=180;
                break;

            default:
                result=90;
        }

        return(result);
    }

}
