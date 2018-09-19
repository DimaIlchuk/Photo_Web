package com.photoweb.piiics.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by thomas on 22/08/2017.
 */

public abstract class DraftsUtils {

    public static final String PRINT_DIRECTORY = "PRINT";
    public static final String ALBUM_DIRECTORY = "ALBUM";

    public static final String ORG_SUBDIRECTORY = "ORG";
    public static final String CROP_SUBDIRECTORY = "CROP";
    public static final String FINAL_SUBDIRECTORY = "FINAL";

    private static String printDirectoryPath;
    private static String albumDirectoryPath;
    private static String originDirectoryPath;

    //getters and setters---------------------------------------------------------------------------

    public static String getPrintDirectoryPath() {
        return printDirectoryPath;
    }

    public static String getAlbumDirectoryPath() {
        return albumDirectoryPath;
    }

    public static String getOriginDirectoryPath() {
        return originDirectoryPath;
    }


    //----------------------------------------------------------------------------------------------

    /*
        Créé les repertoires PRINT / ALBUM s'ils n'existent pas
    */
    public static void createDraftDirectories(Context context) {
        printDirectoryPath = DraftsUtils.getPrivateDirectoryPath(context, PRINT_DIRECTORY);
        albumDirectoryPath = DraftsUtils.getPrivateDirectoryPath(context, ALBUM_DIRECTORY);
        originDirectoryPath = DraftsUtils.getPrivateDirectoryPath(context, ORG_SUBDIRECTORY);
    }

    public static String getPrivateDirectoryPath(Context context, String dirName) {

        File storageDir = new File(context.getFilesDir(), dirName);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.d(dirName, "failed to create directory");
                return null;
            }
        }
        return storageDir.getAbsolutePath();
    }


    public static String getPrivateDirectoryPath(String parentDirName, String dirName) {

        File storageDir = new File(parentDirName, dirName);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.d(dirName, "failed to create directory");
                return null;
            }
        }
        return storageDir.getAbsolutePath();
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}
