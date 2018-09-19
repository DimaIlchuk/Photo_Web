package com.photoweb.piiics.fragments;

import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.LoginActivity;
import com.photoweb.piiics.activities.LoginInCommandActivity;
import com.photoweb.piiics.model.UserCurrent;
import com.photoweb.piiics.utils.BackendAPI;
import com.photoweb.piiics.utils.UserInfo;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by thomas on 14/04/2017.
 */

public class SignUpFragment extends BaseFragment {
    private static final String TAG = "SignUpFragment";

    @BindView(R.id.username_fragment_sign_up) EditText txtUserName;
    @BindView(R.id.mail_fragment_sign_up) EditText txtMail;
    @BindView(R.id.password_fragment_sign_up) EditText txtPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_sign_up;
    }

    @OnClick(R.id.confirm_button_fragment_sign_up)
    public void onConfirmButtonClick() {
        if(txtUserName.getText().toString().trim().equals("") || txtMail.getText().toString().trim().equals("") || txtPassword.getText().toString().trim().equals("")){
            Toast.makeText(getContext(), R.string.ERROR + " : " + R.string.ALL_FIELDS_ERROR, Toast.LENGTH_SHORT).show();

            return;
        }

        if(!isValidEmail(txtMail.getText().toString())){
            Toast.makeText(getContext(), R.string.ERROR + " : " + R.string.NON_VALID, Toast.LENGTH_SHORT).show();

            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        BackendAPI.accountregister(txtUserName.getText().toString().trim(), txtMail.getText().toString().trim(), txtPassword.getText().toString().trim(), Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID), true, getString(R.string.LANG), new BackendAPI.ResponseListener<UserCurrent>() {
            @Override
            public void perform(UserCurrent obj, int s, String errmsg) {
                progressBar.setVisibility(View.GONE);

                if (s < 0) {
                    Log.e(TAG, "BackendAPI.accountregister: " + errmsg);
                    Toast.makeText(getContext(), "Error : " + errmsg, Toast.LENGTH_SHORT).show();
                    return;
                }
                UserInfo.update(obj);

                SharedPreferences prefs = getActivity().getSharedPreferences("USER_INFO", MODE_PRIVATE);

                SharedPreferences.Editor prefsEditor = prefs.edit();

                prefsEditor.putString("email", txtMail.getText().toString().trim());
                prefsEditor.putString("password", txtPassword.getText().toString().trim());
                prefsEditor.commit();

                AdjustEvent event = new AdjustEvent("1plg5o");
                Adjust.trackEvent(event);

                launchNextView();
            }
        });
    }

    private void launchNextView() {
        if (getActivity() instanceof LoginActivity) {
            ((LoginActivity) getActivity()).sendHome();
        } else {
            ((LoginInCommandActivity) getActivity()).sendEditorActivity();
        }

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
