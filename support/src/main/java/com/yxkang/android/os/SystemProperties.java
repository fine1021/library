package com.yxkang.android.os;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * use reflection to get the methods in {@code android.os.SystemProperties}
 */
@SuppressWarnings({"unused", "TryWithIdenticalCatches"})
public class SystemProperties {

    private static final String TAG = SystemProperties.class.getSimpleName();

    /**
     * Get the value for the given key.
     *
     * @param key the key to lookup
     * @return an empty string if the key isn't found
     */
    public static String get(String key) {
        String value = null;
        try {
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method getMethod = sysClass.getDeclaredMethod("get", String.class);
            value = (String) getMethod.invoke(sysClass, key);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d(TAG, key + " : " + value);
        return value;
    }

    /**
     * Get the value for the given key.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return if the key isn't found, return def if it isn't null, or an empty string otherwise
     */
    public static String get(String key, String def) {
        String value = null;
        try {
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method getMethod = sysClass.getDeclaredMethod("get", String.class, String.class);
            value = (String) getMethod.invoke(sysClass, key, def);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d(TAG, key + " : " + value);
        return value;
    }

    /**
     * Get the value for the given key, and return as an integer.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as an integer, or def if the key isn't found or cannot be parsed
     */
    public static int getInt(String key, int def) {
        int value = -1;
        try {
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method getMethod = sysClass.getDeclaredMethod("getInt", String.class, int.class);
            value = (int) getMethod.invoke(sysClass, key, def);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d(TAG, key + " : " + value);
        return value;
    }

    /**
     * Get the value for the given key, and return as a long.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a long, or def if the key isn't found or cannot be parsed
     */
    public static long getLong(String key, long def) {
        long value = -1;
        try {
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method getMethod = sysClass.getDeclaredMethod("getLong", String.class, long.class);
            value = (long) getMethod.invoke(sysClass, key, def);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d(TAG, key + " : " + value);
        return value;
    }

    /**
     * Get the value for the given key, returned as a boolean.
     * Values 'n', 'no', '0', 'false' or 'off' are considered false.
     * Values 'y', 'yes', '1', 'true' or 'on' are considered true.
     * (case sensitive).
     * If the key does not exist, or has any other value, then the default
     * result is returned.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a boolean, or def if the key isn't found or is
     * not able to be parsed as a boolean.
     */
    public static boolean getBoolean(String key, boolean def) {
        boolean value = false;
        try {
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method getMethod = sysClass.getDeclaredMethod("getBoolean", String.class, boolean.class);
            value = (boolean) getMethod.invoke(sysClass, key, def);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d(TAG, key + " : " + value);
        return value;
    }

    /**
     * Set the value for the given key.
     *
     * @param key the key to set
     * @param val the value to set
     */
    public static void set(String key, String val) {
        try {
            Class<?> sysClass = Class.forName("android.os.SystemProperties");
            Method setMethod = sysClass.getDeclaredMethod("set", String.class, String.class);
            setMethod.invoke(sysClass, key, val);
            Log.d(TAG, key + " : " + val);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
