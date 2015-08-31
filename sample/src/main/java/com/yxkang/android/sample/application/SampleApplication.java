package com.yxkang.android.sample.application;

import android.app.Application;

import com.yxkang.android.exception.CrashHandler;

/**
 * SampleApplication
 */
public class SampleApplication extends Application {

    private static SampleApplication sApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        CrashHandler mCrashHandler = CrashHandler.getInstance();
        mCrashHandler.init(getApplicationContext());
        mCrashHandler.setTime(1000);
    }

    public static SampleApplication getInstance() {
        if (sApplication == null) {
            sApplication = new SampleApplication();
        }
        return sApplication;
    }
}
