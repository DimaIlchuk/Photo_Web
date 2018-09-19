package com.photoweb.piiics.fragments.EditorFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PriceReferences.FormatReference;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.PriceReferences;
import com.photoweb.piiics.utils.TransformationHandler;
import com.photoweb.piiics.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thomas on 25/04/2017.
 */

public class CropFragment extends EditorFragment {

    private static final String LOG_TAG = "CropFragment";

    private EditorActivity activity;

    private EditorPic picSelected;
    private Bitmap bitmap;
    private int photoWidth;
    private int photoHeight;

    private FormatReference formatReferenceSelected;

    @BindView(R.id.format_square_image)
    ImageView formatSquare;

    @BindView(R.id.format_standard_image)
    ImageView formatStandard;

    @BindView(R.id.format_panoramic_image)
    ImageView formatPanoramic;

    @BindView(R.id.format_square_price)
    TextView formatSquarePriceTextView;

    @BindView(R.id.format_panoramic_price)
    TextView formatPanoramicPriceTextView;

    @BindView(R.id.crop_image_view)
    CropImageView cropImageView;

    @BindView(R.id.utils)
    LinearLayout utilsLL;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_crop;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (EditorActivity) getActivity();
        activity.setToolbarEditMode(getString(R.string.FORMATS));

        if (getTag().equals("FROM_MINI_EDITOR")) {
            utilsLL.setVisibility(View.INVISIBLE);
        }

        picSelected = ((EditorActivity) getActivity()).getCurrentPic();

        String bitmapPath = picSelected.getOriginalBitmapPath();
        Log.d(LOG_TAG, "bitmap : " + bitmapPath);

        if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){
            bitmapPath = picSelected.picAlbums.get(0).getOriginalBitmapPath();
        }

        bitmap = BitmapFactory.decodeFile(bitmapPath);

        //bitmap = resizeBitmapIfNeeded(bitmap);
        initCropImageView();
        showModificationsPrice();
        selectCurrentFormat();

       // resetOtherFeatureBecauseMultipleFeatureNotSupportedForTheMoment();
    }

    private void initCropImageView() {
        cropImageView.setImageBitmap(bitmap);
        cropImageView.setAutoZoomEnabled(false);
        cropImageView.setFixedAspectRatio(true);
    }

    private void showModificationsPrice() {
        String euroSymbol = " €";
        FormatReference standardFormat = PriceReferences.findFormatReferenceByName(PriceReferences.STANDARD_FORMAT);
        FormatReference squareFormat = PriceReferences.findFormatReferenceByName(PriceReferences.SQUARE_FORMAT);
        FormatReference panoramicFormat = PriceReferences.findFormatReferenceByName(PriceReferences.PANORAMIC_FORMAT);

        String standardPrice = standardFormat.getCurPriceStr() + euroSymbol;
        String squarePrice = squareFormat.getCurPriceStr() + euroSymbol;
        String panoramicPrice = panoramicFormat.getCurPriceStr() + euroSymbol;

        formatSquarePriceTextView.setText(squarePrice);
        formatPanoramicPriceTextView.setText(panoramicPrice);
    }

    /*
        Select the same format as the pic
     */
    private void selectCurrentFormat() {
        if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){
            onSetAlbum();
        }else if (picSelected.getFormatReference().getName().equals(PriceReferences.STANDARD_FORMAT)) {
            onStandardFormatButtonClick();
        } else if (picSelected.getFormatReference().getName().equals(PriceReferences.SQUARE_FORMAT)) {
            onSquareFormatButtonClick();
        } else {
            onPanoramicFormatButtonClick();
        }
    }

    private void resetOtherFeatureBecauseMultipleFeatureNotSupportedForTheMoment() {
       // picSelected.setMargin(((EditorActivity) getActivity()).getModificationsManager().getMarginColorNone());
       // picSelected.setFormat(((EditorActivity) getActivity()).getModificationsManager().getFormatStandard());
    }

    private void onSetAlbum(){
        Bitmap bMap = BitmapFactory.decodeFile(picSelected.picAlbums.get(0).getCropBitmapPath());
        photoHeight = bMap.getHeight();
        photoWidth = bMap.getWidth();

        cropImageView.setAspectRatio(bMap.getWidth(), bMap.getHeight());
    }

    private Rect rectSquareFormat() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int widthSquare;
        int heightSquare;
        Rect square;
        if (width < height) {
            widthSquare = width;
            heightSquare = width;
            int topDistance = (height / 2) - (width / 2);
            square = new Rect(0, topDistance, widthSquare, heightSquare + topDistance);
        } else {
            widthSquare = height;
            heightSquare = height;
            int leftDistance = (width / 2) - (height / 2);
            square = new Rect(leftDistance, 0, widthSquare + leftDistance, heightSquare);
        }
        return square;
    }

    private Rect rectPanoramicFormat() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int widthPanoramic;
        int heightPanoramic;
        Rect panoramic;
        if (width < height) {
            widthPanoramic = width;
            heightPanoramic = widthPanoramic * 18 / 10;
            int topDistance = (height / 2) - (heightPanoramic / 2);
            panoramic = new Rect(0, topDistance, widthPanoramic, heightPanoramic + topDistance);
        } else {
            heightPanoramic = height;
            widthPanoramic = heightPanoramic * 18 / 10;
            int leftDistance = (width / 2) - (widthPanoramic / 2);
            panoramic = new Rect(leftDistance, 0, widthPanoramic + leftDistance, heightPanoramic);
        }
        return panoramic;
    }

    private Rect rectStandardFormat() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int widthPanoramic;
        int heightPanoramic;
        Rect standard;
        if (width < height) {
            widthPanoramic = width;
            heightPanoramic = widthPanoramic * 3 / 2;
            int topDistance = (height / 2) - (heightPanoramic / 2);
            standard = new Rect(0, topDistance, widthPanoramic, heightPanoramic + topDistance);
        } else {
            heightPanoramic = height;
            widthPanoramic = heightPanoramic * 3 / 2;
            int leftDistance = (width / 2) - (widthPanoramic / 2);
            standard = new Rect(leftDistance, 0, widthPanoramic + leftDistance, heightPanoramic);
        }
        return standard;
    }

    @OnClick(R.id.format_square)
    public void onSquareFormatButtonClick() {
        cropImageView.setAspectRatio(1, 1);
//        cropImageView.setCropRect(rectSquareFormat());
        deselectImageRessources();
        formatSquare.setImageResource(R.drawable.carre_noir);
        formatReferenceSelected = PriceReferences.findFormatReferenceByName(PriceReferences.SQUARE_FORMAT);
    }

    @OnClick(R.id.format_panoramic)
    public void onPanoramicFormatButtonClick() {
        if(bitmap.getWidth() > bitmap.getHeight()){
            cropImageView.setAspectRatio(18, 10);
        }else{
            cropImageView.setAspectRatio(10, 18);
        }

        //cropImageView.setCropRect(rectPanoramicFormat());
        deselectImageRessources();
        formatPanoramic.setImageResource(R.drawable.panoramique_noir);
        formatReferenceSelected = PriceReferences.findFormatReferenceByName(PriceReferences.PANORAMIC_FORMAT);
    }

    @OnClick(R.id.format_standard)
    public void onStandardFormatButtonClick() {
        if(bitmap != null){
            if(bitmap.getWidth() > bitmap.getHeight()){
                cropImageView.setAspectRatio(3, 2);
            }else{
                cropImageView.setAspectRatio(2, 3);
            }
            //cropImageView.setCropRect(new Rect(rectStandardFormat()));
            deselectImageRessources();
            formatStandard.setImageResource(R.drawable.standard_noir);
            formatReferenceSelected = PriceReferences.findFormatReferenceByName(PriceReferences.STANDARD_FORMAT);
        }

    }

    private void deselectImageRessources() {
        formatSquare.setImageResource(R.drawable.carre_blanc);
        formatPanoramic.setImageResource(R.drawable.panoramique_blanc);
        formatStandard.setImageResource(R.drawable.standard_blanc);
    }

    @OnClick(R.id.finish_button)
    public void onFinishButtonClick() {
        //Bitmap bitmapCropped = TransformationHandler.initCrop(cropImageView.getCroppedImage(), CommandHandler.get().currentCommand.getProduct());

        progressBar.setVisibility(View.VISIBLE);

        if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")){
            picSelected.picAlbums.get(0).saveCropImage(cropImageView.getCroppedImage(), photoWidth, photoHeight);
        }else{
            picSelected.setFormatReference(formatReferenceSelected);

            Bitmap bitmapCropped;

            if(formatReferenceSelected.equals(PriceReferences.findFormatReferenceByName(PriceReferences.STANDARD_FORMAT)))
            {
                bitmapCropped = TransformationHandler.standardPrintBitmap(cropImageView.getCroppedImage());
            }else if(formatReferenceSelected.equals(PriceReferences.findFormatReferenceByName(PriceReferences.SQUARE_FORMAT)))
            {
                bitmapCropped = TransformationHandler.squarePrintBitmap(cropImageView.getCroppedImage());
            }else{
                bitmapCropped = TransformationHandler.panoPrintBitmap(cropImageView.getCroppedImage());
            }

            ((EditorActivity) getActivity()).saveBitmapCropped(bitmapCropped, picSelected);
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

    @OnClick(R.id.cancel_button)
    public void onCancelButtonClick() {
        ((EditorActivity) getActivity()).sendViewPager();
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
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void sendEditor()
    {
        ((EditorActivity) getActivity()).applyAction(picSelected);
        ((EditorActivity) getActivity()).refreshContent(picSelected);
        ((EditorActivity) getActivity()).sendViewPager();
    }
}