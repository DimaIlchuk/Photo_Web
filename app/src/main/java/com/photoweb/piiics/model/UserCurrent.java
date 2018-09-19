package com.photoweb.piiics.model;

import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by dnizard on 25/04/2017.
 */

public class UserCurrent {
    private static final String TAG = "UserCurrent";

    private int id;
    private String username;
    private String password;
    private String email;
    private String socialtoken;
    private String socialnetwork;
    private int print_available;
    private int book_available;
    private int print_bonus;
    private String sponsor_code;
    private String sponsorer;
    private Boolean optin;
    private Boolean hasCard;
    private String civility;

    public Object get(String field) {
        Object o;
        if(field.equals("id")) o=id;
        else if (field.equals("username")) o=username;
        else if (field.equals("password")) o=password;
        else if (field.equals("email")) o=email;
        else if (field.equals("socialtoken")) o=socialtoken;
        else if (field.equals("socialnetwork")) o=socialnetwork;
        else if (field.equals("print_available")) o=print_available;
        else if (field.equals("book_available")) o=book_available;
        else if (field.equals("print_bonus")) o=print_bonus;
        else if (field.equals("sponsor_code")) o=sponsor_code;
        else if (field.equals("sponsorer")) o=sponsorer;
        else if (field.equals("optin")) o=optin;
        else if (field.equals("hasCard")) o=hasCard;
        else if (field.equals("civility")) o=civility;
        else {
            Log.e(TAG,"Unknown field:"+field); return null;}
        return (o==null?"":o);
    }
    public void set(String field,Object value) {
        if(field.equals("id")) id=(int)value;
        else if (field.equals("username")) username=(String)value;
        else if (field.equals("password")) password=(String)value;
        else if (field.equals("email")) email=(String)value;
        else if (field.equals("socialtoken")) socialtoken=(String)value;
        else if (field.equals("socialnetwork")) socialnetwork=(String)value;
        else if (field.equals("print_available")) print_available=(int)value;
        else if (field.equals("book_available")) book_available=(int)value;
        else if (field.equals("print_bonus")) print_bonus=(int)value;
        else if (field.equals("sponsor_code")) sponsor_code=(String)value;
        else if (field.equals("sponsorer")) sponsorer=(String)value;
        else if (field.equals("optin")) optin=(Boolean) value;
        else if (field.equals("hasCard")) hasCard=(Boolean) value;
        else if (field.equals("civility")) civility=(String)value;
        else Log.e(TAG,"Unknown field:"+field);
    }

    public static UserCurrent fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, UserCurrent.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
