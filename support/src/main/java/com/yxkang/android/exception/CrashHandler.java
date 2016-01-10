package com.yxkang.android.exception;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

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
@SuppressWarnings("ResultOfMethodCallIgnored")
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();
    private static final String BASE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static CrashHandler sHandler = null;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private CrashListener listener = null;
    private boolean storeFile = true;
    private Context mContext;

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
     * save the error log to file
     *
     * @param storeFile <tt>true</tt> if save the log, otherwise will not save
     */
    public void setStoreFile(boolean storeFile) {
        this.storeFile = storeFile;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            if (listener != null) {
                listener.beforeKillProcess();
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

        if (listener != null) {
            listener.handleException(ex);
        }
        if (storeFile) {
            String message = getSystemInfo(getCrashInfo(ex));
            saveCrashInfo2File(message);
        }
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
        builder.append(info.toString()).append("\n");
        return builder.toString();
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

    private void saveCrashInfo2File(String msg) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
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
        } else {
            Log.w(TAG, "ExternalStorageState = " + Environment.getExternalStorageState());
        }
    }

    public void setCrashListener(CrashListener l) {
        this.listener = l;
    }

}
