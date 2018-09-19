package com.photoweb.piiics.model.PriceReferences;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by thomas on 07/08/2017.
 */

public class StickerCategory {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("index")
    @Expose
    private int index;

    @SerializedName("folder")
    @Expose
    private String folder;

    @SerializedName("fr")
    @Expose
    private String folderNameFr;

    @SerializedName("uk")
    @Expose
    private String folderNameUk;

    @SerializedName("cover_id")
    @Expose
    private int coverId;

    @SerializedName("stickers")
    @Expose
    private ArrayList<Sticker> stickers;

    public int getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public String getFolder() {
        return folder;
    }

    public String getFolderNameFr() {
        return folderNameFr;
    }

    public String getFolderNameUk() {
        return folderNameUk;
    }

    public int getCoverId() {
        return coverId;
    }

    public ArrayList<Sticker> getStickers() {
        return stickers;
    }

    public StickerCategory() {}

    public int setStickerFile(File stickerFile) {
        for (Sticker sticker : stickers) {
            if (stickerFile.getName().equals(sticker.getName())) {
                sticker.setStickerFile(stickerFile);
                return 1;
            }
        }
        return -1;
    }
}
