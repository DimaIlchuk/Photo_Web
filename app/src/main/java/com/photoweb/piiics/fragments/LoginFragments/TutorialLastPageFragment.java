package com.photoweb.piiics.fragments.LoginFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.LoginActivity;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.TutorialReceiver;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thomas on 11/09/2017.
 */

public class TutorialLastPageFragment extends BaseFragment {

    LoginActivity activity;

    @BindView(R.id.image)
    ImageView imageView;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_first_time_tutorial_last_page;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((LoginActivity) getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView.setImageResource(R.drawable.tuto4);
    }

    @OnClick(R.id.login)
    public void onLoginClick() {
        setFlagOnSharedPreferences();
        launchNextFragment();
    }

    private void setFlagOnSharedPreferences() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.firstTimeTutorialIsRead), true);
        editor.commit();
    }

    private void launchNextFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    @OnClick(R.id.goHome)
    public void onGoHomeClick() {
        setFlagOnSharedPreferences();
        activity.sendHome();
    }
}
