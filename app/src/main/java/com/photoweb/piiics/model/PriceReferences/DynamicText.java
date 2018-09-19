package com.photoweb.piiics.model.PriceReferences;

import android.graphics.Typeface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dnizard on 04/09/2017.
 */

public class DynamicText {

    public String text;
    public float x;
    public float y;
    public float width;
    public float height;
    public float arg;
    public int color;
    public Typeface font;
    public int position;

    public DynamicText(String text, float x, float y, float width, float height, int color, Typeface font, int position, float arg)
    {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.font = font;
        this.position = position;
        this.arg = arg;
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("text", this.text);
        json.put("x", this.x);
        json.put("y", this.y);
        json.put("width", this.width);
        json.put("height", this.height);
        json.put("color", this.color);
        json.put("position", this.position);
        json.put("arg", this.arg);

        return json;
    }

    public DynamicText(JSONObject jsonObject, ArrayList<Typeface> fonts) throws JSONException {
        this.text = jsonObject.getString("text");
        this.x = (float)jsonObject.getDouble("x");
        this.y = (float)jsonObject.getDouble("y");
        this.width = (float)jsonObject.getDouble("width");
        this.height = (float)jsonObject.getDouble("height");
        this.color = jsonObject.getInt("color");
        this.position = jsonObject.getInt("position");
        this.font = fonts.get(this.position);
        this.arg = (float)jsonObject.getDouble("arg");

    }
}
