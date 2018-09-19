package com.photoweb.piiics.fragments.BasketFragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.photoweb.piiics.Adapters.GeoAutoCompleteAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.AddressData;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;

/**
 * Created by thomas on 29/08/2017.
 */

public class GooglePlaceSearchFragment extends BaseFragment {
    private static final String LOG_TAG = "GooglePaceSearchFrag";

    BasketActivity activity;
    GeoAutoCompleteAdapter adapter;
    String fragmentTag;
    AddressData currentAddressData;

    @BindView(R.id.geo_autocomplete)
    TextView geo_autocomplete;

    @BindView(R.id.listView)
    ListView listView;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_google_place_search;
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

        adapter = new GeoAutoCompleteAdapter(getContext(), activity.getGoogleApiClient());
        listView.setAdapter(adapter);

        geo_autocomplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.updateLocations(s.toString());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AddressData item;
                if (position == 0) {
                    item = adapter.getQueryAddressData();
                } else {
                    item = (AddressData) adapterView.getItemAtPosition(position - 1);
                }

                currentAddressData.initFields(item);
                currentAddressData.setPostalCode(findPostalCode(item.getFullText()));

                activity.getSupportFragmentManager().popBackStack();
            }
        });
    }

    private String findPostalCode(String fullLocation) {
       // Log.i(LOG_TAG, "fullLocation : " + fullLocation);
        Geocoder geocoder = new Geocoder(activity);
        List<Address> adresses = null;
        try {
            adresses = geocoder.getFromLocationName(fullLocation, 1);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        if (adresses != null && !adresses.isEmpty()) {
            Address address = adresses.get(0);
            return address.getPostalCode();
        } else {
            Log.i(LOG_TAG, "adress EMPTY");
        }
        return null;
    }
}
