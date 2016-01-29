package com.yxkang.android.sample.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.yxkang.android.exception.CrashHandler;
import com.yxkang.android.exception.CrashListenerAdapter;
import com.yxkang.android.sample.MainActivity;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SampleApplication
 */
public class SampleApplication extends Application {

    private static final String LOG_TAG = "TrackApplication";
    private static SampleApplication sApplication = null;
    private final ConcurrentHashMap<String, WeakReference<? extends Activity>> map = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private ActivityLifecycle lifecycle = new ActivityLifecycle();
    private MemoryTimeTask task = new MemoryTimeTask();
    private Timer timer = new Timer(true);

    @Override

    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "Application onCreate");
        sApplication = this;
        map.clear();
        registerActivityLifecycleCallbacks(lifecycle);
        CrashHandler mCrashHandler = CrashHandler.getInstance();
        mCrashHandler.init(getApplicationContext());
        mCrashHandler.setStoreFile(true);
        mCrashHandler.setCrashListener(new CrashListenerAdapter() {
            @Override
            public void beforeKillProcess() {
                super.beforeKillProcess();
                Log.i(LOG_TAG, Runtime.getRuntime().totalMemory() + "/" + Runtime.getRuntime().maxMemory() + "/" + Runtime.getRuntime().freeMemory());
                unregisterActivityLifecycleCallbacks(lifecycle);
                finishAllActivities();
                startMainActivity();
            }
        });
    }

    public static SampleApplication getInstance() {
        if (sApplication == null) {
            sApplication = new SampleApplication();
        }
        return sApplication;
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void startTimeTask() {
        timer.schedule(task, 1000, 60 * 1000);
        Log.i(LOG_TAG, "startTimeTask");
    }

    public void stopTimeTask() {
        timer.cancel();
        Log.i(LOG_TAG, "stopTimeTask");
    }

    public void put(String key, WeakReference<? extends Activity> weakReference) {
        lock.lock();
        try {
            map.put(key, weakReference);
        } finally {
            lock.unlock();
        }
    }

    public void remove(String key) {
        lock.lock();
        try {
            map.remove(key);
        } finally {
            lock.unlock();
        }
    }

    public void finishAllActivities() {
        lock.lock();
        try {
            for (Map.Entry<String, WeakReference<? extends Activity>> stringWeakReferenceEntry : map.entrySet()) {
                WeakReference<? extends Activity> activity = stringWeakReferenceEntry.getValue();
                if (activity.get() != null) {
                    activity.get().finish();
                }
            }
            map.clear();
        } finally {
            lock.unlock();
        }
    }

    private class ActivityLifecycle implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.i(LOG_TAG, "onActivityCreated = " + activity.getLocalClassName());
            if (!map.containsKey(activity.getLocalClassName())) {
                WeakReference<Activity> weakReference = new WeakReference<>(activity);
                put(activity.getLocalClassName(), weakReference);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.i(LOG_TAG, "onActivityStarted = " + activity.getLocalClassName());
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.i(LOG_TAG, "onActivityResumed = " + activity.getLocalClassName());
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.i(LOG_TAG, "onActivityPaused = " + activity.getLocalClassName());
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.i(LOG_TAG, "onActivityStopped = " + activity.getLocalClassName());
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.i(LOG_TAG, "onActivitySaveInstanceState = " + activity.getLocalClassName());
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(LOG_TAG, "onActivityDestroyed = " + activity.getLocalClassName());
            if (map.containsKey(activity.getLocalClassName())) {
                remove(activity.getLocalClassName());
            }
        }
    }

    private class MemoryTimeTask extends TimerTask {

        @Override
        public void run() {
            Log.i(LOG_TAG, Runtime.getRuntime().totalMemory() + "/" + Runtime.getRuntime().maxMemory() + "/" + Runtime.getRuntime().freeMemory());
        }
    }
}
