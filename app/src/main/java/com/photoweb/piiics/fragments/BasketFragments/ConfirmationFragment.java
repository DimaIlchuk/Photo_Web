package com.photoweb.piiics.fragments.BasketFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.ad4screen.sdk.A4S;
import com.ad4screen.sdk.analytics.Purchase;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.utils.AWSHandler;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by thomas on 13/07/2017.
 */

public class ConfirmationFragment extends BaseFragment {

    BasketActivity activity;

    @BindView(R.id.sponsorship_code)
    TextView sponsor;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_confirmation;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BasketActivity) getActivity());

        Purchase cart = new Purchase(activity.getCommand().getCommandID(), "EUR", (float)(CommandHandler.get().getTotalPrice())/100);
        A4S.get(activity).trackPurchase(cart);

        A4S.get(getContext()).setView("home");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.updateStepsBar(this);

        ButterKnife.bind(this, view);

        sponsor.setText(UserInfo.get("sponsor_code"));

        activity.showHomeButton();

        A4S.get(getContext()).setView("Confirmation");

        AdjustEvent event = new AdjustEvent("udsloy");
        event.setRevenue((CommandHandler.get().getTotalPrice())/100, "EUR");
        Adjust.trackEvent(event);

        finishSale();
    }

    @OnClick(R.id.share_sponsorship_code)
    public void onShareClick() {
        String shareBody = getString(R.string.SPONSOR_SHARE_TEXT, UserInfo.get("sponsor_code"));
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Rejoins la famille Piiics !");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Partagez votre code parrain"));

        AdjustEvent event = new AdjustEvent("xf2krw");
        Adjust.trackEvent(event);
    }

    public void finishSale()
    {
        AWSHandler.get().uploadCommandOrder();

        UserInfo.updateUser();
    }

}
