package com.photoweb.piiics.model.PriceReferences;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.photoweb.piiics.PriceSecurityException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thomas on 07/08/2017.
 */

public class FormatReference extends APIReference {
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

    private int curPrice = -1;

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

    public int getCurPrice() throws PriceSecurityException {
        if (curPrice == -1) {
            curPrice = getPriceInCts(curPriceStr);
        }
        return curPrice;
    }

    public FormatReference() {
    }

    public FormatReference(int id, String name, String curPriceStr, String refPriceStr) {
        this.id = id;
        this.name = name;
        this.curPriceStr = curPriceStr;
        this.refPriceStr = refPriceStr;
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("id", this.id);
        json.put("name", this.name);
        json.put("curpricestr", this.curPriceStr);
        json.put("curprice", this.curPrice);
        json.put("refprice", this.refPriceStr);

        return json;
    }

    public FormatReference(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
        this.curPriceStr = jsonObject.getString("curpricestr");
        this.curPrice = jsonObject.getInt("curprice");
        this.refPriceStr = jsonObject.getString("refprice");
    }

    public FormatReference clone()
    {
        FormatReference format = new FormatReference();

        format.id = this.id;
        format.name = this.name;
        format.curPriceStr = this.curPriceStr;
        format.curPrice = this.curPrice;
        format.refPriceStr = this.refPriceStr;

        return format;
    }

    @Override
    public String toString() {
        String string;
        string =    "--------------FormatReference : ---------------------------\n";
        string +=   "id : " + String.valueOf(id) + "\n";
        string +=   "name : " + name + "\n";
        string +=   "curPriceStr : " + curPriceStr + "\n";
        string +=   "refPriceStr : " + refPriceStr + "\n";
        string +=   "----------------------------------------------------------\n";
        return string;
    }
}
