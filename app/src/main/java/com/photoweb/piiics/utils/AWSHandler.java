package com.photoweb.piiics.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import com.photoweb.piiics.model.EditorPic;

import java.io.File;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by dnizard on 26/04/2017.
 */

public class AWSHandler {
    private static final String TAG = "AWSHandler";

    private static AWSHandler instance;
    private static AmazonS3 s3;
    private static Context mContext;

    private int count;
    private ArrayList<String> error;

    public static AWSHandler get() {
        if (instance == null) instance = new AWSHandler();
        return instance;
    }

    public void init(Context context) {
        mContext = context;

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                Utils.AWS_POOL,    /* Identity Pool ID */
                Utils.AWS_REGION           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        s3 = new AmazonS3Client(credentialsProvider);
    }

    public void uploadFileWithTitle(String fileURL, String title) {
        File fileToUpload = new File(fileURL);

        if(fileToUpload.exists() && !fileToUpload.isDirectory())
        {
            TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

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
                        Log.d(TAG, "upload count : " + count);

                        //LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("AWS_Upload_Finish"));

                        if (count == -1) {
                            Log.d(TAG, "Time to delete");
                            CommandHandler.get().deleteCurrentCommand();
                        }

                    } else if (state.equals(TransferState.FAILED)) {
                        //Failed
                        //error.add(observer.getAbsoluteFilePath());
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.d(TAG, ex.getLocalizedMessage());
                }
            });
        }
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
                Log.i(TAG, "AWS DOWNLOAD FINISH YESSS : " + key);

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

        File productdir = getApplicationContext().getDir(directories[0], Context.MODE_PRIVATE);
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
            TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

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
                    Log.d(TAG, ex.getLocalizedMessage() + " - " + usedKey);
                }
            });
        }


    }


    //Upload

    public void startUpload() {
        error = new ArrayList<String>();

        ArrayList<EditorPic> pics = CommandHandler.get().currentCommand.getEditorPics();
        count = pics.size();

        for (EditorPic pic : pics) {
            uploadFileWithTitle(pic.getFinalBitmapPath(), "SEND/" + CommandHandler.get().currentCommand.getProduct() + "/" + CommandHandler.get().currentCommand.getCommandID() + "/" + pic.getPhotoID() + ".jpg");
        }
    }

    public void uploadXMLOrder() {
        count++;
        String file = CommandHandler.get().getFinalXMl();

        uploadFileWithTitle(file, "SEND/" + CommandHandler.get().currentCommand.getProduct() + "/" + CommandHandler.get().currentCommand.getCommandID() + "/order.xml");
    }

    /*public void uploadCommandFile() {
        String file = CommandHandler.get().getCommandFile();

        uploadFileWithTitle(file, "COMMAND/" + CommandHandler.get().currentCommand.getProduct() + "/" + CommandHandler.get().currentCommand.getCommandID() + ".txt");
    }*/

    public void uploadCommandOrder() {
        String key = CommandHandler.get().currentCommand.getCommandID() + ".txt";

        if (CommandHandler.get().currentCommand.getProduct().equals("ALBUM")) {
            if (CommandHandler.get().currentCommand.getAlbumOptions().isHasStrongCover()) {
                key = "LPP_" + key;
            } else {
                if (CommandHandler.get().currentCommand.getAlbumOptions().isHasVarnishedPages()) {
                    key = "LPS_V_" + key;
                } else {
                    key = "LPS_" + key;
                }

            }
        }

        String file = CommandHandler.get().getCommandFile();

        uploadFileWithTitle(file, "COMMAND/" + CommandHandler.get().currentCommand.getProduct() + "/" + key);
    }

    public int getPercentUpload() {
        return Math.round(100 * (CommandHandler.get().currentCommand.getEditorPics().size() + 1 - count) / (CommandHandler.get().currentCommand.getEditorPics().size() + 1));
    }

    public int getSizeError()
    {
        return error.size();
    }

    public void treatErrors()
    {
        count = error.size();

        for (String url:error) {
            String[] segments = url.split("/");
            uploadFileWithTitle(url, "SEND/" + CommandHandler.get().currentCommand.getProduct() + "/" + CommandHandler.get().currentCommand.getCommandID() + "/" + segments[segments.length-1]);
        }

        error.clear();
    }

}
