package com.photoweb.piiics.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.R;
import com.photoweb.piiics.model.PriceReferences.BackgroundReference;
import com.photoweb.piiics.model.PriceReferences.DynamicText;
import com.photoweb.piiics.model.PriceReferences.FormatReference;
import com.photoweb.piiics.model.PriceReferences.Sticker;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.DraftsUtils;
import com.photoweb.piiics.utils.FileThumbnailRequestHandler;
import com.photoweb.piiics.utils.PicassoClient;
import com.photoweb.piiics.utils.PriceReferences;
import com.photoweb.piiics.utils.SocialHandler;
import com.photoweb.piiics.utils.StickerHandler;
import com.photoweb.piiics.utils.TransformationHandler;
import com.photoweb.piiics.utils.Utils;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.GraphRequest.TAG;
import static com.photoweb.piiics.activities.SelectPicsActivity.getDirPath;
import static com.photoweb.piiics.utils.BitmapsManager.saveBitmap;
import static com.photoweb.piiics.utils.CreateEditorPicsBitmapsAsync.FINISH_PIC_FILTER;

/**
 * Created by thomas on 21/04/2017.
 */

public class EditorPic implements Serializable {
    private static final String LOG_TAG = "EditorPic";

    private Asset asset;
    private String photoID;

    private String originalBitmapPath;
    private String cropBitmapPath;
    private String finalBitmapPath;
    private int editorActivityAdapterPosition;

    private BackgroundReference backgroundReference;
    private FormatReference formatReference;

    // private String backgroundBitmapPath;

    // private Modification format;
    private Modification margin;//todo

    // private int picPrice;
    private boolean duplicated;
    private int duplicatedNumber;
    //int duplicatedPrice;
    private int copy;
    private ModificationsManager modificationsManager;//todo

    public int index;
    public boolean operated = false;

    private String product;

    //same name of the bitmaps saved in the differents directories for a pic
    private String bitmapName;

    private Target target;

    public HashMap<String, Object> actions = new HashMap<>();

    public ArrayList<PicAlbum> picAlbums = new ArrayList<>();

    //--------------------Constructors--------------------------------------------------------------

    /*
     Used for the duplicateEditorPic() method
     */
    private EditorPic() {}

    public EditorPic(Asset asset, String product, int index) {
        this.copy = 1;
        this.duplicated = false;
        this.duplicatedNumber = 1;


        String photoID = "" + UUID.randomUUID().hashCode();
        this.photoID = photoID;
        this.asset = asset;
       // this.backgroundReference = PriceReferences.getDefaultBackground();
        //this.formatReference = PriceReferences.getDefaultformat();

        //      map.put("index", commandDict.size());

        this.product = product;

        if(product.equals("PRINT")){
            this.bitmapName = this.photoID + ".jpg";
        }else{
            //this.bitmapName = this.photoID + ".png";
            this.bitmapName = this.photoID + ".jpg";
        }


        this.index = index;
        this.backgroundReference = PriceReferences.getDefaultBackground();
        this.formatReference = PriceReferences.getDefaultformat();

        if(product.equals("ALBUM")){
            this.formatReference = PriceReferences.findFormatReferenceByName(PriceReferences.PAGE_FORMAT);
        }

        if(asset == null){
            generateEmpty(getApplicationContext(), CommandHandler.get().currentCommand.getCommandRootDirectoryPath());
        }
    }

    public EditorPic(String type)
    {
        if(type != null){
            this.copy = 1;
            this.duplicated = false;
            this.duplicatedNumber = 1;

            this.photoID = type;
            this.asset = null;

            this.product = "ALBUM";
            this.bitmapName = this.photoID + ".png";

            if(type.equals("FIRST")){
                actions.put("gabarit", "onePhoto.MarginBottomAlbumGabarit");
            }

            this.index = -1;
            this.backgroundReference = PriceReferences.getDefaultBackground();
            this.formatReference = PriceReferences.getDefaultformat();
        }
    }

    public EditorPic(String originalBitmapPath, String cropBitmapPath, String modifiedBitmapPath) {//todo
        this.originalBitmapPath = originalBitmapPath;
        this.cropBitmapPath = cropBitmapPath;
        this.finalBitmapPath = modifiedBitmapPath;
        copy = 1;
        //   picPrice = 0;// init plus utile
        duplicated = false;
        duplicatedNumber = 1;
        //duplicatedPrice = modificationsManager.getDuplicatedPrice();

        /*File productdir = getApplicationContext().getDir("APP", Context.MODE_PRIVATE);
        File mydir = new File(productdir, "Thumbnails");

        File downloadfile = new File(mydir, "Blanc - Bloc.jpg");
        Log.d(LOG_TAG, downloadfile.getPath());
        this.backgroundBitmapPath = downloadfile.getPath(;*/
        //   this.backgroundReference = getDefaultBackground();
   //     this.formatReference = getDefaultFormat();

        this.backgroundReference = PriceReferences.getDefaultBackground();
        this.formatReference = PriceReferences.getDefaultformat();
    }

    public EditorPic(JSONObject json, Context context)
    {
        try {
            this.photoID = json.getString("photoID");
            this.bitmapName = json.getString("bitmapName");
            this.copy = json.getInt("copy");
            this.duplicated = json.getBoolean("duplicated");
            this.duplicatedNumber = json.getInt("duplicatedNumber");
            this.index = json.getInt("index");
            this.product = json.getString("product");

            this.operated = json.getBoolean("operated");

            if(json.has("originalBitmapPath"))
                this.originalBitmapPath = json.getString("originalBitmapPath");
            this.cropBitmapPath = json.getString("cropBitmapPath");
            this.finalBitmapPath = json.getString("finalBitmapPath");

            if(json.has("identifier"))
                this.asset = new Asset(json.getString("identifier"), json.getString("imageURL"), json.getString("imageThumbnail"), json.getString("source"));

            if(json.has("format"))
                this.formatReference = new FormatReference(json.getJSONObject("format"));
            if(json.has("background"))
                this.backgroundReference = new BackgroundReference(json.getJSONObject("background"));

            if(this.formatReference == null){
                this.formatReference = PriceReferences.getDefaultformat();
            }

            if(this.backgroundReference == null){
                this.backgroundReference = PriceReferences.getDefaultBackground();
            }

            JSONArray arrayPics = json.getJSONArray("picAlbums");

            for (int i=0; i<arrayPics.length(); i++){
                this.picAlbums.add(new PicAlbum(arrayPics.getJSONObject(i)));
            }

            JSONObject actions = json.getJSONObject("actions");

            if (actions.has("filter")) {
                this.actions.put("filter", actions.getString("filter"));
            }

            if (actions.has("gabarit")) {
                this.actions.put("gabarit", actions.getString("gabarit"));
            }

            if (actions.has("Stickers")) {
                JSONArray array = actions.getJSONArray("Stickers");
                ArrayList<Sticker> list = new ArrayList<>();

                for (int i=0; i<array.length(); i++){
                    list.add(new Sticker(array.getJSONObject(i)));
                }

                this.actions.put("Stickers", list);
            }

            if (actions.has("Texts")) {
                ArrayList<Typeface> customFonts = new ArrayList<>();

                String[] listFonts;
                try {
                    listFonts = context.getAssets().list("fonts");
                    for (String fontName : listFonts) {
                        Log.i(TAG, "fontName : " + fontName);
                        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);
                        customFonts.add(customFont);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                JSONArray array = actions.getJSONArray("Texts");
                ArrayList<DynamicText> list = new ArrayList<>();

                for (int i=0; i<array.length(); i++){
                    list.add(new DynamicText(array.getJSONObject(i), customFonts));
                }

                this.actions.put("Texts", list);
            }

            if (actions.has("border"))
            {
                this.actions.put("border", Integer.parseInt(actions.getString("border")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Asset getAsset() {
        return asset;
    }

    public String getProduct() {
        return product;
    }

    public String getBitmapName() { return bitmapName; }

    public void setBitmapName(String name){
        this.bitmapName = name;
    }

    //----------------------------------------------------------------------------------------------

    /*
        Créé ou recupère les sous dossiers des bitmaps (ORG / CROP / FINAL)
        Créé les bitmaps dans chaque dossier
     */
    public void createDefaultBitmaps(String commandDirectory, Context context) {
        operated = true;

        createOriginalBitmap(commandDirectory, context);

    }

    public void sendBroadcastFinishPic() {
        Log.i(LOG_TAG, "SEND BROADCAST FINISH PIC");
        Intent intent = new Intent(FINISH_PIC_FILTER);
        intent.putExtra("EditorActivityAdapterPosition", this.getEditorActivityAdapterPosition());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void createFinalBitmap(String parentDirectory) {

        File finale = new File(parentDirectory, bitmapName);

        try {
            DraftsUtils.copyFile(new File(cropBitmapPath), finale);
            finalBitmapPath = finale.getAbsolutePath();
            Log.d(LOG_TAG, "Copy succeed to " + finale.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Copy failed to " + finale.getAbsolutePath() + " : " + e.getMessage());
        } catch (NullPointerException e) {
            Log.d(LOG_TAG, "Copy failed to " + finale.getAbsolutePath() + " : " + e.getMessage());
        }

        sendBroadcastFinishPic();

      //  result.recycle();
    }


    private void createCroppedBitmap(final String parentDirectory) {
        Log.d(LOG_TAG, "ID : " + bitmapName);
        /*if(finalBitmapPath != null && product.equals("ALBUM")){
            //PicAlbum pic = new PicAlbum(originalBitmapPath, parentDirectory, null, bitmapName.replace(".png", "_0.png"));
            PicAlbum pic = new PicAlbum(originalBitmapPath, parentDirectory, null, bitmapName.replace(".jpg", "_0.jpg"), backgroundReference);
            this.picAlbums.add(pic);

            Intent intent = new Intent("FINISH_DOWNLOAD");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            return;
        }*/

        if (product.equals("PRINT")){
            Bitmap result = TransformationHandler.initCrop(originalBitmapPath, product);
            Log.d(LOG_TAG, "size of bitmap : " + result.getHeight());
            Log.d(LOG_TAG, "Copy to final");
            File crop = new File(parentDirectory, bitmapName);

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
                    result.recycle();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "close error " + e.getMessage());
                }
            }
        }else{
            Bitmap cropBitmap = TransformationHandler.initCrop(originalBitmapPath, product);

            //PicAlbum pic = new PicAlbum(originalBitmapPath, parentDirectory, cropBitmap, bitmapName.replace(".png", "_0.png"));
            PicAlbum pic = new PicAlbum(originalBitmapPath, parentDirectory, cropBitmap, bitmapName.replace(".jpg", "_0.jpg"), backgroundReference);
            this.picAlbums.add(pic);

            Bitmap result = TransformationHandler.drawOnPage(cropBitmap, "DefaultAlbumGabarit", BitmapFactory.decodeFile(backgroundReference.getBackgroundFile().getAbsolutePath()));

            Log.d(LOG_TAG, "size of bitmap : " + result.getHeight());
            Log.d(LOG_TAG, "Copy to final");
            File crop = new File(parentDirectory, bitmapName);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(crop);
                // Use the compress method on the BitMap object to write image to the OutputStream
                if(bitmapName.endsWith(".png"))
                    result.compress(Bitmap.CompressFormat.PNG, 100, fos);
                else
                    result.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                cropBitmapPath = crop.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "error " + e.getMessage());
            } finally {
                try {
                    fos.close();
                    result.recycle();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "close error " + e.getMessage());
                }
            }
        }

    }


    /*
        Recupère l'URL de l'image selectionnée (url http ou locale) et créé un bitmap avec
     */
    private void createOriginalBitmap(final String commandDirectory, final Context context) {
        Log.d(LOG_TAG, "creating file : " + bitmapName);

        if(bitmapName != null && bitmapName.equals("FIRST.png") && asset == null){
            try {
                generateFront(context, commandDirectory);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return;

        }

        if(bitmapName != null && bitmapName.equals("LAST.png")){
            generateBack(context, commandDirectory);

            return;
        }

        final String parentDirectory = DraftsUtils.getOriginDirectoryPath();
        final String cropDirectory = DraftsUtils.getPrivateDirectoryPath(commandDirectory, DraftsUtils.CROP_SUBDIRECTORY);
        final String finalDirectory = DraftsUtils.getPrivateDirectoryPath(commandDirectory, DraftsUtils.FINAL_SUBDIRECTORY);

        Log.d(LOG_TAG, "Has to create : " + asset.imageURL);

        final String fileName = asset.identifier + ".jpg";

        File file = new File(parentDirectory, fileName);

        if(!file.exists()){
            Log.d(LOG_TAG, "file not exists! ");

            target = new Target() {

                @Override
                public void onPrepareLoad(Drawable arg0) {
                    return;
                }

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {

                    Log.d(LOG_TAG, "Image Succeed");

                    try {
                        compressBitmap(parentDirectory, bitmap);
                        //bitmap = decodeFile(originalBitmapPath, 2000);
                        //compressBitmap(parentDirectory, bitmap);//todo: refaire ca au propre

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                createCroppedBitmap(cropDirectory);
                                createFinalBitmap(finalDirectory);
                            }

                        }, 100);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable arg0) {
                    Log.d(LOG_TAG, "Image failed " + asset.imageURL);
                    operated = false;

                    createDefaultBitmaps(CommandHandler.get().currentCommand.getCommandRootDirectoryPath(), getApplicationContext());

                    return;
                }
            };

            Handler mainHandler = new Handler(context.getMainLooper());

            if (asset.source.equals("Dropbox")) {

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        PicassoClient.getPicasso()
                                .load(FileThumbnailRequestHandler.buildPicassoUri(asset.imageURL))
                                .skipMemoryCache()
                                .into(target);
                    } // This is your code
                };
                mainHandler.post(myRunnable);

            } else if (asset.source.equals("Google")) {

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        OkHttpDownloader downloader = new OkHttpDownloader(context) {
                            @Override
                            protected HttpURLConnection openConnection(Uri uri) throws IOException {
                                HttpURLConnection connection = super.openConnection(uri);
                                connection.setRequestProperty("Authorization", "Bearer " + SocialHandler.get().googleAuthToken);
                                return connection;
                            }
                        };

                        Log.d(LOG_TAG, "url " + asset.imageURL);
                        Log.d(LOG_TAG, "Bearer " + SocialHandler.get().googleAuthToken);

                        Picasso picasso = new Picasso.Builder(context).downloader(downloader).build();
                        picasso.load(asset.imageURL)
                                .skipMemoryCache()
                                .into(target);
                    } // This is your code
                };
                mainHandler.post(myRunnable);

            } else if (asset.imageURL.startsWith("http")) {
                Log.d(LOG_TAG, "Other than dropbox : " + asset.imageURL);

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, "Starts runnable " + asset.identifier);
                        Picasso.with(context)
                                .load(asset.imageURL)
                                .skipMemoryCache()
                                .into(target);
                    } // This is your code
                };
                mainHandler.post(myRunnable);

            } else {
                originalBitmapPath = asset.imageURL;

                createCroppedBitmap(cropDirectory);
                createFinalBitmap(finalDirectory);
            }
        }else{
            Log.d(LOG_TAG, "file exists! ");

            originalBitmapPath = file.getAbsolutePath();

            createCroppedBitmap(cropDirectory);
            createFinalBitmap(finalDirectory);
        }

    }

    private void generateFront(Context context, final String commandDirectory) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        final String cropDirectory = DraftsUtils.getPrivateDirectoryPath(commandDirectory, DraftsUtils.CROP_SUBDIRECTORY);
        final String finalDirectory = DraftsUtils.getPrivateDirectoryPath(commandDirectory, DraftsUtils.FINAL_SUBDIRECTORY);

        actions.put("gabarit", "onePhoto.MarginBottomAlbumGabarit");

        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inScaled = false;

        final Bitmap placeholder = TransformationHandler.generateThumbnail(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.plus_placeholder_small), 1000);

        final ArrayList<Bitmap> list = new ArrayList<Bitmap>() {{
            add(placeholder);
            //add(placeholder);
            //add(placeholder);
            //add(placeholder);
        }};

        Class defaultGabarit = Class.forName(Utils.package_gabarit + "onePhoto.MarginBottomAlbumGabarit");
        Method m = defaultGabarit.getMethod("applyThumbnailGabarit", ArrayList.class);
        Bitmap result = (Bitmap) m.invoke(defaultGabarit.newInstance(), list);

        placeholder.recycle();

        File crop = new File(cropDirectory, bitmapName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(crop);
            // Use the compress method on the BitMap object to write image to the OutputStream
            result.compress(Bitmap.CompressFormat.PNG, 100, fos);
            cropBitmapPath = crop.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "error " + e.getMessage());
        } finally {
            try {
                fos.close();
                result.recycle();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "close error " + e.getMessage());
            } catch (NullPointerException e){
                Log.d(LOG_TAG, "close error " + e.getMessage());
            }
        }

        createFinalBitmap(finalDirectory);
    }

    private void generateBack(Context context, final String commandDirectory) {
        final String cropDirectory = DraftsUtils.getPrivateDirectoryPath(commandDirectory, DraftsUtils.CROP_SUBDIRECTORY);
        final String finalDirectory = DraftsUtils.getPrivateDirectoryPath(commandDirectory, DraftsUtils.FINAL_SUBDIRECTORY);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap result = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty, options);
        Log.d(LOG_TAG, "COVER SIZE :" + result.getWidth());

        File crop = new File(cropDirectory, bitmapName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(crop);
            // Use the compress method on the BitMap object to write image to the OutputStream
            result.compress(Bitmap.CompressFormat.PNG, 100, fos);
            cropBitmapPath = crop.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "error " + e.getMessage());
        } finally {
            try {
                fos.close();
                //result.recycle();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "close error " + e.getMessage());
            }
        }

        actions.put("Logo", true);

        //createFinalBitmap(finalDirectory);
        result = StickerHandler.drawSticker(result, BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_book), Utils.pageWidth/2, Utils.pageHeight-200, 317, 209, 0);

        crop = new File(finalDirectory, bitmapName);

        fos = null;
        try {
            fos = new FileOutputStream(crop);
            // Use the compress method on the BitMap object to write image to the OutputStream
            result.compress(Bitmap.CompressFormat.PNG, 100, fos);
            finalBitmapPath = crop.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "error " + e.getMessage());
        } finally {
            try {
                fos.close();
                result.recycle();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "close error " + e.getMessage());
            }
        }

        sendBroadcastFinishPic();
    }

    private void generateEmpty(Context context, final String commandDirectory) {
        final String cropDirectory = DraftsUtils.getPrivateDirectoryPath(commandDirectory, DraftsUtils.CROP_SUBDIRECTORY);
        final String finalDirectory = DraftsUtils.getPrivateDirectoryPath(commandDirectory, DraftsUtils.FINAL_SUBDIRECTORY);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        //Bitmap result = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty, options);
        Bitmap result = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(getBackgroundReference().getBackgroundFile().getAbsolutePath()), Utils.pageWidth, Utils.pageHeight, false);
        Log.d(LOG_TAG, "COVER SIZE :" + result.getWidth());

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
                //result.recycle();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "close error " + e.getMessage());
            }
        }

        actions.put("Placeholder", true);

        //createFinalBitmap(finalDirectory);
        result = StickerHandler.drawSticker(result, BitmapFactory.decodeResource(context.getResources(), R.drawable.plus_placeholder), Utils.pageWidth/2, Utils.pageHeight/2, Utils.pageWidth, Utils.pageWidth, 0);

        File finale = new File(finalDirectory, bitmapName);

        FileOutputStream finaleos = null;
        try {
            finaleos = new FileOutputStream(finale);
            // Use the compress method on the BitMap object to write image to the OutputStream
            result.compress(Bitmap.CompressFormat.JPEG, 100, finaleos);
            finalBitmapPath = finale.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "error " + e.getMessage());
        } finally {
            try {
                finaleos.close();
                result.recycle();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "close error " + e.getMessage());
            }
        }

        sendBroadcastFinishPic();
    }

    private static class BasicAuthInterceptor implements com.squareup.okhttp.Interceptor {

        @Override
        public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
            Log.d(TAG, "Bearer " + SocialHandler.get().googleAuthToken);
            final com.squareup.okhttp.Request original = chain.request();
            final com.squareup.okhttp.Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + SocialHandler.get().googleAuthToken)
                    .method(original.method(), original.body());
            return chain.proceed(requestBuilder.build());
        }
    }

    private Bitmap decodeFile(String filePath, int IMAGE_MAX_SIZE){
        Bitmap b = null;

        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            File f = new File(filePath);
            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return b;
    }

    private void compressBitmap(String parentDirectory, Bitmap bitmap) {
        File file = new File(parentDirectory, asset.identifier + ".jpg");
        try {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.flush();
            ostream.close();

            originalBitmapPath = file.getAbsolutePath();
            Log.d(LOG_TAG, "Image saved");

        } catch (IOException e) {
            Log.e("IOException", e.getLocalizedMessage());
        }
    }

    //todo : comparer les 2 constructeurs

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    private void setDuplicated(boolean duplicated) {
        this.duplicated = duplicated;
    }

    public EditorPic duplicateEditorPic() {//todo: check si toutes les vars sont bien clonnées
        EditorPic clonePic = new EditorPic();

        clonePic.photoID = "" + UUID.randomUUID().hashCode();

        clonePic.originalBitmapPath = this.originalBitmapPath;
        clonePic.cropBitmapPath = this.cropBitmapPath.replace(this.photoID, clonePic.photoID);
        clonePic.finalBitmapPath = this.finalBitmapPath.replace(this.photoID, clonePic.photoID);

        Log.d(TAG, "Path : " + this.getCropBitmapPath() + " - " + clonePic.getCropBitmapPath());

        try {
            copy(new File(this.cropBitmapPath), new File(clonePic.cropBitmapPath));
            copy(new File(this.finalBitmapPath), new File(clonePic.finalBitmapPath));
        } catch (IOException e) {
            Log.d(TAG, "error while copy");
            e.printStackTrace();
        }

        clonePic.backgroundReference = this.backgroundReference.clone();
        clonePic.formatReference = this.formatReference.clone();

        clonePic.setDuplicated(true);
        clonePic.setDuplicatedNumber(getDuplicatedNumber() + 1);
        clonePic.setCopy(1);
        clonePic.modificationsManager = this.modificationsManager;
        clonePic.index = this.index + 1;
        clonePic.asset = this.asset;

        //cloneBitmap(clonePic);
        clonePic.actions = new HashMap<String, Object>();

        for (HashMap.Entry<String, Object> entry : this.actions.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Log.i(LOG_TAG, "action key : " + key + ", action value : " + value.toString());
            String keyCloned = key;
            Object valueCloned = null;

            if (key.equals(PriceReferences.STICKERS)) {
                ArrayList<Sticker> stickersTmp = new ArrayList<Sticker>();
                for (Sticker sticker : (ArrayList<Sticker>) value) {
                    stickersTmp.add(new Sticker(sticker));
                }
                valueCloned = stickersTmp;
            }
            clonePic.actions.put(keyCloned, valueCloned);
        }

       /* Iterator it = clonePic.actions.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();

            Log.i(LOG_TAG, pair.getKey() + " = " + pair.getValue());

            if (pair.getKey().equals(PriceReferences.STICKERS)) {
                ArrayList<Sticker> stickers = (ArrayList<Sticker>) pair.getValue();
                for (Sticker sticker : stickers) {
                    Log.i(LOG_TAG, "sticker NAME : " + sticker.getName());
                }
            }
            it.remove();
        }*/
        return clonePic;
    }

    private void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public void showActions() {
        Log.i(LOG_TAG, "START SHOW ACTIONS");
        for (HashMap.Entry<String, Object> entry : this.actions.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Log.i(LOG_TAG, "entry KEY : " + key);
        }
        Log.i(LOG_TAG, "END SHOW ACTIONS");
    }

    private void cloneBitmap(EditorPic clonePic) {
        File dirBitmapsSaved = getDirPath(PicsInfos.modifiedBitmapsDirPAth);
        if (dirBitmapsSaved == null) { // save dans un autre endroit
            //   return null; // checker cette erreur
            return;
        }
        String bitmapCopyPath = saveBitmap(getFinalBitmapPath(), dirBitmapsSaved, clonePic.getDuplicatedNumber());
        setFinalBitmapPath(bitmapCopyPath);
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public int getEditorActivityAdapterPosition() {
        return editorActivityAdapterPosition;
    }

    public void setEditorActivityAdapterPosition(int editorActivityAdapterPosition) {
        this.editorActivityAdapterPosition = editorActivityAdapterPosition;
    }

    public String getOriginalBitmapPath() {
        return originalBitmapPath;
    }

    public String getCropBitmapPath() {

        if(cropBitmapPath == null){
            File crop = new File(DraftsUtils.getPrivateDirectoryPath(CommandHandler.get().currentCommand.getCommandRootDirectoryPath(), DraftsUtils.CROP_SUBDIRECTORY), bitmapName);
            cropBitmapPath = crop.getAbsolutePath();
        }

        return cropBitmapPath;
    }

    public void setCropBitmapPath(String path) {
        this.cropBitmapPath = path;
    }

    public String getFinalBitmapPath() {
        return finalBitmapPath;
    }

    public void setFinalBitmapPath(String finalBitmapPath) {
        this.finalBitmapPath = finalBitmapPath;
    }

    public int getDuplicatedNumber() {
        return duplicatedNumber;
    }

    public void setDuplicatedNumber(int duplicatedNumber) {
        this.duplicatedNumber = duplicatedNumber;
    }

    public int getCopy() {
        return copy;
    }

    public void setCopy(int copy) {
        this.copy = copy;
    }

    public boolean isDuplicated() {
        return duplicated;
    }

    public void decrementCopy() {
        if (copy > 1) {
            copy--;
        }
    }

    public void incrementCopy() {
        //max copy ?
        copy++;
    }

    public Modification getMargin() {
        return margin;
    }

    public void setMargin(Modification margin) {
        this.margin = margin;
    }

    /* public Modification getGabarit() {
         return gabarit;
     }

     public void setGabarit(Modification gabarit) {
         this.gabarit = gabarit;
     }

     public Modification getBackground() {
         return background;
     }

     public void setBackground(Modification background) {
         this.background = background;
     }

     public Modification getFilter() {
         return filter;
     }

     public void setFilter(Modification filter) {
         this.filter = filter;
     }

     public ArrayList<Modification> getTexts() {
         return texts;
     }

     public void setTexts(ArrayList<Modification> texts) {
         this.texts = texts;
     }

     public ArrayList<Modification> getStickers() {
         return stickers;
     }

     public void setStickers(ArrayList<Modification> stickers) {
         this.stickers = stickers;
     }
 */

    public int getIndex() {
        return index;
    }

    public BackgroundReference getBackgroundReference() {
        if(backgroundReference == null){
            return PriceReferences.getDefaultBackground();
        }
        return backgroundReference;
    }

    public void setBackgroundReference(BackgroundReference backgroundReference) {
        this.backgroundReference = backgroundReference;

        for (PicAlbum pic:picAlbums) {
            pic.backgroundReference = backgroundReference;
        }
    }

    public FormatReference getFormatReference() {
        if(formatReference == null){
            return PriceReferences.getDefaultformat();
        }

        return formatReference;
    }

    public void setFormatReference(FormatReference formatReference) {
        this.formatReference = formatReference;
    }

    public int getPicPrice() throws PriceSecurityException {
        //check si reste des photos gratuites
        int priceInCts = 0;
        int promotion = 0;

        //   int formatPrice = format.getModificationPrice();
        //  int marginPrice = margin.getModificationPrice();

        if(getFormatReference() == null){
            priceInCts += PriceReferences.getDefaultformat().getCurPrice();
        }else{
            priceInCts += getFormatReference().getCurPrice();
        }

        priceInCts += getBackgroundPrice();//todo
        priceInCts += getStickersPrice();

        priceInCts *= copy;

        //picPrice += formatPrice + marginPrice;
        //picPrice *= copy;
        //picPrice += (copy - 1) * duplicatedPrice;

      /*  if (duplicated) {
            picPrice += duplicatedPrice;
        }*/

        //picPrice = Promotions.applyFreePicsPromotion(picPrice, format.getModificationID(), modificationsManager, duplicated);
        return priceInCts;
    }

  /*  public int getFormatPrice() throws PriceSecurityException {
        if (formatReference == null) {
            return 0;
        }
        return formatReference.getPriceInCts(formatReference.getCurPriceStr());
    }*/

    public int getStickersSize() {
        Iterator it = actions.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();

            if (pair.getKey().equals(PriceReferences.STICKERS)) {
                ArrayList<Sticker> stickers = (ArrayList<Sticker>) pair.getValue();
                return stickers.size();
            }
        }
        return 0;
    }

    public int getStickersPrice() throws PriceSecurityException {
        int priceInCts = 0;

        Iterator it = actions.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();

            Log.i(LOG_TAG, pair.getKey() + " = " + pair.getValue());

            if (pair.getKey().equals(PriceReferences.STICKERS)) {
                ArrayList<Sticker> stickers = (ArrayList<Sticker>) pair.getValue();
                for (Sticker sticker : stickers) {
                    Log.i(LOG_TAG, "sticker NAME : " + sticker.getName());
                    priceInCts += sticker.getPriceInCts(sticker.getPriceStr());
                }

            }
            //  it.remove();
        }
        return priceInCts;
    }

    public int getBackgroundPrice() throws PriceSecurityException {
        if (backgroundReference == null) {
            return 0;
        }
        return backgroundReference.getPriceInCts(backgroundReference.getPricePrintStr());
    }

    public boolean isEmpty() {
        if (asset == null) {
            return true;
        }
        return false;
    }
}
