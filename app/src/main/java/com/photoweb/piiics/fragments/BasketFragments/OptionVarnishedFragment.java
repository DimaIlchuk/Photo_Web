package com.photoweb.piiics.fragments.BasketFragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.AlbumOptions;
import com.photoweb.piiics.utils.CommandHandler;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thomas on 06/09/2017.
 */

public class OptionVarnishedFragment extends BaseFragment {
    private static final String LOG_TAG = "OptionVarnishedFragment";

    BasketActivity activity;
    AlbumOptions albumOptions;

    @BindView(R.id.currentPrice)
    TextView currentPriceTV;

    @BindView(R.id.refPrice)
    TextView refPriceTV;

    @BindView(R.id.coverIV)
    ImageView coverIV;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_album_option_varnished;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BasketActivity) getActivity());
        albumOptions = CommandHandler.get().currentCommand.getAlbumOptions();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setAlbumOptionsMode(true);

        coverIV.setClipToOutline(true);
        setPrices();
    }

    private void setPrices() {
        float curPrice = Float.valueOf(albumOptions.getVarnishedPagesOption().getCurPriceStr());
        String curPriceStr = albumOptions.getVarnishedPagesOption().getCurPriceStr() + "€";
        currentPriceTV.setText(curPriceStr);

        float refPrice = Float.valueOf(albumOptions.getVarnishedPagesOption().getRefPriceStr());
        if (curPrice == refPrice) {
            refPriceTV.setVisibility(View.GONE);
        } else {
            refPriceTV.setVisibility(View.VISIBLE);
            String refPriceStr = albumOptions.getVarnishedPagesOption().getRefPriceStr() + "€";
            refPriceTV.setText(refPriceStr);
            refPriceTV.setPaintFlags(refPriceTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @OnClick(R.id.dontVarnish)
    public void onDontVarnishedClick() {
        albumOptions.setHasVarnishedPages(false);
        launchNextFragment();
    }

    @OnClick(R.id.doVarnish)
    public void onDoVarnishedClick() {
        albumOptions.setHasVarnishedPages(true);
        launchNextFragment();
    }

    private void launchNextFragment() {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new OptionMateFragment());
        ft.addToBackStack(null);
        ft.commit();
    }
}
