package com.photoweb.piiics.utils;

import android.util.Log;

import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PriceReferences.FormatReference;

import java.util.ArrayList;

/**
 * Created by thomas on 28/04/2017.
 */

public abstract class Promotions {
    private static final String LOG_TAG = "Promotions";

    private static int userFreeStandardFormats = 0;
    private static int userFreeStandardFormatsTmp = 0;

    public static void initUserFreeStandardFormats(int number) {
        userFreeStandardFormats = number;
        userFreeStandardFormatsTmp = number;
    }

    public static int getUserFreeStandardFormats() {
        return userFreeStandardFormats;
    }

    public static int checkFreeStandardFormatPromotion(int priceInCts, ArrayList<EditorPic> pics) {
        userFreeStandardFormats = (UserInfo.getInt("id") == 0) ? 50 : UserInfo.getInt("print_available") + UserInfo.getInt("print_bonus");

        userFreeStandardFormatsTmp = userFreeStandardFormats;
        if (userFreeStandardFormatsTmp == 0) {
            return priceInCts;
        }
        int promotion = 0;
        int standardFormatPrice;

        try {
            standardFormatPrice = PriceReferences.findFormatReferenceByName(PriceReferences.STANDARD_FORMAT).getCurPrice();
        } catch (NullPointerException | PriceSecurityException e) {
            e.printStackTrace();
            return priceInCts;
        }

        for (EditorPic pic : pics) {
            FormatReference fm = new FormatReference();

            if(pic.getFormatReference() == null){
                fm = PriceReferences.getDefaultformat();
            }else{
                fm = pic.getFormatReference();
            }

            if (fm.getName().equals(PriceReferences.STANDARD_FORMAT)) {
                if (!pic.isDuplicated()) {
                    if (userFreeStandardFormatsTmp > 0) {
                        promotion += standardFormatPrice;
                        userFreeStandardFormatsTmp--;
                    }
                }
            }
        }
        int newPrice = priceInCts - promotion;
        if (newPrice < 0) {
            Log.e(LOG_TAG, "FreeStandardPromotion : calculation ERROR");
            return priceInCts;
        }
        return newPrice;
    }

    public static int checkFreePagePromotion(int priceInCts, ArrayList<EditorPic> pics)
    {
        try {
            return Math.max(0, pics.size() - 20)*pics.get(0).getFormatReference().getCurPrice();
        } catch (PriceSecurityException e) {
            e.printStackTrace();
            return Math.max(0, pics.size() - 20)*29;
        }
    }

    /*
        Return the number of free standard formats available for the given pics
     */
    public static int getFreeStandardFormatsInCommand(ArrayList<EditorPic> pics){
        userFreeStandardFormatsTmp = userFreeStandardFormats;
        if (userFreeStandardFormatsTmp == 0) {
            return 0;
        }
        int freeStandardFormatsInCommand = 0;

        for (EditorPic pic : pics) {
            if (pic.getFormatReference().getName().equals(PriceReferences.STANDARD_FORMAT)) {
                if (!pic.isDuplicated()) {
                    if (userFreeStandardFormatsTmp > 0) {
                        freeStandardFormatsInCommand++;
                        userFreeStandardFormatsTmp--;
                    }
                }
            }
        }
        return freeStandardFormatsInCommand;
    }

    /*
    public static boolean isFreePicsPromotionAvailable(String picFormatID, String standardFormatID, int userFreePhotos, boolean duplicated) {
        if (picFormatID.equals(standardFormatID) && (userFreePhotos > 0 && !duplicated)) {
            return true;
        }
        return false;
    }

    public static int applyFreePicsPromotion(int picPrice, String picFormatID, ModificationsManager modificationsManager, boolean duplicated) {

        String standardFormatID = modificationsManager.getFormatStandard().getModificationID();
        int standardFormatPrice = modificationsManager.getFormatStandard().getModificationPrice();
        int userFreePhotos = EditorActivity.getUserFreePhotos();

        if (isFreePicsPromotionAvailable(picFormatID, standardFormatID, userFreePhotos, duplicated)) {
            EditorActivity.setUserFreePhotos(userFreePhotos - 1);
            picPrice -= standardFormatPrice;
            return picPrice;
        }
        return picPrice;
    }

    ///
    public static int getPriceWithPromotions(EditorPic editorPic, Modification modification, ModificationsManager modificationsManager) {
        int userFreePhotos = EditorActivity.getUserFreePhotos();
        if (isFreePicsPromotionAvailable(modification.getModificationID(), modificationsManager.getFormatStandard().getModificationID(), userFreePhotos, editorPic.isDuplicated())) {
            return 0;///
        } else {
            return modification.getModificationPrice();
        }
    }*/
}
