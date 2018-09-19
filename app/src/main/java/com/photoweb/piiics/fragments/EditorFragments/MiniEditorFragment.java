package com.photoweb.piiics.fragments.EditorFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.fragments.FiltersFragment;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PicAlbum;
import com.photoweb.piiics.utils.CommandHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by thomas on 20/09/2017.
 */

public class MiniEditorFragment extends BaseFragment {
    private static final String LOG_TAG = "MiniEditorFragment";

    EditorActivity activity;
    EditorPic editorPic;

    @BindView(R.id.image)
    ImageView imageView;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_mini_editor;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((EditorActivity) getActivity());
        editorPic = activity.getCurrentPic();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity.setToolbarEditMode(getString(R.string.IMAGE));

        String bitmapPath = editorPic.getFinalBitmapPath();

        if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){

            if(editorPic.picAlbums.size() == 0){
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(activity);
                }
                builder.setTitle(R.string.ERROR)
                        .setMessage(R.string.GENERAL_ERROR)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                activity.onBackPressed();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return;
            }

            bitmapPath = editorPic.picAlbums.get(0).getFinalBitmapPath();
        }

        if (bitmapPath == null) {
            Log.i(LOG_TAG, "BITMAP PATH NULL");
        }else{
            Picasso.with(activity)
                    .load(new File(bitmapPath))
                    .fit()
                    .centerInside()
                    //.centerCrop()
                    .skipMemoryCache()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Log.d("LOAD", "failed to load ");
                        }
                    });
        }
    }


    @OnClick(R.id.crop)
    public void onCropClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new CropFragment(), "FROM_MINI_EDITOR")
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.filters)
    public void onFiltersClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new FiltersFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.deletePic)
    public void onDeletePicClick() {
        Log.i(LOG_TAG, "DELETE");

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle(R.string.PAGE_DELETE_TITLE)
                .setMessage(R.string.PHOTO_DELETE)
                .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){

                            if(editorPic.picAlbums.size() > 0){
                                PicAlbum pic = editorPic.picAlbums.get(0);

                                File f = new File(pic.getFinalBitmapPath());
                                f.delete();

                                f = new File(pic.getCropBitmapPath());
                                f.delete();

                                f = new File(editorPic.getFinalBitmapPath());
                                f.delete();

                                f = new File(editorPic.getCropBitmapPath());
                                f.delete();

                                editorPic.picAlbums.remove(0);

                                int index = editorPic.index;
                                //activity.getPics().remove(activity.getCurrentPicPosition());
                                //activity.getPics().add(index, new EditorPic(null, "ALBUM", index));
                                CommandHandler.get().currentCommand.rebootPic(index);

                                try {
                                    CommandHandler.get().currentCommand.saveCommand();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            Intent intent = new Intent("SHOULD_UPDATE");
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                            activity.refreshContent(editorPic);
                            activity.sendViewPager();

                        }else{
                            if (activity.getPics().size() == 1) {
                                CommandHandler.get().deleteCurrentCommand();
                                activity.finish();
                                return;
                            }

                            File f = new File(editorPic.getFinalBitmapPath());
                            f.delete();

                            f = new File(editorPic.getCropBitmapPath());
                            f.delete();

                            activity.getPics().remove(activity.getCurrentPicPosition());
                            ((EditorActivity) getActivity()).getEditorPicsAdapter().notifyDataSetChanged();

                            try {
                                CommandHandler.get().currentCommand.saveCommand();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            activity.onBackPressed();
                        }

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
}
