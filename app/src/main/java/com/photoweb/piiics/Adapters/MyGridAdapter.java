package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.photoweb.piiics.R;
import com.photoweb.piiics.model.Asset;
import com.photoweb.piiics.utils.FileThumbnailRequestHandler;
import com.photoweb.piiics.utils.PicassoClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by thomas on 18/04/2017.
 */

public class MyGridAdapter extends BaseAdapter {

    ArrayList<Asset> currentItems;
    private int CURRENT_STATE;


    LayoutInflater inflater;
    Context context;
    ImageView imageView;


    public MyGridAdapter(Context context, ArrayList<Asset> currentItems) {
   //     this.selectedItems = new ArrayList<GridViewItem>();
     //   this.localItems = new ArrayList<GridViewItem>();
        this.currentItems = currentItems;
     //   this.CURRENT_STATE = SelectPicsStates.SELECTED_ITEMS;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }
/*
    public void changeState(final int STATE) {
        CURRENT_STATE = STATE;
        if (STATE == SelectPicsStates.LOCAL_ITEMS) {
            currentItems = localItems;
        } else { ///
            currentItems = selectedItems;
        }
        notifyDataSetChanged();
    }

    public void refreshSelectedPics() {
        selectedItems.clear();
        for (GridViewItem item : localItems) {
            if (item.isSelected()) {
                selectedItems.add(new GridViewItem(item.getPath(), item.isSelected()));
            }
        }
    }

    public ArrayList<GridViewItem> getLocalItems() {
        return localItems;
    }
*/

    public void switchPicsType(ArrayList<Asset> items) {
        currentItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return currentItems.size();
    }

    @Override
    public Asset getItem(int position) {
        return currentItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;///
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item, null);
        }

        imageView = convertView.findViewById(R.id.image_grid_item);

        ImageView selectImage = convertView.findViewById(R.id.select_icon_grid_item);
        if (currentItems.get(position).selected) {
            selectImage.setVisibility(View.VISIBLE);
        } else {
            selectImage.setVisibility(View.GONE);
        }

        if(currentItems.get(position).source.equals("Dropbox")){
            PicassoClient.getPicasso()
                    .load(FileThumbnailRequestHandler.buildPicassoUri(currentItems.get(position).imageThumbnail))
                    .fit()
                    .centerCrop()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(context, "Error picasso - path file : " + currentItems.get(position).imageThumbnail, Toast.LENGTH_SHORT).show();
                        }
                    });


        }else if(currentItems.get(position).imageThumbnail != null && currentItems.get(position).imageThumbnail.startsWith("http")){
            Picasso.with(context)
                    .load(currentItems.get(position).imageThumbnail)
                    .fit()
                    .centerCrop()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(context, "Error picasso - path file : " + currentItems.get(position).imageThumbnail, Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Picasso.with(context)
                    .load(new File(currentItems.get(position).imageThumbnail))
                    .fit()
                    .centerCrop()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(context, "Error picasso - path file : " + currentItems.get(position).imageThumbnail, Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        return convertView;
    }

}