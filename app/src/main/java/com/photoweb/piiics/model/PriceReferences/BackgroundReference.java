package com.photoweb.piiics.model.PriceReferences;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by thomas on 07/08/2017.
 */

public class BackgroundReference extends APIReference {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("pricePrint")
    @Expose
    private String pricePrintStr;

    @SerializedName("priceAlbum")
    @Expose
    private String priceAlbumStr;

    @SerializedName("refPricePrint")
    @Expose
    private String refPricePrintStr;

    @SerializedName("refPriceAlbum")
    @Expose
    private String refPriceAlbumStr;

    @SerializedName("index")
    @Expose
    private int index;

    private File backgroundFile;
    //private String backgroundFilePath;

    public BackgroundReference() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPricePrintStr() {
        return pricePrintStr;
    }

    public String getPriceAlbumStr() {
        return priceAlbumStr;
    }

    public String getRefPricePrintStr() {
        return refPricePrintStr;
    }

    public String getRefPriceAlbumStr() {
        return refPriceAlbumStr;
    }

    public int getIndex() {
        return index;
    }

    public File getBackgroundFile() {
        return backgroundFile;
    }

    public void setBackgroundFile(File backgroundFile) {
        this.backgroundFile = backgroundFile;
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("id", this.id);
        json.put("name", this.name);
        json.put("pricePrintStr", this.pricePrintStr);
        json.put("priceAlbumStr", this.priceAlbumStr);
        json.put("refPricePrintStr", this.refPricePrintStr);
        json.put("refPriceAlbumStr", this.refPriceAlbumStr);
        json.put("backgroundFile", this.backgroundFile.getAbsolutePath());

        return json;
    }

    public BackgroundReference(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
        this.pricePrintStr = jsonObject.getString("pricePrintStr");
        this.priceAlbumStr = jsonObject.getString("priceAlbumStr");
        this.refPricePrintStr = jsonObject.getString("refPricePrintStr");
        this.refPriceAlbumStr = jsonObject.getString("refPriceAlbumStr");
        this.backgroundFile = new File(jsonObject.getString("backgroundFile"));
    }

    public BackgroundReference clone()
    {
        BackgroundReference bg = new BackgroundReference();

        bg.id = this.id;
        bg.name = this.name;
        bg.pricePrintStr = this.pricePrintStr;
        bg.priceAlbumStr = this.priceAlbumStr;
        bg.refPricePrintStr = this.refPricePrintStr;
        bg.refPriceAlbumStr = this.refPriceAlbumStr;
        bg.backgroundFile = this.backgroundFile;

        return bg;
    }

    @Override
    public String toString() {
        String string;
        string =    "--------------BackgroundReference : ----------------------\n";
        string +=   "id : " + String.valueOf(id) + "\n";
        string +=   "name : " + name + "\n";
        string +=   "pricePrintStr : " + pricePrintStr + "\n";
        string +=   "priceAlbumStr : " + priceAlbumStr + "\n";

        string +=   "backgroundFile : ";
        if(backgroundFile == null) {
            string += "NULL\n";
        } else {
            string += "NOT NULL\n";
        }

        string +=   "----------------------------------------------------------\n";
        return string;
    }
}
