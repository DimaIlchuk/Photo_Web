package com.photoweb.piiics.model;

import android.util.Log;

import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.model.PriceReferences.APIReference;
import com.photoweb.piiics.utils.PriceReferences;

import java.util.ArrayList;

/**
 * Created by dnizard on 31/08/2017.
 */

public class SummaryCategory {
    private static final String LOG_TAG = "SummaryCategory";

    public String categoryName;
    public String categoryType;
    public int categoryIconRessource;
    public int units;
    public int priceInCts;

    public ArrayList<EditorPic> pics;

    public SummaryCategory(String categoryName, int categoryIconRessource) {
        this.categoryName = categoryName;
        this.categoryIconRessource = categoryIconRessource;
        this.categoryType = PriceReferences.NO_FORMAT;
        this.units = 0;
        this.priceInCts = 0;

        pics = new ArrayList<EditorPic>();
    }

    public SummaryCategory(String categoryName, int categoryIconRessource, String categoryType) {
        this.categoryName = categoryName;
        this.categoryIconRessource = categoryIconRessource;
        this.categoryType = categoryType;
        this.units = 0;
        this.priceInCts = 0;

        pics = new ArrayList<EditorPic>();
    }

    public void addUnits(int units) {
        this.units += units;
    }

    public void addPrice(int priceInCts) {
        this.priceInCts += priceInCts;
    }

    public void addPrice(String price) {
        try {
            this.priceInCts += APIReference.getPriceInCts(price);
        } catch (PriceSecurityException pse) {
            Log.i(LOG_TAG, "PriceSecurityException");
            this.priceInCts += 0;
        }
    }

}
