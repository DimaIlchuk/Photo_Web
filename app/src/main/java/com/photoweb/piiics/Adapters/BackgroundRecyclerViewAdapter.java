package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.fragments.BackgroundFragment;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.PriceReferences.BackgroundReference;
import com.photoweb.piiics.utils.TransformationHandler;

import java.util.ArrayList;

/**
 * Created by dnizard on 29/08/2017.
 */

public class BackgroundRecyclerViewAdapter extends RecyclerView.Adapter<BackgroundRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BackgroundReference> backgrounds;
    private BackgroundReference selectedBackground;
    private BaseFragment fragment;

    public BackgroundRecyclerViewAdapter(Context context, ArrayList<BackgroundReference> backgrounds, BaseFragment fragment) {
        this.backgrounds = backgrounds;
        this.context = context;
        this.selectedBackground = null;
        this.fragment = fragment;
    }

    public BackgroundReference getSelectedBackground() {
        return selectedBackground;
    }

    @Override
    public BackgroundRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout mainView;
        BackgroundRecyclerViewAdapter.ViewHolder vh;

        mainView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.utils_background_item, parent, false);
        vh = new ViewHolder(mainView);
        return vh;
    }

    @Override
    public void onBindViewHolder(BackgroundRecyclerViewAdapter.ViewHolder holder, final int position) {

        final BackgroundReference background = backgrounds.get(position);

        Bitmap thumbnail = TransformationHandler.generateThumbnail(BitmapFactory.decodeFile(background.getBackgroundFile().getAbsolutePath()), 200);

        String currentPrice = "";
        String refPrice = "";

        if(((EditorActivity)context).getCommand().getProduct().equals("PRINT")){
            currentPrice = background.getPricePrintStr();
            refPrice = background.getRefPricePrintStr();
        }else{
            currentPrice = background.getPriceAlbumStr();
            refPrice = background.getRefPriceAlbumStr();
        }

        holder.imageView.setImageBitmap(thumbnail);
        holder.textCurrent.setText((currentPrice.equals("0.0") ? context.getString(R.string.FREE) : currentPrice + " €"));

        if(!refPrice.equals(currentPrice)){
            holder.textReference.setText((refPrice.equals("0.0") ? context.getString(R.string.FREE) : refPrice + " €"));
            holder.textReference.setPaintFlags(holder.textReference.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textReference.setVisibility(View.VISIBLE);
        }else{
            holder.textReference.setVisibility(View.INVISIBLE);
        }


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBackground = background;
                if(fragment instanceof BackgroundFragment){
                    ((BackgroundFragment)fragment).updateImage();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return backgrounds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public ImageView imageView;
        public TextView textCurrent;
        public TextView textReference;

        public ViewHolder(View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView;
            imageView = (ImageView) itemView.findViewById(R.id.image_background);
            textCurrent = (TextView) itemView.findViewById(R.id.bg_current_price);
            textReference = (TextView) itemView.findViewById(R.id.bg_ref_price);
        }
    }

}
