package com.photoweb.piiics.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class SignInFragment extends BaseFragment {
    private static final String TAG = "SignInFragment";

    @BindView(R.id.mail_fragment_sign_in) EditText txtMail;
    @BindView(R.id.password_fragment_sin_in) EditText txtPassword;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_sign_in;
    }

    @OnClick(R.id.connect_button_fragment_sign_in)
    public void onConnectButtonClick() {
        if(txtMail.getText().toString().trim().equals("") || txtPassword.getText().toString().trim().equals("")){
            Toast.makeText(getContext(),  R.string.ERROR + " : " + R.string.ALL_FIELDS_ERROR, Toast.LENGTH_SHORT).show();

            return;
        }

        if(!isValidEmail(txtMail.getText().toString())){
            Toast.makeText(getContext(), R.string.ERROR+" : " + getString(R.string.NON_VALID), Toast.LENGTH_SHORT).show();

            return;
        }

        BackendAPI.accountlogin(txtMail.getText().toString().trim(), txtPassword.getText().toString().trim(),true, getString(R.string.LANG), new BackendAPI.ResponseListener<UserCurrent>() {
            @Override
            public void perform(UserCurrent obj, int s, String errmsg) {
                if (s < 0) {
                    Log.e(TAG, "BackendAPI.accountlogin: " + errmsg);
                    Toast.makeText(getContext(), "Error : " + errmsg, Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences prefs = getActivity().getSharedPreferences("USER_INFO", MODE_PRIVATE);

                SharedPreferences.Editor prefsEditor = prefs.edit();

                prefsEditor.putString("email", txtMail.getText().toString().trim());
                prefsEditor.putString("password", txtPassword.getText().toString().trim());
                prefsEditor.commit();

                UserInfo.update(obj);


                launchNextView();
            }
        });
       // launchNextView();
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

    @OnClick(R.id.password_lost_fragment_sin_in)
    public void onPasswordLostClick() {
        AlertDialog.Builder alertDialog;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            alertDialog = new AlertDialog.Builder(getContext());
        }

        alertDialog.setTitle(R.string.PWB_BODY);
        //alertDialog.setMessage("Enter text");

        final EditText input = new EditText(getContext());
        input.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.VALIDATE,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        BackendAPI.askforpassword(input.getText().toString(), new BackendAPI.ResponseListener<UserCurrent>() {
                            @Override
                            public void perform(UserCurrent obj, int s, String errmsg) {

                            }
                        });

                        Toast.makeText(getContext(), R.string.PWD_CONFIRM, Toast.LENGTH_LONG).show();

                    }
                });

        alertDialog.setNegativeButton(R.string.CANCEL,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
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
