package com.photoweb.piiics.Adapters;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BookManagerActivity;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.DraftsUtils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by dnizard on 09/09/2017.
 */

public class CreationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = "CreationAdapter";

    HashMap<Integer, File> creations = new HashMap<>();
    ArrayList<Integer> keys = new ArrayList<>();

    MainActivity activity;
    Boolean stateDelete = false;

    public CreationAdapter(MainActivity activity) {
        this.activity = activity;

        initCreations();
    }

    public void setStateDelete()
    {
        this.stateDelete = !this.stateDelete;
    }

    public Boolean getStateDelete() {
        return this.stateDelete;
    }

    private void deleteCreation(final String command, final int position)
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle(R.string.PAGE_DELETE_TITLE)
                .setMessage(R.string.DELETE_CREATION)
                .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        File f = new File(DraftsUtils.getPrintDirectoryPath(), command);
                        deleteRecursive(f);
                        keys.remove(position);
                        notifyDataSetChanged();

                    }
                })
                .setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    @Override
    public CreationItem onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout mainView;
        CreationItem vh;

        // create a new view
        mainView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_account_command_completed, parent, false);

        vh = new CreationItem(mainView);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CreationItem creationItem = (CreationItem) holder;

        Integer key = keys.get(position);
        final File creationSelected = creations.get(key);
        String product = "";

        SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(creationSelected.lastModified());

        if(creationSelected.getAbsolutePath().contains("PRINT")){
            creationItem.commandTitleTV.setText(R.string.PRINT);
            product = "PRINT";
        }else{
            creationItem.commandTitleTV.setText(R.string.SINGLE_ALBUM);
            product = "ALBUM";
        }

        final String finalProduct = product;

        creationItem.dateTV.setText(sf.format(date));

        creationItem.priceTV.setVisibility(View.INVISIBLE);

        creationItem.mainLayoutRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "OnClick");

                if(stateDelete)
                {
                    deleteCreation(creationSelected.getName().replace(".json", ""), position);
                    return;
                }

                CommandHandler.get().init(creationSelected.getName().replace(".json", ""), finalProduct);
                try {
                    CommandHandler.get().currentCommand.loadCommand(activity);
                    Log.d(LOG_TAG, "current command " + CommandHandler.get().currentCommand.getEditorPics().size());

                    if(creationSelected.getAbsolutePath().contains("PRINT")){
                        Intent intent = new Intent(activity, EditorActivity.class);
                        activity.startActivity(intent);
                    }else{
                        Intent intent = new Intent(activity, BookManagerActivity.class);
                        activity.startActivity(intent);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if(stateDelete){
            creationItem.imgArrow.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(), R.drawable.croix_rouge));
        }else{
            creationItem.imgArrow.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(), R.drawable.fleche_noir));
        }
    }

    @Override
    public int getItemCount() {
        if (keys == null) {
            return 0;
        } else {
            return keys.size();
        }
    }

    public void initCreations()
    {
        if(DraftsUtils.getPrintDirectoryPath() == null){
            DraftsUtils.createDraftDirectories(activity);
        }

        File AllPrints = new File(DraftsUtils.getPrintDirectoryPath());
        File[] files = AllPrints.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                // is directory
                Log.d("CREATIONS", inFile.getName() + " : " + inFile.lastModified());
                File Print = new File(inFile.getAbsolutePath());
                File[] files2 = Print.listFiles();
                for (File inFile2 : files2) {
                    if(inFile2.getName().endsWith(".json")){
                        creations.put((int) inFile2.lastModified(), inFile2);
                        keys.add((int) inFile2.lastModified());
                    }
                }
            }
        }

        File AllAlbums = new File(DraftsUtils.getAlbumDirectoryPath());
        files = AllAlbums.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                // is directory
                Log.d("CREATIONS", inFile.getName() + " : " + inFile.lastModified());
                File Album = new File(inFile.getAbsolutePath());
                File[] files2 = Album.listFiles();
                for (File inFile2 : files2) {
                    if(inFile2.getName().endsWith(".json")){
                        creations.put((int)inFile2.lastModified(), inFile2);
                        keys.add((int) inFile2.lastModified());
                    }
                }
            }
        }

        Collections.sort(keys);

    }

    public static class CreationItem extends RecyclerView.ViewHolder {
        LinearLayout mainView;

        RelativeLayout mainLayoutRL;
        TextView commandTitleTV;
        TextView dateTV;
        TextView priceTV;
        ImageView imgArrow;

        public CreationItem(LinearLayout mainView) {
            super(mainView);
            this.mainView = mainView;
            this.mainLayoutRL = (RelativeLayout) mainView.findViewById(R.id.mainLayout);
            this.commandTitleTV = (TextView) mainView.findViewById(R.id.commandTitle);
            this.dateTV = (TextView) mainView.findViewById(R.id.date);
            this.priceTV = (TextView) mainView.findViewById(R.id.price);
            this.imgArrow = (ImageView) mainView.findViewById(R.id.arrow);
        }
    }
}
