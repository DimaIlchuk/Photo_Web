package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;

import java.util.ArrayList;

/**
 * Created by thomas on 10/07/2017.
 */

public class FontsRecyclerViewAdapter extends RecyclerView.Adapter<FontsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Typeface> customFonts;
    private StickerView stickerView;
    private Typeface selectedTypeFace;
    private int positionfont = 0;

    public FontsRecyclerViewAdapter(Context context, ArrayList<Typeface> customFonts, StickerView stickerView) {
        this.customFonts = customFonts;
        this.context = context;
        this.stickerView = stickerView;
        this.selectedTypeFace = null;
    }

    public Typeface getSelectedTypeFace() {
        return selectedTypeFace;
    }

    public int getPositionfont() {
        return positionfont;
    }

    @Override
    public FontsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout mainView;
        FontsRecyclerViewAdapter.ViewHolder vh;

        mainView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.utils_font_item, parent, false);
        vh = new ViewHolder(mainView);
        return vh;
    }

    @Override
    public void onBindViewHolder(FontsRecyclerViewAdapter.ViewHolder holder, final int position) {

            final Typeface typeface = customFonts.get(position);
            holder.textTV.setTypeface(typeface);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sticker sticker = stickerView.getCurrentSticker();
                    if (sticker instanceof TextSticker) {
                        ((TextSticker) sticker).setTypeface(typeface);
                        stickerView.replace(sticker);
                        selectedTypeFace = typeface;
                        positionfont = position;
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return customFonts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public TextView textTV;

        public ViewHolder(View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView;
            textTV = itemView.findViewById(R.id.text);
        }
    }
}
