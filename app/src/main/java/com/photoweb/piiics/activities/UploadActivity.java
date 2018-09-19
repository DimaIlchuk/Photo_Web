package com.photoweb.piiics.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.appsee.Appsee;
import com.photoweb.piiics.PiiicsExceptionHandler;
import com.photoweb.piiics.R;
import com.photoweb.piiics.utils.AWSHandler;
import com.photoweb.piiics.utils.UserInfo;

import java.io.File;

/**
 * Created by dnizard on 25/04/2017.
 */

public class UploadActivity extends AppCompatActivity {

    public String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Appsee.start("ca29b14487ac4c8e843ecb90c54c413f");

        Thread.setDefaultUncaughtExceptionHandler(new PiiicsExceptionHandler(this));

        setContentView(R.layout.activity_editor);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM";
        listImageFiles(path);

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        AWSHandler.get().uploadFileWithTitle(url, ts + "_" + UserInfo.getInt("id") + "_image.jpg");

    }

    private boolean listImageFiles(String path) {

        String[] list;
        File filePath = new File(path);///
        if (filePath.isDirectory()) {
            list = filePath.list();
            // This is a folder
            for (String file : list) {
                if (!listImageFiles(path + "/" + file))
                    return false;
            }
        } else if ((filePath.getAbsolutePath().endsWith(".jpg"))) {
            url = filePath.getAbsolutePath();
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
