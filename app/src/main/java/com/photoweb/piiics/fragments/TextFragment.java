package com.photoweb.piiics.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.photoweb.piiics.Adapters.ColorsRecyclerViewAdapter;
import com.photoweb.piiics.Adapters.FontsRecyclerViewAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.model.BorderColors;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PriceReferences.DynamicText;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.PriceReferences;
import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;
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
 * Created by dnizard on 10/07/2017.
 */

public class TextFragment extends BaseFragment {
    private static final String TAG = "TextFragment";

    private static final int HOME = 1;
    private static final int FONTS = 2;
    private static final int COLORS = 3;

    EditorActivity activity;

    ImageView imageView;
    ImageView backgroundView;
    private EditorPic picSelected;
    Bitmap originalBitmap;
    Bitmap shownBitmap;

    private int FOOTER_BAR_STATE;

    ArrayList<Typeface> customFonts;
    FontsRecyclerViewAdapter fontsRecyclerViewAdapter;
    private Typeface defaultTypeFace;

    ArrayList<Integer> colors;
    ColorsRecyclerViewAdapter colorsRecyclerViewAdapter;
    private int defaultColor;

    private int defaultposition = 0;

    @BindView(R.id.sticker_view)
    StickerView stickerView;

    @BindView(R.id.custom_font_recycler_view)
    RecyclerView utilElementsRV;

    @BindView(R.id.utils_layout)
    LinearLayout utilsLayoutLL;

    HashMap<Sticker, DynamicText> listOfStickers;
    String currentText = "";

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_text;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        activity = (EditorActivity) getActivity();
        activity.setToolbarEditMode(getString(R.string.TEXT));

        imageView = getActivity().findViewById(R.id.pic_selected_imageview);
        backgroundView = getActivity().findViewById(R.id.pic_selected_background);

        picSelected = ((EditorActivity) getActivity()).getCurrentPic();
        originalBitmap = BitmapFactory.decodeFile(picSelected.getFinalBitmapPath());

        if(originalBitmap == null){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(activity);
            }
            builder.setTitle(R.string.ERROR)
                    .setMessage(R.string.GENERAL_ERROR)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            ((EditorActivity) getActivity()).sendViewPager();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return;
        }

        shownBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);

        imageView.setImageBitmap(originalBitmap);

        listOfStickers = new HashMap<>();

        initStickersView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        utilElementsRV.setLayoutManager(layoutManager);

        customFonts = activity.customFonts;
        fontsRecyclerViewAdapter = new FontsRecyclerViewAdapter(getContext(), customFonts, stickerView);
        defaultTypeFace = customFonts.get(0);///

        colors = getColors();
        colorsRecyclerViewAdapter = new ColorsRecyclerViewAdapter(getContext(), colors, stickerView);
        defaultColor = colors.get(0);///

        FOOTER_BAR_STATE = HOME;

        if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){
            backgroundView.setImageBitmap(BitmapFactory.decodeFile(picSelected.getBackgroundReference().getBackgroundFile().getAbsolutePath()));
        }
    }

    private ArrayList<Integer> getColors() {
        int[] allColors = getContext().getResources().getIntArray(R.array.piiicsColors);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : allColors) {
            Log.i(TAG, "color : " + String.valueOf(color));
            colors.add(color);
        }
        return colors;
    }

    private void initStickersView() {
        stickerView.configDefaultIcons();

        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(),
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(),
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

                float x = (stickerView.getCurrentSticker().getMappedBound().centerX()- ((imageView.getWidth() - newWidth)/2))/zoomScale;
                float y = (stickerView.getCurrentSticker().getMappedBound().centerY()- ((imageView.getHeight() - newHeigth)/2))/zoomScale;
                float width = stickerView.getCurrentSticker().getCurrentWidth()/zoomScale;
                float height = stickerView.getCurrentSticker().getCurrentHeight()/zoomScale;
                float arg = stickerView.getCurrentSticker().getCurrentAngle();

                listOfStickers.put(stickerView.getCurrentSticker(), new DynamicText(currentText, x, y, width, height, BorderColors.getBLACK(), customFonts.get(0), 0, arg));

                Log.d(TAG, "sticker size : " + listOfStickers.get(stickerView.getCurrentSticker()).width + " " + listOfStickers.get(stickerView.getCurrentSticker()).height);

                Log.d(TAG, "sticker center : " + listOfStickers.get(stickerView.getCurrentSticker()).x + " " + listOfStickers.get(stickerView.getCurrentSticker()).y + " / " + stickerView.getCurrentSticker().getMappedBound().centerX() + " " + stickerView.getCurrentSticker().getMappedBound().centerY());

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

                listOfStickers.get(stickerView.getCurrentSticker()).x = (stickerView.getCurrentSticker().getMappedBound().centerX()- ((imageView.getWidth() - newWidth)/2))/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).y = (stickerView.getCurrentSticker().getMappedBound().centerY() - ((imageView.getHeight() - newHeigth)/2))/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).width = stickerView.getCurrentSticker().getCurrentWidth()/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).height = stickerView.getCurrentSticker().getCurrentHeight()/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).arg = stickerView.getCurrentSticker().getCurrentAngle();

                Log.d(TAG, "sticker : " + listOfStickers.get(stickerView.getCurrentSticker()).x + " " + listOfStickers.get(stickerView.getCurrentSticker()).y + " / " + stickerView.getCurrentSticker().getMappedBound().centerX() + " " + stickerView.getCurrentSticker().getMappedBound().centerY());
                Log.d(TAG, "sticker : " + listOfStickers.get(stickerView.getCurrentSticker()).width + " " + listOfStickers.get(stickerView.getCurrentSticker()).height + " / " + stickerView.getCurrentSticker().getMappedBound().right + " " + stickerView.getCurrentSticker().getMappedBound().top);

            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
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

                listOfStickers.get(stickerView.getCurrentSticker()).x = (stickerView.getCurrentSticker().getMappedBound().centerX()- ((imageView.getWidth() - newWidth)/2))/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).y = (stickerView.getCurrentSticker().getMappedBound().centerY() - ((imageView.getHeight() - newHeigth)/2))/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).width = stickerView.getCurrentSticker().getCurrentWidth()/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).height = stickerView.getCurrentSticker().getCurrentHeight()/zoomScale;
                listOfStickers.get(stickerView.getCurrentSticker()).arg = stickerView.getCurrentSticker().getCurrentAngle();
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

        if(originalBitmap.getWidth() > originalBitmap.getHeight()){
            newWidth = imageView.getWidth();
            newHeigth = originalBitmap.getHeight()*imageView.getWidth()/originalBitmap.getWidth();
        }else{
            newHeigth = imageView.getHeight();
            newWidth = originalBitmap.getWidth()*newHeigth/originalBitmap.getHeight();
        }

        return newWidth;
    }

    public float getNewHeight()
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

        return newHeigth;
    }

    public float getZoomScale(){
        float newWidth = 0;
        float newHeigth = 0;

        if(originalBitmap.getWidth() > originalBitmap.getHeight()){
            newWidth = imageView.getWidth();
            newHeigth = originalBitmap.getHeight()*imageView.getWidth()/originalBitmap.getWidth();
        }else{
            newHeigth = imageView.getHeight();
            newWidth = originalBitmap.getWidth()*newHeigth/originalBitmap.getHeight();
        }

        float zoomScaleW = newWidth / originalBitmap.getWidth();
        float zoomScaleH = newHeigth / originalBitmap.getHeight();
        float zoomScale = min(zoomScaleW, zoomScaleH);

        return zoomScale;
    }

    public void sendEditor()
    {
        ((EditorActivity) getActivity()).applyAction(picSelected);
        ((EditorActivity) getActivity()).refreshContent(picSelected);
        ((EditorActivity) getActivity()).sendViewPager();
    }

    public void eraseAll()
    {
        picSelected.actions.remove(PriceReferences.TEXTS);

        sendEditor();
    }

    @OnClick(R.id.cancel_button)
    public void onCancelClick() {
        if (FOOTER_BAR_STATE != HOME) {
            Sticker sticker = stickerView.getCurrentSticker();
            if (sticker instanceof TextSticker) {
                if (FOOTER_BAR_STATE == FONTS) {
                    ((TextSticker) sticker).setTypeface(defaultTypeFace);
                } else if (FOOTER_BAR_STATE == COLORS) {
                    ((TextSticker) sticker).setTextColor(defaultColor);
                }
                stickerView.replace(sticker);
            }
            utilsLayoutLL.setVisibility(View.VISIBLE);
            utilElementsRV.setVisibility(View.GONE);
            FOOTER_BAR_STATE = HOME;
        } else {
            ((EditorActivity) getActivity()).sendViewPager();
        }
    }

    @OnClick(R.id.finish_button)
    public void onFinishClick() {

        if (FOOTER_BAR_STATE != HOME) {
            defaultTypeFace = fontsRecyclerViewAdapter.getSelectedTypeFace();
            defaultColor = colorsRecyclerViewAdapter.getSelectedColor();
            defaultposition = fontsRecyclerViewAdapter.getPositionfont();

            if(listOfStickers.get(stickerView.getCurrentSticker()) != null){
                Log.d(TAG, "font : " + defaultTypeFace);
                //listOfStickers.get(stickerView.getCurrentSticker()).font = defaultTypeFace;
                listOfStickers.get(stickerView.getCurrentSticker()).color = defaultColor;
                listOfStickers.get(stickerView.getCurrentSticker()).position = defaultposition;
            }

            utilsLayoutLL.setVisibility(View.VISIBLE);
            utilElementsRV.setVisibility(View.GONE);
            FOOTER_BAR_STATE = HOME;

            Log.d(TAG, "position : " + defaultposition);
        } else {
            progressBar.setVisibility(View.VISIBLE);

            ArrayList<DynamicText> list = new ArrayList<DynamicText>();

            if(picSelected.actions.get(PriceReferences.TEXTS) != null){
                list = (ArrayList<DynamicText>) picSelected.actions.get(PriceReferences.TEXTS);
            }

            Iterator it = listOfStickers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Log.d(TAG, "value : " + ((DynamicText) pair.getValue()).x + " " + ((DynamicText) pair.getValue()).y);

                list.add((com.photoweb.piiics.model.PriceReferences.DynamicText) pair.getValue());

                it.remove(); // avoids a ConcurrentModificationException
            }
            picSelected.actions.put(PriceReferences.TEXTS, list);
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

    @OnClick(R.id.delete_all_layout)
    public void onDeleteAllClick() {

        progressBar.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                eraseAll();
            }
        }, 50);

    }

    @OnClick(R.id.add_text_layout)
    public void onAddTextClick() {
        AlertDialog.Builder alertDialog;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            alertDialog = new AlertDialog.Builder(getContext());
        }

        alertDialog.setTitle(R.string.ADD_TEXT);
        //alertDialog.setMessage("Enter text");

        final EditText input = new EditText(getContext());
        input.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.ADD_ACTION,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        currentText = input.getText().toString();

                        Typeface custom_font = customFonts.get(0);

                        final TextSticker sticker = new TextSticker(getContext());
                        sticker.setText(input.getText().toString());
                        sticker.setTextColor(Color.BLACK);
                        sticker.setTypeface(custom_font);
                        sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
                        sticker.resizeText();

                        stickerView.addSticker(sticker);
                    }
                });

        alertDialog.setNegativeButton(R.string.CANCEL,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @OnClick(R.id.add_font_layout)
    public void onAddFontClick() {
        utilsLayoutLL.setVisibility(View.GONE);
        utilElementsRV.setVisibility(View.VISIBLE);
        utilElementsRV.setAdapter(fontsRecyclerViewAdapter);
        FOOTER_BAR_STATE = FONTS;
    }

    @OnClick(R.id.add_color_layout)
    public void onAddColorClick() {
        utilsLayoutLL.setVisibility(View.GONE);
        utilElementsRV.setVisibility(View.VISIBLE);
        utilElementsRV.setAdapter(colorsRecyclerViewAdapter);
        FOOTER_BAR_STATE = COLORS;
    }
}