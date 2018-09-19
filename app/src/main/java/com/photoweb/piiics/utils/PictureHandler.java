package com.photoweb.piiics.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.photoweb.piiics.R;
import com.photoweb.piiics.model.Asset;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dnizard on 30/06/2017.
 */

public class PictureHandler {
    private static final String TAG = "PictureHandler";
    private Context context;

    private static PictureHandler instance;
    private static SharedPreferences prefs;

    private Map<String, List<String>> orgDict = new HashMap<>();
    private Map<String, Date> printDict = new HashMap<>();
    private Map<String, Date> albumDict = new HashMap<>();

    public Map<String, Map> currentCommand = new HashMap<>();
    private ArrayList<ArrayList<Object>> queue = new ArrayList<>();

    private Boolean inProgress = false;

    public String commandID;
    public String product;
    public float totalPrice;

    public static PictureHandler get() {
        if (instance == null) instance = new PictureHandler();
        return instance;
    }

    //Initialization
    public void init(Context _context) {
        context = _context;
        prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        loadOrgDict();
        loadPrintDict();
        loadAlbumDict();
    }

    // MARK: Initialisation
    private void loadOrgDict() {
        Log.d(TAG, "load ORG");
        File mydir = context.getDir("ORG", Context.MODE_PRIVATE);
        File fileWithinMyDir = new File(mydir, "index.json");

        if (fileWithinMyDir.exists()) {
            Log.d(TAG, "ORG Exists");
            orgDict = loadDictionary(mydir, "index.json");
        } else {
            Log.d(TAG, "ORG Not Exists");
            saveDictionary(orgDict, mydir, "index.json");
        }
    }

    private void loadPrintDict() {
        Log.d(TAG, "load PRINT");
        File mydir = context.getDir("PRINT", Context.MODE_PRIVATE);
        File fileWithinMyDir = new File(mydir, "index.json");

        if (fileWithinMyDir.exists()) {
            Log.d(TAG, "PRINT Exists");
            printDict = loadDictionary(mydir, "index.json");
        } else {
            Log.d(TAG, "PRINT Not Exists");
            saveDictionary(printDict, mydir, "index.json");
        }
    }

    private void loadAlbumDict() {
        Log.d(TAG, "load ALBUM");
        File mydir = context.getDir("ORG", Context.MODE_PRIVATE);
        File fileWithinMyDir = new File(mydir, "index.json");

        if (fileWithinMyDir.exists()) {
            Log.d(TAG, "ALBUM Exists");
            albumDict = loadDictionary(mydir, "index.json");
        } else {
            Log.d(TAG, "ALBUM Not Exists");
            saveDictionary(albumDict, mydir, "index.json");
        }
    }

    //COMMAND UTILS
    private Map getCurrentCommand(String commandID, String product) {
        File mydir = context.getDir(product + "/" + commandID, Context.MODE_PRIVATE);

        return loadDictionary(mydir, "index.json");
    }

    private void loadCurrentCommand(String commandID, String product) {
        File mydir = context.getDir(product + "/" + commandID, Context.MODE_PRIVATE);

        currentCommand = loadDictionary(mydir, "index.json");
    }

    private void saveCurrentCommand(String commandID, String product) {
        File mydir = context.getDir(product + "/" + commandID, Context.MODE_PRIVATE);

        saveDictionary(currentCommand, mydir, "index.json");
    }

    public String getFinalWithImage(String photoID, String commandID, String product) {
        File productdir = context.getDir(product, Context.MODE_PRIVATE);
        File mydir = new File(productdir, commandID);

        File finalDir = new File(mydir, "FINAL");

        if (product.equals("PRINT")) {
            return finalDir.getAbsolutePath() + "/" + photoID + ".jpg";
        } else {
            return finalDir.getAbsolutePath() + "/" + photoID + ".png";
        }
    }

    public String getCropWithImage(String photoID, String commandID, String product) {
        File productdir = context.getDir(product, Context.MODE_PRIVATE);
        File mydir = new File(productdir, commandID);
        File finalDir = new File(mydir, "CROP");

        return finalDir.getAbsolutePath() + "/" + photoID + ".jpg";
    }

    /*func getBackgroundWithImage(background: String)->URL
    {
        return self.getDocumentsDirectory().appendingPathComponent("APP/Thumbnails/" + background)
    }*/

    // MARK: Photo treatment

    public void addToQueue(Asset asset, String commandID, String product) {
        Log.d(TAG, asset.identifier + " " + commandID + " " + product);

        String photoID = UUID.randomUUID().toString();

        Map<String, Map> commandDict = new HashMap<>();

        File productdir = context.getDir(product, Context.MODE_PRIVATE);
        File mydir = new File(productdir, commandID);

        if (mydir.exists()) {
            commandDict = loadDictionary(mydir, "index.json");

        } else {
            // creates the directory if not present yet
            mydir.mkdir();

            File cropDir = new File(mydir, "CROP");
            cropDir.mkdir();
            File finalDir = new File(mydir, "FINAL");
            finalDir.mkdir();

            saveDictionary(commandDict, mydir, "index.json");

            if (product.equals("ALBUM")) {
                Map<String, Object> first = new HashMap<>();

                first.put("id", "FIRST");
                first.put("background", Utils.defaultPageBg);
                first.put("product", "ALBUM");
                first.put("commandID", commandID);
                first.put("format", "Front");
                first.put("index", 0);

                commandDict.put("FIRST", first);

                ArrayList<Object> fList = new ArrayList<>();
                fList.add("FIRST");
                fList.add(commandID);
                fList.add(product);
                fList.add("FIRST");

                queue.add(fList);
            }
        }

        Map<String, Object> map = new HashMap<>();

        map.put("id", asset.identifier);
        map.put("background", Utils.defaultPageBg);
        map.put("product", product);
        map.put("commandID", commandID);
        map.put("format", (product.equals("PRINT")) ? asset.format : "M");
        map.put("index", commandDict.size());
        map.put("source", asset.imageURL);

        commandDict.put(photoID, map);

        saveDictionary(commandDict, mydir, "index.json");

        currentCommand = commandDict;

        ArrayList<Object> list = new ArrayList<>();
        list.add(asset);
        list.add(commandID);
        list.add(product);
        list.add(photoID);

        queue.add(list);
        Log.d(TAG, "list photoID : " + photoID);
        Log.d(TAG, "queue(0) photoID :  " + (String) queue.get(0).get(3));
        if (queue.size() > 1) {
            Log.d(TAG, "queue(0) photoID :  " + (String) queue.get(1).get(3));
        }

        Log.d(TAG, "Command count " + commandDict.size());

        operateQueue();
        Log.d(TAG, "AddToQueue() FINISH");
    }

    public void removeFromQueue(String asset, String commandID, String product)// mauvais nom -> removeFromCommandDict
    {
        Map<String, Map> commandDict = new HashMap<>();

        File productdir = context.getDir(product, Context.MODE_PRIVATE);
        File mydir = new File(productdir, commandID);
        commandDict = loadDictionary(mydir, "index.json");

        Iterator it = commandDict.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry item = (Map.Entry) it.next();

            Map value = (Map) item.getValue();

            if (value.get("id").equals(asset)) {
                it.remove();
            }
        }

        saveDictionary(commandDict, mydir, "index.json");

        currentCommand = commandDict;
    }

    public void generateCropFromPics() {
        Log.d(TAG, "START GENERATE CROP FROM PRICS");
        Iterator it = currentCommand.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry item = (Map.Entry) it.next();

            Map value = (Map) item.getValue();
            String photoID = (String) item.getKey();

            String commandID = (String) value.get("commandID");
            String product = (String) value.get("product");
            String url = (String) value.get("source");

            if (url.startsWith("http")) {
                File mydir = context.getDir("ORG", Context.MODE_PRIVATE);
                String identifier = (String) value.get("id");
                File file = new File(mydir, identifier + ".jpg");

                url = file.getAbsolutePath();
            }

            File productdir = context.getDir(product, Context.MODE_PRIVATE);
            File mydir = new File(productdir, commandID);
            File cropDir = new File(mydir, "CROP");

            generateCrop(url, cropDir.getAbsolutePath() + "/" + photoID + ".jpg", product);
            Log.d(TAG, "    - CROP GENERATED");
        }
        Log.d(TAG, "END GENERATE CROP FROM PRICS");
    }

    private void operateQueue() {
        Log.d(TAG, "queue size " + queue.size());

        if (!inProgress && queue.size() > 0) {
            inProgress = true;
            Log.d(TAG, "start queue");

            ArrayList<Object> operation = queue.get(0);

            Log.i(TAG, "photoID from get(0) : " + (String) operation.get(3));

            //TODO FIRST
            if (operation.get(3).equals("FIRST")) {
                generateFirst((String) operation.get(1));
                return;
            }

            //TODO LAST
            if (operation.get(3).equals("LAST")) {
                generateLast((String) operation.get(1));
                return;
            }

            final Asset asset = (Asset) operation.get(0);
            final String commandID = (String) operation.get(1);
            final String product = (String) operation.get(2);
            final String photoID = (String) operation.get(3);

            Log.i(TAG, "Asset from get(0) : identifier : " + asset.identifier);
            Log.i(TAG, "Asset from get(0) : imageURL : " + asset.imageURL);
            Log.i(TAG, "photoID from get(0) : " + photoID);

            if (orgDict.get(asset.identifier) != null) {
                Log.d(TAG, "Already exists");

                String url = asset.imageURL;

                if (url.startsWith("http")) {
                    File mydir = context.getDir("ORG", Context.MODE_PRIVATE);
                    File file = new File(mydir, asset.identifier + ".jpg");

                    url = file.getAbsolutePath();
                }

                orgDict.get(asset.identifier).add(commandID);

                File productdir = context.getDir(product, Context.MODE_PRIVATE);
                File mydir = new File(productdir, commandID);
                File cropDir = new File(mydir, "CROP");

            //    generateCrop(url, cropDir.getAbsolutePath() + "/" + photoID + ".jpg", product);
            } else {
                Log.d(TAG, "Has to create : " + asset.imageURL);

                final String fileName = asset.identifier + ".jpg";
                Target target = new Target() {

                    @Override
                    public void onPrepareLoad(Drawable arg0) {
                        return;
                    }

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {

                        try {
                            File mydir = context.getDir("ORG", Context.MODE_PRIVATE);
                            File file = new File(mydir, fileName);
                            try {
                                file.createNewFile();
                                FileOutputStream ostream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                ostream.flush();
                                ostream.close();
                                Log.d(TAG, "Image saved");

                                File productdir = context.getDir(product, Context.MODE_PRIVATE);
                                File commanddir = new File(productdir, commandID);
                                File cropDir = new File(commanddir, "CROP");

//                                generateCrop(file.getAbsolutePath(), cropDir.getAbsolutePath() + "/" + photoID + ".jpg", product);
                            } catch (IOException e) {
                                Log.e("IOException", e.getLocalizedMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable arg0) {
                        Log.d(TAG, "Image failed");
                        return;
                    }
                };

                if (asset.source.equals("Dropbox")) {
                    PicassoClient.getPicasso()
                            .load(FileThumbnailRequestHandler.buildPicassoUri(asset.imageURL))
                            .into(target);

                } else if (asset.imageURL.startsWith("http")) {
                    Log.d(TAG, "Other than dropbox");

                    Picasso.with(context)
                            .load(asset.imageURL)
                            .into(target);
                } else {
                    File productdir = context.getDir(product, Context.MODE_PRIVATE);
                    File mydir = new File(productdir, commandID);
                    File cropDir = new File(mydir, "CROP");

                    //      generateCrop(asset.imageURL, cropDir.getAbsolutePath() + "/" + photoID + ".jpg", product);
                }

                ArrayList<String> list = new ArrayList<>();
                list.add(commandID);

                orgDict.put(asset.identifier, list);

                File mydir = context.getDir("ORG", Context.MODE_PRIVATE);
                saveDictionary(orgDict, mydir, "index.json");
            }
        }
    }

    private void generateFirst(final String commandID) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inDither = true;

                Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.plus_placeholder, options);

                Bitmap result = TransformationHandler.MarginBottomAlbumGabarit(icon, icon);

                File productdir = context.getDir("ALBUM", Context.MODE_PRIVATE);
                File mydir = new File(productdir, commandID);
                File finalDir = new File(mydir, "FINAL");

                File pageFile = new File(finalDir.getAbsolutePath() + "/FIRST.png");

                FileOutputStream fosPage = null;
                try {
                    fosPage = new FileOutputStream(pageFile);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    result.compress(Bitmap.CompressFormat.PNG, 100, fosPage);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "error " + e.getMessage());
                } finally {
                    try {
                        fosPage.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "close error " + e.getMessage());
                    }
                }

                result.recycle();
                icon.recycle();

                if (queue.size() > 0) {
                    queue.remove(0);
                }

                inProgress = false;
                operateQueue();
            }
        });
    }

    private void generateLast(final String commandID) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.logo_wo);

                Bitmap result = TransformationHandler.lastPage(true, icon);

                File productdir = context.getDir("ALBUM", Context.MODE_PRIVATE);
                File mydir = new File(productdir, commandID);
                File finalDir = new File(mydir, "FINAL");

                File pageFile = new File(finalDir.getAbsolutePath() + "/LAST.png");

                FileOutputStream fosPage = null;
                try {
                    fosPage = new FileOutputStream(pageFile);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    result.compress(Bitmap.CompressFormat.PNG, 100, fosPage);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "error " + e.getMessage());
                } finally {
                    try {
                        fosPage.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "close error " + e.getMessage());
                    }
                }

                result.recycle();
                icon.recycle();

                if (queue.size() > 0) {
                    queue.remove(0);
                }

                inProgress = false;
                operateQueue();
            }
        });
    }

    private void generateCrop(final String org, final String dest, final String product) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Generate crop for " + org);
                Bitmap result = TransformationHandler.initCrop(org, product);
                Log.d(TAG, "size of bitmap : " + result.getHeight());
                Log.d(TAG, "Copy to final");
                File crop = new File(dest);

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(crop);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    result.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "error " + e.getMessage());
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "close error " + e.getMessage());
                    }
                }

                if (product.equals("PRINT")) {
                    String newURL = dest.replace("CROP", "FINAL");
                    File finale = new File(newURL);

                    try {
                        copyFile(crop, finale);
                        Log.d(TAG, "Copy succeed to " + newURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Copy failed to " + newURL + " : " + e.getMessage());
                    }
                } else {
                    Bitmap page = TransformationHandler.drawOnPage(result, "DefaultAlbumGabarit");
                    String finalDest = dest.replace("CROP", "FINAL");
                    finalDest = finalDest.replace(".jpg", ".png");

                    Log.d(TAG, "Copy to final");
                    File pageFile = new File(finalDest);

                    FileOutputStream fosPage = null;
                    try {
                        fosPage = new FileOutputStream(pageFile);
                        // Use the compress method on the BitMap object to write image to the OutputStream
                        page.compress(Bitmap.CompressFormat.PNG, 100, fosPage);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "error " + e.getMessage());
                    } finally {
                        try {
                            fosPage.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "close error " + e.getMessage());
                        }
                    }
                    page.recycle();
                }

                result.recycle();
                if (queue.size() > 0) {
                    queue.remove(0);
                }
                inProgress = false;
                operateQueue();
            }
        });
    }

    public void addLast(String commandID) {
        Map<String, Map> commandDict = new HashMap<>();

        File productdir = context.getDir("ALBUM", Context.MODE_PRIVATE);
        File mydir = new File(productdir, commandID);

        commandDict = loadDictionary(mydir, "index.json");

        Map<String, Object> first = new HashMap<>();

        first.put("id", "LAST");
        first.put("background", Utils.defaultPageBg);
        first.put("product", "ALBUM");
        first.put("commandID", commandID);
        first.put("format", "Back");
        first.put("index", commandDict.size());

        commandDict.put("LAST", first);

        ArrayList<Object> fList = new ArrayList<>();
        fList.add("LAST");
        fList.add(commandID);
        fList.add("ALBUM");
        fList.add("LAST");

        saveDictionary(commandDict, mydir, "index.json");

        currentCommand = commandDict;

        queue.add(fList);

        operateQueue();
    }

    // Utils
    private Map loadDictionary(File dir, String file) {
        File fileWithinMyDir = new File(dir, file);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileWithinMyDir);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Map anotherMap = (Map) ois.readObject();
            ois.close();

            return anotherMap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    private void saveDictionary(Map map, File dir, String file) {
        File fileWithinMyDir = new File(dir, file);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileWithinMyDir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}
