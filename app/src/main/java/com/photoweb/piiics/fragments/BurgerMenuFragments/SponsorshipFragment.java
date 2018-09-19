package com.photoweb.piiics.fragments.BurgerMenuFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.utils.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by thomas on 06/09/2017.
 */

public class SponsorshipFragment extends BaseFragment {
    MainActivity activity;

    @BindView(R.id.sponsorship_code)
    TextView tvSponsor;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_sponsorship;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((MainActivity)getActivity());

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setToolbar();

        tvSponsor.setText(UserInfo.get("sponsor_code"));
    }

    private void setToolbar() {
        Toolbar toolbar = activity.getToolbar();

        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.SPONSOR);
    }

    @OnClick(R.id.share)
    public void onShareClick() {
        String shareBody = getString(R.string.SPONSOR_SHARE_TEXT, UserInfo.get("sponsor_code"));
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Rejoins la famille Piiics !");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Partagez votre code parrain"));
    }
}
