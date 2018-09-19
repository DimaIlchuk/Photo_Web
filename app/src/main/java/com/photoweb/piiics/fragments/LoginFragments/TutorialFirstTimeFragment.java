package com.photoweb.piiics.fragments.LoginFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.photoweb.piiics.Adapters.FirstTimeTutorialPagerAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.LoginActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.TutorialReceiver;
import com.photoweb.piiics.utils.BackendAPI;
import com.photoweb.piiics.utils.NetworkUtils;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 18/09/2017.
 */

public class TutorialFirstTimeFragment extends BaseFragment {
    private static final String LOG_TAG = "TutorialFirstTimeFrag";

    LoginActivity activity;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.indicator)
    com.viewpagerindicator.CirclePageIndicator indicator;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.emptyView)
    TextView emptyViewTV;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tutorial_first_time;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((LoginActivity) getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbar();
        requestTutorialText();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
    }

    private void requestTutorialText() {
        progressBar.setVisibility(View.VISIBLE);

        Call<TutorialReceiver> requestCall = BackendAPI.piiicsapi.requestTutorialText(getString(R.string.LANG));
        requestCall.enqueue(new Callback<TutorialReceiver>() {
            @Override
            public void onResponse(Call<TutorialReceiver> call, Response<TutorialReceiver> response) {
                Log.i(LOG_TAG, "response : " + call.request().url().toString());

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    handleEmptyView(false);
                    activity.setTutorialReceiver(response.body());
                    initViewPager();
                    Log.i(LOG_TAG, "Get TutorialText good");
                } else {
                    Log.i(LOG_TAG, "request auth failed with error code: " + String.valueOf(response.code()));
                    if (response.code() == 404) {
                        Log.i(LOG_TAG, "URL invalide");
                    } else {
                        Log.i(LOG_TAG, "Error");
                    }
                    handleEmptyView(true);
                    launchLoginFragment();
                }
            }

            @Override
            public void onFailure(Call<TutorialReceiver> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                handleEmptyView(true);
                NetworkUtils.checkConnectedToANetwork(getActivity(), false);
                launchLoginFragment();
            }
        });
    }


    private void launchLoginFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    private void initViewPager() {
        FirstTimeTutorialPagerAdapter adapter = new FirstTimeTutorialPagerAdapter(getActivity(), getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        indicator.setViewPager(viewPager);

        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(4 * density);
        indicator.setPageColor(0xFFFFFFFF);
        indicator.setFillColor(0xFFe21246);
        indicator.setStrokeColor(0xFFe21246);
        indicator.setStrokeWidth(1 * density);
    }

    private void handleEmptyView(boolean toShow) {
        if (toShow) {
            emptyViewTV.setVisibility(View.VISIBLE);
        } else {
            emptyViewTV.setVisibility(View.GONE);
        }
    }
}
