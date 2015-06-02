package com.yxkang.android.util;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by fine on 2015/6/17.
 */
public class StatusBarManager {

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
                int i5 = Integer.parseInt(localClass.getField("status_bar_height")
                        .get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    public static void setStatusBar(Activity activity, View root) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            root.setPadding(0, getStatusBarHeight(activity), 0, 0);
        }
    }
}
