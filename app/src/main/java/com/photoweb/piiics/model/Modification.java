package com.photoweb.piiics.model;

import com.photoweb.piiics.utils.Promotions;

import java.io.Serializable;

/**
 * Created by thomas on 27/04/2017.
 */

public class Modification implements Serializable {

    private String modificationID;
    private int modificationPrice;

    public Modification(String modificationID, int modificationPrice) {
        this.modificationID = modificationID;
        this.modificationPrice = modificationPrice;
    }

    public String getModificationID() {
        return modificationID;
    }

    public int getModificationPrice() {
        return modificationPrice;
    }

    public int showModificationPrice(EditorPic editorPic, ModificationsManager modificationsManager) {
       // int price = Promotions.getPriceWithPromotions(editorPic, this, modificationsManager);
        //return price;
        return -1;
    }
}
