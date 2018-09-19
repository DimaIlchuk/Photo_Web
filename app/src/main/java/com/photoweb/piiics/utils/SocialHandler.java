package com.photoweb.piiics.utils;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.photoweb.piiics.R;
import com.photoweb.piiics.model.Album;
import com.photoweb.piiics.model.Asset;
import com.photoweb.piiics.model.UserCurrent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dnizard on 25/04/2017.
 */

public class SocialHandler implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "SocialHandler";
    private Context context;

    private CallbackManager FacebookClient;
    private AccessTokenTracker FacebookAccessTokenTracker;

    private GoogleApiClient GoogleClient;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount GoogleAccount;

    private static final int GOOGLE_REQUEST_CODE = 4444;
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 5555;

    private static SocialHandler instance;
    private static SharedPreferences prefs;

    /** Instance of the Google Play controller */
    private GDController GDController;
    public static int currentPage = 1;
    private String googleAccountName;
    public String googleAuthToken;
    GoogleAccountCredential credential;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private Drive service;

    /** Dropbox variables **/
    private String mPath = "";


    public Map<String, ArrayList<Album>> albums = new HashMap<String, ArrayList<Album>>();
    public Map<String, ArrayList<Asset>> photos = new HashMap<String, ArrayList<Asset>>();

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    public static SocialHandler get() {
        if (instance == null) instance = new SocialHandler();
        return instance;
    }

    public void init(Context _context) {
        context = _context;
        prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        //FacebookInit();
        GoogleInit();
    }

    public void setContext(Context _context){
        context = _context;
    }

    public void destroy() {
        FacebookAccessTokenTracker.stopTracking();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            FacebookClient.onActivityResult(requestCode, resultCode, result);
        }else if (requestCode == GOOGLE_REQUEST_CODE) { // Google + callback
            Log.d(TAG, "request code");
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(result), null);
        }else if (requestCode == RESOLVE_CONNECTION_REQUEST_CODE) { // Google + callback
            Log.d(TAG, "result : " + resultCode);
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();

            }

        }else if(requestCode ==  GoogleConstants.REQUEST_ACCOUNT_PICKER){

            if (resultCode == RESULT_OK && result != null &&
                    result.getExtras() != null) {
                Log.d(TAG,"Google account selected : " + result.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                //Toast.makeText(context, "Google account selected : " + result.getStringExtra(AccountManager.KEY_ACCOUNT_NAME), Toast.LENGTH_SHORT).show();

                googleAccountName =
                        result.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                mGoogleApiClient.connect();
                //fetchAuthToken();

            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG,"Google account unspecified");
                //Toast.makeText(context, "Google account unspecified", Toast.LENGTH_SHORT).show();
            }

        }else if(requestCode == GoogleConstants.REQUEST_AUTHORIZATION){
            //Toast.makeText(context, "Authorization get", Toast.LENGTH_SHORT).show();
            fetchAuthToken();
        } else {
            //Toast.makeText(context,"Unknown request code received:" + requestCode, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Unknown request code received:" + requestCode);
        }
    }

    private void accountloginsocial(final String socialnetwork, final String socialtoken, String username, String email) {
        Log.d(TAG, "create account");

        BackendAPI.accountloginsocial(socialnetwork, socialtoken, username, email, Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID), true, context.getString(R.string.LANG), new BackendAPI.ResponseListener<UserCurrent>() {
            public void perform(UserCurrent obj, int s, String errmsg) {
                if (s < 0) {
                    Log.d(TAG, "BackendAPI.accountloginsocial: " + errmsg);
                    return;
                }
                UserInfo.update(obj);

                SharedPreferences prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

                SharedPreferences.Editor prefsEditor = prefs.edit();

                prefsEditor.putString("socialnetwork", socialnetwork);
                prefsEditor.putString("socialtoken", socialtoken);
                prefsEditor.commit();

                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GlobEvent_SocialConnect"));
            }
        });
    }

    public void connect(String network, Boolean createAccount) {
        if (network.equals("facebook")) ConnectFacebook(createAccount);
        else if (network.equals("google")) ConnectGoogle(createAccount);
        else if (network.equals("instagram")) ConnectInstagram(createAccount);
        else if (network.equals("dropbox")) ConnectDropbox();
    }

    public void disconnect(String network) {
        if (network.equals("facebook")) DisconnectFacebook();
        else if (network.equals("google")) DisconnectGoogle();
        else if (network.equals("instagram")) DisconnectInstagram();
        //todo: disconnect DropBox ?
    }

    //FACEBOOK CONNECT
    public void FacebookInit(Context mContext) {
        FacebookSdk.sdkInitialize(mContext.getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override public void onInitialized() {

            }
        });

        FacebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    Log.e(TAG, "Facebook is disconnected");
                    disconnect("facebook"); //Force disconnect
                }
            }
        };
    }

    private void ConnectFacebook(final Boolean createAccount) {
        FacebookClient = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(FacebookClient, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v(TAG, "Facebook connect onSuccess");
                String social_token = AccessToken.getCurrentAccessToken().getToken();
                // App code

                if(createAccount){
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v(TAG, response.toString());

                                    // Application code
                                    try {
                                        String email = object.getString("email");
                                        String id = object.getString("id");
                                        String username = object.getString("first_name");

                                        accountloginsocial("facebook", id, username, email);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,first_name,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }else{
                    getFacebookAlbums();
                }

            }

            @Override
            public void onCancel() {
                Log.v(TAG, "Facebook connect onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.v(TAG, "Facebook connect onError " + error.getMessage());
                Toast.makeText(context.getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
        final List<String> permissions = Arrays.asList("public_profile", "email", "user_friends", "user_photos");
        LoginManager.getInstance().logInWithReadPermissions((Activity) context, permissions);
    }

    private void DisconnectFacebook() {
        LoginManager.getInstance().logOut();
    }

    public void getFacebookAlbums()
    {
        if(AccessToken.getCurrentAccessToken() != null){
            //Toast.makeText(context.getApplicationContext(), "Got Token try to download photos", Toast.LENGTH_LONG).show();

            Log.d(TAG, "Token : " + AccessToken.getCurrentAccessToken().getToken() + " - " + AccessToken.getCurrentAccessToken().isExpired());
            //Toast.makeText(context.getApplicationContext(), "Got a Facebook Token", Toast.LENGTH_LONG).show();

            if(prefs == null) prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Facebook", AccessToken.getCurrentAccessToken().getToken());
            editor.commit();

            final ArrayList<Album> albumsFB = new ArrayList<Album>();

            GraphRequest request = new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "me/albums",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            /* handle the result */
                            //Toast.makeText(context.getApplicationContext(), "Got an awser !", Toast.LENGTH_LONG).show();
                            try {
                                JSONArray data = response.getJSONObject().getJSONArray("data");

                                //Toast.makeText(context.getApplicationContext(), data.length() + " albums found !", Toast.LENGTH_LONG).show();

                                for(int i = 0; i < data.length(); i++){
                                    JSONObject oneAlbum = data.getJSONObject(i);
                                    Log.d(TAG, oneAlbum.toString());
                                    Album album = new Album();

                                    //get your values
                                    album.id = oneAlbum.getString("id");
                                    album.name = oneAlbum.getString("name");
                                    album.createdAt = oneAlbum.getString("created_time");

                                    getFacebookAssetsForAlbum(album);

                                    albumsFB.add(album);
                                }

                                Log.d(TAG, albumsFB.toString());
                                albums.put("Facebook", albumsFB);

                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("UpdateAsset"));

                            } catch (Exception e){
                                e.printStackTrace();
                                //Toast.makeText(context.getApplicationContext(), "Damn ! An error !" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            );

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, created_time");
            request.setParameters(parameters);
            request.executeAsync();

        }else{
            //Toast.makeText(context.getApplicationContext(), "No token received !", Toast.LENGTH_LONG).show();
        }
    }

    public void getFacebookAssetsForAlbum(final Album album)
    {
        if(AccessToken.getCurrentAccessToken() != null){

            final ArrayList<Asset> assets = new ArrayList<Asset>();

            GraphRequest request = new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    album.id+"/photos",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            /* handle the result */
                            try {
                                JSONArray data = response.getJSONObject().getJSONArray("data");

                                for(int i = 0; i < data.length(); i++){
                                    JSONObject oneAsset = data.getJSONObject(i);
                                    Log.d(TAG, oneAsset.toString());

                                    JSONArray images = oneAsset.getJSONArray("images");

                                    JSONObject original = images.getJSONObject(0);
                                    JSONObject thumnail = images.getJSONObject(images.length()-1);

                                    Asset asset = new Asset(oneAsset.getString("id"), original.getString("source"), thumnail.getString("source"), "Facebook");

                                    assets.add(asset);
                                }

                                Log.d(TAG, assets.toString());
                                album.assets = assets;

                                if(assets.size() > 0){
                                    album.coverPhoto = assets.get(0).imageThumbnail;
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
            );

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, images");
            request.setParameters(parameters);
            request.executeAsync();

        }
    }

    //GOOGLE CONNECT
    private void GoogleInit() {
        GDController = GDController.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleClient = new GoogleApiClient.Builder(context.getApplicationContext())
                .enableAutoManage((FragmentActivity) context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                //.addApi(com.google.android.gms.drive.Drive.API)
                //.addScope(com.google.android.gms.drive.Drive.SCOPE_FILE)
                .build();

        final OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(GoogleClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result, opr);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult, opr);
                }
            });
        }

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(com.google.android.gms.drive.Drive.API)
                .addScope(com.google.android.gms.drive.Drive.SCOPE_FILE)
                .addScope(new Scope(DriveScopes.DRIVE_READONLY))
                //.addScope(new Scope(GoogleConstants.GPHOTOS_SCOPE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void ConnectGoogle(Boolean createAccount) {

        if(createAccount){
            if (GoogleClient == null) GoogleInit();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(GoogleClient);

            ((Activity) context).startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE);
        }else{
            if (mGoogleApiClient == null) GoogleInit();

            mGoogleApiClient.connect();

        }

    }

    private void handleSignInResult(GoogleSignInResult googleSignInResult, OptionalPendingResult<GoogleSignInResult> opr) {
        if (!googleSignInResult.isSuccess()) {
            Log.e(TAG, "Google handleSignInResult: " + googleSignInResult.getStatus());
            if (opr == null)
                Toast.makeText(context.getApplicationContext(), "Error handleSignInResult: " + googleSignInResult.getStatus(), Toast.LENGTH_SHORT).show();
            return;
        }
        GoogleAccount = googleSignInResult.getSignInAccount();
        if (GoogleAccount != null) {
            if(prefs == null) prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Google", GoogleAccount.getIdToken());
            googleAuthToken = GoogleAccount.getIdToken();
            //GDController.setAPIToken(GoogleAccount.getIdToken());
            editor.commit();

            Log.d(TAG, "signin account");

            accountloginsocial("google", GoogleAccount.getId(), GoogleAccount.getGivenName(), GoogleAccount.getEmail());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult((Activity) context, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            //GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();

            Log.e(TAG, "Google onConnectionFailed: " + connectionResult.getErrorMessage());
            Toast.makeText(context.getApplicationContext(), "Error : Connection to Google failed", Toast.LENGTH_SHORT).show();
        }

        /*if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Google onConnectionFailed: " + connectionResult.getErrorMessage());
            Toast.makeText(context.getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void DisconnectGoogle() {
        UserInfo.set("google_id", "");
        Auth.GoogleSignInApi.signOut(GoogleClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.i(TAG, "DisconnectGoogle completed");
                    }
                });
    }

    public void getGooglePhotos()
    {
        if(photos.get("Google") != null){
            for (Asset photo : photos.get("Google")) {
                photo.selected = false;
            }
        }else{
            if(prefs == null) prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

            googleAccountName = prefs.getString(GoogleConstants.PREF_ACCOUNT_NAME, "");

            if(!googleAccountName.equals("")){
                fetchAuthToken();
            }
        }
    }

    public void initGooglePhotos()
    {
        //revokeToken();

        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);

        Log.d(TAG,"Starting activity for Choosing Account");
        //Toast.makeText(context.getApplicationContext(), "Starting activity for Choosing Account", Toast.LENGTH_SHORT).show();
        ((Activity) context).startActivityForResult(intent, GoogleConstants.REQUEST_ACCOUNT_PICKER);


    }

    private void fetchAuthToken() {
        if (googleAccountName != null && !googleAccountName.equals("")) {
            Log.d(TAG, "Account : " + googleAccountName);
            /*Toast.makeText(context.getApplicationContext(),
                    "I have a Google Account",
                    Toast.LENGTH_SHORT)
                    .show();*/

            if(prefs == null) prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(GoogleConstants.PREF_ACCOUNT_NAME, googleAccountName);
            editor.apply();

            new AsyncTask<Void, Void, String>(){

                @Override
                protected String doInBackground(Void... voids) {

                    List<String> scopes = Arrays.asList(DriveScopes.DRIVE_READONLY);

                    try {
                        credential = GoogleAccountCredential.usingOAuth2(context, scopes)
                                .setSelectedAccountName(googleAccountName);
                        googleAuthToken = credential.getToken();
                        Log.d(TAG, "Account Set : " + credential.getSelectedAccountName());
                        Log.d(TAG, "Google Token : " + googleAuthToken);

                        //revokeToken();

                        service = new Drive.Builder(transport, jsonFactory, credential)
                                .setApplicationName("Piiics")
                                .build();

                        FileList files = service.files().list()
                                .setFields("nextPageToken, files(id, name, thumbnailLink)")
                                .setQ("mimeType='image/jpeg'")
                                .setPageSize(500)
                                .execute();
                        Log.d(TAG, files.toString());

                        //Toast.makeText(context.getApplicationContext(), "Files get : " + files.getFiles().size(), Toast.LENGTH_SHORT).show();

                        final ArrayList<Asset> assetsGL = new ArrayList<Asset>();

                        for (File file:files.getFiles()) {
                            Asset photo = new Asset(file.getId(), "https://www.googleapis.com/drive/v3/files/"+file.getId()+"?alt=media",file.getThumbnailLink(),"Google");

                            assetsGL.add(photo);
                        }

                        photos.put("Google", assetsGL);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("UpdateAsset"));

                        //getGooglePhotos();

                        //service = new Drive.builder(httpTransport, jsonFactory, null).setApplicationName("MyAppName")
                        //.setHttpRequestInitializer(credential).build();
                    } catch (UserRecoverableAuthIOException e) {
                        Log.d(TAG, "Not auth");
                        //Toast.makeText(context.getApplicationContext(), "No Auth", Toast.LENGTH_SHORT).show();
                        ((Activity) context).startActivityForResult(e.getIntent(), GoogleConstants.REQUEST_AUTHORIZATION);
                    } catch (IOException e) {
                        Log.d(TAG, "Something else");
                        //Toast.makeText(context.getApplicationContext(), "Something else : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                    } catch (GoogleAuthException e) {
                        Log.d(TAG, e.getMessage());
                        //Toast.makeText(context.getApplicationContext(), "I don't have permissions !", Toast.LENGTH_SHORT).show();
                        if(e.getMessage().equals("NeedPermission")){
                            connect("google", false);
                        }
                        e.printStackTrace();
                    } catch (IllegalArgumentException e){
                        Log.d(TAG, "Something else");
                        //Toast.makeText(context.getApplicationContext(), "Something else : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                    }

                    return null;
                }
            }.execute();

            /*GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE);
            credential.setSelectedAccountName(googleAccountName);
            Drive service = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).build();

            new AsyncTask(){
                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        Log.d(TAG,"Requesting token for account: " +
                                googleAccountName);
                        googleAuthToken = GoogleAuthUtil.getToken(getApplicationContext(),
                                googleAccountName, GoogleConstants.GPHOTOS_SCOPE);

                        Log.d(TAG, "Received Token: " + googleAuthToken);
                        GDController.setAPIToken(googleAuthToken);
                        getGooglePhotos();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (UserRecoverableAuthException e) {
                        ((Activity) context).startActivityForResult(e.getIntent(), GoogleConstants.REQ_SIGN_IN_REQUIRED);
                    } catch (GoogleAuthException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    return null;
                }
            };*/
        } else {
            //chooseGoogleAccount();
        }
    }

    //INSTAGRAM CONNECT
    private void ConnectInstagram(final Boolean createAccount) {
        String client_id = "de4b995ec89b44579a5f716fc808b959";
        String instaurl = String.format("https://api.instagram.com/oauth/authorize/?response_type=token&client_id=%s&redirect_uri=%s", client_id, Utils.INSTA_URL);

        SocialOauthDialog.OAuthDialogListener sodl = new SocialOauthDialog.OAuthDialogListener() {
            @Override
            public void onComplete(String url) {
                String accessToken = url.substring(url.lastIndexOf("=") + 1);
                Log.d(TAG, accessToken);
                if (accessToken == null || accessToken.length() == 0) {
                    Log.e(TAG, "Incorrect instagram token " + url);
                    return;
                }

                if(prefs == null) prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

                prefs.edit().putString("INSTAGRAM_access_token", accessToken).apply();
                if(createAccount)
                    GetInstagramInfos();
                else
                    getInstagramPhotos();
            }

            @Override
            public void onError(String error) {
                Log.i(TAG, "ConnectInstagram error:" + error);
            }
        };
        (new SocialOauthDialog(context, instaurl, sodl)).show();
    }

    private void GetInstagramInfos() {
        try {
            if(prefs == null) prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

            String access_token = prefs.getString("INSTAGRAM_access_token", null);
            if (access_token == null) {
                Log.e(TAG, "Cant retrieve instagram infos without token");
                return;
            }
            final URL url = new URL("https://api.instagram.com/v1/users/self/?access_token=" + access_token);
            Request request = new Request.Builder().url(url).build();

            (new OkHttpClient()).newCall(request).enqueue(new okhttp3.Callback() {
                @Override public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "GetInstagramInfos error:" + e.getMessage());
                }

                @Override public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String rep = response.body().string();
                    try {
                        JSONObject jsonObj = (JSONObject) new JSONTokener(rep).nextValue();
                        String username = jsonObj.getJSONObject("data").getString("username");
                        String userID = jsonObj.getJSONObject("data").getString("id");
                        String customInstagramToken = userID + ";" + username;
                        Log.i(TAG, "GetInstagramInfos: " + customInstagramToken);
                        accountloginsocial("instagram", userID, username, "");
                    } catch (Exception e) {
                        Log.e(TAG, "Error instagram API: " + e.getMessage() + " / Response:" + rep);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "GetInstagramInfos error:" + e.getMessage());
        }
    }

    public void getInstagramPhotos()
    {
        if(prefs == null) prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

        String access_token = prefs.getString("INSTAGRAM_access_token", null);
        if (access_token != null) {
            //Toast.makeText(context.getApplicationContext(), "I have an instagram token", Toast.LENGTH_SHORT).show();
            try{
                final URL url = new URL("https://api.instagram.com/v1/users/self/media/recent/?access_token=" + access_token + "&count=100");
                Request request = new Request.Builder().url(url).build();

                (new OkHttpClient()).newCall(request).enqueue(new okhttp3.Callback() {
                    @Override public void onFailure(okhttp3.Call call, IOException e) {
                        Log.e(TAG, "GetInstagramInfos error:" + e.getMessage());
                        //Toast.makeText(context.getApplicationContext(), "API Instagram errors : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        String rep = response.body().string();
                        try {
                            JSONObject jsonObj = (JSONObject) new JSONTokener(rep).nextValue();
                            Log.d(TAG, jsonObj.toString());

                            JSONArray jsonData  = jsonObj.getJSONArray("data");

                            int length = jsonData.length();

                            //Toast.makeText(context.getApplicationContext(), "Found " + length + " photos", Toast.LENGTH_SHORT).show();

                            if (length > 0) {
                                final ArrayList<Asset> assetsIG = new ArrayList<Asset>();

                                for (int i = 0; i < length; i++) {
                                    JSONObject jsonThumbnail = jsonData.getJSONObject(i).getJSONObject("images").getJSONObject("thumbnail");
                                    JSONObject jsonPhoto = jsonData.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution");

                                    Asset photo = new Asset(jsonData.getJSONObject(i).getString("id"), jsonPhoto.getString("url"),jsonThumbnail.getString("url"),"Instagram");

                                    assetsIG.add(photo);
                                }

                                photos.put("Instagram", assetsIG);

                                if(jsonObj.has("pagination")){
                                    JSONObject jsonPagination  = jsonObj.getJSONObject("pagination");

                                    if(jsonPagination.has("next_url")){
                                        getNextInstagram((String)jsonPagination.get("next_url"));
                                        return;
                                    }
                                }

                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("UpdateAsset"));
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "Error instagram API: " + e.getMessage() + " / Response:" + rep);
                            //Toast.makeText(context.getApplicationContext(), "API Instagram error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }catch (Exception e){
                Log.e(TAG, "GetInstagramPhotos error:" + e.getMessage());
                //Toast.makeText(context.getApplicationContext(), "GetInstagramPhotos error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getNextInstagram(String urlNext)
    {
        final ArrayList<Asset> assetsIG = photos.get("Instagram");

        try{
            final URL url = new URL(urlNext);
            Request request = new Request.Builder().url(url).build();

            (new OkHttpClient()).newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "GetInstagramInfos error:" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String rep = response.body().string();
                    try {
                        JSONObject jsonObj = (JSONObject) new JSONTokener(rep).nextValue();
                        Log.d(TAG, jsonObj.toString());

                        JSONArray jsonData  = jsonObj.getJSONArray("data");

                        int length = jsonData.length();

                        //Toast.makeText(context.getApplicationContext(), "Found " + length + " photos", Toast.LENGTH_SHORT).show();

                        if (length > 0) {
                            //final ArrayList<Asset> assetsIG = new ArrayList<Asset>();

                            for (int i = 0; i < length; i++) {
                                JSONObject jsonThumbnail = jsonData.getJSONObject(i).getJSONObject("images").getJSONObject("thumbnail");
                                JSONObject jsonPhoto = jsonData.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution");

                                Asset photo = new Asset(jsonData.getJSONObject(i).getString("id"), jsonPhoto.getString("url").replace("s640x640", "s1080x1080").replace("vp/", ""),jsonThumbnail.getString("url"),"Instagram");

                                assetsIG.add(photo);
                            }

                            photos.put("Instagram", assetsIG);

                            if(jsonObj.has("pagination")){
                                JSONObject jsonPagination  = jsonObj.getJSONObject("pagination");

                                if(jsonPagination.has("next_url")){
                                    getNextInstagram((String)jsonPagination.get("next_url"));
                                    return;
                                }
                            }

                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("UpdateAsset"));
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Error instagram API: " + e.getMessage() + " / Response:" + rep);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("UpdateAsset"));
                    }
                }
            });

        }catch (Exception e){
            Log.e(TAG, "GetInstagramPhotos error:" + e.getMessage());
            //Toast.makeText(context.getApplicationContext(), "GetInstagramPhotos error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void DisconnectInstagram() {
        UserInfo.set("instagram_id", "");
    }

    //DROPBOX CONNECT
    public void ConnectDropbox()
    {
        if(prefs == null) prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

        String accessToken = prefs.getString("Dropbox", null);
        if (accessToken == null) {
            com.dropbox.core.android.Auth.startOAuth2Authentication(context, context.getString(R.string.app_key));
            if (accessToken != null) {
                prefs.edit().putString("Dropbox", accessToken).apply();
                initAndLoadAlbumDropbox(accessToken);
            }else{
                Log.d(TAG, "Dropbox not connected");
            }
        } else {
            initAndLoadAlbumDropbox(accessToken);
        }


    }

    public void onResume()
    {
        if(prefs == null) prefs = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

        String accessToken = prefs.getString("Dropbox", null);
        if (accessToken == null) {
            accessToken = com.dropbox.core.android.Auth.getOAuth2Token();
            if (accessToken != null) {
                prefs.edit().putString("Dropbox", accessToken).apply();
                initAndLoadAlbumDropbox(accessToken);
            }
        } else {
            initAndLoadAlbumDropbox(accessToken);
        }

    }

    private void initAndLoadAlbumDropbox(String accessToken)
    {
        DropboxClientFactory.init(accessToken);
        PicassoClient.init(this.context, DropboxClientFactory.getClient());

        albums.remove("Dropbox");
        photos.remove("Dropbox");

        final ArrayList<Asset> assetsDB = new ArrayList<Asset>();
        final ArrayList<Album> albumsDB = new ArrayList<Album>();

        new ListFolderTask(DropboxClientFactory.getClient(), new ListFolderTask.Callback(){
            @Override
            public void onDataLoaded(ListFolderResult result) {
                //mFilesAdapter.setFiles(result.getEntries());
                for (Metadata entry:result.getEntries()) {
                    Log.d(TAG, entry.getClass().toString());

                    if (entry instanceof FolderMetadata)
                    {
                        Album album = new Album();

                        //get your values
                        album.id = ((FolderMetadata) entry).getId();
                        album.name = entry.getName();
                        //album.createdAt = entry.;
                        //album.coverPhoto = oneAlbum.getString("cover_photo");

                        //getFacebookAssetsForAlbum(album);

                        albumsDB.add(album);

                    }else if (entry.getName().toLowerCase().endsWith(".jpg") || entry.getName().toLowerCase().endsWith(".png")) {
                        FileMetadata file = (FileMetadata)entry;

                        Asset photo = new Asset(file.getId(), file.getPathLower(), file.getPathLower(), "Dropbox");

                        assetsDB.add(photo);
                    }
                }

                if(albumsDB.size() > 0){
                    if(assetsDB.size() > 0){
                        Album album = new Album();

                        //get your values
                        album.id = UUID.randomUUID().toString();
                        album.name = "This folder";

                        albumsDB.add(0, album);

                    }

                    albums.put("Dropbox", albumsDB);
                }else{
                    photos.put("Dropbox", assetsDB);
                }

                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("UpdateAsset"));
            }

            @Override
            public void onError(Exception e) {

                Log.e(TAG, "Failed to list folder.", e);
                Toast.makeText(context,
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(mPath);
    }

    public void setDBPath(String str)
    {
        mPath = mPath + "/" + str;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "API Connect");
        /*Toast.makeText(context.getApplicationContext(),
                "Connected to API now getting photos",
                Toast.LENGTH_SHORT)
                .show();*/
        fetchAuthToken();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
