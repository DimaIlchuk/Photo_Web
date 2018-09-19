package com.photoweb.piiics.utils.stripe;

import com.photoweb.piiics.model.UserCurrent;
import com.photoweb.piiics.utils.BackendAPI;
import com.photoweb.piiics.utils.UserInfo;
import com.stripe.android.EphemeralKeyProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.util.Log;

import com.stripe.android.EphemeralKeyUpdateListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.content.Context.MODE_PRIVATE;

/**
 * An implementation of {@link com.stripe.android.EphemeralKeyProvider} that can be used to generate
 * ephemeral keys on the backend.
 */
public class PiiicsEphemeralKeyProvider implements com.stripe.android.EphemeralKeyProvider {

    private @NonNull CompositeSubscription mCompositeSubscription;
    private @NonNull StripeService mStripeService;
    private @NonNull ProgressListener mProgressListener;
    private @NonNull Context mContext;
    private int mCall;

    public PiiicsEphemeralKeyProvider(@NonNull ProgressListener progressListener, Context context) {
        Retrofit retrofit = RetrofitFactory.getInstance();
        mStripeService = retrofit.create(StripeService.class);
        mCompositeSubscription = new CompositeSubscription();
        mProgressListener = progressListener;
        mContext = context;
        mCall = 0;
    }

    @Override
    public void createEphemeralKey(@NonNull @Size(min = 4) String apiVersion,
                                   @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {
        mCall++;
        if(mCall == 1){
            Map<String, String> apiParamMap = new HashMap<>();
            apiParamMap.put("apiVersion", apiVersion);
            apiParamMap.put("userid", "" + UserInfo.getInt("id"));

            SharedPreferences prefs = mContext.getSharedPreferences("USER_INFO", MODE_PRIVATE);

            mCompositeSubscription.add(
                    mStripeService.createEphemeralKey(apiParamMap, "Bearer " + prefs.getString("token", ""))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<ResponseBody>() {
                                @Override
                                public void call(ResponseBody response) {
                                    try {
                                        String rawKey = response.string();
                                        keyUpdateListener.onKeyUpdate(rawKey);
                                        mProgressListener.onStringResponse(rawKey);
                                    } catch (IOException iox) {

                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    mProgressListener.onStringResponse(throwable.getMessage());
                                }
                            }));
        }
    }

    public interface ProgressListener {
        void onStringResponse(String string);
    }
}