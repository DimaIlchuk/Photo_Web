package com.photoweb.piiics.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;

import org.json.JSONException;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by thomas on 21/09/2017.
 */

public class CreateEditorPicsBitmapsAsync {
    private static final String LOG_TAG = "CreateBitmapsAsync";

    public static final String FINISH_PIC_FILTER = "FinishPic";

    private Command command;
    private static boolean allBitmapsCreated = false;

    public static boolean isAllBitmapsCreated() {
        return allBitmapsCreated;
    }

    public void start(Command command) {
        this.command = command;
        allBitmapsCreated = false;

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(CreateEditorPicsBitmapsAsync.FINISH_PIC_FILTER));

        createEditorPicBitmaps();
    }


    private void createEditorPicBitmaps() {
        EditorPic editorPic = null;
        ArrayList<EditorPic> pics;
        if (command.getProduct().equals("ALBUM")) {
            pics = new ArrayList<>();
            pics.add(command.getAlbumFrontCover());
            pics.addAll(command.getEditorPics());
            pics.add(command.getAlbumBackCover());
        } else {
            pics = command.getEditorPics();
        }
        Log.d(LOG_TAG, "EDITOR PICS SIZE : " + pics.size());
        if ((editorPic = checkEditorPicWithNoBitmaps(pics)) != null) {
            new CreateEditorPicBitmapsTask().execute(editorPic);
            //Log.i(LOG_TAG, editorPic.getBackgroundReference().toString());
            //Log.i(LOG_TAG, editorPic.getFormatReference().toString());
        }else{
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("Editor_Init_Finish"));
        }
    }

    /*
        Check if an EditorPic in the command has to create her defaults bitmaps
        Mark the EditorPic with her position in the ArrayList
        Update the AllBitmapsCreated flag
     */
    private EditorPic checkEditorPicWithNoBitmaps(ArrayList<EditorPic> pics) {
        int i = 0;
        Log.d(LOG_TAG, "CHECK INIT");
        for (EditorPic editorPic : pics) {
            if (editorPic.getAsset() != null || editorPic.getPhotoID().equals("FIRST") || editorPic.getPhotoID().equals("LAST")) {
                if (!editorPic.operated) {
                    Log.i(LOG_TAG, "START CREATE BITMAPS");
                    editorPic.setEditorActivityAdapterPosition(i);
                    allBitmapsCreated = false;
                    return editorPic;
                }
            }
            i++;

            Log.d(LOG_TAG, "CHECK " + i);
        }
        allBitmapsCreated = true;
        return null;
    }

    private class CreateEditorPicBitmapsTask extends AsyncTask<EditorPic, Integer, EditorPic> {

        protected EditorPic doInBackground(EditorPic... editorPics) {
            EditorPic editorPic = editorPics[0];
            Log.d(LOG_TAG, editorPic.getPhotoID());

            editorPic.createDefaultBitmaps(command.getCommandRootDirectoryPath(), getApplicationContext());
            return editorPic;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(EditorPic editorPic) {
            /*Log.i(LOG_TAG, "Bitmaps created " + editorPic.operated);

            saveCommand();
            //sendBroadcastFinishPic(editorPic);

            createEditorPicBitmaps();*/
        }
    }

    private void saveCommand() {
        try {
            CommandHandler.get().currentCommand.saveCommand();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "Bitmaps created");

            saveCommand();
            //sendBroadcastFinishPic(editorPic);

            createEditorPicBitmaps();
        }
    };

    private void sendBroadcastFinishPic(EditorPic editorPic) {
        Log.i(LOG_TAG, "SEND BROADCAST FINISH PIC");
        Intent intent = new Intent(FINISH_PIC_FILTER);
        intent.putExtra("EditorActivityAdapterPosition", editorPic.getEditorActivityAdapterPosition());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
