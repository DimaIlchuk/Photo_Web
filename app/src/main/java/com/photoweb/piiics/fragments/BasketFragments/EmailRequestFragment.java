package com.photoweb.piiics.fragments.BasketFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.utils.UserInfo;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by thomas on 13/07/2017.
 */

public class EmailRequestFragment extends BaseFragment {
    static final String FRAGMENT_TRANSACTION_TAG = "EmailRequestFragment";
    private BasketActivity activity;
    private String email;

    @BindView(R.id.emailAddress)
    EditText emailAddressET;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_email_request;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BasketActivity) getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.updateStepsBar(this);
    }

    @OnClick(R.id.validate)
    public void onValidateClick() {
        if (addressCompleted()) {
            //UserInfo.set("email", email);
            SharedPreferences prefs = getActivity().getSharedPreferences("USER_INFO", MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putString("email", email);
            prefsEditor.commit();

            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new DeliveryMethodFragment());
            ft.addToBackStack(FRAGMENT_TRANSACTION_TAG);
            ft.commit();
        } else {
            Toast.makeText(activity, R.string.ALL_FIELDS_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    private boolean addressCompleted() {
        email = emailAddressET.getText().toString();
        if(email.isEmpty()){
            return false;
        }

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
