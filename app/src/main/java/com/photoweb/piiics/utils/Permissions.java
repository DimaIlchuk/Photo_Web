package com.photoweb.piiics.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by thomas on 23/08/2017.
 */

public class Permissions {
    private final static String LOG_TAG = "Permissions";

    public final int REQUEST_PERMISSION_STORAGE = 1000;
    public final int REQUEST_PERMISSION_INTERNET = 1001;
    public final int REQUEST_PERMISSION_ACCOUNTS = 1002;
    public final int REQUEST_ALL_PERMISSIONS = 1003;

    private Activity mActivity;
    private int currentRequest;

    public Permissions(Activity activity) {
        mActivity = activity;
    }

    /*
        return 1 if the permissions are already granted, 0 if we need to request it
     */
    public int checkAllPermissions(Fragment fragment) {
        int internetPermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.INTERNET);
        int networkStatePermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_NETWORK_STATE);
        int writeExternalStoragePermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExternalStoragePermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int getAccountsPermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.GET_ACCOUNTS);

        if (internetPermission != PackageManager.PERMISSION_GRANTED
                || networkStatePermission != PackageManager.PERMISSION_GRANTED
                || writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED
                || readExternalStoragePermission != PackageManager.PERMISSION_GRANTED
                || getAccountsPermission != PackageManager.PERMISSION_GRANTED) {

            String[] ALL_PERMISSIONS = {
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,//besoin d'external storage ?
                    Manifest.permission.GET_ACCOUNTS,
            };

            fragment.requestPermissions(ALL_PERMISSIONS, REQUEST_ALL_PERMISSIONS);
        } else {
            return 1;
        }
        return 0;
    }



    /*
        Internet
     */
 /*   public void checkPermissionInternet(int REQUEST_PERMISSION_INTERNET) {
        currentRequest = REQUEST_PERMISSION_INTERNET;
        String[] PERMISSIONS_INTERNET = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        };
        int internetPermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.INTERNET);
        int networkStatePermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_NETWORK_STATE);
        if (internetPermission != PackageManager.PERMISSION_GRANTED || networkStatePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, PERMISSIONS_INTERNET, REQUEST_PERMISSION_INTERNET);
        } else {
            permissionInternetGranted();
        }
    }

    public void permisionInternetResult(@NonNull String[] permissions,
                                        @NonNull int[] grantResults) {
        if (permissions.length == 0) {
            permissionInternetDenied();
            return;
        }
        for (int x : grantResults) {
            if (x != PackageManager.PERMISSION_GRANTED) {
                permissionInternetDenied();
                return;
            }
        }
        permissionInternetGranted();
    }

    private void permissionInternetGranted() {
        Log.i("UtilsPermissions", "Permission internet granted !");
    }

    private void permissionInternetDenied() {
        checkPermissionInternet(currentRequest); // a voir
    }*/


    /*
        Storage
     */
   /* public void checkPermissionStorage(int REQUEST_PERMISSION_STORAGE) {
        String[] PERMISSIONS_STORAGE = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int readPermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, PERMISSIONS_STORAGE, REQUEST_PERMISSION_STORAGE);
        } else {
            permissionStorageGranted();
        }
    }

    public void permissionStorageResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions.length == 0) {
            permissionStorageDenied(requestCode);
            return;
        }
        for (int x : grantResults) {
            if (x != PackageManager.PERMISSION_GRANTED) {
                permissionStorageDenied(requestCode);
                return;
            }
        }
        permissionStorageGranted();
    }

    public void permissionStorageDenied(int requestCode) {
        Log.i(LOG_TAG, "permission Storage failed");
        checkPermissionStorage(requestCode);////
    }

    public void permissionStorageGranted() {
        Log.i(LOG_TAG, "permission Storage success");
    }
*/
}
