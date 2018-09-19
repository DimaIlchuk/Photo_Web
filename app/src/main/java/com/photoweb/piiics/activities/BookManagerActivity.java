package com.photoweb.piiics.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.appsee.Appsee;
import com.photoweb.piiics.PiiicsExceptionHandler;
import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.BookManagerFragment;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.CreateEditorPicsBitmapsAsync;
import com.photoweb.piiics.utils.PopUps;
import com.photoweb.piiics.utils.PriceReferences;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by thomas on 21/09/2017.
 */

public class BookManagerActivity extends AppCompatActivity {
    public static final String LOG_TAG = "BookManagerActivity";

    public Command command;
    public ArrayList<EditorPic> pics;

    public ProgressDialog dialog;
    public int count = 0;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Appsee.start("ca29b14487ac4c8e843ecb90c54c413f");

        Thread.setDefaultUncaughtExceptionHandler(new PiiicsExceptionHandler(this));

        setContentView(R.layout.activity_book_manager);
        ButterKnife.bind(this);

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

        pics = command.getEditorPics();

        new CreateEditorPicsBitmapsAsync().start(command);

        count = checkEditorPicWithNoBitmaps();

        //count = pics.size();
        if(count > 0){
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
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                BookManagerFragment bookManagerFragment = new BookManagerFragment();
                ft.replace(R.id.fragment_container, bookManagerFragment);
                ft.commit();
            }
        }

    }

    private int checkEditorPicWithNoBitmaps() {
        int i = 0;
        Log.d(LOG_TAG, "CHECK INIT");
        for (EditorPic editorPic : pics) {
            Log.d(LOG_TAG, "CHECK INIT "+editorPic.getPhotoID());
            if (editorPic.getAsset() != null || editorPic.getPhotoID().equals("FIRST") || editorPic.getPhotoID().equals("LAST")) {
                if (!editorPic.operated) {
                    i++;
                }
            }
        }

        if(command.getAlbumFrontCover().getFinalBitmapPath() == null){
            i = i+2;
        }

        return i;
    }

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

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                BookManagerFragment bookManagerFragment = new BookManagerFragment();
                ft.replace(R.id.fragment_container, bookManagerFragment);
                ft.commitAllowingStateLoss();
            }

        }
    };

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

        if (launchedBy != null && launchedBy.equals("MainActivity")) {
            PopUps.popUpCancelCommand(this, getString(R.string.ATTENTION), getString(R.string.LEAVE));
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.continue_icon)
    public void onContinueClick() {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra("FROM", BookManagerActivity.LOG_TAG);
        intent.putExtra("PAGE_POSITION", "FRONT_COVER");
        startActivity(intent);
    }
}
