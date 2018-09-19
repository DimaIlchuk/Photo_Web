package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.model.PriceReferences.Sticker;
import com.photoweb.piiics.utils.TransformationHandler;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.StickerView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by thomas on 10/07/2017.
 */

public class StickersGridViewAdapter extends BaseAdapter {

    private ArrayList<Sticker> stickersFiles;
    private Context context;
    private LayoutInflater inflater;
    private GridView stickersGV;
    private View fragmentView;
    private HashMap<com.xiaopo.flying.sticker.Sticker, Sticker> list;

    public StickersGridViewAdapter(Context context, ArrayList<Sticker> stickersFiles, GridView stickersGV, View fragmentView, HashMap<com.xiaopo.flying.sticker.Sticker, Sticker> list) {
        this.stickersFiles = stickersFiles;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.stickersGV = stickersGV;
        this.fragmentView = fragmentView;
        this.list = list;
    }

    @Override
    public int getCount() {
        return stickersFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return stickersFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.stickers_gridview_item, null);
        }

        Sticker sticker = stickersFiles.get(position);

        ImageView imageIV = convertView.findViewById(R.id.image);

        final File currentFile = sticker.getStickerFile();

        if(currentFile != null){
            final Bitmap imageBmp = TransformationHandler.generateThumbnail(BitmapFactory.decodeFile(currentFile.getAbsolutePath()), 200);
            imageIV.setImageBitmap(imageBmp);

            TextView textCurrent = convertView.findViewById(R.id.sticker_current_price);
            TextView textReference = convertView.findViewById(R.id.sticker_ref_price);

            textCurrent.setText((sticker.getPriceStr().equals("0.0") ? context.getString(R.string.FREE) : sticker.getPriceStr() + " €"));

            if(!sticker.getRefPriceStr().equals(sticker.getPriceStr())){
                textReference.setText((sticker.getRefPriceStr().equals("0.0") ? context.getString(R.string.FREE) : sticker.getRefPriceStr() + " €"));
                textReference.setPaintFlags(textReference.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textReference.setVisibility(View.VISIBLE);
            }else{
                textReference.setVisibility(View.INVISIBLE);
            }


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "file name : " + currentFile.getName(), Toast.LENGTH_SHORT).show();
                    stickersGV.setVisibility(View.GONE);
                    StickerView stickerView = (StickerView) fragmentView.findViewById(R.id.sticker_view);

                    BitmapDrawable stickerDrawable = new BitmapDrawable(context.getResources(), imageBmp);

                    DrawableSticker mSticker = new DrawableSticker(stickerDrawable);
                    list.put(mSticker, stickersFiles.get(position));

                    stickerView.addSticker(mSticker);

                }
            });
        }

        return convertView;
    }
}
