package com.photoweb.piiics.fragments.LoginFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.LoginActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.fragments.SignInFragment;
import com.photoweb.piiics.fragments.SignUpFragment;
import com.photoweb.piiics.utils.SocialHandler;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thomas on 14/04/2017.
 */

public class LoginFragment extends BaseFragment {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_login;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.facebook_layout_fragment_login)
    public void onFacebookButtonClick() {
        progressBar.setVisibility(View.VISIBLE);

        SocialHandler.get().connect("facebook", true);
    }

    @OnClick(R.id.google_layout_fragment_login)
    public void onGoogleButtonClick() {
        progressBar.setVisibility(View.VISIBLE);

        SocialHandler.get().connect("google", true);
    }

    @OnClick(R.id.instagram_layout_fragment_login)
    public void onInstagramButtonClick() {
        progressBar.setVisibility(View.VISIBLE);

        SocialHandler.get().connect("instagram", true);
    }

    @OnClick(R.id.create_account_button_fragment_login)
    public void onCreateAccountButtonClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SignUpFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.sign_in_fragment_login)
    public void onSignInButtonClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SignInFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.exit_button)
    public void onExitButtonClick() {
        if (getActivity() instanceof LoginActivity) {
            ((LoginActivity)getActivity()).sendHome();
        } else {
            getActivity().finish();
            //((LoginInCommandActivity)getActivity()).sendHome();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
    }

}
