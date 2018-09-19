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

public class OptionMateFragment extends BaseFragment {
    private static final String LOG_TAG = "OptionMateFragment";

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
        return R.layout.fragment_album_option_mate;
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
        float curPrice = Float.valueOf(albumOptions.getMateCoverOption().getCurPriceStr());
        String curPriceStr = albumOptions.getMateCoverOption().getCurPriceStr() + "€";
        currentPriceTV.setText(curPriceStr);

        float refPrice = Float.valueOf(albumOptions.getMateCoverOption().getRefPriceStr());
        if (curPrice == refPrice) {
            refPriceTV.setVisibility(View.GONE);
        } else {
            refPriceTV.setVisibility(View.VISIBLE);
            String refPriceStr = albumOptions.getMateCoverOption().getRefPriceStr() + "€";
            refPriceTV.setText(refPriceStr);
            refPriceTV.setPaintFlags(refPriceTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @OnClick(R.id.mateCover)
    public void onMateCoverClick() {
        albumOptions.setHasMateCover(true);
        launchNextFragment();
    }

    @OnClick(R.id.classicCover)
    public void onClassicCoverClick() {
        albumOptions.setHasMateCover(false);
        launchNextFragment();
    }

    private void launchNextFragment() {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new SummaryFragment());
        ft.addToBackStack(null);
        ft.commit();
    }
}
