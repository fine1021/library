package com.yxkang.android.sample.application;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.yxkang.android.exception.CrashHandler;
import com.yxkang.android.exception.CrashListenerAdapter;

import org.apache.log4j.Level;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * SampleApplication
 */
public class SampleApplication extends Application {

    private static final String LOG_TAG = "TrackApplication";
    private static SampleApplication sApplication = null;
    private final ConcurrentHashMap<String, WeakReference<? extends Activity>> map = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private ActivityLifecycle lifecycle = new ActivityLifecycle();

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
            }
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            log4jConfigure(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                log4jConfigure(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
            } else {
                Log.w(LOG_TAG, "don't have WRITE_EXTERNAL_STORAGE permission");
            }
        }
    }

    /**
     * %m 输出代码中指定的消息<br/>
     * %p 输出优先级，即DEBUG，INFO，WARN，ERROR，FATAL；5表示宽度为5（即输出结果以5个字符宽度对齐），负号表示左对齐<br/>
     * %r 输出自应用启动到输出该log信息耗费的毫秒数<br/>
     * %c 输出所属的类目，通常就是所在类的全名<br/>
     * %t 输出产生该日志事件的线程名<br/>
     * %n 输出一个回车换行符，Windows平台为“rn”，Unix平台为“n”<br/>
     * %d 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyyy MMM dd HH:mm:ss,SSS}，输出类似：2002年10月18日 22：10：28，921<br/>
     * %l 输出日志事件的发生位置，包括类目名、发生的线程，以及在代码中的行数。<br/>
     *
     * @param sdcardExist sdcardExist
     */
    public synchronized void log4jConfigure(boolean sdcardExist) {
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                getPackageName() + File.separator + "log" + File.separator + "android-log4j.log";
        try {
            LogConfigurator logConfigurator = new LogConfigurator();
            logConfigurator.setResetConfiguration(true);
            logConfigurator.setFileName(fileName);
            logConfigurator.setRootLevel(Level.DEBUG);
            logConfigurator.setFilePattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [%t] [%c{2}]-[%L] %m%n");
            logConfigurator.setMaxFileSize(1024 * 1024 * 5);
            logConfigurator.setUseFileAppender(sdcardExist);
            logConfigurator.setUseLogCatAppender(true);
            logConfigurator.setImmediateFlush(true);
            logConfigurator.configure();
        } catch (Exception e) {
            Log.e(LOG_TAG, "log4jConfigure", e);
        }
    }

    public static SampleApplication instance() {
        if (sApplication == null) {
            sApplication = new SampleApplication();
        }
        return sApplication;
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
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(LOG_TAG, "onActivityDestroyed = " + activity.getLocalClassName());
            if (map.containsKey(activity.getLocalClassName())) {
                remove(activity.getLocalClassName());
            }
        }
    }
}
