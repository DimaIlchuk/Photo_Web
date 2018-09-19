package com.photoweb.piiics.model.PriceReferences;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by dnizard on 11/07/2017.
 */

public class Sticker extends APIReference {
    private static final String LOG_TAG = "Sticker";

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("category_id")
    @Expose
    private int categoryId;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("price")
    @Expose
    private String priceStr;

    @SerializedName("refPrice")
    @Expose
    private String refPriceStr;

    private File stickerFile;

    public float x;
    public float y;
    public float width;
    public float height;
    public float arg;

    public Sticker(Sticker stickerData, float x, float y, float width, float height, float arg){
        this.id = stickerData.getId();
        this.categoryId = stickerData.getCategoryId();
        this.name = stickerData.getName();
        this.priceStr = stickerData.getPriceStr();
        this.refPriceStr = stickerData.getRefPriceStr();
        this.stickerFile = stickerData.getStickerFile();

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.arg = arg;
    }

    public Sticker() {
        this.stickerFile = null;
        this.x = -1;
        this.y = -1;
        this.width = -1;
        this.height = -1;
        this.arg = -1;
    }

    public Sticker(Sticker sticker) {
        this.id = sticker.getId();
        this.categoryId = sticker.getCategoryId();
        this.name = sticker.getName();
        this.priceStr = sticker.getPriceStr();
        this.refPriceStr = sticker.getRefPriceStr();
        this.stickerFile = sticker.getStickerFile();

        this.x = sticker.x;
        this.y = sticker.y;
        this.width = sticker.width;
        this.height = sticker.height;
        this.arg = sticker.arg;
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("id", this.id);
        json.put("categoryId", this.categoryId);
        json.put("name", this.name);
        json.put("priceStr", this.priceStr);
        json.put("refPriceStr", this.refPriceStr);
        json.put("stickerFile", this.stickerFile.getAbsolutePath());
        json.put("x", this.x);
        json.put("y", this.y);
        json.put("width", this.width);
        json.put("height", this.height);
        json.put("arg", this.arg);

        return json;
    }

    public Sticker(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.categoryId = jsonObject.getInt("categoryId");
        this.name = jsonObject.getString("name");
        this.priceStr = jsonObject.getString("priceStr");
        this.refPriceStr = jsonObject.getString("refPriceStr");
        this.stickerFile = new File(jsonObject.getString("stickerFile"));
        this.x = (float)jsonObject.getDouble("x");
        this.y = (float)jsonObject.getDouble("y");
        this.width = (float)jsonObject.getDouble("width");
        this.height = (float)jsonObject.getDouble("height");
        this.arg = (float)jsonObject.getDouble("arg");

    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getPriceStr() {
        return priceStr;
    }

    public String getRefPriceStr() {
        return refPriceStr;
    }

    public File getStickerFile() {
        return stickerFile;
    }

    public void setStickerFile(File stickerFile) {
        this.stickerFile = stickerFile;
    }
}
