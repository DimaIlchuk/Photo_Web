package com.photoweb.piiics.fragments.BasketFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.AlbumOptions;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PriceReferences.DynamicText;
import com.photoweb.piiics.model.PriceReferences.Sticker;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.StickerHandler;
import com.photoweb.piiics.utils.Utils;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thomas on 06/09/2017.
 */

public class OptionLogoFragment extends BaseFragment {
    private static final String LOG_TAG = "OptionLogoFragment";

    BasketActivity activity;
    AlbumOptions albumOptions;

    int bookQuantity = 1;
    boolean isNoLogo = false;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.quantityNumber)
    TextView quantityNumberTV;

    @BindView(R.id.checkbox)
    CheckBox logoCB;

    @BindView(R.id.logoPrice)
    TextView logoPriceTV;

    @BindView(R.id.additionalBookPrice)
    TextView additionalBookPriceTV;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_album_option_logo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BasketActivity) getActivity());
        albumOptions = CommandHandler.get().currentCommand.getAlbumOptions();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setClipToOutline(true);
        }
        activity.setAlbumOptionsMode(true);

        setPrices();
        quantityNumberTV.setText(String.valueOf(bookQuantity));
        refreshCheckBoxLogo();
    }

    private void setPrices() {
        String logoPrice = "(" + albumOptions.getNoLogoOption().getCurPriceStr() + "€)";
        logoPriceTV.setText(logoPrice);

        String additionalBookPrice = albumOptions.getAdditionalBook().getCurPriceStr() + "€";
        additionalBookPriceTV.setText(additionalBookPrice);
    }

    private void refreshCheckBoxLogo() {
        logoCB.setChecked(isNoLogo);
    }

    @OnClick(R.id.quantityNegative)
    public void onQuantityNegativeClick() {
        if (bookQuantity > 1) {
            bookQuantity--;
            quantityNumberTV.setText(String.valueOf(bookQuantity));
            albumOptions.setBookQuantity(bookQuantity);
        }
    }

    @OnClick(R.id.quantityPositive)
    public void onQuantityPositiveClick() {
        bookQuantity++;
        quantityNumberTV.setText(String.valueOf(bookQuantity));
        albumOptions.setBookQuantity(bookQuantity);
    }

    @OnClick(R.id.checkbox)
    public void onCheckBoxClick() {
        isNoLogo = logoCB.isChecked();
        albumOptions.setHasNoLogo(logoCB.isChecked());
    }

    @OnClick(R.id.validate)
    public void onValidateClick() {
        Command command = CommandHandler.get().currentCommand;
        EditorPic picSelected = command.getAlbumBackCover();

        if(isNoLogo && picSelected.actions.get("Logo") != null){
            picSelected.actions.remove("Logo");
            TreatBackCover(command, picSelected);
        }

        if (!isNoLogo && picSelected.actions.get("Logo") == null){
            picSelected.actions.put("Logo", true);
            TreatBackCover(command, picSelected);
        }

        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new OptionCoverFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void TreatBackCover(Command command, EditorPic picSelected){
        Bitmap finale = BitmapFactory.decodeFile(picSelected.getCropBitmapPath());

        if (picSelected.actions.get("Stickers") != null) {
            ArrayList<Sticker> list = (ArrayList<Sticker>) picSelected.actions.get("Stickers");

            for (Sticker sticker : list) {
                Log.i(LOG_TAG, "sticker name : " + sticker.getName() + ", sticker fileName : " + sticker.getStickerFile().getName());
                finale = StickerHandler.drawSticker(finale, BitmapFactory.decodeFile(sticker.getStickerFile().getAbsolutePath()), sticker.x, sticker.y, Math.round(sticker.width), Math.round(sticker.height), sticker.arg);
            }
        }

        if (picSelected.actions.get("Texts") != null) {
            ArrayList<DynamicText> list = (ArrayList<DynamicText>) picSelected.actions.get("Texts");
            ArrayList<Typeface> customFonts = getFonts();

            for (DynamicText sticker : list) {
                Log.i(LOG_TAG, "Text : " + sticker.position);
                Log.i(LOG_TAG, "Text Font : " + customFonts.get(sticker.position).toString());

                Bitmap txt = StickerHandler.generateBitmapFromText(sticker.text, 360, sticker.color, customFonts.get(sticker.position), sticker.height);
                Log.d(LOG_TAG, "Text : " + txt.getWidth() + " - " + txt.getHeight());
                Log.d(LOG_TAG, "Text : " + (sticker.x - txt.getWidth()/2) + " - " + (sticker.y - txt.getHeight()/2));

                finale = StickerHandler.drawSticker(finale, txt, sticker.x, sticker.y, txt.getWidth(), txt.getHeight(), sticker.arg);
            }
        }

        if (picSelected.actions.get("Logo") != null) {
            finale = StickerHandler.drawSticker(finale, BitmapFactory.decodeResource(getResources(), R.drawable.logo_book), Utils.pageWidth/2, Utils.pageHeight-200, 317, 209, 0);
        }

        File picFile = new File(picSelected.getFinalBitmapPath());
        picFile.delete();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picSelected.getFinalBitmapPath());

            if(picSelected.getFinalBitmapPath().endsWith(".png")){
                finale.compress(Bitmap.CompressFormat.PNG, 100, out);
            }else{
                finale.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            command.saveCommand();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Typeface> getFonts() {
        ArrayList<Typeface> customFonts = new ArrayList<>();

        String[] listFonts;
        try {
            listFonts =getContext().getAssets().list("fonts");
            for (String fontName : listFonts) {
                Log.i(LOG_TAG, "fontName : " + fontName);
                Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                customFonts.add(customFont);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return customFonts;
    }

}
