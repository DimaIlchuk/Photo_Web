package com.photoweb.piiics.model;

/**
 * Created by thomas on 18/04/2017.
 */

public class GridViewItem {

    private String path;
    private boolean selected;
    private int sourceType;
   // private boolean isDirectory;
   // private Bitmap image;


    public GridViewItem(String path, int sourceType) {
        this.path = path;
        selected = false;
        this.sourceType = sourceType;
    }

    public GridViewItem(String path, boolean selected, int sourceType) {
        this.path = path;
        this.selected = selected;
        this.sourceType = sourceType;
    }

    public String getPath() {
        return path;
    }

    public int getSourceType() { return sourceType; }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
