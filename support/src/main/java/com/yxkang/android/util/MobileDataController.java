package com.yxkang.android.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * MobileDataController
 */
@SuppressWarnings({"TryWithIdenticalCatches", "unused"})
public class MobileDataController {

    private static final String TAG = "MobileDataController";

    public static void setMobileDataStatus(Context context, boolean enabled) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setMobileDataEnabled(context, enabled);
        } else {
            setDataEnabled(context, enabled);
        }
    }

    public static boolean getMobileDataStatus(Context context) {

        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return getMobileDataEnabled(context);
        } else {
            return getDataEnabled(context);
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(context.getContentResolver(), "mobile_data", 0) == 1;
        } else {
            return Settings.Secure.getInt(context.getContentResolver(), "mobile_data", 0) == 1;
        }
    }

    private static void setMobileDataEnabled(Context context, boolean enabled) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class<?> cls = manager.getClass();
            Method method = cls.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            method.setAccessible(true);
            method.invoke(manager, enabled);
            Log.d(TAG, "setMobileDataEnabled = " + enabled);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static boolean getMobileDataEnabled(Context context) {
        boolean isOpen = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class<?> cls = manager.getClass();
            Method method = cls.getDeclaredMethod("getMobileDataEnabled", (Class[]) null);
            method.setAccessible(true);
            isOpen = (boolean) method.invoke(manager, (Object[]) null);
            Log.d(TAG, "getMobileDataEnabled = " + isOpen);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    /**
     * root command su -c 'service call phone 83 i32 1'
     *
     * @param context context
     * @param enabled if enable the mobile data enable
     */
    private static void setDataEnabled(Context context, boolean enabled) {
        boolean system = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) > 0;
        if (system) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                Class<?> cls = manager.getClass();
                Method method = cls.getDeclaredMethod("setDataEnabled", boolean.class);
                method.setAccessible(true);
                method.invoke(manager, enabled);
                Log.d(TAG, "setDataEnabled = " + enabled);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            if (RootUtil.exeRootCommand("")) {
                String transactionCode = getTransactionCode(context);
                StringBuilder command = new StringBuilder();
                command.append("su -c ");
                command.append("service call phone ");
                command.append(transactionCode).append(" ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    SubscriptionManager manager = SubscriptionManager.from(context);
                    int id = 0;
                    if (manager.getActiveSubscriptionInfoCount() > 0)
                        id = manager.getActiveSubscriptionInfoList().get(0).getSubscriptionId();
                    command.append("i32 ");
                    command.append(String.valueOf(id)).append(" ");
                }
                command.append("i32 ");
                command.append(enabled ? "1" : "0");
                RootUtil.exeRootCommand(command.toString());
            } else {
                Log.w(TAG, "only support a rooted phone !");
            }
        }
    }

    private static boolean getDataEnabled(Context context) {
        boolean isOpen = false;
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> cls = manager.getClass();
            Method method = cls.getDeclaredMethod("getDataEnabled", (Class[]) null);
            method.setAccessible(true);
            isOpen = (boolean) method.invoke(manager, (Object[]) null);
            Log.d(TAG, "getDataEnabled = " + isOpen);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    private static String getTransactionCode(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
            Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            Object ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
            Class ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());
            Class stub = ITelephonyClass.getDeclaringClass();
            Field field = stub.getDeclaredField("TRANSACTION_setDataEnabled");
            field.setAccessible(true);
            return String.valueOf(field.getInt(null));
        } catch (Exception e) {
            Log.e(TAG, "", e);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                return "86";
            else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
                return "83";
        }
        return "";
    }
}

