package com.photoweb.piiics.model;

import android.support.annotation.NonNull;

/**
 * Created by thomas on 03/08/2017.
 */

public class DeliveryMethod {

    @NonNull
    private String companyName;

    @NonNull
    private String deliveryMethodInfos;

    private String identifier;

    private int priceInCts;

    private boolean visibility;
    private boolean defaultDelivery;

    public DeliveryMethod(@NonNull String companyName, @NonNull String deliveryMethodInfos, String identifier) {
        this.companyName = companyName;
        this.deliveryMethodInfos = deliveryMethodInfos;
        this.identifier = identifier;
        this.priceInCts = -1;
        this.visibility = true;
        this.defaultDelivery = false;
    }

    public void setIdentifier(String identifier){
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public boolean isDefaultDelivery() {
        return defaultDelivery;
    }

    public void setDefaultDelivery(boolean defaultDelivery) {
        this.defaultDelivery = defaultDelivery;
    }

    @NonNull
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(@NonNull String companyName) {
        this.companyName = companyName;
    }

    @NonNull
    public String getDeliveryMethodInfos() {
        return deliveryMethodInfos;
    }

    public void setDeliveryMethodInfos(@NonNull String deliveryMethodInfos) {
        this.deliveryMethodInfos = deliveryMethodInfos;
    }

    public int getPrice() {
        return priceInCts;
    }

    public void setPrice(int priceInCts) {
        this.priceInCts = priceInCts;
    }

    public String getPriceStr(int priceInCts) {

        boolean negativePrice = false;
        if (priceInCts < 0) {
            priceInCts *= -1;
            negativePrice = true;
        }
        int priceRounded = priceInCts / 100;
        int cents = priceInCts % 100;

        String priceRoundedStr = String.valueOf(priceRounded);
        String centsStr;
        if (cents == 0) {
            centsStr = "00";
        } else {
            centsStr = String.valueOf(cents);
        }
        if (negativePrice) {
            return "-" + priceRoundedStr + "." + centsStr + "€";
        } else {
            return "+" + priceRoundedStr + "." + centsStr + "€";
        }
    }
}
