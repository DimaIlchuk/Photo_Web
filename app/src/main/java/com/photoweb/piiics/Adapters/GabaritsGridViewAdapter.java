package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.utils.TransformationHandler;
import com.photoweb.piiics.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dnizard on 21/08/2017.
 */

public class GabaritsGridViewAdapter extends RecyclerView.Adapter {

    private List<String> gabaritsList;
    private Context context;
    private LayoutInflater inflater;
    private Bitmap thumbnail;
    private Bitmap placeholder;
    private Bitmap background;
    private EditorPic picSelected;
    private ProgressBar mProgress;

    public GabaritsGridViewAdapter(Context context, List<String> gabList, EditorPic picSelected, ProgressBar progressBar) {
        this.gabaritsList = gabList;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.picSelected = picSelected;
        this.mProgress = progressBar;

        if(picSelected.picAlbums.size() > 0){
            this.thumbnail = TransformationHandler.generateThumbnail(BitmapFactory.decodeFile(picSelected.picAlbums.get(0).getOriginalBitmapPath()), 200);
            this.placeholder = TransformationHandler.generateThumbnail(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.plus_placeholder), 100);
            this.background = TransformationHandler.generateThumbnail(BitmapFactory.decodeFile(picSelected.getBackgroundReference().getBackgroundFile().getAbsolutePath()), 200);
        }
    }

    @Override
    public int getItemCount() {
        return gabaritsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout mainView;
        CaraItem item;

        mainView = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.stickers_gridview_item, parent, false);
        item = new CaraItem(mainView);

        return item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CaraItem item = (CaraItem) holder;

        item.textView.setVisibility(View.INVISIBLE);

        item.background.setImageBitmap(background);

        final String currentGab = gabaritsList.get(position);

        final ArrayList<Bitmap> list = new ArrayList<Bitmap>() {{
            add(thumbnail);
            //add(placeholder);
            //add(placeholder);
            //add(placeholder);
        }};

        //defaultButton.setImageBitmap(TransformationHandler.get().applyGabarit("DefaultGabarit", thumbnail, backgroundBitmap));
        try {
            Class defaultGabarit = Class.forName(Utils.package_gabarit + currentGab);
            Method m = defaultGabarit.getMethod("applyThumbnailGabarit", ArrayList.class);
            item.image.setImageBitmap(TransformationHandler.generateThumbnail((Bitmap) m.invoke(defaultGabarit.newInstance(), list), 200));

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

        item.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "file name : " + currentFile.getName(), Toast.LENGTH_SHORT).show();
                //picSelected.actions.put("AlbumGabarit", currentGab);

                mProgress.setVisibility(View.VISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        sendEditor(currentGab);
                    }
                }, 50);


            }
        });
    }

    /*@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.stickers_gridview_item, null);
        }
        ImageView imageIV = convertView.findViewById(R.id.image);
        ImageView backgroundIV = convertView.findViewById(R.id.background);
        TextView textView = convertView.findViewById(R.id.sticker_current_price);
        textView.setVisibility(View.INVISIBLE);

        backgroundIV.setImageBitmap(background);

        final String currentGab = gabaritsList.get(position);

        final ArrayList<Bitmap> list = new ArrayList<Bitmap>() {{
            add(thumbnail);
            //add(placeholder);
            //add(placeholder);
            //add(placeholder);
        }};

        //defaultButton.setImageBitmap(TransformationHandler.get().applyGabarit("DefaultGabarit", thumbnail, backgroundBitmap));
        try {
            Class defaultGabarit = Class.forName(Utils.package_gabarit + currentGab);
            Method m = defaultGabarit.getMethod("applyThumbnailGabarit", ArrayList.class);
            imageIV.setImageBitmap(TransformationHandler.generateThumbnail((Bitmap) m.invoke(defaultGabarit.newInstance(), list), 200));

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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "file name : " + currentFile.getName(), Toast.LENGTH_SHORT).show();
                //picSelected.actions.put("AlbumGabarit", currentGab);

                mProgress.setVisibility(View.VISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        sendEditor(currentGab);
                    }
                }, 50);


            }
        });

        return convertView;
    }*/

    private void sendEditor(String currentGab)
    {
        try {
            picSelected.actions.put("gabarit", currentGab);

            Class gabaritCls = Class.forName(Utils.package_gabarit + currentGab);
            Method m = gabaritCls.getMethod("applyGabarit", EditorPic.class);
            //imageView.setImageBitmap((Bitmap) m.invoke(gabaritCls.newInstance(), list));

            ((EditorActivity) context).saveAlbumCropped((Bitmap) m.invoke(gabaritCls.newInstance(), picSelected), picSelected);

            ((EditorActivity) context).applyAction(picSelected);
            ((EditorActivity) context).refreshContent(picSelected);
            ((EditorActivity) context).sendViewPager();

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
    }

    public static class CaraItem extends RecyclerView.ViewHolder {
        LinearLayout mainView;

        ImageView background;
        ImageView image;
        TextView textView;

        public CaraItem(LinearLayout mainView) {
            super(mainView);
            this.mainView = mainView;
            this.background = mainView.findViewById(R.id.background);
            this.image = mainView.findViewById(R.id.image);
            this.textView = mainView.findViewById(R.id.sticker_current_price);
        }
    }

}
