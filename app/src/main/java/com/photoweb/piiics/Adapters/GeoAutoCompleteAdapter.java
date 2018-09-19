package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacesStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.photoweb.piiics.R;
import com.photoweb.piiics.model.AddressData;

import java.util.ArrayList;


/**
 * Created by thomas on 28/08/2017.
 */

public class GeoAutoCompleteAdapter extends BaseAdapter {
    private static final String LOG_TAG = "GeoAutoCompleteAdapter";

    private Context context;
    private ArrayList<AddressData> resultList;

    //String query;
    private AddressData queryAddressData;

    AutocompleteFilter autocompleteFilter;
    PendingResult<AutocompletePredictionBuffer> result;
    LatLngBounds latLngBounds;
    GoogleApiClient googleApiClient;

    public AddressData getQueryAddressData() {
        return queryAddressData;
    }

    public GeoAutoCompleteAdapter(Context context, GoogleApiClient googleApiClient) {
        this.context = context;
        this.queryAddressData = new AddressData();

        this.autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("FR")
                .build();
        this.latLngBounds = new LatLngBounds(new LatLng(42.00032515, -4.70214844), new LatLng(51.4813829, 8.52539063));
        this.googleApiClient = googleApiClient;
        resultList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return resultList.size() + 1;
    }

    @Override
    public AddressData getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.geo_search_result_item, parent, false);
        }

        String fullAddress;
        if (position == 0) {
            fullAddress = queryAddressData.getFullText();
        } else {
            fullAddress = getItem(position - 1).getFullText();
        }

        ((TextView) convertView.findViewById(R.id.geo_search_result_text)).setText(fullAddress);

        return convertView;
    }

    public void updateLocations(final String query) {//creer un temps de latence pour ne pas avoir a faire une recherche sur chaque lettre
        queryAddressData.setFullText(query);
        queryAddressData.setAddress(query);
        new UpdateLocationsTask().execute(query);
    }

    private class UpdateLocationsTask extends AsyncTask<String, Integer, ArrayList<AddressData>> {
        protected ArrayList<AddressData> doInBackground(String... queries) {
            String query = queries[0];
            //Toast.makeText(context.getApplicationContext(), "Search query:" + query, Toast.LENGTH_SHORT).show();
            return findLocations(query);
        }

        protected void onPostExecute(ArrayList<AddressData> locations) {
            resultList = locations;
            notifyDataSetChanged();
        }
    }

    private ArrayList<AddressData> findLocations(String queryText) {

        ArrayList<AddressData> results = new ArrayList<>();

        result = Places.GeoDataApi.getAutocompletePredictions(googleApiClient, queryText,
                latLngBounds, autocompleteFilter);

        AutocompletePredictionBuffer autocompletePredictionBuffer;
        autocompletePredictionBuffer = result.await();

        Log.i(LOG_TAG, "query code : " + String.valueOf(autocompletePredictionBuffer.getStatus().getStatusCode()));

        if (autocompletePredictionBuffer.getStatus().isSuccess()) {
            //Toast.makeText(context.getApplicationContext(), "success!", Toast.LENGTH_SHORT).show();
            int i = 0;
            int resultsCount = autocompletePredictionBuffer.getCount();
            while (i < resultsCount) {
                AutocompletePrediction prediction = autocompletePredictionBuffer.get(i);
                AddressData addressData = new AddressData(prediction);
                results.add(addressData);
                i++;
            }
            autocompletePredictionBuffer.release();
        } else {
            Log.i(LOG_TAG, "STATUS CODE STRING : " + PlacesStatusCodes.getStatusCodeString(autocompletePredictionBuffer.getStatus().getStatusCode()));
            //Toast.makeText(context.getApplicationContext(), "error ! " + PlacesStatusCodes.getStatusCodeString(autocompletePredictionBuffer.getStatus().getStatusCode()), Toast.LENGTH_SHORT).show();
        }
        return results;
    }
}
