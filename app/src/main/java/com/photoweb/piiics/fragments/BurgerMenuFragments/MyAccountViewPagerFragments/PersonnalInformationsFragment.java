package com.photoweb.piiics.fragments.BurgerMenuFragments.MyAccountViewPagerFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.AddressData;
import com.photoweb.piiics.model.User;
import com.photoweb.piiics.utils.UserInfo;

import butterknife.BindView;

/**
 * Created by thomas on 06/09/2017.
 */

public class PersonnalInformationsFragment extends BaseFragment {
    private static final String LOG_TAG = "PersonnalInfosFrag";

    @BindView(R.id.lastName)
    EditText lastNameET;

    @BindView(R.id.firstName)
    EditText firstNameET;

    @BindView(R.id.address)
    EditText addressET;

    @BindView(R.id.additionalAddress)
    EditText additionAddressET;

    @BindView(R.id.postalCode)
    EditText postalCodeET;

    @BindView(R.id.city)
    EditText cityET;

    @BindView(R.id.country)
    EditText countryET;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_account_personnal_informations;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setFields();
    }

    private void setFields() {
        String str;
        AddressData userAddressData = UserInfo.getAddressData();

        if (userAddressData != null){
            if ((str = userAddressData.getLastName()) != null) {
                lastNameET.setText(str);
            }
            if ((str = userAddressData.getFirstName()) != null) {
                firstNameET.setText(str);
            }
            if ((str = userAddressData.getAddress()) != null) {
                addressET.setText(str);
            }
            if ((str = userAddressData.getAdditionalAddress()) != null) {
                additionAddressET.setText(str);
            }
            if ((str = userAddressData.getPostalCode()) != null) {
                postalCodeET.setText(str);
            }
            if ((str = userAddressData.getCity()) != null) {
                cityET.setText(str);
            }
            if ((str = userAddressData.getCountry()) != null) {
                countryET.setText(str);
            }
        }
    }

    private void saveUserInformations() {
        AddressData userAddressData = UserInfo.getAddressData();

        userAddressData.setLastName(lastNameET.getText().toString());
        userAddressData.setFirstName(firstNameET.getText().toString());
        userAddressData.setAddress(addressET.getText().toString());
        userAddressData.setAdditionalAddress(additionAddressET.getText().toString());
        userAddressData.setPostalCode(postalCodeET.getText().toString());
        userAddressData.setCity(cityET.getText().toString());
        userAddressData.setCountry(countryET.getText().toString());
        UserInfo.saveAddressData(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "OnStop()");
        saveUserInformations();
    }
}
