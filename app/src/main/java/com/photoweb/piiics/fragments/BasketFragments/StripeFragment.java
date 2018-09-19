package com.photoweb.piiics.fragments.BasketFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.stripe.android.view.CardInputWidget;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StripeFragment extends BaseFragment {
    private static final String LOG_TAG = "StripeFragment";
    BasketActivity activity;

    @BindView(R.id.card_input_widget)
    CardInputWidget widget;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_stripe;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BasketActivity) getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        activity.updateStepsBar(this);
    }
}
