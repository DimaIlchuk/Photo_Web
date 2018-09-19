package com.photoweb.piiics.model.PriceReferences;

import android.util.Log;

import com.photoweb.piiics.PriceSecurityException;

import java.io.Serializable;

/**
 * Created by thomas on 08/08/2017.
 */

public abstract class APIReference implements Serializable {
    private static final String LOG_TAG = "APIReference";

    public static int getPriceInCts(String priceStr) throws PriceSecurityException {
        int priceInCts = 0;

        String[] strings = priceStr.split("[.]");

        Log.i(LOG_TAG, "PRICE STR : " + priceStr);
        for (String str : strings) {
            Log.i(LOG_TAG, "String du SPLIT : " + str);
        }
        if (strings.length != 2) {
            throw  new PriceSecurityException();
        }

        int euros = Integer.valueOf(strings[0]);
        int cents = Integer.valueOf(strings[1]);

        if (strings[1].length() == 1) {
            cents *= 10;
        }

        Log.i(LOG_TAG, "EUROS : " + String.valueOf(euros));
        Log.i(LOG_TAG, "CENTS : " + String.valueOf(cents));

        if (euros < 0 || cents < 0) {
            throw  new PriceSecurityException();
        }

        priceInCts = euros * 100;
        priceInCts += cents;
        Log.i(LOG_TAG, "PRICE IN CENTS : " + String.valueOf(priceInCts));
        if (priceInCts < 0) {
            throw  new PriceSecurityException();
        }
        return priceInCts;
    }
}
