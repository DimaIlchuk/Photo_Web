package com.photoweb.piiics.model;

import java.io.Serializable;

/**
 * Created by dnizard on 16/06/2017.
 */

public class Asset implements Serializable {
    public String identifier;
    public String imageURL;
    public String imageThumbnail;
    public String source;

    public String background = "Blanc - Bloc.jpg";
    public String format = "1796 : 1205";

    public boolean selected = false;

    public Asset(String id, String url, String thumbnail, String src){
        this.identifier = id;
        this.imageURL = url;
        this.imageThumbnail = thumbnail;
        this.source = src;
    }
}
