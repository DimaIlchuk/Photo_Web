package com.photoweb.piiics;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.photoweb.piiics.activities.LoginActivity;

/**
 * Created by dnizard on 12/01/2018.
 */

public class PiiicsExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Activity activity;

    public PiiicsExceptionHandler(Activity a) {
        activity = a;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(PiiicsApplication.getInstance().getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) PiiicsApplication.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);

        PiiicsApplication.getInstance().mDefaultUEH.uncaughtException(thread, ex);

        activity.finish();
        System.exit(2);
    }
}
