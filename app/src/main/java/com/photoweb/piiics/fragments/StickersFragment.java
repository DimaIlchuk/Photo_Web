package com.photoweb.piiics.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.photoweb.piiics.Adapters.StickersRecyclerViewAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.PriceReferences;
import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.ZoomIconEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.Math.min;

/**
 * Created by dnizard on 03/07/2017.
 */

public class StickersFragment extends BaseFragment {
    private static final String TAG = "StickersFragment";

    private EditorActivity activity;

    ImageView imageView;
    ImageView backgroundView;
    private EditorPic picSelected;
    Bitmap originalBitmap;
  //  Bitmap shownBitmap;
  //  Bitmap thumbnail;

    StickersRecyclerViewAdapter stickersRecyclerViewAdapter;

    HashMap<Sticker, com.photoweb.piiics.model.PriceReferences.Sticker> listOfStickers;
    HashMap<Sticker, com.photoweb.piiics.model.PriceReferences.Sticker> mapStickers;

    @BindView(R.id.stickers_recycler_view)
    RecyclerView stickersRV;

    @BindView(R.id.sticker_view)
    StickerView stickerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_stickers;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { // a refacto
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        activity = (EditorActivity) getActivity();
        activity.setToolbarEditMode(getString(R.string.STICKERS));

        imageView = getActivity().findViewById(R.id.pic_selected_imageview);
        backgroundView = getActivity().findViewById(R.id.pic_selected_background);

        picSelected = ((EditorActivity) getActivity()).getCurrentPic();
        originalBitmap = BitmapFactory.decodeFile(picSelected.getFinalBitmapPath());//final ?

        //shownBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        //thumbnail = TransformationHandler.generateThumbnail(originalBitmap);

        imageView.setImageBitmap(originalBitmap);

        listOfStickers = new HashMap<>();
        mapStickers = new HashMap<>();

        initStickersBar();
        initStickersView();

        if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){
            backgroundView.setImageBitmap(BitmapFactory.decodeFile(picSelected.getBackgroundReference().getBackgroundFile().getAbsolutePath()));
        }
    }

    private void initStickersBar() {

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        stickersRV.setLayoutManager(layoutManager);

        stickersRecyclerViewAdapter = new StickersRecyclerViewAdapter(getContext(), PriceReferences.getStickerCategories(), getView(), mapStickers, this);
        stickersRV.setAdapter(stickersRecyclerViewAdapter);
    }

    private void initStickersView() {
        stickerView.configDefaultIcons();

        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(),
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        final BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(),
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_scale_white_18dp),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon));

        stickerView.setLocked(false);
        stickerView.setConstrained(true);

        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerAdded : " + stickerView.getCurrentSticker().getMappedCenterPoint().toString() + " , " + stickerView.getCurrentSticker().getCurrentWidth() + "-" +  stickerView.getCurrentSticker().getCurrentHeight() + " , " + stickerView.getCurrentSticker().getCurrentAngle());
                Log.d(TAG, listOfStickers.toString());

                float newWidth = getNewWidth();
                float newHeigth = getNewHeight();

                float zoomScale = getZoomScale();

                Log.d(TAG, "zoom : " + zoomScale + " " + newWidth + " " + newHeigth);

                Log.d(TAG, "current size : " + stickerView.getCurrentSticker().getCurrentHeight() + " - " + stickerView.getCurrentSticker().getCurrentWidth());
                Log.d(TAG, "bitmap size : " + stickerView.getCurrentSticker().getDrawable().getIntrinsicWidth() + " - " + stickerView.getCurrentSticker().getDrawable().getIntrinsicHeight());


                float x = (stickerView.getCurrentSticker().getMappedBound().centerX()- ((imageView.getWidth() - newWidth)/2))/zoomScale;
                float y = (stickerView.getCurrentSticker().getMappedBound().centerY()- ((imageView.getHeight() - newHeigth)/2))/zoomScale;
                //float width = (stickerView.getCurrentSticker().getMappedBound().right - stickerView.getCurrentSticker().getMappedBound().left)/zoomScale;
                //float height = (stickerView.getCurrentSticker().getMappedBound().bottom - stickerView.getCurrentSticker().getMappedBound().top)/zoomScale;
                float width = stickerView.getCurrentSticker().getCurrentWidth()/zoomScale;
                float height = stickerView.getCurrentSticker().getCurrentHeight()/zoomScale;
                float arg = stickerView.getCurrentSticker().getCurrentAngle();

                listOfStickers.put(stickerView.getCurrentSticker(), new com.photoweb.piiics.model.PriceReferences.Sticker(mapStickers.get(stickerView.getCurrentSticker()), x, y, width, height, arg));
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                //stickerView.removeAllSticker();
                Log.d(TAG, "onStickerClicked");
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDeleted");
                listOfStickers.remove(stickerView.getCurrentSticker());
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDragFinished : " + stickerView.getCurrentSticker().getMappedCenterPoint().toString() + " , " + stickerView.getCurrentSticker().getCurrentWidth() + "-" +  stickerView.getCurrentSticker().getCurrentHeight() + " , " + stickerView.getCurrentSticker().getCurrentAngle());
                if(isInside(stickerView.getCurrentSticker().getMappedBound().left, stickerView.getCurrentSticker().getMappedBound().top)){
                    Log.d(TAG, "It's inside !");
                }else{
                    Log.d(TAG, "It's not !");
                }

                float newWidth = getNewWidth();
                float newHeigth = getNewHeight();

                float zoomScale = getZoomScale();

                Log.d(TAG, "zoom : " + zoomScale + " " + newWidth + " " + newHeigth);

                Log.d(TAG, "current size : " + stickerView.getCurrentSticker().getCurrentHeight() + " - " + stickerView.getCurrentSticker().getCurrentWidth());
                Log.d(TAG, "bitmap size : " + stickerView.getCurrentSticker().getDrawable().getIntrinsicWidth() + " - " + stickerView.getCurrentSticker().getDrawable().getIntrinsicHeight());


                //listOfStickers.get(stickerView.getCurrentSticker()).x = (stickerView.getCurrentSticker().getMappedBound().left - ((imageView.getWidth() - newWidth)/2))/zoomScale;
                //listOfStickers.get(stickerView.getCurrentSticker()).y = (stickerView.getCurrentSticker().getMappedBound().top - (imageView.getHeight() - newHeigth)/2)/zoomScale;

                listOfStickers.get(stickerView.getCurrentSticker()).x = (stickerView.getCurrentSticker().getMappedBound().centerX()- ((imageView.getWidth() - newWidth)/2))/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).y = (stickerView.getCurrentSticker().getMappedBound().centerY() - ((imageView.getHeight() - newHeigth)/2))/zoomScale;

                //listOfStickers.get(stickerView.getCurrentSticker()).width = (stickerView.getCurrentSticker().getMappedBound().right - stickerView.getCurrentSticker().getMappedBound().left)/zoomScale;
                //listOfStickers.get(stickerView.getCurrentSticker()).height = (stickerView.getCurrentSticker().getMappedBound().bottom - stickerView.getCurrentSticker().getMappedBound().top)/zoomScale;

                listOfStickers.get(stickerView.getCurrentSticker()).width = stickerView.getCurrentSticker().getCurrentWidth()/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).height = stickerView.getCurrentSticker().getCurrentHeight()/zoomScale;


                listOfStickers.get(stickerView.getCurrentSticker()).arg = stickerView.getCurrentSticker().getCurrentAngle();

                Log.d(TAG, "sticker size : " + listOfStickers.get(stickerView.getCurrentSticker()).width + " " + listOfStickers.get(stickerView.getCurrentSticker()).height);

                Log.d(TAG, "sticker center : " + listOfStickers.get(stickerView.getCurrentSticker()).x + " " + listOfStickers.get(stickerView.getCurrentSticker()).y + " / " + stickerView.getCurrentSticker().getMappedBound().centerX() + " " + stickerView.getCurrentSticker().getMappedBound().centerY());

            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerZoomFinished : " + stickerView.getCurrentSticker().getMappedCenterPoint().toString() + " , " + stickerView.getCurrentSticker().getCurrentWidth() + "-" +  stickerView.getCurrentSticker().getCurrentHeight() + " , " + stickerView.getCurrentSticker().getCurrentAngle());

                if(isInside(stickerView.getCurrentSticker().getMappedBound().left, stickerView.getCurrentSticker().getMappedBound().top)){
                    Log.d(TAG, "It's inside !");
                }else{
                    Log.d(TAG, "It's not !");
                }

                float newWidth = getNewWidth();
                float newHeigth = getNewHeight();

                float zoomScale = getZoomScale();

                Log.d(TAG, "zoom : " + zoomScale + " " + newWidth + " " + newHeigth);

                Log.d(TAG, "current size : " + stickerView.getCurrentSticker().getCurrentHeight() + " - " + stickerView.getCurrentSticker().getCurrentWidth());
                Log.d(TAG, "bitmap size : " + stickerView.getCurrentSticker().getDrawable().getIntrinsicWidth() + " - " + stickerView.getCurrentSticker().getDrawable().getIntrinsicHeight());


                //listOfStickers.get(stickerView.getCurrentSticker()).x = (stickerView.getCurrentSticker().getMappedBound().left - ((imageView.getWidth() - newWidth)/2))/zoomScale;
                //listOfStickers.get(stickerView.getCurrentSticker()).y = (stickerView.getCurrentSticker().getMappedBound().top - (imageView.getHeight() - newHeigth)/2)/zoomScale;

                listOfStickers.get(stickerView.getCurrentSticker()).x = (stickerView.getCurrentSticker().getMappedBound().centerX()- ((imageView.getWidth() - newWidth)/2))/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).y = (stickerView.getCurrentSticker().getMappedBound().centerY() - ((imageView.getHeight() - newHeigth)/2))/zoomScale;

                //listOfStickers.get(stickerView.getCurrentSticker()).width = (stickerView.getCurrentSticker().getMappedBound().right - stickerView.getCurrentSticker().getMappedBound().left)/zoomScale;
                //listOfStickers.get(stickerView.getCurrentSticker()).height = (stickerView.getCurrentSticker().getMappedBound().bottom - stickerView.getCurrentSticker().getMappedBound().top)/zoomScale;

                listOfStickers.get(stickerView.getCurrentSticker()).width = stickerView.getCurrentSticker().getCurrentWidth()/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).height = stickerView.getCurrentSticker().getCurrentHeight()/zoomScale;

                listOfStickers.get(stickerView.getCurrentSticker()).arg = stickerView.getCurrentSticker().getCurrentAngle();

                Log.d(TAG, "sticker size : " + listOfStickers.get(stickerView.getCurrentSticker()).width + " " + listOfStickers.get(stickerView.getCurrentSticker()).height);

                Log.d(TAG, "sticker center : " + listOfStickers.get(stickerView.getCurrentSticker()).x + " " + listOfStickers.get(stickerView.getCurrentSticker()).y + " / " + stickerView.getCurrentSticker().getMappedBound().centerX() + " " + stickerView.getCurrentSticker().getMappedBound().centerY());

            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerFlipped");
            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                Log.d(TAG, "onDoubleTapped: double tap will be with two click");
            }
        });
    }

    public boolean isInside(float x, float y)
    {
        float newWidth = 0;
        float newHeigth = 0;

        if(originalBitmap.getWidth() > originalBitmap.getHeight()){
            newWidth = imageView.getWidth();
            newHeigth = originalBitmap.getHeight()*imageView.getWidth()/originalBitmap.getWidth();
        }else{
            newHeigth = imageView.getHeight();
            newWidth = originalBitmap.getWidth()*newHeigth/originalBitmap.getHeight();
        }

        float zoomScale = getZoomScale();

        return (x > (imageView.getWidth() - newWidth)/2) && (x < (imageView.getWidth() + newWidth)/2) && (y > (imageView.getHeight() - newHeigth)/2) && (y > (imageView.getHeight() - newHeigth)/2);
    }

    public float getNewWidth()
    {
        float newWidth = 0;
        float newHeigth = 0;

        if(originalBitmap == null)
            return 0;

        if(originalBitmap.getWidth() > originalBitmap.getHeight()){
            newWidth = imageView.getWidth();
        }else{
            newHeigth = imageView.getHeight();
            newWidth = originalBitmap.getWidth()*newHeigth/originalBitmap.getHeight();
        }

        return newWidth;
    }

    public float getNewHeight()
    {
        float newHeigth = 0;

        if(originalBitmap != null){
            if(originalBitmap.getWidth() > originalBitmap.getHeight()){
                newHeigth = originalBitmap.getHeight()*imageView.getWidth()/originalBitmap.getWidth();
            }else{
                newHeigth = imageView.getHeight();
            }
        }

        return newHeigth;
    }

    public float getZoomScale(){
        float newWidth;
        float newHeigth;

        if(originalBitmap == null){
            return 1;
        }

        if(originalBitmap.getWidth() > originalBitmap.getHeight()){
            newWidth = imageView.getWidth();
            newHeigth = originalBitmap.getHeight()*imageView.getWidth()/originalBitmap.getWidth();
        }else{
            newHeigth = imageView.getHeight();
            newWidth = originalBitmap.getWidth()*newHeigth/originalBitmap.getHeight();
        }

        Log.d(TAG, "scale : " + originalBitmap.getWidth() + " - " + originalBitmap.getHeight() + " / " + newWidth + " - " + newHeigth);

        float zoomScaleW = newWidth / originalBitmap.getWidth();
        float zoomScaleH = newHeigth / originalBitmap.getHeight();
        float zoomScale = min(zoomScaleW, zoomScaleH);

        return zoomScale;
    }

    public void eraseAll()
    {
        picSelected.actions.remove(PriceReferences.STICKERS);

        progressBar.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                sendEditor();
            }
        }, 50);
    }

    public void sendEditor()
    {
        ((EditorActivity) getActivity()).applyAction(picSelected);
        ((EditorActivity) getActivity()).refreshContent(picSelected);
        ((EditorActivity) getActivity()).sendViewPager();
    }

    @OnClick(R.id.cancel_button)
    public void onCancelClick() {
        ((EditorActivity) getActivity()).sendViewPager();
    }

    @OnClick(R.id.finish_button)
    public void onFinishClick() {
        progressBar.setVisibility(View.VISIBLE);

        ArrayList<com.photoweb.piiics.model.PriceReferences.Sticker> list = new ArrayList<com.photoweb.piiics.model.PriceReferences.Sticker>();

        if(picSelected.actions.get(PriceReferences.STICKERS) != null){
            list = (ArrayList<com.photoweb.piiics.model.PriceReferences.Sticker>) picSelected.actions.get(PriceReferences.STICKERS);
        }

        Iterator it = listOfStickers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Log.d(TAG, "value : " + ((com.photoweb.piiics.model.PriceReferences.Sticker) pair.getValue()).x + " " + ((com.photoweb.piiics.model.PriceReferences.Sticker) pair.getValue()).y);

            list.add((com.photoweb.piiics.model.PriceReferences.Sticker) pair.getValue());

            it.remove(); // avoids a ConcurrentModificationException
        }
        picSelected.actions.put(PriceReferences.STICKERS, list);
        picSelected.showActions();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                sendEditor();
            }
        }, 50);
    }
}
