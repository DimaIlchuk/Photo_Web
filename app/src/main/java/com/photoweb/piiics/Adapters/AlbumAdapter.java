package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.model.Album;
import com.photoweb.piiics.model.Asset;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by dnizard on 16/06/2017.
 */

public class AlbumAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Album> mDataSource;

    public AlbumAdapter(Context context, List<Album> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void switchAlbumsType(List<Album> items) {
        mDataSource = items;
        notifyDataSetChanged();
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        Album album = mDataSource.get(position);
        View rowView = mInflater.inflate(R.layout.list_item_album, parent, false);

        TextView tx = (TextView) rowView.findViewById(R.id.album_title);
        tx.setText(album.name);

        ImageView img = (ImageView) rowView.findViewById(R.id.album_image);

        if(album.assets != null && album.assets.size() > 0){
            Asset cover = album.assets.get(0);

            if(cover.imageThumbnail.startsWith("http")){
                Picasso.with(mContext).load(cover.imageThumbnail)
                        .fit()
                        .centerCrop()
                        .into(img);
            }else{
                Picasso.with(mContext).load(new File(cover.imageThumbnail))
                        .fit()
                        .centerCrop()
                        .into(img);
            }
        }

        return rowView;
    }
}
