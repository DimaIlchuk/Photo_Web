package com.photoweb.piiics.Adapters;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.EditorFragments.MiniEditorFragment;
import com.photoweb.piiics.model.EditorPic;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by thomas on 20/04/2017.
 */

public class EditorPicsAdapter extends PagerAdapter {
    private static final String LOG_TAG = "EditorPicsAdapter";
    AppCompatActivity activity;
    ArrayList<EditorPic> pics;

    Bitmap currentImageLoaded;

    int editorPicThreadPosition = -1;

    public EditorPicsAdapter(AppCompatActivity activity, ArrayList<EditorPic> pics) {
        this.activity = activity;
        this.pics = pics;
    }

    public Bitmap getCurrentImageLoaded() {
        return currentImageLoaded;
    }


    @Override
    public int getItemPosition(Object object) {
        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return pics.size();
    }


    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == ((ViewGroup) obj);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
      // RelativeLayout layout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.editor_page, container, false);

        LayoutInflater inflater = LayoutInflater.from(activity);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.editor_page, container, false);

        layout.setTag(position);

        ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

        if (progressBar == null) {
            Log.i(LOG_TAG, "progressBar NULL");
        } else {
            Log.i(LOG_TAG, "progressBar NOT NULL");
        }

        ImageView mImageView = (ImageView) layout.findViewById(R.id.imageView);
        mImageView.setScaleType(ImageView.ScaleType.CENTER);

        final String bitmapPath = pics.get(position).getFinalBitmapPath();
        if (bitmapPath == null) {
            progressBar.setVisibility(View.VISIBLE);
            Log.i(LOG_TAG, "bitmapPath NULL");
        } else {
            Log.i(LOG_TAG, "bitmapPath NOT NULL");
            progressBar.setVisibility(View.GONE);
            Picasso.with(activity)
                    .load(new File(bitmapPath))
                    .fit()
                    .centerInside()
                    //.centerCrop()
                    .skipMemoryCache()
                    .into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onError() {
                            //Toast.makeText(mContext, "Error picasso - path file : " + path, Toast.LENGTH_SHORT).show();
                            Log.d("LOAD", "failed to load " + bitmapPath);
                           // File file = new File(bitmapPath);
                        }
                    });
        }
      //  mImageView.setImageBitmap(pics.get(position).getBitmap());


       // ((ViewPager) container).removeView(layout);

        container.setTag(String.valueOf(pics.get(position).getIndex()));

        container.addView(layout);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pics.get(position).operated){
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new MiniEditorFragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        //((ViewPager) container).addView(layout, 0);

//        currentImageLoaded = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();

/*        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (picSelected.actions.get("AlbumGabarit") != null) {
                    try {
                        PointF point = new PointF(motionEvent.getX(), motionEvent.getY());
                        Class gabaritCls = Class.forName(Utils.package_gabarit + picSelected.actions.get("AlbumGabarit"));
                        Method m = gabaritCls.getMethod("isInside", PointF.class);
                        m.invoke(gabaritCls.newInstance(), point);

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }
                return false;
            }
        });

        return mImageView;*/
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ViewGroup) obj);
    }
}