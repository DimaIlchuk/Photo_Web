package com.photoweb.piiics.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ad4screen.sdk.A4S;
import com.google.gson.Gson;
import com.photoweb.piiics.R;
import com.photoweb.piiics.model.AddressData;
import com.photoweb.piiics.model.UserCurrent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dnizard on 25/04/2017.
 */

public class UserInfo {
    private static final String TAG = "UserInfo";
    private static SharedPreferences prefs;
    private static UserCurrent user;
    private static Context context;
    private static AddressData addressData;

    public static AddressData getAddressData() {
        return addressData;
    }

    public static void setAddressData(AddressData addressData) {
        UserInfo.addressData = addressData;
    }

    public static void init(Context _context) {
        context = _context;
        prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

        user = UserCurrent.fromJson(prefs.getString("CurUser", null));
        if (user == null) user = new UserCurrent();
        else{
            Log.d(TAG, prefs.getString("email", "") + " : " + prefs.getString("password", ""));

            if(!prefs.getString("email", "").equals("") && !prefs.getString("password", "").equals("")){
                BackendAPI.accountlogin(prefs.getString("email", ""), prefs.getString("password", ""), true, context.getString(R.string.LANG), new BackendAPI.ResponseListener<UserCurrent>() {
                    @Override
                    public void perform(UserCurrent obj, int s, String errmsg) {
                        if (s < 0) {
                            Log.e(TAG, "BackendAPI.accountlogin: " + errmsg);
                            return;
                        }

                        user = obj;


                    }
                });
            }else if(!prefs.getString("socialnetwork", "").equals("") && !prefs.getString("socialtoken", "").equals("")){
                //BackendAPI.accountlogin(prefs.getString("email", ""), prefs.getString("password", ""), new BackendAPI.ResponseListener<UserCurrent>() {
                BackendAPI.accountloginsocial(prefs.getString("socialnetwork", ""), prefs.getString("socialtoken", ""), "", "", Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID), true, context.getString(R.string.LANG), new BackendAPI.ResponseListener<UserCurrent>() {
                @Override
                    public void perform(UserCurrent obj, int s, String errmsg) {
                        if (s < 0) {
                            Log.e(TAG, "BackendAPI.accountlogin: " + errmsg);
                            return;
                        }

                        user = obj;


                    }
                });
            }
        }

        loadAddressData(context);

        if((int)user.get("id") > 0){
            Bundle bundle = new Bundle();
            bundle.putString("id_client", "" + user.get("id"));
            bundle.putString("first_name", "" + user.get("username"));
            bundle.putInt("free_items_remaining", getInt("print_available") + getInt("print_bonus"));

            DateFormat dateFormat = new SimpleDateFormat("MM");
            Date date = new Date();
            bundle.putString("current_month", dateFormat.format(date));

            A4S.get(context).updateDeviceInfo(bundle);
        }

    }

    private static void loadAddressData(Context context) {
        SharedPreferences  mPrefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("AddressData", "");
        if (json.length() == 0) {
            addressData = new AddressData();
        } else {
            addressData = gson.fromJson(json, AddressData.class);
        }
    }

    public static void saveAddressData(Context context) {
        SharedPreferences  mPrefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(addressData);
        prefsEditor.putString("AddressData", json);
        prefsEditor.commit();
    }

    public static void updateUser() {
        BackendAPI.getprofile((int)user.get("id"), new BackendAPI.ResponseListener<UserCurrent>() {
            @Override
            public void perform(UserCurrent obj, int s, String errmsg) {
                Log.d(TAG, obj.toString());
                user = obj;
            }
        });
    }

    public static void update(UserCurrent uc) {
        //Note: when refreshing user info, the session token is not sent back > keep the existing token
        user = uc;
        prefs.edit().putString("CurUser", user.toJson()).apply();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GlobEvent_UserUpdated"));
    }

    public static int getInt(String field) {
        return (int) user.get(field);
    }

    public static String get(String field) {
        return (String) user.get(field);
    }

    public static UserCurrent get() {
        return user;
    }

    public static boolean getBoolean(String field) {
        return (boolean) user.get(field);
    }


    public static void set(final String field, final Object value) {
        Map<String, String> map = new HashMap<>();
        //Need to format the booleans to 0 or 1 to send them to backend
        if (value instanceof Boolean) map.put(field, ((boolean)value?"1":"0"));
        else map.put(field, (String) value);

        /*BackendAPI.accountprofilechange(UserInfo.getToken(), map, new BackendAPI.ResponseListener<String>() {
            @Override
            public void perform(String obj, int s, String errmsg) {
                if (s < 0) {
                    Log.d(TAG, "BackendAPI.accountprofilechange: " + errmsg);
                    return;
                }
                user.set(field, value);
                update(user);
            }
        });*/
    }

    public static void clearUserInfo() {
        user = new UserCurrent();
        update(user);
    }

    public static String getUserEmail() {
        return prefs.getString("email", "");
    }

    public static String print() {
        return user.toJson();
    }
}
