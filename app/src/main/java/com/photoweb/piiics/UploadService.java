package com.photoweb.piiics;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dnizard on 29/11/2017.
 */

public class UploadService extends Service {
    private static final String LOG_TAG = "UploadService";
    public static int id = 55555;
    public static final String PRIMARY_CHANNEL = "default";

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    private int count;
    private int countError = 0;
    private int size = 0;
    private ArrayList<String> error;
    private ArrayList<String> arrayList;

    private static AmazonS3 s3;

    public UploadService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL, "Notification", NotificationManager.IMPORTANCE_HIGH);
            mNotifyManager.createNotificationChannel(channel);
        }

        mBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL);
        mBuilder.setContentTitle("Piiics")
                .setContentText("Chargement des images")
                .setSmallIcon(R.drawable.icon_status)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, BasketActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);

        mBuilder.setProgress(100, 0, false);

        startForeground(id, mBuilder.build());

        Log.d(LOG_TAG, "Got an intent");
        error = new ArrayList<>();

        arrayList = intent.getStringArrayListExtra("array");
        count = arrayList.size();
        size = arrayList.size();

        uploadFileWithTitle();

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                this,    /* get the context for the application */
                Utils.AWS_POOL,    /* Identity Pool ID */
                Utils.AWS_REGION           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        s3 = new AmazonS3Client(credentialsProvider);

        //startUpload();


        //mNotifyManager.notify(id, mBuilder.build());

    }

    private void updatePercentage() {
        int percentage = getPercentUpload();

        mBuilder.setProgress(100, percentage, false);

        if(percentage >= 100){
            mBuilder.setContentText("Chargement terminé !");
            mBuilder.setProgress(0,0, false);

            if (error.size() > 0){
                Intent intent = new Intent("AWS_Upload_Finish_With_Errors");
                intent.putExtra("errors", error.size());
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }else{
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("AWS_Upload_Finish"));
            }


        }else{
            arrayList.remove(0);
            countError = 0;

            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("AWS_Upload_Progress"));

            uploadFileWithTitle();
        }

        mNotifyManager.notify(id, mBuilder.build());
    }

    public void uploadFileWithTitle() {

        String[] split = arrayList.get(0).split("P111c5");
        String fileURL = split[0];
        String title = split[1];

        File fileToUpload = new File(fileURL);

        if(fileToUpload.exists() && !fileToUpload.isDirectory()){
            Log.d(LOG_TAG, "Upload file : " + fileURL);
            TransferUtility transferUtility = new TransferUtility(s3, this);

            final TransferObserver observer = transferUtility.upload(
                    Utils.AWS_BUCKET,     /* The bucket to upload to */
                    title,    /* The key for the uploaded object */
                    fileToUpload        /* The file where the data to upload exists */
            );

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state.equals(TransferState.COMPLETED)) {
                        //Success
                        count--;

                        Log.d(LOG_TAG, "upload count : " + count);
                        updatePercentage();

                    /*if (count == -1) {
                        Log.d(LOG_TAG, "Time to delete");
                        CommandHandler.get().deleteCurrentCommand();
                    }*/

                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    //Log.d(LOG_TAG, "Progress : " + 100*bytesCurrent/bytesTotal + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.d(LOG_TAG, "Got an error");
                    Log.d(LOG_TAG, ex.getLocalizedMessage());

                    //Toast.makeText(UploadService.this, "Erreur : " + ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    countError++;

                    if(countError < 3){
                        uploadFileWithTitle();
                    }else{
                        count--;
                        error.add(observer.getAbsoluteFilePath() + "P111c5" + observer.getKey());
                        updatePercentage();
                    }
                }
            });
        }else{
            count--;
            error.add(fileURL + "P111c5" + title);
            updatePercentage();
        }


    }

    public int getPercentUpload() {
        return Math.round(100 * (size + 1 - count) / (size + 1));
    }


}
