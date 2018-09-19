package com.photoweb.piiics.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.photoweb.piiics.R;
import com.photoweb.piiics.model.FAQReceiver;
import com.photoweb.piiics.model.PriceReferences.BackgroundReference;
import com.photoweb.piiics.model.PriceReferences.FormatAndBookReferenceGeneral;
import com.photoweb.piiics.model.PriceReferences.StickerCategory;
import com.photoweb.piiics.model.PromoCode;
import com.photoweb.piiics.model.TutorialReceiver;
import com.photoweb.piiics.model.UserCommandCompleted;
import com.photoweb.piiics.model.UserCurrent;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dnizard on 25/04/2017.
 */

public final class BackendAPI {
    private static final String TAG = "BackendAPI";
    public static Context context;

    public static PiiicsAPI piiicsapi = null;

    public static class BooleanSerializer implements JsonDeserializer<Boolean> {
        //Note: specific gson deserializer to transform "0"/"1" into boolean
        @Override
        public Boolean deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            return (arg0.getAsString().equals("1") || arg0.getAsString().equals("true"));
        }
    }

    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.NONE); //Level.BODY for complete log

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(logging);
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void init(Context ctx) {
        context = ctx;
        //Note: uncomment all this (including .client... line) for complete api call logging:
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); //Level.BODY for complete log
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        //Note: using a specific gson converter to properly transform "0"/"1" to booleans
        Gson gson = new GsonBuilder().registerTypeAdapter(boolean.class, new BooleanSerializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getUnsafeOkHttpClient().build())
                .build();
        piiicsapi = retrofit.create(PiiicsAPI.class); //Create instance of piiics API interface
    }

    public interface ResponseListener<T> {
        void perform(T obj, int s, String errmsg);
    }

    public static class BaseResponse<T> {
        public T data;

        public BaseResponse(T data) {
            this.data = data;
        }
    }

    public static class SaleResponse<T> {
        public T sale;

        public SaleResponse(T data) {
            this.sale = data;
        }
    }

    public static class CodeResponse<T> {
        public T code;

        public CodeResponse(T code) {
            this.code = code;
        }
    }

    public static class UserResponse<T> {
        public String access_token;
        public String expires_in;
        public T user;

        public UserResponse(String access_token, String expires_in, T user) {
            this.access_token = access_token;
            this.expires_in = expires_in;
            this.user = user;
        }
    }

    public static <T> void API_global_call(Call<BaseResponse<T>> call, final ResponseListener<T> myrl) {
        //T is class of data element if success (eg: List<Stream>)
        //Execute the request asynchronously
        call.enqueue(new Callback<BaseResponse<T>>() {
            @Override
            public void onResponse(Call<BaseResponse<T>> call, Response<BaseResponse<T>> response) {
                Log.i(TAG, "response : " + call.request().url().toString());
                Log.i(TAG, "response code : " + String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    Log.i(TAG, "API GLOBAL CALL good");
                } else {
                    Log.i(TAG, "API GLOBAL CALL NOT good");
                }
                if (response.errorBody() != null) {
                    try {
                        String strerr = response.errorBody().string();
                        strerr = strerr.replace("\"", ""); //remove double quotes
                        int pos = strerr.indexOf("code:");
                        if (pos > 0) {
                            strerr = strerr.substring(pos + 5, strerr.length() - 2);
                            strerr = strerr.replace(",desc:", "-");
                        }
                        if(myrl!=null) myrl.perform(null, -1, strerr);
                        else Log.e(TAG,strerr);
                        return;
                    } catch (IOException e) {
                        if(myrl!=null) myrl.perform(null, -2, e.getLocalizedMessage());
                        else Log.e(TAG,e.getLocalizedMessage());
                        return;
                    }
                }
                if(myrl!=null) {
                    Log.i(TAG, "API GLOBAL CALL PERFORM MYRL");
                    myrl.perform(response.body().data, 0, "");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<T>> call, Throwable t) {
                if(myrl!=null) myrl.perform(null, -3, t.getMessage());
                else Log.e(TAG,"BackendAPI onFailure: " + t.getMessage());
            }
        });
    }

    public static <T> void API_sale_call(Call<SaleResponse<T>> call, final ResponseListener<T> myrl) {
        //T is class of data element if success (eg: List<Stream>)
        //Execute the request asynchronously
        call.enqueue(new Callback<SaleResponse<T>>() {
            @Override
            public void onResponse(Call<SaleResponse<T>> call, Response<SaleResponse<T>> response) {
                Log.i(TAG, "response : " + call.request().url().toString());
                Log.i(TAG, "response code : " + String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    Log.i(TAG, "API GLOBAL CALL good");
                } else {
                    Log.i(TAG, "API GLOBAL CALL NOT good");
                }
                if (response.errorBody() != null) {
                    try {
                        String strerr = response.errorBody().string();
                        strerr = strerr.replace("\"", ""); //remove double quotes
                        int pos = strerr.indexOf("code:");
                        if (pos > 0) {
                            strerr = strerr.substring(pos + 5, strerr.length() - 2);
                            strerr = strerr.replace(",desc:", "-");
                        }
                        if(myrl!=null) myrl.perform(null, -1, strerr);
                        else Log.e(TAG,strerr);
                        return;
                    } catch (IOException e) {
                        if(myrl!=null) myrl.perform(null, -2, e.getLocalizedMessage());
                        else Log.e(TAG,e.getLocalizedMessage());
                        return;
                    }
                }
                if(myrl!=null) {
                    Log.i(TAG, "API GLOBAL CALL PERFORM MYRL");
                    myrl.perform(response.body().sale, 0, "");
                }
            }

            @Override
            public void onFailure(Call<SaleResponse<T>> call, Throwable t) {
                if(myrl!=null) myrl.perform(null, -3, t.getMessage());
                else Log.e(TAG,"BackendAPI onFailure: " + t.getMessage());
            }
        });
    }

    public static <T> void API_user_call(Call<UserResponse<T>> call, final ResponseListener<T> myrl) {
        //T is class of data element if success (eg: List<Stream>)
        //Execute the request asynchronously
        call.enqueue(new Callback<UserResponse<T>>() {
            @Override
            public void onResponse(Call<UserResponse<T>> call, Response<UserResponse<T>> response) {
                if (response.errorBody() != null) {
                    try {
                        String strerr = response.errorBody().string();
                        strerr = strerr.replace("\"", ""); //remove double quotes
                        int pos = strerr.indexOf("code:");
                        if (pos > 0) {
                            strerr = strerr.substring(pos + 5, strerr.length() - 2);
                            strerr = strerr.replace(",desc:", "-");
                        }
                        if(myrl!=null) myrl.perform(null, -1, strerr);
                        else Log.e(TAG,strerr);
                        return;
                    } catch (IOException e) {
                        if(myrl!=null) myrl.perform(null, -2, e.getLocalizedMessage());
                        else Log.e(TAG,e.getLocalizedMessage());
                        return;
                    }
                }

                Log.d(TAG, "Token : " + response.body().access_token);

                SharedPreferences prefs = BackendAPI.context.getSharedPreferences("USER_INFO", MODE_PRIVATE);

                SharedPreferences.Editor prefsEditor = prefs.edit();

                prefsEditor.putString("token", response.body().access_token);
                prefsEditor.commit();

                if(myrl!=null) myrl.perform(response.body().user, 0, "");
            }

            @Override
            public void onFailure(Call<UserResponse<T>> call, Throwable t) {
                if(myrl!=null) myrl.perform(null, -3, t.getMessage());
                else Log.e(TAG,"BackendAPI onFailure: " + t.getMessage());
            }
        });
    }

    public static <T> void API_code_call(Call<CodeResponse<T>> call, final ResponseListener<T> myrl) {
        //T is class of data element if success (eg: List<Stream>)
        //Execute the request asynchronously
        call.enqueue(new Callback<CodeResponse<T>>() {
            @Override
            public void onResponse(Call<CodeResponse<T>> call, Response<CodeResponse<T>> response) {
                if (response.errorBody() != null) {
                    try {
                        String strerr = response.errorBody().string();
                        strerr = strerr.replace("\"", ""); //remove double quotes
                        int pos = strerr.indexOf("code:");
                        if (pos > 0) {
                            strerr = strerr.substring(pos + 5, strerr.length() - 2);
                            strerr = strerr.replace(",desc:", "-");
                        }
                        if(myrl!=null) myrl.perform(null, -1, strerr);
                        else Log.e(TAG,strerr);
                        return;
                    } catch (IOException e) {
                        if(myrl!=null) myrl.perform(null, -2, e.getLocalizedMessage());
                        else Log.e(TAG,e.getLocalizedMessage());
                        return;
                    }
                }
                if(myrl!=null) myrl.perform(response.body().code, 0, "");
            }

            @Override
            public void onFailure(Call<CodeResponse<T>> call, Throwable t) {
                if(myrl!=null) myrl.perform(null, -3, t.getMessage());
                else Log.e(TAG,"BackendAPI onFailure: " + t.getMessage());
            }
        });
    }

    public static <T> void API_standard_call(Call<T> call, final ResponseListener<T> myrl) {
        //T is class of data element if success (eg: List<Stream>)
        //Execute the request asynchronously
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.errorBody() != null) {
                    try {
                        String strerr = response.errorBody().string();
                        strerr = strerr.replace("\"", ""); //remove double quotes
                        int pos = strerr.indexOf("code:");
                        if (pos > 0) {
                            strerr = strerr.substring(pos + 5, strerr.length() - 2);
                            strerr = strerr.replace(",desc:", "-");
                        }
                        if(myrl!=null) myrl.perform(null, -1, strerr);
                        else Log.e(TAG,strerr);
                        return;
                    } catch (IOException e) {
                        if(myrl!=null) myrl.perform(null, -2, e.getLocalizedMessage());
                        else Log.e(TAG,e.getLocalizedMessage());
                        return;
                    }
                }
                if(myrl!=null) myrl.perform(response.body(), 0, "");
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if(myrl!=null) myrl.perform(null, -3, t.getMessage());
                else Log.e(TAG,"BackendAPI onFailure: " + t.getMessage());
            }
        });
    }

    public interface PiiicsAPI {
        @FormUrlEncoded
        @POST("user/login")
        Call<UserResponse<UserCurrent>> accountlogin(@Field("email") String email,
                                                     @Field("password") String password,
                                                     @Field("Android") Boolean Android,
                                                     @Field("lang") String lang);

        @FormUrlEncoded
        @POST("user/loginsocial")
        Call<UserResponse<UserCurrent>> accountloginsocial(@Field("socialnetwork") String socialnetwork,
                                                           @Field("socialtoken") String socialtoken,
                                                           @Field("username") String username,
                                                           @Field("email") String email,
                                                           @Field("udid") String udid,
                                                           @Field("Android") Boolean Android,
                                                           @Field("lang") String lang);

        @FormUrlEncoded
        @POST("user/register")
        Call<UserResponse<UserCurrent>> accountregister(@Field("username") String username,
                                                        @Field("email") String email,
                                                        @Field("password") String password,
                                                        @Field("udid") String udid,
                                                        @Field("Android") Boolean Android,
                                                        @Field("lang") String lang);

        @GET("user/profile/{userid}")
        Call<UserCurrent> getprofile(@Path("userid") int id);

        @GET("background/get")
        Call<ArrayList<BackgroundReference>> requestBackgroundReferences();

        @GET("config/setting")
        Call<FormatAndBookReferenceGeneral> requestFormatAndBookReferences();

        @GET("sticker/category/0")
        Call<ArrayList<StickerCategory>> requestStickerCategoryReferences();

        @GET("config/faq/{lang}")
        Call<FAQReceiver> requestFAQ(@Path("lang") String lang);


        @GET("config/tuto/{lang}")
        Call<TutorialReceiver> requestTutorialText(@Path("lang") String lang);


        @GET("sale/usersale/{id}")
        Call<ArrayList<UserCommandCompleted>> getUserCommandCompleted(@Path("id") String id, @Header("Authorization") String authHeader);

        @FormUrlEncoded
        @POST("sale/cart")
        Call<SaleResponse<UserCommandCompleted>> addcart(@Field("identifier") String identifier,
                                                         @Field("userid") int userid,
                                                         @Field("product") String product,
                                                         @Field("amount") float amount,
                                                         @Field("free") int free,
                                                         @Field("status") String status,
                                                         @Field("promocode") String promocode,
                                                         @Field("deliveryAddress") String deliveryAddress,
                                                         @Field("billingAddress") String billingAddress, @Header("Authorization") String authHeader);

        @FormUrlEncoded
        @POST("sale/add")
        Call<SaleResponse<UserCommandCompleted>> addsale(@Field("identifier") String identifier,
                                              @Field("userid") int userid,
                                              @Field("product") String product,
                                              @Field("amount") float amount,
                                              @Field("free") int free,
                                              @Field("status") String status,
                                              @Field("promocode") String promocode,
                                              @Field("deliveryAddress") String deliveryAddress,
                                              @Field("billingAddress") String billingAddress, @Header("Authorization") String authHeader);

        @FormUrlEncoded
        @POST("sale/focsale")
        Call<SaleResponse<UserCommandCompleted>> focsale(@Field("identifier") String identifier, @Header("Authorization") String authHeader);

        @FormUrlEncoded
        @POST("sale/insert")
        Call<BaseResponse<HashMap<String, Object>>>  insertitems(@FieldMap Map<String, Object> params, @Header("Authorization") String authHeader);

        @FormUrlEncoded
        @POST("sale/paypalpayment")
        Call<HashMap<String, String>> paypalpayment(@Field("identifier") String identifier,
                                                         @Field("nonce") String nonce, @Header("Authorization") String authHeader);

        @FormUrlEncoded
        @POST("sale/createcharge")
        Call<HashMap<String, String>> createcharge(@Field("identifier") String identifier,
                                                    @Field("token") String token, @Header("Authorization") String authHeader);

        @FormUrlEncoded
        @POST("user/updatesponsor")
        Call<CodeResponse<PromoCode>> addsponsorcode(@Field("id") int id,
                                                     @Field("sponsor") String sponsor, @Header("Authorization") String authHeader);

        @FormUrlEncoded
        @POST("user/AskForPassword")
        Call<UserResponse<UserCurrent>> askforpassword(@Field("email") String email);

    }

    public static void accountlogin(String email, String password, Boolean Android, String lang, final ResponseListener<UserCurrent> myrl) {
        Call<UserResponse<UserCurrent>> call = piiicsapi.accountlogin(email, password, Android, lang);
        API_user_call(call, myrl);
    }

    public static void accountloginsocial(String socialnetwork, String socialtoken, String username, String email, String udid, Boolean Android, String lang, final ResponseListener<UserCurrent> myrl) {
        Call<UserResponse<UserCurrent>> call = piiicsapi.accountloginsocial(socialnetwork, socialtoken, username, email, udid, Android, lang);
        API_user_call(call, myrl);
    }

    public static void accountregister(String username, String email, String password, String udid, Boolean Android, String lang, final ResponseListener<UserCurrent> myrl) {
        Call<UserResponse<UserCurrent>> call = piiicsapi.accountregister(username, email, password, udid, Android, lang);
        API_user_call(call, myrl);
    }

    public static void getUserCommands(String userid, String token, final ResponseListener<ArrayList<UserCommandCompleted>> myrl)
    {
        Call<ArrayList<UserCommandCompleted>> call = piiicsapi.getUserCommandCompleted(userid, "Bearer " + token);
        API_standard_call(call, myrl);
    }

    public static void addcart(String identifier, int userid, String product, float amount, int free, String status, String promocode, String deliveryAddress, String billingAddress, String token, final ResponseListener<UserCommandCompleted> myrl) {
        Call<SaleResponse<UserCommandCompleted>> call = piiicsapi.addcart(identifier, userid, product, amount, free, status, promocode, deliveryAddress, billingAddress, "Bearer " + token);
        API_sale_call(call, myrl);
    }

    public static void addsale(String identifier, int userid, String product, float amount, int free, String status, String promocode, String deliveryAddress, String billingAddress, String token, final ResponseListener<UserCommandCompleted> myrl) {
        Call<SaleResponse<UserCommandCompleted>> call = piiicsapi.addsale(identifier, userid, product, amount, free, status, promocode, deliveryAddress, billingAddress, "Bearer " + token);
        API_sale_call(call, myrl);
    }

    public static void focsale(String identifier, String token, final ResponseListener<UserCommandCompleted> myrl){
        Call<SaleResponse<UserCommandCompleted>> call = piiicsapi.focsale(identifier, "Bearer " + token);
        API_sale_call(call, myrl);
    }

    public static void insertitems(Map<String, Object> params, String token, final ResponseListener<HashMap<String, Object>> myrl) {
        Call<BaseResponse<HashMap<String, Object>>> call = piiicsapi.insertitems(params, "Bearer " + token);
        API_global_call(call, myrl);
    }

    /*public static void paypalpayment(String identifier, String nonce, String token, final ResponseListener<HashMap<String, String>> myrl) {
        Call<BaseResponse<HashMap<String, String>>> call = piiicsapi.paypalpayment(identifier, nonce, "Bearer " + token);
        API_global_call(call, myrl);
    }*/

    public static void getprofile(int userid, final ResponseListener<UserCurrent> myrl) {
        Call<UserCurrent> call = piiicsapi.getprofile(userid);
        API_standard_call(call, myrl);
    }

    public static void addsponsorcode(int id, String sponsor, String token, final ResponseListener<PromoCode> myrl) {
        Log.d("BackendAPI", token);
        Call<CodeResponse<PromoCode>> call = piiicsapi.addsponsorcode(id, sponsor,"Bearer " + token);
        API_code_call(call, myrl);
    }

    public static void askforpassword(String email, final ResponseListener<UserCurrent> myrl) {
        Call<UserResponse<UserCurrent>> call = piiicsapi.askforpassword(email);
        API_user_call(call, myrl);
    }
}
