package com.photoweb.piiics.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.photoweb.piiics.Adapters.GabaritsGridViewAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.PriceReferences;
import com.photoweb.piiics.utils.TransformationHandler;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dnizard on 03/07/2017.
 */

public class GabaritsFragment extends BaseFragment {

    private static final String TAG = "GabaritsFragment";

    private EditorActivity activity;

    ImageView imageView;
    ImageView backgroundView;
    private EditorPic picSelected;
    Bitmap originalBitmap;
    Bitmap shownBitmap;
    Bitmap backgroundBitmap;
    Bitmap thumbnail;

    List<String> gabaritNames = Arrays.asList("DefaultGabarit", "RoundedRectGabarit", "DiamondGabarit", "SquareLeftGabarit", "RoundGabarit", "RotateGabarit", "ReverseRotate", "PolaroidGabarit", "PanaPolaroidGabarit", "SquarePolaroidGabarit", "MarginBottomGabarit", "FourTierGabarit", "ThirdHalfGabarit");
    List<String> standardGabarits = Arrays.asList("RoundedRectGabarit","SquareLeftGabarit","RoundGabarit","DiamondGabarit","RotateGabarit","MarginBottomGabarit","PolaroidGabarit");
    List<String> squareGabarits = Arrays.asList("RoundedRectGabarit", "ThirdHalfGabarit", "RoundGabarit", "DiamondGabarit", "ReverseRotate", "SquarePolaroidGabarit");
    List<String> panoGabarits = Arrays.asList("RoundedRectGabarit", "SquareLeftGabarit", "FourTierGabarit", "RotateGabarit", "PanaPolaroidGabarit");
    List<String> onePhotoGabarits = Arrays.asList("onePhoto.MarginBottomAlbumGabarit", "onePhoto.CenterThirdHalfAlbumGabarit", "onePhoto.FullScreenAlbumGabarit", "onePhoto.RotateAlbumGabarit", "onePhoto.RoundedRectAlbumGabarit", "onePhoto.LeftThirdHalfAlbumGabarit", "onePhoto.RoundAlbumGabarit", "onePhoto.RoundedSquareAlbumGabarit", "onePhoto.DiamondAlbumGabarit");
    List<String> twoPhotosGabarits = Arrays.asList("twoPhotos.BigAndSmallFourTierAlbumGabarit", "twoPhotos.DoubleFourTierAlbumGabarit", "twoPhotos.DoubleSquareAlbumGabarit", "twoPhotos.FourTierAndRoundAlbumGabarit", "twoPhotos.SixteenNinethAndDiamondAlbumGabarit", "twoPhotos.SixteenNinethAndRoundAlbumGabarit", "twoPhotos.ThirdHalfAndRoundAlbumGabarit");
    List<String> threePhotosGabarits = Arrays.asList("threePhotos.SixteenNinethAndTwoFourAlbumGabarit", "threePhotos.TripleRoundAlbumGabarit", "threePhotos.TripleSixteenNinethAlbumGabarit", "threePhotos.TripleSquareAlbumGabarit");
    List<String> fourPhotosGabarits = Arrays.asList("fourPhotos.QuadrupleRoundAlbumGabarit", "threePhotos.TripleRoundAlbumGabarit", "fourPhotos.QuadrupleSquareAlbumGabarit", "fourPhotos.QuadrupleThirdHalfAlbumGabarit", "fourPhotos.SixteenNinethAndThreeFourThirdAlbumGabarit", "fourPhotos.ThirdHalfAndThreeRoundAlbumGabarit");
    //List<Integer> buttons = Arrays.asList(R.id.gabarit_default, R.id.gabarit_roundedrect, R.id.gabarit_squareleft, R.id.gabarit_round, R.id.gabarit_rotate, R.id.gabarit_reverserotate, R.id.gabarit_polaroid, R.id.gabarit_panapolaroid, R.id.gabarit_squarepolaroid, R.id.gabarit_marginbottom, R.id.gabarit_fourtier, R.id.gabarit_thirdhalf);

    List<String> gabarits;

    String selectedGabarit = "";

    private View mView;

    @BindView(R.id.menu_icons) LinearLayout lLayout;
    //@BindView(R.id.gabarit_default) CircleImageView defaultButton;
    @BindView(R.id.gabarits_grid) GridView gabaritGrid;

    @BindView(R.id.print_toolbar)
    HorizontalScrollView horizontalScrollView;

    @BindView(R.id.album_toolbar)
    RecyclerView linearLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_gabarits;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { // a refacto
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        activity = (EditorActivity) getActivity();
        activity.setToolbarEditMode(getString(R.string.GABARIT));

        mView = view;

        imageView = getActivity().findViewById(R.id.pic_selected_imageview);
        backgroundView = getActivity().findViewById(R.id.pic_selected_background);

        picSelected = ((EditorActivity) getActivity()).getCurrentPic();
        originalBitmap = BitmapFactory.decodeFile(picSelected.getCropBitmapPath());
        if(picSelected.getBackgroundReference() != null)
            backgroundBitmap = BitmapFactory.decodeFile(picSelected.getBackgroundReference().getBackgroundFile().getAbsolutePath());

        shownBitmap = TransformationHandler.generateThumbnail(originalBitmap, 800);

        thumbnail = TransformationHandler.generateThumbnail(originalBitmap, 100);

        originalBitmap = BitmapFactory.decodeFile(picSelected.getFinalBitmapPath());

        imageView.setImageBitmap(originalBitmap);

        if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")) {
            backgroundView.setImageBitmap(BitmapFactory.decodeFile(picSelected.getBackgroundReference().getBackgroundFile().getAbsolutePath()));
            initAlbumBar();
        }else{
            if(picSelected.getFormatReference().getName().equals(PriceReferences.STANDARD_FORMAT)){
                gabarits = standardGabarits;
            }else if(picSelected.getFormatReference().getName().equals(PriceReferences.SQUARE_FORMAT)){
                gabarits = squareGabarits;
            }else{
                gabarits = panoGabarits;
            }

            initToolBar();
        }

    }

    private void initAlbumBar()
    {
        horizontalScrollView.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);

        LinearLayoutManager layout = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        linearLayout.setLayoutManager(layout);
        linearLayout.setAdapter(new GabaritsGridViewAdapter(activity, onePhotoGabarits, picSelected, progressBar));
        
    }

    private void initToolBar()
    {
        ImageView defaultBTN = new ImageView(getContext());
        LinearLayout.LayoutParams lParams=new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.imageview_width), (int) getResources().getDimension(R.dimen.imageview_height));
        lParams.gravity= Gravity.CENTER;
        defaultBTN.setLayoutParams(lParams);

        defaultBTN.setTag(String.valueOf(10));

        defaultBTN.setImageBitmap(TransformationHandler.get().applyGabarit("DefaultGabarit", thumbnail, backgroundBitmap));

        defaultBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedGabarit = "DefaultGabarit";
                imageView.setImageBitmap(TransformationHandler.get().applyGabarit(selectedGabarit, shownBitmap, backgroundBitmap));
            }
        });

        lLayout.addView(defaultBTN);

        int i = 0;

        for (String gabarit: gabarits) {
            ImageView button = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.imageview_width), (int) getResources().getDimension(R.dimen.imageview_height));
            layoutParams.gravity= Gravity.CENTER;
            layoutParams.setMarginStart((int) getResources().getDimension(R.dimen.margin_start));
            button.setLayoutParams(layoutParams);

            button.setTag(String.valueOf(11 + i));

            button.setImageBitmap(TransformationHandler.get().applyGabarit(gabarit, thumbnail, backgroundBitmap));

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, gabarits.get(Integer.parseInt((String) view.getTag()) - 11));
                    selectedGabarit = gabarits.get(Integer.parseInt((String) view.getTag()) - 11);
                    imageView.setImageBitmap(TransformationHandler.get().applyGabarit(selectedGabarit, shownBitmap, backgroundBitmap));
                }
            });

            lLayout.addView(button);

            i++;
        }

        /*for (int i=0; i<buttons.size(); i++)
        {
            CircleImageView button = (CircleImageView) mView.findViewById(buttons.get(i));
            button.setImageBitmap(TransformationHandler.get().applyGabarit(gabaritNames.get(i), thumbnail, backgroundBitmap));
        }*/
    }

    /*@OnClick(R.id.gabarit_1)
    public void onGabarit1Click()
    {
        gabaritGrid.setVisibility(View.VISIBLE);

        GabaritsGridViewAdapter gridViewAdapter = new GabaritsGridViewAdapter(getContext(), onePhotoGabarits, picSelected, progressBar);
        gabaritGrid.setAdapter(gridViewAdapter);
    }

    @OnClick(R.id.gabarit_2)
    public void onGabarit2Click()
    {
        gabaritGrid.setVisibility(View.VISIBLE);

        GabaritsGridViewAdapter gridViewAdapter = new GabaritsGridViewAdapter(getContext(), twoPhotosGabarits, picSelected, progressBar);
        gabaritGrid.setAdapter(gridViewAdapter);
    }

    @OnClick(R.id.gabarit_3)
    public void onGabarit3Click()
    {
        gabaritGrid.setVisibility(View.VISIBLE);

        GabaritsGridViewAdapter gridViewAdapter = new GabaritsGridViewAdapter(getContext(), threePhotosGabarits, picSelected, progressBar);
        gabaritGrid.setAdapter(gridViewAdapter);
    }

    @OnClick(R.id.gabarit_4)
    public void onGabarit4Click()
    {
        gabaritGrid.setVisibility(View.VISIBLE);

        GabaritsGridViewAdapter gridViewAdapter = new GabaritsGridViewAdapter(getContext(), fourPhotosGabarits, picSelected, progressBar);
        gabaritGrid.setAdapter(gridViewAdapter);
    }*/

    @OnClick(R.id.cancel_button)
    public void onCancelClick()
    {
        ((EditorActivity) getActivity()).sendViewPager();
    }

    @OnClick(R.id.finish_button)
    public void onFinishClick()
    {
        progressBar.setVisibility(View.VISIBLE);

        if(!selectedGabarit.equals("")){
            picSelected.actions.put("gabarit", selectedGabarit);
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
        try{
            ((EditorActivity) getActivity()).applyAction(picSelected);
            ((EditorActivity) getActivity()).getEditorPicsAdapter().notifyDataSetChanged();
            ((EditorActivity) getActivity()).sendViewPager();
        }catch (NullPointerException e){
            Log.d(TAG, "error");
        }

    }

    /*@OnClick(R.id.gabarit_default)
    public void onGabaritApply(CircleImageView btn)
    {
        Log.d(TAG, gabaritNames.get(Integer.parseInt((String) btn.getTag()) - 10));
        selectedGabarit = "onePhoto.DefaultAlbumGabarit";
        //imageView.setImageBitmap(TransformationHandler.get().applyGabarit(selectedGabarit, originalBitmap, backgroundBitmap));

        ArrayList<Bitmap> list = new ArrayList<Bitmap>() {{
            add(originalBitmap);
        }};

        try {
            Class gabaritCls = Class.forName(Utils.package_gabarit + selectedGabarit);
            Method m = gabaritCls.getMethod("applyGabarit", ArrayList.class);
            imageView.setImageBitmap((Bitmap) m.invoke(gabaritCls.newInstance(), list));

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
    }*/
}
