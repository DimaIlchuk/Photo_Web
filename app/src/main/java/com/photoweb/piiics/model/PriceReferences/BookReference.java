package com.photoweb.piiics.model.PriceReferences;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by thomas on 07/08/2017.
 */

public class BookReference extends APIReference {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("curprice")
    @Expose
    private String curPriceStr;

    @SerializedName("refprice")
    @Expose
    private String refPriceStr;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurPriceStr() {
        return curPriceStr;
    }

    public String getRefPriceStr() {
        return refPriceStr;
    }

    public BookReference() {
    }

    public BookReference(FormatReference formatReference) {
        this.id = formatReference.getId();
        this.name = formatReference.getName();
        this.curPriceStr = formatReference.getCurPriceStr();
        this.refPriceStr = formatReference.getRefPriceStr();
    }

    public BookReference(int id, String name, String curPriceStr, String refPriceStr){
        this.id = id;
        this.name = name;
        this.curPriceStr = curPriceStr;
        this.refPriceStr = refPriceStr;
    }
}
