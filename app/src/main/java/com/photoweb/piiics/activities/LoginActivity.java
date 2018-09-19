package com.photoweb.piiics.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.ad4screen.sdk.A4S;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.appsee.Appsee;
import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.LoginFragments.SplashscreenFragment;
import com.photoweb.piiics.model.TutorialReceiver;
import com.photoweb.piiics.utils.AWSHandler;
import com.photoweb.piiics.utils.PriceReferences;
import com.photoweb.piiics.utils.SocialHandler;
import com.photoweb.piiics.utils.UserInfo;
import com.photoweb.piiics.utils.Utils;

import java.io.File;
import java.util.Calendar;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by thomas on 14/04/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = "LoginActivity";

    private static AmazonS3 s3;

    TutorialReceiver tutorialReceiver;

    public TutorialReceiver getTutorialReceiver() {
        return tutorialReceiver;
    }

    public void setTutorialReceiver(TutorialReceiver tutorialReceiver) {
        this.tutorialReceiver = tutorialReceiver;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Appsee.start("ca29b14487ac4c8e843ecb90c54c413f");

        A4S.get(this).setPushNotificationLocked(true);
        A4S.get(this).setInAppDisplayLocked(true);

        SocialHandler.get().init(this);

        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            SplashscreenFragment splashscreenFragment = new SplashscreenFragment();
            ft.replace(R.id.fragment_container, splashscreenFragment);
            ft.commit();
        }
    }

    public void startAWSrequests() {
        //AWSHandler.get().getFolder(PriceReferences.BACKGROUNDS);
        //AWSHandler.get().getFolder(PriceReferences.STICKERS);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                this,    /* get the context for the application */
                Utils.AWS_POOL,    /* Identity Pool ID */
                Utils.AWS_REGION           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        s3 = new AmazonS3Client(credentialsProvider);

        getFolder(PriceReferences.BACKGROUNDS);
        getFolder(PriceReferences.STICKERS);

        Bundle bundle = new Bundle();
        bundle.putString("current_month", "value");
        bundle.putString("free_items_remaining", "50");

        Calendar calendar = Calendar.getInstance();
        bundle.putString("current_month", String.valueOf(calendar.get(Calendar.MONTH)));

        if (UserInfo.getInt("id") != 0) {
            bundle.putString("id_client", String.valueOf(UserInfo.getInt("id")));
            bundle.putString("first_name", UserInfo.get("UserName"));
        }

        A4S.get(this).updateDeviceInfo(bundle);
    }

    public void getFolder(String key) {
        final ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(Utils.AWS_BUCKET)
                .withPrefix(key);

        DownloadFolderClass downloadFolderClass = new DownloadFolderClass();
        downloadFolderClass.key = key;

        DownloadFolderClass.DownloadFolderTask task = downloadFolderClass.new DownloadFolderTask();
        task.execute(listObjectsRequest);
    }

    private class DownloadFolderClass {

        String key;

        public class DownloadFolderTask extends AsyncTask<ListObjectsRequest, Void, Void> {
            @Override
            protected Void doInBackground(ListObjectsRequest... listObjectsRequests) {
                try
                {
                    ListObjectsRequest listObjectsRequest = listObjectsRequests[0];

                    ObjectListing objectListing = s3.listObjects(listObjectsRequest);
                    for (S3ObjectSummary object : objectListing.getObjectSummaries()) {
                        System.out.println(object.getKey());
                        if (object.getKey().toLowerCase().endsWith(".png") || object.getKey().toLowerCase().endsWith(".jpg")) {
                            downloadFileWith(object.getKey());
                        }
                    }
                }catch (AmazonServiceException e) {
                    System.err.println(e.getErrorMessage());
                    System.exit(1);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.i(LOG_TAG, "AWS DOWNLOAD FINISH YESSS : " + key);

                if (key.equals(PriceReferences.BACKGROUNDS)) {
                    PriceReferences.setBackgroundFilesDL(true);
                } else if (key.equals(PriceReferences.STICKERS)) {
                    PriceReferences.setStickerFilesDL(true);
                }
            }
        }
    }

    public void downloadFileWith(final String key) {
        String[] directories = key.split("/");

        File productdir = getDir(directories[0], Context.MODE_PRIVATE);
        File mydir = new File(productdir, directories[1]);

        if (!mydir.exists()) {
            mydir.mkdir();
        }

        File downloadfile = new File(mydir, directories[2]);

        String newkey = key;

        if(key.startsWith("Stickers/1, 2, 3 etc")){
            newkey = newkey.replace("Stickers/1, 2, 3 etc", "123sticker").replace("PNG", "png");
        }

        final String usedKey = newkey;

        if (!downloadfile.exists()) {
            TransferUtility transferUtility = new TransferUtility(s3, this);

            TransferObserver observer = transferUtility.download(
                    Utils.AWS_BUCKET,     /* The bucket to download from */
                    usedKey,    /* The key for the object to download */
                    downloadfile        /* The file to download the object to */
            );

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.d(LOG_TAG, ex.getLocalizedMessage() + " - " + usedKey);
                }
            });
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Listen to GlobEvent events
        LocalBroadcastManager.getInstance(this).registerReceiver(onEventReceived,
                new IntentFilter("GlobEvent_SocialConnect"));
        A4S.get(this).startActivity(this);//crashs
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
    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(onEventReceived);
        SocialHandler.get().destroy();
    }

    public void sendHome() {
        Intent intent = new Intent(this, MainActivity.class);

        finish();
        startActivity(intent);
    }

    private BroadcastReceiver onEventReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onEventReceived: " + intent);
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(onEventReceived);
            sendHome();
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        A4S.get(this).setIntent(intent);
        // ...
    }
}
