package com.yxkang.android.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.yxkang.android.os.SystemProperties;

/**
 * StatusBarManager
 */
public class StatusBarManager {

    public static final String TAG = "StatusBarManager";

    public static final String STATUS_BAR_HEIGHT = "status_bar_height";
    public static final String NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    public static final String CONFIG_SHOW_NAVIGATION_BAR = "config_showNavigationBar";
    public static final String TYPE_DIMEN = "dimen";
    public static final String TYPE_BOOL = "bool";
    public static final String VIRTUAL_KEY = "qemu.hw.mainkeys";

    private static int getStatusBarHeight(Activity activity) {
        int statusHeight;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField(STATUS_BAR_HEIGHT)
                        .get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "status_bar_height : " + statusHeight);
        return statusHeight;
    }

    private static int getSystemResourceIntValue(Activity activity, String name) {
        int result = 0;
        Resources res = activity.getResources();
        int resourceId = res.getIdentifier(name, TYPE_DIMEN, "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        Log.d(TAG, name + " : " + result);
        return result;
    }

    private static boolean getSystemResourceBoolValue(Activity activity, String name) {
        boolean result = false;
        Resources resources = activity.getResources();
        int resourcesId = resources.getIdentifier(name, TYPE_BOOL, "android");
        if (resourcesId > 0) {
            result = resources.getBoolean(resourcesId);
        }
        Log.d(TAG, name + " : " + result);
        return result;
    }

    public static boolean isHasNavigationBar(Activity activity) {
        boolean result = getSystemResourceBoolValue(activity, CONFIG_SHOW_NAVIGATION_BAR);
        String navBarOverride = SystemProperties.get(VIRTUAL_KEY);
        if (!TextUtils.isEmpty(navBarOverride)) {
            if (navBarOverride.equals("1")) result = false;
            else if (navBarOverride.equals("0")) result = true;
        }
        Log.d(TAG, VIRTUAL_KEY + " : " + result);
        return result;
    }

    public static void test(Activity activity) {
        getSystemResourceIntValue(activity, STATUS_BAR_HEIGHT);
        getSystemResourceIntValue(activity, NAVIGATION_BAR_HEIGHT);
        getSystemResourceBoolValue(activity, CONFIG_SHOW_NAVIGATION_BAR);
        isHasNavigationBar(activity);
    }

    public static void setStatusBar(Activity activity, View root) {
        setStatusBar(activity, root, 0);
    }

    public static void setStatusBar(Activity activity, View root, int paddingTop) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            if (root != null) {
                root.setPadding(0, getStatusBarHeight(activity) + paddingTop, 0, 0);
            }
        }
    }
}
