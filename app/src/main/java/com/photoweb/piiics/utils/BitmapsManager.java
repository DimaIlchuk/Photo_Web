package com.photoweb.piiics.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by thomas on 28/04/2017.
 */

public class BitmapsManager {

    public static String saveBitmap(String sourceFullPath, File dirBitmapsSaved, int duplicatedNumber) {
        File picSourceFile = new File(sourceFullPath);
        String destFileName = picSourceFile.getName();
      //  Log.i("BitmapsManager", "duplicatedNumber : " + String.valueOf(duplicatedNumber));
       // Log.i("BitmapsManager", "destFileName : " + destFileName);
        if (duplicatedNumber > 1) {
            int suffixIndex = destFileName.lastIndexOf('.');
            String tmpPrefix = destFileName.substring(0, suffixIndex);
            String tmpSuffix = destFileName.substring(suffixIndex, destFileName.length());
         //   Log.i("BitmapsManager", "tmpPrefix : " + tmpPrefix);
           // Log.i("BitmapsManager", "tmpSuffix : " + tmpSuffix);
            tmpPrefix += String.valueOf(duplicatedNumber);
            destFileName = tmpPrefix + tmpSuffix;
            //Log.i("BitmapsManager", "destFileName : " + destFileName);
        }
        File bitmapDestFile = new File(dirBitmapsSaved, destFileName);
        try {
            copyFile(picSourceFile, bitmapDestFile);
         //   Log.i("BitmapsManager", "Copy Good");
            return bitmapDestFile.getAbsolutePath();
        } catch (IOException e) {
          //  Toast.makeText(this, "copy file fail", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;
    }

    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

}
