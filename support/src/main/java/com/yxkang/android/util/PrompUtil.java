package com.yxkang.android.util;

/**
 * @author yexiaokang
 */
@SuppressWarnings("ALL")
public class PrompUtil {

    private static boolean Debug = true;

    public static void InitMode(boolean debug) {
        Debug = debug;
    }

    public static final class Log {

        public static void d(String TAG, String msg) {
            if (Debug) {
                android.util.Log.d(TAG, msg);
            }
        }

        public static void i(String TAG, String msg) {
            if (Debug) {
                android.util.Log.i(TAG, msg);
            }
        }

        public static void e(String TAG, String msg) {
            android.util.Log.e(TAG, msg);
        }

        public static void w(String TAG, String msg) {
            android.util.Log.w(TAG, msg);
        }
    }

}
