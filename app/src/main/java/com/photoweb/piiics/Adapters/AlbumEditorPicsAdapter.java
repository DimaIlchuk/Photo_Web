package com.photoweb.piiics.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.activities.SelectPicsActivity;
import com.photoweb.piiics.fragments.EditorFragments.MiniEditorFragment;
import com.photoweb.piiics.model.EditorPic;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by thomas on 20/04/2017.
 */

public class AlbumEditorPicsAdapter extends PagerAdapter {
    public static final String LOG_TAG = "AlbumEditorPicsAdapter";

    EditorActivity activity;

    ArrayList<EditorPic> pics;
    EditorPic frontCover;
    EditorPic backCover;

    public static int frontCoverSize = 1;
    public static int backCoverSize = 1;

    public static int PICK_PIC_REQUEST = 1;

    public AlbumEditorPicsAdapter(EditorActivity activity) {
        this.activity = activity;
        /*this.pics = CommandHandler.get().currentCommand.getEditorPics();
        this.frontCover = CommandHandler.get().currentCommand.getAlbumFrontCover();
        this.backCover = CommandHandler.get().currentCommand.getAlbumBackCover();*/

        this.pics = activity.getPics();
    }

    @Override
    public int getItemPosition(Object object) {
        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return pics.size();
    }


    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == ((ViewGroup) obj);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
      // RelativeLayout layout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.editor_page, container, false);

        LayoutInflater inflater = LayoutInflater.from(activity);
        final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.editor_album_page, container, false);

        final ProgressBar pagePB = (ProgressBar) layout.findViewById(R.id.progressBar);
        final ImageView pageIV = (ImageView) layout.findViewById(R.id.imageView);
        final ImageView backgroundIV = (ImageView) layout.findViewById(R.id.background);

        pageIV.setScaleType(ImageView.ScaleType.CENTER);
        layout.setTag(position);

        final EditorPic currentPic;
        /*if (position == 0) {
            currentPic = frontCover;
            setPagePic(currentPic, pageIV, backgroundIV, pagePB);
            container.setTag("0");
        } else if (position == (pics.size() + frontCoverSize + backCoverSize - 1)) {
            currentPic = backCover;
            setPagePic(currentPic, pageIV, backgroundIV, pagePB);
            container.setTag(String.valueOf(getCount()));
        } else {
            currentPic = pics.get(position - frontCoverSize);
            setPagePic(currentPic, pageIV, backgroundIV, pagePB);
            container.setTag(String.valueOf(pics.get(position - frontCoverSize).getIndex()));
        }*/

        currentPic = pics.get(position);
        setPagePic(currentPic, pageIV, backgroundIV, pagePB);
        container.setTag(String.valueOf(pics.get(position).getIndex()));

        container.addView(layout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBackCover(position)) {
                    if (currentPic.getAsset() == null) {
                        //Toast.makeText(activity, "To SHOW : SELECT ACTIVITY", Toast.LENGTH_SHORT).show();

                        showPic(currentPic.getFinalBitmapPath(), pageIV, currentPic.getBackgroundReference().getBackgroundFile().getAbsolutePath(), backgroundIV, pagePB);

                        activity.setEmptyAlbumPageLayout(layout);
                        activity.setEditorPicAlbumPage(currentPic);

                        Intent intent = new Intent(activity, SelectPicsActivity.class);
                        intent.putExtra("PRODUCT", "ALBUM");
                        intent.putExtra("launchedBy", LOG_TAG);
                        activity.startActivityForResult(intent, PICK_PIC_REQUEST);
                    } else {
                        //Toast.makeText(activity, "To SHOW : miiEditorFragment", Toast.LENGTH_SHORT).show();
                        activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MiniEditorFragment())
                        .addToBackStack(null)
                        .commit();
                    }
                }
            }
        });
        return layout;
    }

    private boolean isBackCover(int position) {
        if (position == (pics.size() + frontCoverSize + backCoverSize - 1)) {
            return true;
        }
        return false;
    }

    private void setPagePic(EditorPic editorPic, ImageView pageIV, ImageView bg, ProgressBar pagePB) {
        if (editorPic.getAsset() != null || editorPic.getPhotoID().equals("FIRST") || editorPic.getPhotoID().equals("LAST")) {
            Log.i(LOG_TAG, "asset NOT NULL " + editorPic.index);
            final String bitmapPath = editorPic.getFinalBitmapPath();
            showPic(bitmapPath, pageIV, (editorPic.getBackgroundReference() == null) ? "" : editorPic.getBackgroundReference().getBackgroundFile().getAbsolutePath(), bg, pagePB);
        } else {
            Log.i(LOG_TAG, "asset NULL " + editorPic.index);
            showEmptyPic(activity, pageIV, pagePB);
        }
    }

    private void showPic(String bitmapPath, ImageView pageIV, String backgroundPath, ImageView bg, ProgressBar pagePB) {
        if (bitmapPath == null) {
            pagePB.setVisibility(View.VISIBLE);
            pageIV.setImageResource(android.R.color.transparent);
            Log.i(LOG_TAG, "bitmapPath NULL");
        } else {
            Log.i(LOG_TAG, "bitmapPath NOT NULL");
            pagePB.setVisibility(View.GONE);
            Picasso.with(activity)
                    .load(new File(bitmapPath))
                    .fit()
                    .centerInside()
                    .skipMemoryCache()
                    .into(pageIV);

            Picasso.with(activity)
                    .load(new File(backgroundPath))
                    .fit()
                    .centerInside()
                    .into(bg);
        }
    }

    public static void showEmptyPic(Activity activity, ImageView pageIV, ProgressBar pagePB) {
        pagePB.setVisibility(View.GONE);
        Picasso.with(activity)
                .load(R.drawable.ajout_photos_livre_02)
                .fit()
                .centerInside()
                .skipMemoryCache()
                .into(pageIV);
    }

    public void setPics(ArrayList<EditorPic> pics){
        this.pics = pics;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ViewGroup) obj);
    }
}