package com.photoweb.piiics.fragments.BurgerMenuFragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.LoginActivity;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.TutorialReceiver;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by thomas on 11/09/2017.
 */

public class TutorialPageFragment extends BaseFragment {

    TutorialReceiver tutorialReceiver;
    private int viewPagerPosition;

    public void setViewPagerPosition(int viewPagerPosition) {
        this.viewPagerPosition = viewPagerPosition;
    }

    @BindView(R.id.image)
    ImageView imageView;

    @BindView(R.id.title)
    TextView titleTV;

    @BindView(R.id.content)
    TextView contentTV;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tutorial_page;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof MainActivity) {
            tutorialReceiver = ((MainActivity) getActivity()).getTutorialReceiver();
        } else {
            tutorialReceiver = ((LoginActivity) getActivity()).getTutorialReceiver();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPageDatas();
    }

    private void setPageDatas() {
        if(tutorialReceiver != null){
            int i = 0;
            ArrayList<TutorialReceiver.Content> contents = tutorialReceiver.getContents();

            titleTV.setText(contents.get(viewPagerPosition).getTitle());
            contentTV.setText(contents.get(viewPagerPosition).getContent().replace("\\n", "\n"));

            if(imageView.getDrawable() != null){
                ((BitmapDrawable)imageView.getDrawable()).getBitmap().recycle();
            }

            int resId = R.drawable.tuto1;

            switch (viewPagerPosition) {
                case 0:
                    //Picasso.with(activity).load(R.drawable.tuto1).fit().into(imageView);
                    resId = R.drawable.tuto1;
                    break;
                case 1:
                    resId = R.drawable.tuto2;
                    break;
                case 2:
                    resId = R.drawable.tuto3;
                    break;
            }

            Picasso.with(getActivity())
                    .load(resId)
                    .fit()
                    .centerInside()
                    //.centerCrop()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onError() {
                            //Toast.makeText(mContext, "Error picasso - path file : " + path, Toast.LENGTH_SHORT).show();
                            Log.d("LOAD", "failed to load");
                            // File file = new File(bitmapPath);
                        }
                    });
        }
    }
}
