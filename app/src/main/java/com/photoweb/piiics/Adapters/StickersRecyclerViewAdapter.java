package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.StickersFragment;
import com.photoweb.piiics.model.PriceReferences.StickerCategory;
import com.xiaopo.flying.sticker.Sticker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by thomas on 10/07/2017.
 */

public class StickersRecyclerViewAdapter extends RecyclerView.Adapter<StickersRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<StickerCategory> categories;
    private View fragmentView;
    private HashMap<Sticker, com.photoweb.piiics.model.PriceReferences.Sticker> list;
    private StickersFragment fragment;

    public StickersRecyclerViewAdapter(Context context, ArrayList<StickerCategory> categories, View view, HashMap<Sticker, com.photoweb.piiics.model.PriceReferences.Sticker> list, StickersFragment fragment) {
        this.categories = categories;
        this.fragmentView = view;
        this.context = context;
        this.list = list;
        this.fragment = fragment;
    }

    @Override
    public StickersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout mainView;
        StickersRecyclerViewAdapter.ViewHolder vh;

        mainView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stickers_category_item, parent, false);
        vh = new ViewHolder(mainView);
        return vh;
    }

    @Override
    public void onBindViewHolder(StickersRecyclerViewAdapter.ViewHolder holder, int position) {

        if (position == 0) {
            holder.imageIV.setImageResource(R.drawable.poubelle_gris);
            holder.nameTV.setText(R.string.PAGE_DELETE_TITLE);//verifier le texte

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment.eraseAll();
                }
            });
        } else {
            final StickerCategory categoryStickers = categories.get(position - 1);

            holder.nameTV.setText(categoryStickers.getFolderNameFr());//todo : getFolderFr a call

            if (!categoryStickers.getStickers().isEmpty()) {
                Log.d("Sticker", categoryStickers.getFolder());
                Log.d("Sticker", categoryStickers.getStickers().get(0).getName());

                for (com.photoweb.piiics.model.PriceReferences.Sticker sticker:categoryStickers.getStickers()) {
                    if(sticker.getId() == categoryStickers.getCoverId()){
                        if(sticker.getStickerFile() != null){
                            Bitmap imageBmp = BitmapFactory.decodeFile(sticker.getStickerFile().getAbsolutePath());
                            holder.imageIV.setImageBitmap(imageBmp);
                        }

                        break;
                    }
                }

                final GridView stickersGV = fragmentView.findViewById(R.id.stickers_grid);

                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            stickersGV.setVisibility(View.VISIBLE);
                            //animation
                            setGridView(stickersGV, categoryStickers);
                    }
                });
            } else {
                //todo: afficher une image par défaut si la catégorie de stickers existe mais est vide
            }
        }
    }

    private void setGridView(GridView stickerGV, StickerCategory categoryStickers) {
        StickersGridViewAdapter stickersGridViewAdapter = new StickersGridViewAdapter(context, categoryStickers.getStickers(), stickerGV, fragmentView, list);
        stickerGV.setAdapter(stickersGridViewAdapter);
    }

    //+ 1 pour compter l'item 'tout effacer'
    @Override
    public int getItemCount() {
        return categories.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public ImageView imageIV;
        public TextView nameTV;

        public ViewHolder(View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView;
            nameTV = (TextView) itemView.findViewById(R.id.category_name);
            imageIV = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
