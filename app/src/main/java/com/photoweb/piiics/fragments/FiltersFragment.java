package com.photoweb.piiics.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.FilterHandler;
import com.photoweb.piiics.utils.TransformationHandler;
import com.photoweb.piiics.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dnizard on 03/07/2017.
 */

public class FiltersFragment extends BaseFragment {
    private static final String TAG = "FiltersFragment";

    private EditorActivity activity;

    ImageView imageView;
    private EditorPic picSelected;
    Bitmap originalBitmap;
    Bitmap shownBitmap;
    Bitmap thumbnail;

    List<String> filterNames = Arrays.asList("DefaultFilter", "SepiaFilter", "ContrastFilter", "InvertFilter", "HueFilter", "GrayscaleFilter", "SobelFilter", "EmbossFilter", "MonoFilter", "VignetteFilter");
    List<Integer> buttons = Arrays.asList(R.id.filter_default, R.id.filter_sepia, R.id.filter_contrast, R.id.filter_invert, R.id.filter_hue, R.id.filter_grayscale, R.id.filter_sobel, R.id.filter_emboss, R.id.filter_mono, R.id.filter_vignette);

    String selectedFilter = "";

    private View mView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_filters;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { // a refacto
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        activity = (EditorActivity) getActivity();
        activity.setToolbarEditMode(getString(R.string.FILTER));

        mView = view;

        imageView = (ImageView) getActivity().findViewById(R.id.pic_selected_imageview);

        picSelected = ((EditorActivity) getActivity()).getCurrentPic();

        String bitmapPath = picSelected.getCropBitmapPath();

        if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){
            bitmapPath = picSelected.picAlbums.get(0).getCropBitmapPath();
        }

        originalBitmap = BitmapFactory.decodeFile(bitmapPath);

        shownBitmap = TransformationHandler.generateThumbnail(originalBitmap, 1000);

        thumbnail = TransformationHandler.generateThumbnail(originalBitmap, 200);

        imageView.setImageBitmap(shownBitmap);

        initToolBar();

    }

    private void initToolBar()
    {
        for (int i=0; i<buttons.size(); i++)
        {
            CircleImageView button = (CircleImageView) mView.findViewById(buttons.get(i));
            button.setImageBitmap(FilterHandler.get().applyFilter(filterNames.get(i), thumbnail));
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
        progressBar.setVisibility(View.VISIBLE);

        if(!selectedFilter.equals("")){
            //picSelected.actions.put("filter", selectedFilter);

            if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){
                picSelected.picAlbums.get(0).actions.put("filter", selectedFilter);
            }else{
                picSelected.actions.put("filter", selectedFilter);
            }

        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms

                if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){
                    applyGabarit();
                }

                sendEditor();
            }
        }, 50);
    }

    public void applyGabarit()
    {
        picSelected.picAlbums.get(0).applyAllTransformations();

        try {
            String gabarit = "DefaultAlbumGabarit";

            if(picSelected.actions.get("gabarit") != null)
                gabarit = (String) picSelected.actions.get("gabarit");

            Class defaultGabarit = Class.forName(Utils.package_gabarit + gabarit);
            Method m = defaultGabarit.getMethod("drawOnPage", EditorPic.class);
            m.invoke(defaultGabarit.newInstance(), picSelected);

            ((EditorActivity) getContext()).saveAlbumCropped((Bitmap) m.invoke(defaultGabarit.newInstance(), picSelected), picSelected);

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
        ((EditorActivity) getActivity()).applyAction(picSelected);
        ((EditorActivity) getActivity()).refreshContent(picSelected);
        ((EditorActivity) getActivity()).sendViewPager();
    }

    @OnClick({R.id.filter_default, R.id.filter_sepia, R.id.filter_contrast, R.id.filter_invert, R.id.filter_hue, R.id.filter_grayscale, R.id.filter_sobel, R.id.filter_emboss, R.id.filter_mono, R.id.filter_vignette})
    public void onFilterApply(CircleImageView btn)
    {
        Log.d(TAG, filterNames.get(Integer.parseInt((String) btn.getTag()) - 10));
        selectedFilter = filterNames.get(Integer.parseInt((String) btn.getTag()) - 10);
        imageView.setImageBitmap(FilterHandler.get().applyFilter(selectedFilter, shownBitmap));
    }
}
