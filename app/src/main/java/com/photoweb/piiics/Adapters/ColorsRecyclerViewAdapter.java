package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.photoweb.piiics.R;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;

import java.util.ArrayList;

/**
 * Created by thomas on 10/07/2017.
 */

public class ColorsRecyclerViewAdapter extends RecyclerView.Adapter<ColorsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Integer> colors;
    private StickerView stickerView;
    private int selectedColor;

    public ColorsRecyclerViewAdapter(Context context, ArrayList<Integer> colors, StickerView stickerView) {
        this.colors = colors;
        this.context = context;
        this.stickerView = stickerView;
        this.selectedColor = colors.get(0);
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    @Override
    public ColorsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout mainView;
        ColorsRecyclerViewAdapter.ViewHolder vh;

        mainView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.utils_color_item, parent, false);
        vh = new ViewHolder(mainView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ColorsRecyclerViewAdapter.ViewHolder holder, final int position) {
        final int color = colors.get(position);

        GradientDrawable bgShape = (GradientDrawable) holder.imageIV.getBackground().getCurrent();

        bgShape.setColor(color);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sticker sticker = stickerView.getCurrentSticker();
                if (sticker instanceof TextSticker) {
                    ((TextSticker) sticker).setTextColor(color);
                    stickerView.replace(sticker);
                    selectedColor = color;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public ImageView imageIV;

        public ViewHolder(View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView;
            imageIV = itemView.findViewById(R.id.image);
        }
    }
}
