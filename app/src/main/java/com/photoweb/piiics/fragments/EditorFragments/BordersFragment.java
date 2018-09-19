package com.photoweb.piiics.fragments.EditorFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.model.BorderColors;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.utils.TransformationHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by thomas on 26/04/2017.
 */

public class BordersFragment extends EditorFragment {

    private EditorActivity activity;

    ImageView imageView;
    private EditorPic picSelected;
    Bitmap originalBitmap;
    Bitmap shownBitmap;

    int selectedColor = 0;

    @BindView(R.id.border_white) ImageView borderWhite;
    @BindView(R.id.border_black) ImageView borderBlack;
    @BindView(R.id.border_yellow) ImageView borderYellow;
    @BindView(R.id.border_green) ImageView borderGreen;
    @BindView(R.id.border_dark_blue) ImageView borderDarkBlue;
    @BindView(R.id.border_light_blue) ImageView borderLightBlue;
    @BindView(R.id.border_purple) ImageView borderPurple;
    @BindView(R.id.border_pink) ImageView borderPink;
    @BindView(R.id.border_light_pink) ImageView borderLightPink;
    @BindView(R.id.border_light_brown) ImageView borderLightBrown;
    @BindView(R.id.border_dark_brown) ImageView borderDarkBrown;
    @BindView(R.id.border_salmon) ImageView borderSalmon;
    @BindView(R.id.border_dark_red) ImageView borderDarkRed;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_borders;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { // a refacto
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        activity = (EditorActivity) getActivity();
        activity.setToolbarEditMode(getString(R.string.MARGIN));

        imageView = getActivity().findViewById(R.id.pic_selected_imageview);

        picSelected = ((EditorActivity) getActivity()).getCurrentPic();
        originalBitmap = BitmapFactory.decodeFile(picSelected.getFinalBitmapPath());

        shownBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);

        imageView.setImageBitmap(shownBitmap);
       // picSelected.setMargin(((EditorActivity) getActivity()).getModificationsManager().getMarginColorNone());

        borderWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getWHITE());
            }
        });

        borderBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getBLACK());
            }
        });

        borderYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getYELLOW());
            }
        });

        borderGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getGREEN());
            }
        });

        borderDarkBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getDARK_BLUE());
            }
        });

        borderLightBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getLIGHT_BLUE());
            }
        });

        borderPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getPURPLE());
            }
        });

        borderPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getPINK());
            }
        });

        borderLightPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getLIGHT_PINK());
            }
        });

        borderLightBrown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getLIGHT_BROWN());
            }
        });

        borderDarkBrown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getDARK_BROWN());
            }
        });

        borderSalmon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getSALMON());
            }
        });

        borderDarkRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(BorderColors.getDARK_RED());
            }
        });
/*
        borderBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(Color.BLACK);
            }
        });

        borderYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColor(Color.YELLOW);
            }
        });*/
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

        if(selectedColor != 0){
            picSelected.actions.put("border", selectedColor);
        }else{
            picSelected.actions.remove("border");
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

    public void sendEditor()
    {
        ((EditorActivity) getActivity()).applyAction(picSelected);
        ((EditorActivity) getActivity()).getEditorPicsAdapter().notifyDataSetChanged();
        ((EditorActivity) getActivity()).sendViewPager();
    }

    @OnClick(R.id.no_border)
    public void onNoBorderClick()
    {
        selectedColor = 0;
        if(picSelected.actions.get("border") != null) {
            shownBitmap = TransformationHandler.get().removeBorder(originalBitmap.copy(originalBitmap.getConfig(), true));
        }else{
            shownBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        }

        imageView.setImageBitmap(shownBitmap);
    }

    private void addColor(int color) {
        selectedColor = color;
        shownBitmap = TransformationHandler.get().addBorder(originalBitmap.copy(originalBitmap.getConfig(), true), color, (picSelected.actions.get("border") != null));
        imageView.setImageBitmap(shownBitmap);
        //picSelected.setMargin(((EditorActivity) getActivity()).getModificationsManager().getMarginColorSet());
    }
}
