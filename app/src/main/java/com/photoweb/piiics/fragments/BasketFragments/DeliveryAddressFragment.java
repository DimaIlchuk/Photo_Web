package com.photoweb.piiics.fragments.BasketFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.AddressData;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.UserInfo;
import com.photoweb.piiics.utils.Utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by thomas on 13/07/2017.
 */

public class DeliveryAddressFragment extends BaseFragment {
    private static final String LOG_TAG = "DeliveryAdressFragment";
    public static final String FRAGMENT_TRANSACTION_DELIVERY_ADDRESS_TAG = "DeliveryAddressFragment";
    static final String FRAGMENT_TRANSACTION_BILLING_ADDRESS_TAG = "BillingAddressFragment";
    private final String GENDER_KEY_PREFERENCE = "isGenderMan";

    BasketActivity activity;
    String fragmentTag;
    AddressData currentAddressData;
    String selectedCountry;

    @BindView(R.id.title)
    TextView titleTV;

    @BindView(R.id.lastname)
    EditText lastNameET;

    @BindView(R.id.firstname)
    EditText firstNameET;

    @BindView(R.id.address)
    EditText addressTV;

    @BindView(R.id.additional_address)
    EditText additionalAddressET;

    @BindView(R.id.postal_code)
    EditText postalCodeET;

    @BindView(R.id.city)
    EditText cityET;

    @BindView(R.id.country)
    TextView countryET;

    @BindView(R.id.same_billing_delivery)
    CheckBox sameBillingDeliveryCB;

    @BindView(R.id.same_billing_delivery_layout)
    LinearLayout sameBillingDeliveryLL;

    @BindView(R.id.genderSwitch)
    RadioGroup genderSwitchRG;

    @BindView(R.id.man)
    RadioButton manRB;

    @BindView(R.id.woman)
    RadioButton womanRB;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_delivery_address;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BasketActivity) getActivity());
        fragmentTag = getTag();
        currentAddressData = activity.getCurrentAddressData(fragmentTag);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.updateStepsBar(this);
        initTitle();
        initSameBillingLayout();
        initGenderSelected();
        if (fragmentTag.equals(activity.getDeliveryAddressTag())) {
            initFieldsWithUserInfos();
        }
    }

    private void initGenderSelected() {
        SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
        checkGenderButton(settings.getBoolean(GENDER_KEY_PREFERENCE, false));
    }

    private void initTitle() {
        String title;
        if (fragmentTag.equals(activity.getDeliveryAddressTag())) {
            title = getString(R.string.DELIVERY_ADDRESS);
        } else {
            title = getString(R.string.BILLING_ADDRESS);
        }
        titleTV.setText(title);
    }

    private void initSameBillingLayout() {
        if (fragmentTag.equals(activity.getDeliveryAddressTag())) {
            sameBillingDeliveryLL.setVisibility(View.VISIBLE);
        } else {
            sameBillingDeliveryLL.setVisibility(View.GONE);
        }
    }

    private void initFieldsWithUserInfos() {
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
                addressTV.setText(str);
            }
            if ((str = userAddressData.getAdditionalAddress()) != null) {
                additionalAddressET.setText(str);
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
            if ((str = userAddressData.getCountryCode()) != null) {
                selectedCountry = str;
            }else{
                countryET.setText("");
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        initFieldsWithAddressData();
    }

    private void initFieldsWithAddressData() {
        if (currentAddressData != null) {
            if (currentAddressData.getFirstName() != null) {
                firstNameET.setText(currentAddressData.getFirstName());
            }

            if (currentAddressData.getLastName() != null) {
                lastNameET.setText(currentAddressData.getLastName());
            }

            if (currentAddressData.getAddress() != null) {
                addressTV.setText(currentAddressData.getAddress());
            }

            if (currentAddressData.getAdditionalAddress() != null) {
                additionalAddressET.setText(currentAddressData.getAdditionalAddress());
            }

            if (currentAddressData.getCity() != null) {
                cityET.setText(currentAddressData.getCity());
            }
            if (currentAddressData.getCountry() != null) {
                countryET.setText(currentAddressData.getCountry());
            }
            if (currentAddressData.getCountryCode() != null) {
                selectedCountry = currentAddressData.getCountryCode();
            }
            if (currentAddressData.getPostalCode() != null) {
                postalCodeET.setText(currentAddressData.getPostalCode());
            }
        }
    }

    @OnClick(R.id.man)
    public void onManButtonClick() {
        checkGenderButton(true);
    }

    @OnClick(R.id.woman)
    public void onWomanButtonClick() {
        checkGenderButton(false);
    }

    private void checkGenderButton(boolean isManButton) {
        if (isManButton) {
            womanRB.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            manRB.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

            womanRB.setChecked(false);
            manRB.setChecked(true);
        } else {
            manRB.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            womanRB.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

            manRB.setChecked(false);
            womanRB.setChecked(true);
        }
    }

    private void initAddressData() {
        if (currentAddressData == null) {
            return;
        }
        if (manRB.isChecked()) {
            currentAddressData.setCivility("M");
        } else {
            currentAddressData.setCivility("F");
        }
        currentAddressData.setLastName(lastNameET.getText().toString());
        currentAddressData.setFirstName(firstNameET.getText().toString());
        currentAddressData.setAddress(addressTV.getText().toString());
        currentAddressData.setAdditionalAddress(additionalAddressET.getText().toString());
        currentAddressData.setPostalCode(postalCodeET.getText().toString());
        currentAddressData.setCity(cityET.getText().toString());
        currentAddressData.setCountry(countryET.getText().toString());
        currentAddressData.setCountryCode(selectedCountry);
    }

    private boolean isFieldsCompleted() {

        if (currentAddressData == null) {
            return false;
        }

        String firstName = currentAddressData.getFirstName();
        String lastName = currentAddressData.getLastName();
        String address = currentAddressData.getAddress();
        String postalCode = currentAddressData.getPostalCode();
        String city = currentAddressData.getCity();
        String country = currentAddressData.getCountry();

        if (firstName != null && !firstName.isEmpty() &&
                lastName != null && !lastName.isEmpty() &&
                address != null && !address.isEmpty() &&
                postalCode != null && !postalCode.isEmpty() &&
                city != null && !city.isEmpty() &&
                country != null && !country.isEmpty() &&
                selectedCountry != null && !selectedCountry.isEmpty()) {
            return true;
        }
        return false;
    }

    @OnClick(R.id.country)
    public void onCountryClick() {
        //Creating the instance of PopupMenu
        final CharSequence[] array = {getString(R.string.FRANCE),
                getString(R.string.GERMANY),
                getString(R.string.ITALY),
                getString(R.string.SPAIN),
                getString(R.string.NETHERLANDS),
                getString(R.string.AUSTRIA),
                getString(R.string.BELGIUM),
                getString(R.string.BULGARIA),
                getString(R.string.CYPRUS),
                getString(R.string.CROATIA),
                getString(R.string.DENMARK),
                getString(R.string.ESTONIA),
                getString(R.string.FINLAND),
                getString(R.string.GREECE),
                getString(R.string.HUNGARY),
                getString(R.string.IRLAND),
                getString(R.string.LATVIA),
                getString(R.string.LITHUANIA),
                getString(R.string.LUXEMBOURG),
                getString(R.string.MALTA),
                getString(R.string.POLAND),
                getString(R.string.PORTUGAL),
                getString(R.string.CZECH),
                getString(R.string.ROMANIA),
                getString(R.string.BRITAIN),
                getString(R.string.SLOVAKIA),
                getString(R.string.SLOVENIA),
                getString(R.string.SWEDEN)};

        new AlertDialog.Builder(activity)
                .setSingleChoiceItems(array, 0, null)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        selectedCountry = Utils.listCountries[selectedPosition];
                        countryET.setText(array[selectedPosition]);
                        // Do something useful withe the position of the selected radio button
                    }
                })
                .show();

    }

    @OnClick(R.id.validate)
    public void onValidateClick() {

        if(!checkPostCode(postalCodeET.getText().toString())) {
            Toast.makeText(activity, R.string.INVALID_POSTCODE, Toast.LENGTH_LONG).show();
            return;
        }

        initAddressData();
        if (isFieldsCompleted()) {
            saveGenderPreference();
            saveUserAddressData();
            if (doWeKnowBillingAddress()) {
                if (fragmentTag.equals(activity.getDeliveryAddressTag())) {
                    CommandHandler.get().setBillingDict((AddressData) CommandHandler.get().getDeliveryDict().clone());
                    startNextFragment(new DeliveryMethodFragment(), null, FRAGMENT_TRANSACTION_DELIVERY_ADDRESS_TAG);
                } else if (fragmentTag.equals(activity.getBillingAddressTag())) {
                    startNextFragment(new DeliveryMethodFragment(), null, FRAGMENT_TRANSACTION_BILLING_ADDRESS_TAG);
                }
            } else {
                startNextFragment(new DeliveryAddressFragment(), activity.getBillingAddressTag(), FRAGMENT_TRANSACTION_DELIVERY_ADDRESS_TAG);
            }
        } else {
            Toast.makeText(activity, R.string.ALL_FIELDS_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    private Boolean checkPostCode(String postCode)
    {
        switch (Arrays.asList(Utils.listCountries).indexOf(selectedCountry)) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 9:
            case 11:
            case 12:
                if(postCode.length() == 5 && validZipCode(postCode, "^[0-9]{5}")){
                    return true;
                }else{
                    return false;
                }

            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 14:
            case 18:
            case 21:
                if(postCode.length() == 4 && validZipCode(postCode, "^[0-9]{4}")){
                    return true;
                }else{
                    return false;
                }

            case 23:
                if(postCode.length() == 6 && validZipCode(postCode,"^[0-9]{6}")){
                    return true;
                }else{
                    return false;
                }

            case 16:
                if(postCode.length() == 7){
                    if(postCode.startsWith("LV-")){
                        if(validZipCode(postCode.replace("LV-", ""), "^[0-9]{4}")){
                            return true;
                        }
                    }
                }
                if(postCode.length() == 6){
                    if(postCode.startsWith("LV")){
                        if(validZipCode(postCode.replace("LV", ""), "^[0-9]{4}")){
                            return true;
                        }
                    }
                }

                return false;

            case 17:
                if(postCode.length() == 8){
                    if(postCode.startsWith("LT-")){
                        if(validZipCode(postCode.replace("LT-", ""), "^[0-9]{5}")){
                            return true;
                        }
                    }
                }
                if(postCode.length() == 7){
                    if(postCode.startsWith("LT")){
                        if(validZipCode(postCode.replace("LT", ""), "^[0-9]{5}")){
                            return true;
                        }
                    }
                }
                return false;

            case 19:
                if(postCode.length() == 7 && validZipCode(postCode, "^[a-zA-Z]{3}[0-9]{4}")){
                    return true;
                }else if(postCode.length() == 8 && validZipCode(postCode, "^[a-zA-Z ]{4}[0-9]{4}")){
                    return true;
                }else{
                    return false;
                }

            case 20:
                if(postCode.length() == 5 && validZipCode(postCode, "^[0-9]{5}")){
                    return true;
                }else if(postCode.length() == 6 && validZipCode(postCode,  "^[0-9-]{6}")){
                    return true;
                }else if(postCode.length() == 6 && validZipCode(postCode,  "^[0-9 ]{6}")){
                    return true;
                }else{
                    return false;
                }

            case 13:
            case 22:
            case 25:
            case 27:
                if(postCode.length() == 5 && validZipCode(postCode, "^[0-9]{5}")){
                    return true;
                }else if(postCode.length() == 6 && validZipCode(postCode, "^[0-9 ]{6}")){
                    return true;
                }else{
                    return false;
                }

            case 24:
            case 26:
                if(postCode.length() > 0){
                    return true;
                }else{
                    return false;
                }

            default:
                return true;
        }
    }

    private Boolean validZipCode(String postCode, String regex)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(postCode);

        return matcher.matches();
    }

    private void saveUserAddressData() {
        if (fragmentTag.equals(activity.getDeliveryAddressTag())) {
            UserInfo.setAddressData((AddressData) currentAddressData.clone());
            UserInfo.saveAddressData(getContext());
        }
    }

    private void saveGenderPreference() {
        SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (currentAddressData.getCivility().equals("M")) {
            editor.putBoolean(GENDER_KEY_PREFERENCE, true);
        } else {
            editor.putBoolean(GENDER_KEY_PREFERENCE, false);
        }
        editor.commit();
    }

    private boolean doWeKnowBillingAddress() {
        if (fragmentTag.equals(activity.getBillingAddressTag())) {
            return true;
        } else {
            if (sameBillingDeliveryCB.isChecked()) {
                return true;
            }
        }
        return false;
    }

    private void startNextFragment(Fragment fragment, String fragmentTag, String transactionTag) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();

        if (fragment instanceof DeliveryMethodFragment && userEmailUnknow()) {
            ft.replace(R.id.fragment_container, new EmailRequestFragment());
        } else {
            ft.replace(R.id.fragment_container, fragment, fragmentTag);
        }

        ft.addToBackStack(transactionTag);
        ft.commit();
    }

    private boolean userEmailUnknow() {
        /*String userEmail = UserInfo.get("email");
        if (userEmail == null || userEmail.isEmpty()) {
            return true;
        }
        return false;*/
        SharedPreferences prefs = activity.getSharedPreferences("USER_INFO", MODE_PRIVATE);

        if(prefs.getString("email", "").equals("")){
            return true;
        }

        return false;
    }
}
