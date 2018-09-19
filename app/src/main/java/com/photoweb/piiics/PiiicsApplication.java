package com.photoweb.piiics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.photoweb.piiics.utils.AWSHandler;
import com.photoweb.piiics.utils.BackendAPI;
import com.photoweb.piiics.utils.SocialHandler;
import com.photoweb.piiics.utils.UserInfo;

import io.fabric.sdk.android.Fabric;

/**
 * Created by dnizard on 25/04/2017.
 */

public class PiiicsApplication extends MultiDexApplication {
    private static final String TAG = "PiiicsApplication";

    public static PiiicsApplication instance;

    public static Thread.UncaughtExceptionHandler mDefaultUEH;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        //MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Fabric.with(this, new Crashlytics());

        FirebaseApp.initializeApp(this);

        BackendAPI.init(this);
        UserInfo.init(this);
        SocialHandler.get().FacebookInit(this);
        AWSHandler.get().init(this);

        String appToken = "dmi5zaokfxmo";
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(this, appToken, environment);
        Adjust.onCreate(config);

        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();

        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static PiiicsApplication getInstance() {
        return instance;
    }

    private static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }

        //...
    }
}
