package com.yxkang.android.exception;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yxkang.android.os.SystemProperties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * a class to handle the android application crash.
 * use {@link #getInstance()} method to get the instance,
 * then use {@link #init(Context)} method to init
 */
@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();
    private static final String BASE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static CrashHandler sHandler = null;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private long mTime = 1000;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (sHandler == null) {
            sHandler = new CrashHandler();
        }
        return sHandler;
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * @param time sleep time
     */
    public void setTime(long time) {
        mTime = time;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(mTime);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error : ", e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            Log.w(TAG, "exception is null");
            return false;
        }

        final String msg = getCrashInfo(ex);
        if (TextUtils.isEmpty(msg)) {
            Log.w(TAG, "exception msg is empty");
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                write(getSystemInfo(msg));
                Toast.makeText(mContext, "sorry for crash!", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
        return true;
    }

    private String getSystemInfo(String bundle) {
        StringBuilder builder = new StringBuilder();
        String stringValue;
        int intValue;
        stringValue = SystemProperties.get("ro.build.display.id");
        builder.append("id").append(" : ").append(stringValue).append("\n");
        stringValue = SystemProperties.get("ro.build.version.incremental");
        builder.append("incremental").append(" : ").append(stringValue).append("\n");
        intValue = SystemProperties.getInt("ro.build.version.sdk", 0);
        builder.append("sdk").append(" : ").append(intValue).append("\n");
        stringValue = SystemProperties.get("ro.build.version.release");
        builder.append("release").append(" : ").append(stringValue).append("\n");
        stringValue = SystemProperties.get("ro.build.date");
        builder.append("date").append(" : ").append(stringValue).append("\n");
        stringValue = SystemProperties.get("ro.product.name");
        builder.append("name").append(" : ").append(stringValue).append("\n");
        stringValue = SystemProperties.get("ro.product.manufacturer");
        builder.append("manufacturer").append(" : ").append(stringValue).append("\n");
        builder.append(bundle).append("\n");
        return builder.toString();
    }

    private String getCrashInfo(Throwable ex) {
        StringBuilder builder = new StringBuilder();
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        builder.append(ex.getLocalizedMessage()).append("\n");
        builder.append(info.toString()).append("\n");
        return builder.toString();
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

    private void write(String msg) {
        String dirPath = BASE_DIR + File.separator + mContext.getPackageName();
        File dir = new File(dirPath, "crash");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name = getCurrentTime() + ".log";
        File log = new File(dir, name);
        try {
            FileWriter fr = new FileWriter(log);
            BufferedWriter br = new BufferedWriter(fr);
            br.write(msg);
            br.flush();
            br.close();
        } catch (IOException e) {
            Log.e(TAG, "Error : ", e);
        }
    }
}
