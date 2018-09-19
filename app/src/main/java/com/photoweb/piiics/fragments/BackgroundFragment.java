package com.photoweb.piiics.fragments;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.photoweb.piiics.Adapters.BackgroundRecyclerViewAdapter;
import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PriceReferences.BackgroundReference;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.PriceReferences;
import com.photoweb.piiics.utils.TransformationHandler;
import com.photoweb.piiics.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dnizard on 05/07/2017.
 */

public class BackgroundFragment extends BaseFragment {
    private static final String TAG = "BackgroundFragment";

    private EditorActivity activity;

    ImageView imageView;
    ImageView backgroundView;
    private EditorPic picSelected;
    Bitmap originalBitmap;
    Bitmap shownBitmap;
    Bitmap backgroundBitmap;


    BackgroundReference selectedBackground = null;
    BackgroundReference defaultBackground = null;

    BackgroundRecyclerViewAdapter backgroundRecyclerViewAdapter;

    @BindView(R.id.custom_bg_recycler_view)
    RecyclerView utilElementsRV;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    //String selectedBackground = "";
    //String defaultBackground = "";

    private View mView;

    //@BindView(R.id.menu_icons)
    //LinearLayout lLayout;
    //@BindView(R.id.background_white) ImageView defaultButton;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_background;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { // a refacto
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        activity = (EditorActivity) getActivity();
        activity.setToolbarEditMode(getString(R.string.BACKGROUND));

        mView = view;

        imageView = getActivity().findViewById(R.id.pic_selected_imageview);
        backgroundView = getActivity().findViewById(R.id.pic_selected_background);

        picSelected = ((EditorActivity) getActivity()).getCurrentPic();
        originalBitmap = BitmapFactory.decodeFile(picSelected.getCropBitmapPath());
        backgroundBitmap = BitmapFactory.decodeFile(picSelected.getBackgroundReference().getBackgroundFile().getAbsolutePath());

        if(originalBitmap != null) shownBitmap = TransformationHandler.generateThumbnail(originalBitmap, 1000);

        originalBitmap = BitmapFactory.decodeFile(picSelected.getFinalBitmapPath());

        imageView.setImageBitmap(originalBitmap);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        utilElementsRV.setLayoutManager(layoutManager);

        backgroundRecyclerViewAdapter = new BackgroundRecyclerViewAdapter(getContext(), PriceReferences.getBackgrounds(), this);
        utilElementsRV.setAdapter(backgroundRecyclerViewAdapter);
        backgroundRecyclerViewAdapter.notifyDataSetChanged();

        if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){

            if(picSelected.getPhotoID().equals("FIRST") || picSelected.getPhotoID().equals("LAST")){
                Log.d("TAG", "FISRT");
            }else{
                backgroundView.setImageBitmap(backgroundBitmap);

                String currentGab = "onePhoto.DefaultAlbumGabarit";
                if(picSelected.actions.get("gabarit") != null)
                    currentGab = (String)picSelected.actions.get("gabarit");

                if(picSelected.picAlbums.size() > 0){
                    final ArrayList<Bitmap> list = new ArrayList<Bitmap>() {{
                        add(BitmapFactory.decodeFile(picSelected.picAlbums.get(0).getOriginalBitmapPath()));
                        //add(placeholder);
                        //add(placeholder);
                        //add(placeholder);
                    }};

                    //defaultButton.setImageBitmap(TransformationHandler.get().applyGabarit("DefaultGabarit", thumbnail, backgroundBitmap));
                    try {
                        Class defaultGabarit = Class.forName(Utils.package_gabarit + currentGab);
                        Method m = defaultGabarit.getMethod("applyThumbnailGabarit", ArrayList.class);
                        imageView.setImageBitmap(TransformationHandler.generateThumbnail((Bitmap) m.invoke(defaultGabarit.newInstance(), list), 1000));

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (java.lang.InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        checkForGabarit();

        Log.d(TAG, "COUNT BG " + PriceReferences.getBackgrounds().size());

    }

    private void checkForGabarit()
    {
        if(activity.getCommand().getProduct().equals("PRINT") && picSelected.actions.get("gabarit") == null){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getContext());
            }
            builder.setTitle(R.string.INFOS)
                    .setMessage(R.string.VISIBLE_BACK)
                    .setNeutralButton(R.string.UNDERSTAND, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void updateImage()
    {
        selectedBackground = backgroundRecyclerViewAdapter.getSelectedBackground();

        if(CommandHandler.get().currentCommand.getProduct().equals("PRINT")){
            if(picSelected.actions.get("gabarit") != null){
                imageView.setImageBitmap(TransformationHandler.get().applyGabarit((String)picSelected.actions.get("gabarit"), shownBitmap, BitmapFactory.decodeFile(selectedBackground.getBackgroundFile().getAbsolutePath())));
            }
        }else{
            backgroundView.setImageBitmap(BitmapFactory.decodeFile(selectedBackground.getBackgroundFile().getAbsolutePath()));

        }
    }

    @OnClick(R.id.cancel_button)
    public void onCancelClick()
    {
        ((EditorActivity) getActivity()).sendViewPager();
    }

    @OnClick(R.id.finish_button)
    public void onFinishClick()
    {
        if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){
            /*AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getContext());
            }
            builder.setTitle("Fonds")
                    .setMessage("Souhaitez-vous appliquer ce fond à tout le livre ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            progressBar.setVisibility(View.VISIBLE);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms
                                    applyBackgrounds();
                                }
                            }, 50);
                        }
                    })
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            progressBar.setVisibility(View.VISIBLE);

                            picSelected.setBackgroundReference(selectedBackground);

                            applyBackgroundToAlbum(picSelected);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms
                                    sendEditor();
                                }
                            }, 50);

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();*/

            progressBar.setVisibility(View.VISIBLE);

            picSelected.setBackgroundReference(selectedBackground);

            int price = 0;

            try {
                price = picSelected.getBackgroundPrice();
            } catch (PriceSecurityException e) {
                e.printStackTrace();
            }

            Boolean toGenerate = true;

            if(picSelected.getBitmapName().endsWith(".png")){
                toGenerate = false;
            }else if(price > 0){
                toGenerate = true;
                picSelected.setBitmapName(picSelected.getBitmapName().replace(".jpg", ".png"));
                picSelected.setCropBitmapPath(picSelected.getCropBitmapPath().replace(".jpg", ".png"));
                picSelected.setFinalBitmapPath(picSelected.getFinalBitmapPath().replace(".jpg", ".png"));
            }

            //if(picSelected.getPhotoID().equals("FIRST") || picSelected.getPhotoID().equals("LAST")){
            if(!toGenerate){
                sendEditor();
            }else{
                applyBackgroundToAlbum(picSelected);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        sendEditor();
                    }
                }, 50);
            }

        }else{
            progressBar.setVisibility(View.VISIBLE);

            picSelected.setBackgroundReference(selectedBackground);

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

    private void applyBackgroundToAlbum(EditorPic pic)
    {
        try {
            String currentGab = "onePhoto.DefaultAlbumGabarit";
            if(pic.actions.get("gabarit") != null)
                currentGab = (String)pic.actions.get("gabarit");

            Class gabaritCls = Class.forName(Utils.package_gabarit + currentGab);
            Method m = gabaritCls.getMethod("applyGabarit", EditorPic.class);
            //imageView.setImageBitmap((Bitmap) m.invoke(gabaritCls.newInstance(), list));

            activity.saveAlbumCropped((Bitmap) m.invoke(gabaritCls.newInstance(), pic), pic);

            ((EditorActivity) getActivity()).applyAction(pic);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void sendEditor()
    {
        try {
            ((EditorActivity) getActivity()).applyAction(picSelected);
            ((EditorActivity) getActivity()).refreshContent(picSelected);
            ((EditorActivity) getActivity()).sendViewPager();
        }catch (NullPointerException e){
            Log.d(TAG, "error");
        }

    }

    private void applyBackgrounds() {
        for (EditorPic pic: CommandHandler.get().currentCommand.getEditorPics()){
            pic.setBackgroundReference(selectedBackground);

            applyBackgroundToAlbum(pic);
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                sendEditor();
            }
        }, 50);
    }

    /*@OnClick(R.id.background_white)
    public void onGabaritApply(ImageView btn)
    {
        selectedBackground = defaultBackground;
        imageView.setImageBitmap(TransformationHandler.get().applyGabarit((String)picSelected.actions.get("gabarit"), originalBitmap, BitmapFactory.decodeFile(selectedBackground.getBackgroundFile().getAbsolutePath())));
    }*/
}
