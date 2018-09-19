package com.photoweb.piiics.model.PriceReferences;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.photoweb.piiics.model.PriceReferences.FormatReference;

import java.util.ArrayList;

/**
 * Created by thomas on 07/08/2017.
 */

public class FormatAndBookReferenceGeneral {

    @SerializedName("config")
    @Expose
    private ArrayList<FormatReference> formatAndBookReferences;

    public ArrayList<FormatReference> getFormatAndBookReferences() {
        return formatAndBookReferences;
    }

    public FormatAndBookReferenceGeneral() {
    }
}
