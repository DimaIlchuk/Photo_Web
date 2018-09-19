package com.photoweb.piiics.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ad4screen.sdk.A4S;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.appsee.Appsee;
import com.photoweb.piiics.Adapters.AlbumAdapter;
import com.photoweb.piiics.Adapters.AlbumEditorPicsAdapter;
import com.photoweb.piiics.Adapters.MyGridAdapter;
import com.photoweb.piiics.PiiicsExceptionHandler;
import com.photoweb.piiics.R;
import com.photoweb.piiics.model.Album;
import com.photoweb.piiics.model.Asset;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.SelectPicsStates;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.PopUps;
import com.photoweb.piiics.utils.PriceReferences;
import com.photoweb.piiics.utils.SocialHandler;
import com.photoweb.piiics.utils.UserInfo;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static com.photoweb.piiics.utils.BitmapsManager.saveBitmap;

/**
 * Created by thomas on 19/04/2017.
 */

public class SelectPicsActivity extends AppCompatActivity {
    private static final String LOG_TAG = "SelectPicsActivity";

    ArrayList<Asset> selectedItems = new ArrayList<Asset>(); //list which contains pointers to selected items in other lists
    ArrayList<Asset> localItems = new ArrayList<Asset>();
    ArrayList<Album> localAlbums = new ArrayList<>();
    ArrayList<Asset> currentItems = new ArrayList<Asset>(); //pointer list

    private String launchedBy;
    private Boolean insideAlbum = false;

    int backgroundThreadsLoading;

    private Cursor cursor;

    MyGridAdapter adapter;
    AlbumAdapter albumAdapter;

    @BindView(R.id.gridview)
    GridView gridView;
    @BindView(R.id.albumList)
    ListView listAlbum;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.rl_emptyview)
    RelativeLayout rlEmpty;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private int CURRENT_STATE = SelectPicsStates.LOCAL_ITEMS;

    @BindView(R.id.selected_pics_icon_counter)
    TextView selectedPicsCounter;
    @BindView(R.id.local_pics_icon_counter)
    TextView localPicsCounter;
    @BindView(R.id.fb_pics_icon_counter)
    TextView fbPicsCounter;
    @BindView(R.id.goo_pics_icon_counter)
    TextView gooPicsCounter;
    @BindView(R.id.insta_pics_icon_counter)
    TextView instaPicsCounter;
    @BindView(R.id.dp_pics_icon_counter)
    TextView dpPicsCounter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static SharedPreferences prefs;

    private static final String TAG = "SelectPics";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Appsee.start("ca29b14487ac4c8e843ecb90c54c413f");

        Thread.setDefaultUncaughtExceptionHandler(new PiiicsExceptionHandler(this));

        setContentView(R.layout.activity_select_pics);
        ButterKnife.bind(this);
        setToolbar();
        updateCounters();

        adapter = new MyGridAdapter(this, localItems);
        albumAdapter = new AlbumAdapter(this, localAlbums);
        checkPermissionStorage();

        //currentItems = localItems;
        //Log.d(TAG, "1. Current :" + currentItems.size() + " - Local :" + localItems.size());
        //adapter.switchPicsType(localItems);

        gridView.setAdapter(adapter);
        gridView.setEmptyView(rlEmpty);

        listAlbum.setAdapter(albumAdapter);

        prefs = getApplicationContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);

        SocialHandler.get().setContext(this);
        SocialHandler.get().getFacebookAlbums();
        SocialHandler.get().getGooglePhotos();
        SocialHandler.get().getInstagramPhotos();

        launchedBy = getIntent().getStringExtra("launchedBy");

        //if (launchedBy == null || !launchedBy.equals(AlbumEditorPicsAdapter.LOG_TAG)) {
        if(CommandHandler.get().currentCommand == null){
            //PictureHandler.get().init(this);
            String product = getIntent().getStringExtra("PRODUCT");
            Long tsLong = System.currentTimeMillis()/1000;
            String commandID = tsLong.toString() + "_" + UUID.randomUUID().hashCode();
            Log.d(TAG, commandID);

            CommandHandler.get().init(commandID, product);

            JSONObject eventValue = new JSONObject();
            A4S.get(this).trackEvent(1001, eventValue.toString());

            LocalBroadcastManager.getInstance(this).registerReceiver(onUpdateReceived,
                    new IntentFilter("UpdateAsset"));
        }
    }

    @OnClick(R.id.selected_pics_icon)
    public void onSelectedClick() {
        insideAlbum = false;
        progressBar.setVisibility(View.GONE);

        gridView.setVisibility(View.VISIBLE);
        listAlbum.setVisibility(View.GONE);

        CURRENT_STATE = SelectPicsStates.SELECTED_ITEMS;
        currentItems = selectedItems;
        Log.d(TAG, "2. Current :" + currentItems.size() + " - Local :" + localItems.size());
        //refreshSelectedItems();
        adapter.switchPicsType(selectedItems);

        updateSelectWithTag(1000);
    }

    @OnClick(R.id.local_pics_icon)
    public void onLocalClick() {
        insideAlbum = false;
        progressBar.setVisibility(View.GONE);

        gridView.setVisibility(View.GONE);
        listAlbum.setVisibility(View.VISIBLE);

        CURRENT_STATE = SelectPicsStates.LOCAL_ITEMS;
        if (localAlbums.size() == 0) { ///
            checkPermissionStorage();
        }else{
            albumAdapter.switchAlbumsType(localAlbums);

            listAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    gridView.setVisibility(View.VISIBLE);
                    listAlbum.setVisibility(View.GONE);
                    insideAlbum = true;

                    Album album = localAlbums.get(i);

                    currentItems = (ArrayList<Asset>) album.assets.clone();
                    adapter.switchPicsType(currentItems);
                }
            });
        }
        /*currentItems = (ArrayList<Asset>)localItems.clone();
        Log.d(TAG, "3. Current :" + currentItems.size() + " - Local :" + localItems.size());
        adapter.switchPicsType(currentItems);*/

        updateSelectWithTag(1001);
    }

    @OnClick({R.id.fb_pics_icon, R.id.goo_pics_icon, R.id.insta_pics_icon, R.id.dp_pics_icon})
    public void onSocialClick(View view) {
        insideAlbum = false;
        CURRENT_STATE = Integer.parseInt(view.getTag().toString());
        updateAssets();
        updateSelectWithTag(Integer.parseInt(view.getTag().toString()));
    }

    @OnClick(R.id.empty_view)
    public void onEmptyClick(View view) {
        progressBar.setVisibility(View.VISIBLE);

        switch (CURRENT_STATE) {
            case 1002:
                SocialHandler.get().connect("facebook", false);
                break;
            case 1003:
                checkPermissionAccount();
                break;
            case 1004:
                SocialHandler.get().connect("instagram", false);
                break;
            case 1005:
                SocialHandler.get().connect("dropbox", false);
                break;
            default:
                break;
        }
    }

    @OnItemClick(R.id.gridview)
    public void onItemClick(int position) {
        Asset item = currentItems.get(position);
        if (launchedBy != null && launchedBy.equals(AlbumEditorPicsAdapter.LOG_TAG)) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", item);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            itemSwapSelectedState(item);
            refreshSelectedItems(item);
            updateCounters();
            adapter.notifyDataSetChanged();
        }
    }

    public void itemSwapSelectedState(Asset item) {
        if (item.selected) {
            item.selected = false;
            // PictureHandler.get().removeFromQueue(item.identifier, commandID, product);
        } else {
            item.selected = true;
            // PictureHandler.get().addToQueue(item, commandID, product);
        }
    }

    public void refreshSelectedItems(Asset item) {
        if (item.selected) {
            selectedItems.add(item);
            CommandHandler.get().currentCommand.addAsset(item);
        } else {
            selectedItems.remove(item);
            CommandHandler.get().currentCommand.removeAsset(item);
        }
    }

    private void updateCounters() {
        /*
        if (CURRENT_STATE == SelectPicsStates.SELECTED_ITEMS) {
            updateIconCounter(selectedPicsCounter, selectedItems.size());
            updateIconCounter(localPicsCounter, getSelectedPicsOf(localItems));
        } else if (CURRENT_STATE == SelectPicsStates.LOCAL_ITEMS) {
            updateIconCounter(selectedPicsCounter, selectedItems.size());
            updateIconCounter(localPicsCounter, getSelectedPicsOf(localItems));
        }
        */
        updateIconCounter(selectedPicsCounter, selectedItems.size());
        updateIconCounter(localPicsCounter, getAssetByNetwork("Local"));
        updateIconCounter(fbPicsCounter, getAssetByNetwork("Facebook"));
        updateIconCounter(gooPicsCounter, getAssetByNetwork("Google"));
        updateIconCounter(instaPicsCounter, getAssetByNetwork("Instagram"));
        updateIconCounter(dpPicsCounter, getAssetByNetwork("Dropbox"));
    }

    private int getAssetByNetwork(String network) {
        int x = 0;
        for (Asset item : selectedItems) {
            if (item.source.equals(network)) {
                x++;
            }
        }
        return x;
    }

    private void updateIconCounter(TextView tv, int picsNumber) {
        if (picsNumber == 0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(String.valueOf(picsNumber));
        }
    }

    private BroadcastReceiver onUpdateReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onEventReceived: " + intent);
            updateAssets();
        }
    };

    private void updateAssets() {
        progressBar.setVisibility(View.GONE);

        //currentItems = socialItems;
        Log.d(TAG, "4. Current :" + currentItems.size() + " - Local :" + localItems.size());
        //adapter.switchPicsType(socialItems);

        switch (CURRENT_STATE) {
            case 1002:
                emptyView.setText(getString(R.string.CONNECT, "Facebook"));
                emptyView.setBackgroundColor(ContextCompat.getColor(this, R.color.facebook_connector));
                emptyView.setTextColor(ContextCompat.getColor(this, R.color.piics_white));
//                Log.d(TAG, "Facebook " + SocialHandler.get().albums.get("Facebook").size());

                if(SocialHandler.get().albums.get("Facebook") == null || SocialHandler.get().albums.get("Facebook").size() == 0){
                    gridView.setVisibility(View.VISIBLE);
                    listAlbum.setVisibility(View.GONE);

                    currentItems.clear();

                    adapter.switchPicsType(currentItems);
                }else{
                    gridView.setVisibility(View.GONE);
                    listAlbum.setVisibility(View.VISIBLE);

                    final List<Album> listAlbums = SocialHandler.get().albums.get("Facebook");
                    Log.d("Select", "Facebook " + listAlbums.size());

                    albumAdapter.switchAlbumsType(listAlbums);

                    listAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            gridView.setVisibility(View.VISIBLE);
                            listAlbum.setVisibility(View.GONE);
                            insideAlbum = true;

                            Album album = listAlbums.get(i);

                            currentItems = (ArrayList<Asset>) album.assets.clone();
                            Log.d(TAG, "5. Current :" + currentItems.size() + " - Local :" + localItems.size());
                            adapter.switchPicsType(album.assets);
                        }
                    });
                }

                break;
            case 1003:
                emptyView.setText(getString(R.string.CONNECT, "Google Photos"));
                emptyView.setBackgroundColor(ContextCompat.getColor(this, R.color.google_connector));
                emptyView.setTextColor(ContextCompat.getColor(this, R.color.piics_black));

                gridView.setVisibility(View.VISIBLE);
                listAlbum.setVisibility(View.GONE);

                if (SocialHandler.get().photos.get("Google") == null) {
                    Log.d("Select", "Google is empty");
                    currentItems.clear();
                    Log.d(TAG, "6. Current :" + currentItems.size() + " - Local :" + localItems.size());
                } else {
                    Log.d("Select", "Google is not empty");
                    currentItems = (ArrayList<Asset>) SocialHandler.get().photos.get("Google").clone();
                    Log.d(TAG, "7. Current :" + currentItems.size() + " - Local :" + localItems.size());

                    if(currentItems.size() < 100){

                        if(!isFinishing()){
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(this);
                            }
                            builder.setTitle(R.string.INFOS)
                                    .setMessage(R.string.GOOGLEDRIVE)
                                    .setPositiveButton(R.string.GO, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://support.google.com/drive/answer/6156103"));
                                            startActivity(i);
                                        }
                                    })
                                    .setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                }

                adapter.switchPicsType(currentItems);
                break;
            case 1004:
                emptyView.setText(getString(R.string.CONNECT, "INSTAGRAM"));
                emptyView.setBackgroundColor(ContextCompat.getColor(this, R.color.instagram_connector));
                emptyView.setTextColor(ContextCompat.getColor(this, R.color.piics_white));

                gridView.setVisibility(View.VISIBLE);
                listAlbum.setVisibility(View.GONE);

                if (SocialHandler.get().photos.get("Instagram") == null) {
                    Log.d("Select", "Instagram is empty");
                    currentItems.clear();
                    Log.d(TAG, "8. Current :" + currentItems.size() + " - Local :" + localItems.size());
                } else {
                    Log.d("Select", "Instagram is not empty");
                    currentItems = (ArrayList<Asset>) SocialHandler.get().photos.get("Instagram").clone();
                    Log.d(TAG, "9. Current :" + currentItems.size() + " - Local :" + localItems.size());
                }

                adapter.switchPicsType(currentItems);
                break;
            case 1005:
                emptyView.setText(getString(R.string.CONNECT, "DROPBOX"));
                emptyView.setBackgroundColor(ContextCompat.getColor(this, R.color.dropbox_connector));
                emptyView.setTextColor(ContextCompat.getColor(this, R.color.piics_white));

                if (SocialHandler.get().photos.get("Dropbox") == null && SocialHandler.get().albums.get("Dropbox") == null) {
                    Log.d("Select", "Dropbox is empty");
                    gridView.setVisibility(View.VISIBLE);
                    listAlbum.setVisibility(View.GONE);

                    currentItems.clear();
                    Log.d(TAG, "10. Current :" + currentItems.size() + " - Local :" + localItems.size());

                    adapter.switchPicsType(currentItems);
                } else if (SocialHandler.get().photos.get("Dropbox") == null) {
                    Log.d("Select", "Dropbox is not empty");
                    gridView.setVisibility(View.GONE);
                    listAlbum.setVisibility(View.VISIBLE);

                    final List<Album> listAlbumsDB = SocialHandler.get().albums.get("Dropbox");

                    albumAdapter.switchAlbumsType(listAlbumsDB);

                    listAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            SocialHandler.get().setDBPath(listAlbumsDB.get(i).name);
                            SocialHandler.get().ConnectDropbox();
                            insideAlbum = true;

                        }
                    });
                } else {
                    gridView.setVisibility(View.VISIBLE);
                    listAlbum.setVisibility(View.GONE);

                    currentItems = (ArrayList<Asset>) SocialHandler.get().photos.get("Dropbox").clone();
                    Log.d(TAG, "11. Current :" + currentItems.size() + " - Local :" + localItems.size());

                    adapter.switchPicsType(currentItems);
                }

                break;
            default:
                break;
        }
    }

    public void updateSelectWithTag(int tag) {
        for (int i = 0; i < 6; i++) {
            View view = findViewById(R.id.llSources).findViewWithTag(Integer.toString(2000 + i));
            if (i + 2000 == tag + 1000) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
            }

        }
    }

    public static File getDirPath(String nameDir) {
        File dir = null;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File root = Environment.getExternalStorageDirectory();
            dir = new File(root.getAbsolutePath() + "/" + nameDir);///
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        return dir;
    }

    private void clearDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    private ArrayList<String> saveSelectedBitmaps(ArrayList<String> selectedPicPaths, String dirPath) { // verifier droits ecriture
        File dirBitmapsSaved = getDirPath(dirPath);
        if (dirBitmapsSaved == null) { // save dans un autre endroit
            Toast.makeText(this, "Impossible d'accéder au dossier de destination", Toast.LENGTH_LONG).show();
            return null; // checker cette erreur
        }
        clearDir(dirBitmapsSaved);// cas user revient en arrière sans faire exprès ?
        ArrayList<String> bitmapPaths = new ArrayList<String>();
        for (String sourceFullPath : selectedPicPaths) {
            String bitmapCopyPath = saveBitmap(sourceFullPath, dirBitmapsSaved, 1);
            if (bitmapCopyPath != null) {
                bitmapPaths.add(bitmapCopyPath);
            }
        }
        return bitmapPaths;
    }

    @OnClick(R.id.continue_icon)
    public void continueToEditor() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(SelectPicsActivity.this, R.string.ATLEAST_1, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedItems.size() < 20 && CommandHandler.get().currentCommand.getProduct().equals("ALBUM")) {
            Toast.makeText(SelectPicsActivity.this, R.string.ATLEAST_20, Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        JSONObject eventValue = new JSONObject();
        A4S.get(this).trackEvent(1002, eventValue.toString());

        if (CommandHandler.get().currentCommand.getProduct().equals("PRINT") && selectedItems.size() > UserInfo.getInt("print_available") + UserInfo.getInt("print_bonus") && UserInfo.getInt("id") > 0) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle(R.string.INFOS)
                    .setMessage(getString(R.string.ABOVE_FREEPRINT, PriceReferences.getDefaultformat().getCurPriceStr()))
                    .setPositiveButton(R.string.UNDERSTAND, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            createCommand();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            createCommand();
        }
    }

    private void createCommand() {
        String commandID = UUID.randomUUID().toString();
        String product = getIntent().getStringExtra("PRODUCT");

        Command command = new Command(commandID, product);

        /*if(product.equals("ALBUM"))
            PictureHandler.get().addLast(commandID);*/

        int index = 0;
        for (Asset asset : selectedItems) {
            EditorPic editorPic = new EditorPic(asset, command.getProduct(), index);
            command.getEditorPics().add(editorPic);
            index++;

            //   createEditorPicFromAsset(editorPic, command);
        }
        progressBar.setVisibility(View.GONE);

        /*if (UserInfo.getInt("id") == 0) {
            launchLoginInCommandActivity(command);
        } else {
            launchEditorActivity(command);
        }*/

        if(product.equals("ALBUM")) {
            launchBookManager(command);
        } else {
            launchEditorActivity(command);
        }
    }


    private void launchBookManager(Command command) {
        Intent intent = new Intent(SelectPicsActivity.this, BookManagerActivity.class);
        startActivity(intent);
    }

    private void launchLoginInCommandActivity(Command command) {

        AdjustEvent event = new AdjustEvent("wjbgsu");
        Adjust.trackEvent(event);

        Intent intent = new Intent(SelectPicsActivity.this, LoginInCommandActivity.class);
        startActivity(intent);
    }


    private void launchEditorActivity(Command command) {

        Intent intent = new Intent(SelectPicsActivity.this, EditorActivity.class);
     //   intent.putExtra("COMMAND", command);
        startActivity(intent);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private ArrayList<String> gatherPicsPaths() {
        ArrayList<String> picsPaths = new ArrayList<String>();
        for (Asset item : selectedItems) {
            picsPaths.add(item.imageURL);
        }
        return picsPaths;
    }

    private int getSelectedPicsOf(ArrayList<Asset> items) {
        int x = 0;
        for (Asset item : items) {
            if (item.selected) {
                x++;
            }
        }
        return x;
    }

    private boolean listImageFiles(String path) {

        String[] list;
        File filePath = new File(path);///
        if (filePath.isDirectory()) {
            list = filePath.list();
            // This is a folder
            for (String file : list) {
                if (!listImageFiles(path + "/" + file))
                    return false;
            }
        } else if ((filePath.getAbsolutePath().endsWith(".jpg")) || (filePath.getAbsolutePath().endsWith(".png"))) {
            //GridViewItem item = new GridViewItem(filePath.getAbsolutePath(), SelectPicsStates.LOCAL_ITEMS);
            Asset item = new Asset(filePath.getAbsolutePath().substring(filePath.getAbsolutePath().lastIndexOf("/") + 1), filePath.getAbsolutePath(), filePath.getAbsolutePath(), "Local");
            localItems.add(item);
        }
        return true;
    }


    private int REQUEST_PERMISSION_STORAGE = 1;
    private int REQUEST_PERMISSION_ACCOUNT = 11111;

    private void checkPermissionStorage() {
        String[] PERMISSIONS_STORAGE = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED) {// tester ce code avec 1 == 1 ?
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_STORAGE);// revoir la doc, pas mal e details
        } else {
            permissionStorageGranted();
            //checkPermissionAccount();
        }
    }

    private void checkPermissionAccount() {
        String[] PERMISSIONS_ACCOUNT = {
                Manifest.permission.GET_ACCOUNTS
        };
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

        if (readPermission != PackageManager.PERMISSION_GRANTED) {// tester ce code avec 1 == 1 ?
            ActivityCompat.requestPermissions(this, PERMISSIONS_ACCOUNT, REQUEST_PERMISSION_ACCOUNT);// revoir la doc, pas mal e details
        } else {
            permissionAccountGranted();
        }
    }

    private void getPhotoLibrary() {
        // Set up an array of the Thumbnail Image ID column we want
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATA
        };

        String orderBy = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC";

        cursor = getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                orderBy);


        Log.i("ListingImages"," query count=" + cursor.getCount());

        HashMap<String, Album> tmp = new HashMap<>();

        if (cursor.moveToFirst()) {
            String bucket;
            String date;
            int bucketColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);

            int dataColumn = cursor.getColumnIndex(
                    MediaStore.Images.Media.DATA
            );

            int idColum = cursor.getColumnIndex(
                    MediaStore.Images.Media._ID
            );

            long today = System.currentTimeMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = sdf.format(today);

            do {
                // Get the field values
                bucket = cursor.getString(bucketColumn);

                // Do something with the values.
                Log.i("ListingImages", " bucket=" + bucket);

                if(tmp.get(bucket) == null){
                    Album album = new Album();

                    //get your values
                    album.id = "localAlbum_" + bucket;
                    album.name = bucket;
                    album.createdAt = dateString;
                    album.assets = new ArrayList<>();

                    tmp.put(bucket, album);
                }

                Asset item = new Asset(cursor.getString(idColum), cursor.getString(dataColumn), cursor.getString(dataColumn), "Local");
                tmp.get(bucket).assets.add(item);

            } while (cursor.moveToNext());

            Map<String, Album> map = new TreeMap<>(tmp);

            for(Map.Entry<String, Album> entry : map.entrySet()) {
                Album value = entry.getValue();

                Log.i("ListingImages", " bucket=" + entry.getKey() + " size : " + value.assets.size());

                localAlbums.add(value);

                // do what you have to do here
                // In your case, another loop.
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (permissions.length == 0) {
                permissionStorageDenied();
                checkPermissionStorage();
                return;
            }
            for (int x : grantResults) {
                if (x != PackageManager.PERMISSION_GRANTED) {
                    permissionStorageDenied();
                    checkPermissionStorage();
                    return;
                }
            }
            permissionStorageGranted();
            checkPermissionStorage();
        }

        if (requestCode == REQUEST_PERMISSION_ACCOUNT) {
            if (permissions.length == 0) {
                permissionAccountDenied();
                return;
            }
            for (int x : grantResults) {
                if (x != PackageManager.PERMISSION_GRANTED) {
                    permissionAccountDenied();
                    return;
                }
            }
            permissionAccountGranted();
        }
    }

    public void permissionStorageDenied() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(R.string.INFOS)
                .setMessage("Cette permission est indispensable pour nous permettre d'accéder à vos photos.")
                .setPositiveButton("Réessayer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        checkPermissionStorage();
                    }
                })
                .setNegativeButton("Réessayer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void permissionStorageGranted() {
        getPhotoLibrary();
        //adapter.switchPicsType(localItems);
        //currentItems = localItems;
        //Log.d(TAG, "12. Current :" + currentItems.size() + " - Local :" + localItems.size());

        gridView.setVisibility(View.GONE);
        listAlbum.setVisibility(View.VISIBLE);

        Log.d("Select", "Local Albums " + localAlbums.size());

        albumAdapter.switchAlbumsType(localAlbums);

        listAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gridView.setVisibility(View.VISIBLE);
                listAlbum.setVisibility(View.GONE);
                insideAlbum = true;

                Album album = localAlbums.get(i);

                currentItems = (ArrayList<Asset>) album.assets.clone();
                adapter.switchPicsType(currentItems);
            }
        });


    }

    public void permissionAccountDenied() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(R.string.INFOS)
                .setMessage("Cette permission est indispensable pour nous permettre d'accéder à votre compte Google Photos.")
                .setPositiveButton("Réessayer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        checkPermissionAccount();
                    }
                })
                .setNegativeButton("Réessayer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();////
    }

    public void permissionAccountGranted() {
        SocialHandler.get().initGooglePhotos();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        String launchedBy = intent.getStringExtra("launchedBy");

        Log.i(LOG_TAG, "On SupportNavigateUp, launchedBy : " + launchedBy);

        if(insideAlbum){
            gridView.setVisibility(View.GONE);
            listAlbum.setVisibility(View.VISIBLE);

            insideAlbum = false;
        }else if (launchedBy != null && launchedBy.equals("MainActivity")) {
            PopUps.popUpCancelCommand(SelectPicsActivity.this, getString(R.string.ATTENTION), getString(R.string.LEAVE));
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        Log.i(TAG, "onActivityResult:" + requestCode + " " + resultCode + " " + result);

        Uri ttt = getIntent().getData();
        Log.i(TAG, "Insta uri: " + ttt);
        SocialHandler.get().onActivityResult(requestCode, resultCode, result);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SocialHandler.get().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        A4S.get(this).stopActivity(this);
        // ...
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        A4S.get(this).setIntent(intent);
        // ...
    }
}
