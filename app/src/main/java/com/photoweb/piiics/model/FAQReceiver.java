package com.photoweb.piiics.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by thomas on 06/09/2017.
 */

public class FAQReceiver {

    @SerializedName("language")
    @Expose
    private String language;

    @SerializedName("content")
    @Expose
    private ArrayList<Content> contents;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ArrayList<Content> getContents() {
        return contents;
    }

    public void setContents(ArrayList<Content> contents) {
        this.contents = contents;
    }

    public class Content {

        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("question")
        @Expose
        private String question;

        @SerializedName("answer")
        @Expose
        private String answer;

        @SerializedName("lang")
        @Expose
        private String lang;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }
    }
}
