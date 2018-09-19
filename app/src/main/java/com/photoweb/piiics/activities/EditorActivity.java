package com.photoweb.piiics.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ad4screen.sdk.A4S;
import com.ad4screen.sdk.analytics.Cart;
import com.ad4screen.sdk.analytics.Item;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.appsee.Appsee;
import com.photoweb.piiics.Adapters.AlbumEditorPicsAdapter;
import com.photoweb.piiics.Adapters.EditorPicsAdapter;
import com.photoweb.piiics.PiiicsExceptionHandler;
import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.EditorAlbumHomeFragment;
import com.photoweb.piiics.fragments.EditorPrintsHomeFragment;
import com.photoweb.piiics.model.Asset;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PriceReferences.BackgroundReference;
import com.photoweb.piiics.model.PriceReferences.DynamicText;
import com.photoweb.piiics.model.PriceReferences.Sticker;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.CreateEditorPicsBitmapsAsync;
import com.photoweb.piiics.utils.FilterHandler;
import com.photoweb.piiics.utils.PopUps;
import com.photoweb.piiics.utils.PriceReferences;
import com.photoweb.piiics.utils.StickerHandler;
import com.photoweb.piiics.utils.TransformationHandler;
import com.photoweb.piiics.utils.UserInfo;
import com.photoweb.piiics.utils.Utils;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by thomas on 20/04/2017.
 */

public class EditorActivity extends AppCompatActivity {
    private static final String LOG_TAG = "EditorActivity";

    public static final String DOWNLOAD_PIC = "DownloadPic";

    private Command command;
    private ArrayList<EditorPic> pics = new ArrayList<EditorPic>();
    private EditorPicsAdapter editorPicsAdapter;
    private int currentPicPosition = -1;
    private String from = null;

    private ViewGroup emptyAlbumPageLayout;
    private EditorPic editorPicAlbumPage;

    public ArrayList<Typeface> customFonts;

    public ProgressDialog dialog;
    public int count = 0;

    public float[] percent = {1, 0.7f, 0.8f, 0.8f, 0.9f, 0.7f, 0.8f, 0.9f, 0.7f, 0.8f, 1, 0.7f, 1, 0.9f, 0.6f, 1};

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progressBar) ProgressBar progressBar;

    public Command getCommand() {
        return CommandHandler.get().currentCommand;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Appsee.start("ca29b14487ac4c8e843ecb90c54c413f");

        Thread.setDefaultUncaughtExceptionHandler(new PiiicsExceptionHandler(this));

        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        String from = getIntent().getStringExtra("FROM");
        command = CommandHandler.get().currentCommand;

        if(command == null){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle(R.string.ERROR)
                    .setMessage(R.string.GENERAL_ERROR)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return;
        }

        //pics = command.getEditorPics();

        if (command.getProduct().equals("ALBUM")) {
            pics = new ArrayList<>();
            pics.add(command.getAlbumFrontCover());
            pics.addAll(command.getEditorPics());
            pics.add(command.getAlbumBackCover());
        } else {
            pics = command.getEditorPics();
        }

        customFonts = getFonts();

        initToolbar();

        if (PriceReferences.areBackgroundDatasDL()) {
            PriceReferences.setBackgroundsFiles();
        } else {
            //todo
        }
        if (PriceReferences.areStickerDatasDL()) {
            PriceReferences.setStikersFiles();
        } else {
            //todo
        }

        // initPics();
        FilterHandler.get().init(this);

        new CreateEditorPicsBitmapsAsync().start(command);

        count = checkEditorPicWithNoBitmaps();

        if(count > 0){
            //count = pics.size();
            dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.DOWNLOAD_PROGRESS, count));
            dialog.show();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onEventReceived,
                    new IntentFilter("Editor_Init_Finish"));

            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
                    new IntentFilter(CreateEditorPicsBitmapsAsync.FINISH_PIC_FILTER));
        }else{
            if (from != null && from.equals(BookManagerActivity.LOG_TAG)) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                    ft.replace(R.id.fragment_container, new EditorAlbumHomeFragment(), "EditorAlbumHomeFragment");
                    ft.commit();
                }
            } else {
                editorPicsAdapter = new EditorPicsAdapter(EditorActivity.this, pics);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                    EditorPrintsHomeFragment editorPrintsHomeFragment = new EditorPrintsHomeFragment();
                    ft.replace(R.id.fragment_container, editorPrintsHomeFragment, "EditorPrintsHomeFragment");
                    ft.commit();
                }
            }
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onDownloadReceiver,
                new IntentFilter("FINISH_DOWNLOAD"));
    }

    public void refreshPics()
    {
        pics = new ArrayList<>();
        pics.add(command.getAlbumFrontCover());
        pics.addAll(command.getEditorPics());
        pics.add(command.getAlbumBackCover());
    }

    private int checkEditorPicWithNoBitmaps() {
        int i = 0;
        Log.d(LOG_TAG, "CHECK INIT");
        for (EditorPic editorPic : pics) {
            if (editorPic.getAsset() != null) {
                if (!editorPic.operated) {
                    i++;
                }
            }
        }

        Log.d(LOG_TAG, "value : " + i);

        return i;
    }

    private BroadcastReceiver onDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "Bitmaps created");

            try {
                Class gabaritCls = Class.forName(Utils.package_gabarit + editorPicAlbumPage.actions.get("gabarit"));
                Method m = gabaritCls.getMethod("applyGabarit", EditorPic.class);

                saveAlbumCropped((Bitmap) m.invoke(gabaritCls.newInstance(), editorPicAlbumPage), editorPicAlbumPage);

                applyAction(editorPicAlbumPage);
                refreshContent(editorPicAlbumPage);
                //sendViewPager();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            //imageView.setImageBitmap((Bitmap) m.invoke(gabaritCls.newInstance(), list));


        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "Bitmaps created");
            count--;
            dialog.setMessage(getString(R.string.DOWNLOAD_PROGRESS, count));
        }
    };

    private BroadcastReceiver onEventReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onEventReceived: " + intent);
            dialog.dismiss();

            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(onEventReceived);
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);

            if (from != null && from.equals(BookManagerActivity.LOG_TAG)) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                    ft.replace(R.id.fragment_container, new EditorAlbumHomeFragment(), "EditorAlbumHomeFragment");
                    ft.commit();
                }
            } else {
                editorPicsAdapter = new EditorPicsAdapter(EditorActivity.this, pics);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                    EditorPrintsHomeFragment editorPrintsHomeFragment = new EditorPrintsHomeFragment();
                    ft.replace(R.id.fragment_container, editorPrintsHomeFragment, "EditorPrintsHomeFragment");
                    ft.commitAllowingStateLoss();
                }
            }
        }
    };

    public void setEditorPicAlbumPage(EditorPic editorPicAlbumPage) {
        this.editorPicAlbumPage = editorPicAlbumPage;
    }

    public void setEmptyAlbumPageLayout(ViewGroup emptyAlbumPageLayout) {
        this.emptyAlbumPageLayout = emptyAlbumPageLayout;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getCurrentPicPosition() {
        return currentPicPosition;
    }

    public EditorPicsAdapter getEditorPicsAdapter() {
        return editorPicsAdapter;
    }

    public ArrayList<EditorPic> getPics() {
        return pics;
    }


    public void sendViewPager() {
        getSupportFragmentManager().popBackStack();
    }

    public void refreshContent(EditorPic pic) {
        if(CommandHandler.get().currentCommand.getProduct().equals("PRINT")){
            editorPicsAdapter.notifyDataSetChanged();
        }else{
            pic.sendBroadcastFinishPic();
        }
    }

    /*
        Get the position of the last pic showed by the slider (ViewPager)
    */
    public EditorPic getCurrentPic() {
        if(currentPicPosition < pics.size() && currentPicPosition > -1){
            EditorPic currentPic = pics.get(currentPicPosition);
            return currentPic;
        }

        EditorPic currentPic = pics.get(0);
        return currentPic;
    }

    /*
       Save the position of the current pic showed by the slider (ViewPager)
    */
    public void setCurrentPicPosition(int position) {
        currentPicPosition = position;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    public void setToolbarDefault() {
        TextView continueButton = (TextView) toolbar.findViewById(R.id.continue_icon);
        continueButton.setVisibility(View.VISIBLE);

        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.EDITOR);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }


    public void setToolbarEditMode(String title) {
        TextView continueButton = toolbar.findViewById(R.id.continue_icon);
        continueButton.setVisibility(View.GONE);

        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.validation_green));

        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(title);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.validation_green));
        }
    }

    private void launchLoginInCommandActivity(Command command) {
        Intent intent = new Intent(EditorActivity.this, LoginInCommandActivity.class);
        //   intent.putExtra("COMMAND", command);
        startActivity(intent);
    }

    @OnClick(R.id.continue_icon)
    public void onContinueClick() {

        if (UserInfo.getInt("id") == 0) {
            launchLoginInCommandActivity(command);
        } else {
            if (!CreateEditorPicsBitmapsAsync.isAllBitmapsCreated()) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle(R.string.INFOS)
                        .setMessage("Vos fichiers sont en cours de création. Merci de réessayer dans quelques instants.")
                        .setPositiveButton("Ok, j'ai compris", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return;
            }

            //Check if there is empty page
            if(command != null && command.getProduct().equals("ALBUM") && pics != null){
                if(progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);

                for (EditorPic pic:pics) {
                    if(pic.getAsset() == null){
                        if (pic.actions != null && pic.actions.get("Placeholder") != null) {
                            pic.actions.remove("Placeholder");
                            applyAction(pic);
                        }
                    }
                }

                if(progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }

            try {
                Item item = new Item(command.getCommandID(), command.getProduct(), command.getProduct(), "EUR", (float) (CommandHandler.get().currentCommand.getAllPicsPrice()) / 100, 1);
                Cart cart = new Cart("CartId", item);

                A4S.get(this).trackAddToCart(cart);

                AdjustEvent event = new AdjustEvent("mtgtsd");
                Adjust.trackEvent(event);
            } catch (PriceSecurityException pse) {
                //
            }

            Intent intent = new Intent(this, BasketActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof EditorPrintsHomeFragment) {
            Intent intent = getIntent();
            String launchedBy = intent.getStringExtra("launchedBy");

            if (launchedBy != null && launchedBy.equals("MainActivity")) {
                PopUps.popUpCancelCommand(EditorActivity.this, getString(R.string.ATTENTION), getString(R.string.LEAVE));
            } else {
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    /*
        Save the bitmap modified in local
     */
    public void saveBitmapModified(Bitmap bitmap, EditorPic picSelected) {
        File picFile = new File(picSelected.getFinalBitmapPath());
        picFile.delete();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picSelected.getFinalBitmapPath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveBitmapCropped(Bitmap bitmap, EditorPic picSelected) {
        File picFile = new File(picSelected.getCropBitmapPath());
        picFile.delete();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picSelected.getCropBitmapPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveAlbumCropped(Bitmap bitmap, EditorPic picSelected) {
        File picFile = new File(picSelected.getCropBitmapPath());
        picFile.delete();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picSelected.getCropBitmapPath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void applyAction(EditorPic picSelected) {
        progressBar.setVisibility(View.VISIBLE);

        if(command.getProduct().equals("PRINT")){
            Bitmap finale = BitmapFactory.decodeFile(picSelected.getCropBitmapPath());
            Bitmap bg;
            if (picSelected.getBackgroundReference() == null) {//pas censer arriver
                BackgroundReference defaultBackgroundReference = PriceReferences.getDefaultBackground();
                bg = BitmapFactory.decodeFile(defaultBackgroundReference.getBackgroundFile().getAbsolutePath());//todo: a modifier, sinon ca offre le background blanc
            } else {
                bg = BitmapFactory.decodeFile(picSelected.getBackgroundReference().getBackgroundFile().getAbsolutePath());
                //        Log.d("EDIT", picSelected.getBackgroundReference().getBackgroundFile().getAbsolutePath());
            }

            if (picSelected.actions.get("filter") != null) {
                finale = FilterHandler.get().applyFilter((String) picSelected.actions.get("filter"), finale);
            }

            if (picSelected.actions.get("gabarit") != null) {
                finale = TransformationHandler.get().applyGabarit((String) picSelected.actions.get("gabarit"), finale, bg);
            }

            if (picSelected.actions.get("Stickers") != null) {
                ArrayList<Sticker> list = (ArrayList<Sticker>) picSelected.actions.get("Stickers");

                for (Sticker sticker : list) {
                    Log.i(LOG_TAG, "sticker name : " + sticker.getName() + ", sticker fileName : " + sticker.getStickerFile().getName());
                    finale = StickerHandler.drawSticker(finale, BitmapFactory.decodeFile(sticker.getStickerFile().getAbsolutePath()), sticker.x, sticker.y, Math.round(sticker.width), Math.round(sticker.height), sticker.arg);
                }
            }

            if (picSelected.actions.get("Texts") != null) {
                ArrayList<DynamicText> list = (ArrayList<DynamicText>) picSelected.actions.get("Texts");

                for (DynamicText sticker : list) {
                    Log.i(LOG_TAG, "Text : " + sticker.position);
                    Log.i(LOG_TAG, "Text Font : " + customFonts.get(sticker.position).toString());

                    Bitmap txt = StickerHandler.generateBitmapFromText(sticker.text, 360, sticker.color, customFonts.get(sticker.position), sticker.height*percent[sticker.position]);
                    Log.d(LOG_TAG, "Text : " + txt.getWidth() + " - " + txt.getHeight());
                    Log.d(LOG_TAG, "Text : " + (sticker.x - txt.getWidth()/2) + " - " + (sticker.y - sticker.height/2));

                    finale = StickerHandler.drawSticker(finale, txt, sticker.x, sticker.y, txt.getWidth(), txt.getHeight(), sticker.arg);
                }
            }

            if (picSelected.actions.get("border") != null) {
                finale = TransformationHandler.get().addBorder(finale.copy(finale.getConfig(), true), (int) (picSelected.actions.get("border")), false);
            }

            File picFile = new File(picSelected.getFinalBitmapPath());
            picFile.delete();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(picSelected.getFinalBitmapPath());

                finale.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                command.saveCommand();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Bitmap finale = BitmapFactory.decodeFile(picSelected.getCropBitmapPath());

            if (picSelected.actions.get("Stickers") != null) {
                ArrayList<Sticker> list = (ArrayList<Sticker>) picSelected.actions.get("Stickers");

                for (Sticker sticker : list) {
                    Log.i(LOG_TAG, "sticker name : " + sticker.getName() + ", sticker fileName : " + sticker.getStickerFile().getName());
                    finale = StickerHandler.drawSticker(finale, BitmapFactory.decodeFile(sticker.getStickerFile().getAbsolutePath()), sticker.x, sticker.y, Math.round(sticker.width), Math.round(sticker.height), sticker.arg);
                }
            }

            if (picSelected.actions.get("Texts") != null) {
                ArrayList<DynamicText> list = (ArrayList<DynamicText>) picSelected.actions.get("Texts");

                for (DynamicText sticker : list) {
                    Log.i(LOG_TAG, "Text : " + sticker.position);
                    Log.i(LOG_TAG, "Text Font : " + customFonts.get(sticker.position).toString());

                    Bitmap txt = StickerHandler.generateBitmapFromText(sticker.text, 360, sticker.color, customFonts.get(sticker.position), sticker.height);
                    Log.d(LOG_TAG, "Text : " + txt.getWidth() + " - " + txt.getHeight());
                    Log.d(LOG_TAG, "Text : " + (sticker.x - txt.getWidth()/2) + " - " + (sticker.y - txt.getHeight()/2));

                    finale = StickerHandler.drawSticker(finale, txt, sticker.x, sticker.y, txt.getWidth(), txt.getHeight(), sticker.arg);
                }
            }

            if (picSelected.actions.get("Logo") != null) {
                finale = StickerHandler.drawSticker(finale, BitmapFactory.decodeResource(getResources(), R.drawable.logo_book), Utils.pageWidth/2, Utils.pageHeight-200, 317, 209, 0);
            }

            if (picSelected.actions.get("Placeholder") != null) {
                finale = StickerHandler.drawSticker(finale, BitmapFactory.decodeResource(getResources(), R.drawable.plus_placeholder), Utils.pageWidth/2, Utils.pageHeight/2, Utils.pageWidth/2, Utils.pageWidth/2, 0);
            }

            File picFile = new File(picSelected.getFinalBitmapPath());
            picFile.delete();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(picSelected.getFinalBitmapPath());

                if(picSelected.getFinalBitmapPath().endsWith(".png")){
                    finale.compress(Bitmap.CompressFormat.PNG, 100, out);
                }else{
                    finale.compress(Bitmap.CompressFormat.JPEG, 100, out);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                command.saveCommand();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        progressBar.setVisibility(View.GONE);
    }

    private ArrayList<Typeface> getFonts() {
        ArrayList<Typeface> customFonts = new ArrayList<>();

        String[] listFonts;
        try {
            listFonts =getAssets().list("fonts");
            for (String fontName : listFonts) {
                Log.i(LOG_TAG, "fontName : " + fontName);
                Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/" + fontName);
                customFonts.add(customFont);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return customFonts;
    }

    @Override
    protected void onResume() {
        super.onResume();
        A4S.get(this).startActivity(this);
        // ...
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AlbumEditorPicsAdapter.PICK_PIC_REQUEST) {
            Log.i(LOG_TAG, "ONACTIVITY RESULT");
            if(resultCode == Activity.RESULT_OK) {
                Log.i(LOG_TAG, "RESULT OK");
                Asset result = (Asset) data.getSerializableExtra("result");
                editorPicAlbumPage.setAsset(result);
                editorPicAlbumPage.operated = false;

                if (editorPicAlbumPage.actions.get("Placeholder") != null)
                    editorPicAlbumPage.actions.remove("Placeholder");

                dialog = new ProgressDialog(this);
                dialog.setMessage(getString(R.string.DOWNLOAD_PROGRESS, 1));
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        new CreateEditorPicsBitmapsAsync().start(command);
                    }
                }, 50);


            } else if (resultCode == Activity.RESULT_CANCELED) {
                final ProgressBar pagePB = (ProgressBar) emptyAlbumPageLayout.findViewById(R.id.progressBar);
                final ImageView pageIV = (ImageView) emptyAlbumPageLayout.findViewById(R.id.imageView);
                AlbumEditorPicsAdapter.showEmptyPic(this, pageIV, pagePB);
            }
        }
    }//onActivityResult
}
