package com.photoweb.piiics.model;

import android.util.Log;

import com.google.android.gms.location.places.AutocompletePrediction;

/**
 * Created by thomas on 30/08/2017.
 */

public class AddressData implements Cloneable {
    private static final String LOG_TAG = "AddressData";

    private String fullText;

    private String civility;
    private String firstName;
    private String lastName;
    private String address;
    private String additionalAddress;
    private String postalCode;
    private String city;
    private String country;
    private String countryCode;


    public String getCivility() {
        return civility;
    }

    public void setCivility(String civility) {
        this.civility = civility;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAdditionalAddress() {
        return additionalAddress;
    }

    public void setAdditionalAddress(String additionalAddress) {
        this.additionalAddress = additionalAddress;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPostalCodeAndCity() {
        return postalCode + " " + city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public AddressData() {
        this.fullText = null;
        this.firstName = null;
        this.lastName = null;
        this.address = null;
        this.additionalAddress = null;
        this.postalCode = null;
        this.city = null;
        this.country = null;
        this.countryCode = null;
    }


    public AddressData(AutocompletePrediction autocompletePrediction) {
        this.fullText = autocompletePrediction.getFullText(null).toString();
        this.address = autocompletePrediction.getPrimaryText(null).toString();

        extractCityAndCountry(autocompletePrediction.getSecondaryText(null).toString());

        this.postalCode = null;
    }

    private void extractCityAndCountry(String secondaryText) {
        String[] tab = secondaryText.split(",");
        if (tab.length == 2) {
            this.city = tab[0].trim();
            this.country = tab[1].trim();
        } else {
            Log.i(LOG_TAG, "autocompletePrediction.getSecondaryText : split fail");
            this.city = null;
            this.country = null;
        }
    }

    public void initFields(AddressData addressData) {
        this.fullText = addressData.getFullText();
        this.address = addressData.getAddress();
        this.city = addressData.getCity();
        this.country = addressData.getCountry();
        this.countryCode = addressData.getCountryCode();
    }

    @Override
    public String toString() {
        return "AddressData{" +
                "fullText='" + fullText + '\'' +
                ", civility='" + civility + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", additionalAddress='" + additionalAddress + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public Object clone()
    {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
