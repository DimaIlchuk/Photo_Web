package com.photoweb.piiics.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by thomas on 11/09/2017.
 */

public class TutorialReceiver {

    @SerializedName("language")
    @Expose
    private String language;

    @SerializedName("content")
    @Expose
    private ArrayList<Content> contents;

    public String getLanguage() {
        return language;
    }

    public ArrayList<Content> getContents() {
        return contents;
    }

    public class Content {
        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("content")
        @Expose
        private String content;

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }

}
