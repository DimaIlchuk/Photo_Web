package com.photoweb.piiics.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.ad4screen.sdk.A4S;
import com.appsee.Appsee;
import com.photoweb.piiics.PiiicsExceptionHandler;
import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.LoginFragments.LoginFragment;
import com.photoweb.piiics.utils.SocialHandler;

/**
 * Created by thomas on 14/04/2017.
 */

public class LoginInCommandActivity extends AppCompatActivity {
    private static final String LOG_TAG = "LoginInCommandActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Appsee.start("ca29b14487ac4c8e843ecb90c54c413f");

        Thread.setDefaultUncaughtExceptionHandler(new PiiicsExceptionHandler(this));

        setContentView(R.layout.activity_login);

        SocialHandler.get().init(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            LoginFragment loginFragment = new LoginFragment();
            ft.replace(R.id.fragment_container, loginFragment);
            ft.commit();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        A4S.get(this).startActivity(this);

        //Listen to GlobEvent events
        LocalBroadcastManager.getInstance(this).registerReceiver(onEventReceived,
                new IntentFilter("GlobEvent_SocialConnect"));
    }

    private BroadcastReceiver onEventReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onEventReceived: " + intent);
            sendEditorActivity();
        }
    };

    public void sendEditorActivity() {
        Intent intent = new Intent(this, EditorActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        A4S.get(this).setIntent(intent);
        // ...
    }

    @Override
    protected void onPause() {
        super.onPause();
        A4S.get(this).stopActivity(this);
        // ...
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        Log.i(LOG_TAG, "onActivityResult:" + requestCode + " " + resultCode + " " + result);

        Uri ttt = getIntent().getData();
        Log.i(LOG_TAG, "Insta uri: " + ttt);
        SocialHandler.get().onActivityResult(requestCode, resultCode, result);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
