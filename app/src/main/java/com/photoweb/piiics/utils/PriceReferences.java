package com.photoweb.piiics.utils;

import android.content.Context;
import android.util.Log;

import com.photoweb.piiics.model.PriceReferences.BackgroundReference;
import com.photoweb.piiics.model.PriceReferences.BookReference;
import com.photoweb.piiics.model.PriceReferences.FormatReference;
import com.photoweb.piiics.model.PriceReferences.StickerCategory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by thomas on 07/08/2017.
 */

public abstract class PriceReferences {
    private static final String LOG_TAG = "PriceReferences";

    public static final String STICKERS = "Stickers";
    public static final String TEXTS = "Texts";
    public static final String BACKGROUNDS = "APP";

    public static final String NO_FORMAT = "nope";
    public static final String STANDARD_FORMAT = "standard";
    public static final String SQUARE_FORMAT = "square";
    public static final String PANORAMIC_FORMAT = "panoramic";
    public static final String PAGE_FORMAT = "page";

    private static boolean backgroundReferencesDL = false;
    private static boolean backgroundFilesDL = false;

    private static boolean stickerReferencesDL = false;
    private static boolean stickerFilesDL = false;

    private static ArrayList<BackgroundReference> backgrounds = null;
    private static ArrayList<FormatReference> formats = null;
    private static ArrayList<StickerCategory> stickerCategories = null;

    private static ArrayList<BookReference> bookReferences = null;

    private static String DEFAULT_BACKGROUND_NAME = "Blanc";

    private static FormatReference defFormat = new FormatReference(1, "standard", "0.15", "0.19");
    //private static BackgroundReference defaultBackground = null;

//    private static FormatReference defaultformat = null;


    //---getters and setters------------------------------------------------------------------------

    public static ArrayList<BackgroundReference> getBackgrounds() {
        return backgrounds;
    }

    public static void setBackgrounds(ArrayList<BackgroundReference> backgrounds) {

        Collections.sort(backgrounds, new Comparator<BackgroundReference>() {
            @Override
            public int compare(BackgroundReference t0, BackgroundReference t1) {
                return t0.getIndex() - t1.getIndex();
            }

        });

        PriceReferences.backgrounds = backgrounds;
    }

    public static ArrayList<FormatReference> getFormats() {
        return formats;
    }

    public static void setFormats(ArrayList<FormatReference> formats) {
        PriceReferences.formats = formats;
    }

    public static ArrayList<StickerCategory> getStickerCategories() {
        return stickerCategories;
    }

    public static void setStickerCategories(ArrayList<StickerCategory> stickerCategories) {
        ArrayList<StickerCategory> tmp = new ArrayList<StickerCategory>(stickerCategories.size());

        for (StickerCategory cat : stickerCategories) {
            if (cat.getIndex() > 0) {
                tmp.add(cat);
            }
        }

        stickerCategories.clear();
        stickerCategories.addAll(tmp);

        Collections.sort(stickerCategories, new Comparator<StickerCategory>() {
            @Override
            public int compare(StickerCategory t0, StickerCategory t1) {
                return t0.getIndex() - t1.getIndex();
            }
        });
        PriceReferences.stickerCategories = stickerCategories;
    }

    public static ArrayList<BookReference> getBookReferences() {
        return bookReferences;
    }

    public static void setBookReferences(ArrayList<BookReference> bookReferences) {
        PriceReferences.bookReferences = bookReferences;
    }

    public static BackgroundReference getDefaultBackground() {
        //return defaultBackground;
        return findBackgroundReferenceByName(DEFAULT_BACKGROUND_NAME);
    }

    public static FormatReference getDefaultformat() {
        //return defaultformat;
        return findFormatReferenceByName(STANDARD_FORMAT);
    }

    public static void setBackgroundReferencesDL(boolean backgroundReferencesDL) {
        PriceReferences.backgroundReferencesDL = backgroundReferencesDL;
    }

    public static void setBackgroundFilesDL(boolean backgroundFilesDL) {
        PriceReferences.backgroundFilesDL = backgroundFilesDL;
    }

    public static void setStickerReferencesDL(boolean stickerReferencesDL) {
        PriceReferences.stickerReferencesDL = stickerReferencesDL;
    }

    public static void setStickerFilesDL(boolean stickerFilesDL) {
        PriceReferences.stickerFilesDL = stickerFilesDL;
    }

    public static boolean areStickerDatasDL() {
        return stickerFilesDL && stickerReferencesDL;
    }

    public static boolean areBackgroundDatasDL() {
        return backgroundFilesDL && backgroundReferencesDL;
    }

    //----------------------------------------------------------------------------------------------

    public static void setStikersFiles() {
        File mainDir = getApplicationContext().getDir(PriceReferences.STICKERS, Context.MODE_PRIVATE);
        final File[] stickerCategoryDirs = mainDir.listFiles();

        for (StickerCategory stickerCategory : PriceReferences.getStickerCategories()) {
            Log.d(LOG_TAG, "sticker " + stickerCategory.getFolder());
            for (File stickerCategoryDir : stickerCategoryDirs) {
                Log.d(LOG_TAG, "stickerDir " + stickerCategoryDir.getName());
                if (stickerCategory.getFolder().equals(stickerCategoryDir.getName())) {
                    for (File stickerFile : stickerCategoryDir.listFiles()) {
                        Log.d(LOG_TAG, "stickerFile " + stickerFile.getName());
                        if (stickerCategory.setStickerFile(stickerFile) == -1) {
                            Log.i(LOG_TAG, "setStickerFile ERROR, stickerFile name : " + stickerFile.getName());
                        }
                    }
                }
            }
        }
    }


    public static void setBackgroundsFiles() {
        File mainDir = getApplicationContext().getDir(PriceReferences.BACKGROUNDS, Context.MODE_PRIVATE);
        final File[] backgroundDirs = mainDir.listFiles();
        File[] backgroundFiles = null;

        for (File backgroundDir : backgroundDirs) {
            if (backgroundDir.getName().equals("Thumbnails")) {
                backgroundFiles = backgroundDir.listFiles();
            }
        }

        for (BackgroundReference backgroundReference : PriceReferences.getBackgrounds()) {
            for (File backgroundFile : backgroundFiles) {
                String backgroundRefNameWithFileSuffix = backgroundReference.getName() + " - Bloc.jpg";//todo : corriger ca
                Log.i(LOG_TAG, "\nbackgroundRef name : " + backgroundReference.getName());
                Log.i(LOG_TAG, "\nbackgroundRef nameWithFileSuffix : " + backgroundRefNameWithFileSuffix);
                Log.i(LOG_TAG, "\nbackground FILE name : " + backgroundFile.getName());

                if (backgroundRefNameWithFileSuffix.equals(backgroundFile.getName())) {
                    backgroundReference.setBackgroundFile(backgroundFile);
                    break;
                }
            }
        }

        Log.i(LOG_TAG, "------------------------tests BACKGROUND START-----------------------");
        for (BackgroundReference backgroundReference : PriceReferences.getBackgrounds()) {
            Log.i(LOG_TAG, "\nbackgroundRef name : " + backgroundReference.getName());
            //Log.i(LOG_TAG, "\nbackground FILE name : " + backgroundReference.getBackgroundFile().getName());
        }
        Log.i(LOG_TAG, "------------------------tests BACKGROUND END-----------------------");
    }

    /*
    public static boolean initDefaultBackground() {
        defaultBackground = findBackgroundReferenceByName(DEFAULT_BACKGROUND_NAME);
        if (defaultBackground == null) {
            return false;
        } else {
            return true;
        }
    }*/

    public static BackgroundReference findBackgroundReferenceByName(String name) {
        if(backgrounds != null){
            for (BackgroundReference backgroundReference : backgrounds) {
                if (name.equals(backgroundReference.getName())) {
                    return backgroundReference;
                }
            }
        }

        return null;
    }

   /* public static boolean initDefaultFormat() {
        defaultformat = findFormatReferenceByName(STANDARD_FORMAT);
        if (defaultformat == null) {
            return false;
        } else {
            return true;
        }
    }*/

    public static FormatReference findFormatReferenceByName(String name) {
        if(formats != null){
            for (FormatReference formatReference : formats) {
                if (name.equals(formatReference.getName())) {
                    return formatReference;
                }
            }
        }else{
            return defFormat;
        }


        return null;
    }

    public static BookReference findBookReferenceByName(String name) {
        if(bookReferences != null){
            for (BookReference bookReference : bookReferences) {
                if (name.equals(bookReference.getName())) {
                    return bookReference;
                }
            }
        }

        return null;
    }
}
