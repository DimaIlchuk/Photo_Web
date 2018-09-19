package com.photoweb.piiics.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import com.photoweb.piiics.model.PriceReferences.BackgroundReference;
import com.photoweb.piiics.utils.DraftsUtils;
import com.photoweb.piiics.utils.FilterHandler;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by dnizard on 26/11/2017.
 */

public class PicAlbum implements Serializable {
    private static final String LOG_TAG = "PicAlbum";

    private String originalBitmapPath;
    private String cropBitmapPath;
    private String finalBitmapPath;

    private String cropDirectory;

    private String photoID;

    public int index;

    private String bitmapName;

    private Target target;

    public HashMap<String, Object> actions = new HashMap<>();

    public BackgroundReference backgroundReference;

    private PicAlbum() {}

    public PicAlbum(String org, String cropDirectory, Bitmap bmap, String name, BackgroundReference bg){
        this.index = 0;

        this.originalBitmapPath = org;
        this.bitmapName = name;
        this.cropDirectory = cropDirectory;

        this.backgroundReference = bg;

        if(bmap != null)
            saveCropImage(bmap);
    }

    public void saveCropImage(Bitmap bmap)
    {
        if(bitmapName.endsWith(".png"))
        {
            File crop = new File(cropDirectory, bitmapName);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(crop);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                cropBitmapPath = crop.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "error " + e.getMessage());
            } finally {
                try {
                    fos.close();

                    applyAllTransformations();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "close error " + e.getMessage());
                }
            }
        }else {

            Bitmap result;

            if(backgroundReference == null){
                result = Bitmap.createBitmap(bmap.getWidth(), bmap.getHeight(), Bitmap.Config.ARGB_8888);
            }else{
                result = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(backgroundReference.getBackgroundFile().getAbsolutePath()), bmap.getWidth(), bmap.getHeight(), false);
            }

            Canvas canvas = new Canvas(result);
            if(backgroundReference == null) canvas.drawColor(Color.WHITE);

            canvas.drawBitmap(bmap, (result.getWidth() - bmap.getWidth())/2, (result.getHeight() - bmap.getHeight())/2, null);

            File crop = new File(cropDirectory, bitmapName);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(crop);
                // Use the compress method on the BitMap object to write image to the OutputStream
                result.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                cropBitmapPath = crop.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "error " + e.getMessage());
            } finally {
                try {
                    if(fos != null)
                        fos.close();

                    applyAllTransformations();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "close error " + e.getMessage());
                }
            }
        }

    }

    public void saveCropImage(Bitmap bmap, int width, int height)
    {
        if(bitmapName.endsWith(".png"))
        {
            File crop = new File(cropDirectory, bitmapName);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(crop);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                cropBitmapPath = crop.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "error " + e.getMessage());
            } finally {
                try {
                    fos.close();

                    applyAllTransformations();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "close error " + e.getMessage());
                }
            }
        }else {

            Bitmap result = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(backgroundReference.getBackgroundFile().getAbsolutePath()), width, height, false);
            Bitmap toDraw = Bitmap.createScaledBitmap(bmap, width, height, false);
            Canvas canvas = new Canvas(result);

            canvas.drawBitmap(toDraw, 0, 0, null);

            toDraw.recycle();

            File crop = new File(cropDirectory, bitmapName);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(crop);
                // Use the compress method on the BitMap object to write image to the OutputStream
                result.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                cropBitmapPath = crop.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "error " + e.getMessage());
            } finally {
                try {
                    fos.close();

                    applyAllTransformations();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "close error " + e.getMessage());
                }
            }
        }

    }

    private void generateFinal(String parentDirectory)
    {
        File finale = new File(parentDirectory, bitmapName);

        try {
            DraftsUtils.copyFile(new File(cropBitmapPath), finale);
            finalBitmapPath = finale.getAbsolutePath();
            Log.d(LOG_TAG, "Copy succeed to " + finale.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Copy failed to " + finale.getAbsolutePath() + " : " + e.getMessage());
        }
    }

    public void applyAllTransformations()
    {
        Bitmap finale = BitmapFactory.decodeFile(getCropBitmapPath());

        if (actions.get("filter") != null) {
            finale = FilterHandler.get().applyFilter((String) actions.get("filter"), finale);
        }

        File picFile = new File(cropDirectory.replace("CROP", "FINAL"), bitmapName);
        finalBitmapPath = picFile.getAbsolutePath();
        picFile.delete();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(getFinalBitmapPath());

            if(bitmapName.endsWith(".png"))
                finale.compress(Bitmap.CompressFormat.PNG, 100, out);
            else
                finale.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    finale.recycle();
                    finalBitmapPath = picFile.getAbsolutePath();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getOriginalBitmapPath() {
        return originalBitmapPath;
    }

    public String getCropBitmapPath() {
        return cropBitmapPath;
    }

    public String getFinalBitmapPath() {
        return finalBitmapPath;
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("index", index);
        json.put("originalBitmapPath", originalBitmapPath);
        json.put("cropBitmapPath", cropBitmapPath);
        json.put("cropDirectory", cropDirectory);
        json.put("finalBitmapPath", finalBitmapPath);

        json.put("bitmapName", bitmapName);

        json.put("background", backgroundReference.getJSON());

        return json;
    }

    public PicAlbum(JSONObject jsonObject) throws JSONException {
        this.index = jsonObject.getInt("index");
        this.originalBitmapPath = jsonObject.getString("originalBitmapPath");
        this.cropBitmapPath = jsonObject.getString("cropBitmapPath");
        this.cropDirectory = jsonObject.getString("cropDirectory");
        this.finalBitmapPath = jsonObject.getString("finalBitmapPath");
        this.bitmapName = jsonObject.getString("bitmapName");
        this.backgroundReference = new BackgroundReference(jsonObject.getJSONObject("background"));

    }

}
