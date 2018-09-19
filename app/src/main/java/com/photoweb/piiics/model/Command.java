package com.photoweb.piiics.model;

import android.content.Context;
import android.util.Log;

import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.model.PriceReferences.DynamicText;
import com.photoweb.piiics.model.PriceReferences.Sticker;
import com.photoweb.piiics.utils.DraftsUtils;
import com.photoweb.piiics.utils.Promotions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by thomas on 22/08/2017.
 */

public class Command implements Serializable {
    public static final String LOG_TAG = "Command";

    //dossier racine du brouillon de la commande
    private String commandRootDirectoryPath;

    private String commandID;
    private String product;
    //private ArrayList<Asset> assets;

    ArrayList<EditorPic> editorPics;

    EditorPic albumFrontCover;
    EditorPic albumBackCover;

    AlbumOptions albumOptions;

    //private static String PRINT_FORMAT_SIZE = "1796 : 1205";
    //private static String ALBUM_FORMAT_SIZE = "M";
    //String format;

    public AlbumOptions getAlbumOptions() {
        return albumOptions;
    }

    public EditorPic getAlbumFrontCover() {
        return albumFrontCover;
    }

    public void setAlbumFrontCover(EditorPic albumFrontCover) {
        this.albumFrontCover = albumFrontCover;
    }

    public EditorPic getAlbumBackCover() {
        return albumBackCover;
    }

    public void setAlbumBackCover(EditorPic albumBackCover) {
        this.albumBackCover = albumBackCover;
    }

    public ArrayList<EditorPic> getEditorPics() {
        return editorPics;
    }

    public String getProduct() {
        return product;
    }

    public String getCommandRootDirectoryPath() {
        return commandRootDirectoryPath;
    }

    public String getCommandID() {
        return commandID;
    }

    //public Command(String commandID, String product, ArrayList<Asset> assets) {
    public Command(String commandID, String product) {
        this.commandID = commandID;
        this.product = product;
        //this.assets = assets;
        this.editorPics = new ArrayList<>();
        this.commandRootDirectoryPath = createRootDirectory();

        if(product.equals("ALBUM")) {

            this.albumFrontCover = new EditorPic("FIRST");
            this.albumBackCover = new EditorPic("LAST");

            this.albumOptions = new AlbumOptions();

        }
        /*this.format = null;
        if (product.equals("PRINT")) {// remplacer "PRINT" par les maccros
            this.format = PRINT_FORMAT_SIZE;
        } else {
            this.format = ALBUM_FORMAT_SIZE;
        }*/
    }

    public void switchPages(int start, int end)
    {
        EditorPic drag = new EditorPic(null);
        EditorPic drop = new EditorPic(null);

        for (EditorPic pic:editorPics) {
            if(pic.index == start)
                drag = pic;

            if(pic.index == end)
                drop = pic;
        }

        drag.index = end;
        drop.index = start;

        try {
            saveCommand();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addAsset(Asset asset) {
        EditorPic editorPic = new EditorPic(asset, getProduct(), getEditorPics().size());
        getEditorPics().add(editorPic);
    }

    public void removeAsset(Asset asset) {
        for (Iterator<EditorPic> it = getEditorPics().iterator(); it.hasNext(); ) {
            EditorPic editorPic = it.next();
            if (editorPic.getAsset().identifier.equals(asset.identifier)) {
                it.remove();
            }
        }
    }

    /*
        Recupère le dossier PRINT ou ALBUM en fonction du type
        Créé le dossier racine de la commande
     */
    private String createRootDirectory() {

        String parentDirectoryPath = null;
        if (product.equals("PRINT")) {
            parentDirectoryPath = DraftsUtils.getPrintDirectoryPath();
        } else {
            parentDirectoryPath = DraftsUtils.getAlbumDirectoryPath();
        }
        return DraftsUtils.getPrivateDirectoryPath(parentDirectoryPath, commandID);
    }

    public int getAllPicsPrice() throws PriceSecurityException {
        int totalPrice = 0;
        int bookQuantity = (albumOptions == null) ? 1 : albumOptions.getBookQuantity();
        Log.d("DEBUG", "book qty : " + bookQuantity);

        for (EditorPic pic : editorPics) {
            totalPrice += pic.getPicPrice() * bookQuantity;
            Log.d("DEBUG", "total qty : " + totalPrice);
        }

        if(product.equals("PRINT")){
            totalPrice = Promotions.checkFreeStandardFormatPromotion(totalPrice, editorPics);
        }else{
            totalPrice = Promotions.checkFreePagePromotion(totalPrice, editorPics);
        }

        Log.d("DEBUG", "total qty : " + totalPrice);
        return totalPrice;
    }

    public String getAllPicsPriceStr() throws PriceSecurityException {
        int totalPriceInCents = getAllPicsPrice();
        return convertPriceToString(totalPriceInCents);
    }

    /*public String getTotalPriceStr() throws PriceSecurityException {
        if (product.equals("ALBUM")) {
            return getAllPicsPriceStr()
        } else {
            return getAllPicsPriceStr(); //+ return the deliveryPrice ?
        }
    }*///todo

    /*
        Convert the price in cents integer to String
        String format : "X.XX €"
    */
    public static String convertPriceToString(int price) {
        int cents = price % 100;
        int priceInteger = price / 100;

        String priceString = String.valueOf(priceInteger) + ".";
        if (cents < 10) {
            priceString += "0" + String.valueOf(cents);
        } else {
            priceString += String.valueOf(cents);
        }
        priceString += " €";
        return priceString;
    }

    public int getTotalTirages() {
        int tirages = 0;
        for (EditorPic editorPic : editorPics) {
            tirages += editorPic.getCopy();
        }
        return tirages;
    }

    public void rebootPic(int index)
    {
        Log.d(LOG_TAG, "size : "+editorPics.size());
        for (int i=0;i<editorPics.size();i++) {
            if(editorPics.get(i).index == index){
                editorPics.set(i,  new EditorPic(null, "ALBUM", index));
            }
        }
    }

    public void saveCommand() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("commandID", commandID);
        jsonObject.put("product", product);

        //jsonObject.put("editorPics", editorPics.toString());

        JSONArray jsonPics = new JSONArray();

        for (EditorPic pic : editorPics) {
            //if (pic.getAsset() != null) {

                jsonPics.put(saveEditorPictoJSON(pic));
            //}
        }

        jsonObject.put("pics", jsonPics);

        if(product.equals("ALBUM")) {
            jsonObject.put("front", saveEditorPictoJSON(albumFrontCover));
            jsonObject.put("back", saveEditorPictoJSON(albumBackCover));
        }

        Log.d(LOG_TAG, jsonObject.toString());

        String fileName = getCommandRootDirectoryPath() + "/" + getCommandID() + ".json";

        FileWriter fileWriter = null;
        File file = new File(fileName);

        // if file doesn't exists, then create it
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();

            fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public JSONObject saveEditorPictoJSON(EditorPic pic) throws JSONException
    {
        JSONObject picObj = new JSONObject();

        picObj.put("photoID", pic.getPhotoID());
        picObj.put("bitmapName", pic.getBitmapName());
        picObj.put("copy", pic.getCopy());
        picObj.put("duplicated", pic.isDuplicated());
        picObj.put("duplicatedNumber", pic.getDuplicatedNumber());
        picObj.put("index", pic.getIndex());
        picObj.put("product", pic.getProduct());

        if(pic.getOriginalBitmapPath() != null)
            picObj.put("originalBitmapPath", pic.getOriginalBitmapPath());

        picObj.put("cropBitmapPath", pic.getCropBitmapPath());
        picObj.put("finalBitmapPath", pic.getFinalBitmapPath());

        if(pic.getFormatReference() != null)
            picObj.put("format", pic.getFormatReference().getJSON());

        if(pic.getBackgroundReference() != null)
            picObj.put("background", pic.getBackgroundReference().getJSON());

        if(pic.getAsset() != null){
            picObj.put("identifier", pic.getAsset().identifier);
            picObj.put("imageURL", pic.getAsset().imageURL);
            picObj.put("imageThumbnail", pic.getAsset().imageThumbnail);
            picObj.put("source", pic.getAsset().source);
        }

        picObj.put("operated", pic.operated);

        JSONArray picalbums = new JSONArray();

        for (PicAlbum picalbum:pic.picAlbums) {
            picalbums.put(picalbum.getJSON());
        }

        picObj.put("picAlbums", picalbums);

        JSONObject actions = new JSONObject(pic.actions);

        if (pic.actions.get("Stickers") != null) {
            JSONArray stickers = new JSONArray();

            ArrayList<Sticker> list = (ArrayList<Sticker>) pic.actions.get("Stickers");

            for (Sticker stick : list) {
                stickers.put(stick.getJSON());
            }

            actions.put("Stickers", stickers);
        }

        if (pic.actions.get("Texts") != null) {
            JSONArray stickers = new JSONArray();

            ArrayList<DynamicText> list = (ArrayList<DynamicText>) pic.actions.get("Texts");

            for (DynamicText stick : list) {
                stickers.put(stick.getJSON());
            }

            actions.put("Texts", stickers);
        }

        picObj.put("actions", actions);

        return picObj;
    }

    public void loadCommand(Context context) throws IOException, JSONException {
        FileInputStream fis = new FileInputStream(getCommandRootDirectoryPath() + "/" + getCommandID() + ".json");
        String json = "";

        byte[] input = new byte[fis.available()];
        while (fis.read(input) != -1) {
        }
        json += new String(input);

        Log.d(LOG_TAG, json);

        JSONObject jsonObject = new JSONObject(json);

        JSONArray jsonArray = jsonObject.getJSONArray("pics");

        for (int i = 0; i < jsonArray.length(); i++) {
            EditorPic pic = new EditorPic(jsonArray.getJSONObject(i), context);
            this.editorPics.add(pic);
        }

        if(jsonObject.has("front")){
            albumFrontCover = new EditorPic(jsonObject.getJSONObject("front"), context);
            albumBackCover = new EditorPic(jsonObject.getJSONObject("back"), context);
        }

    }
}
