package com.photoweb.piiics.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by thomas on 08/08/2017.
 */

public final class NetworkUtils {
    private static final String LOG_TAG = "NetworkUtils";

    /*
 *  Check if the device is connected to a network
 */
    public static boolean checkConnectedToANetwork(Activity activity, boolean backgroundThread) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
                Log.i(LOG_TAG, "L'appareil n'est pas connecté");
            return false;
        }
        return true;
    }

}
