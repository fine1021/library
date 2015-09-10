package com.yxkang.android.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by fine on 2015/6/17.
 */
@SuppressWarnings("ALL")
public class ScreenUtil {

    /**
     * get the screen width
     *
     * @param context context
     * @return screen width
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * get the screen height
     *
     * @param context context
     * @return screen height
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * get the screen width and height
     *
     * @param activity activity
     * @return width and height
     */
    public static int[] getScreenWH(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return new int[]{dm.widthPixels, dm.heightPixels};
    }


    /**
     * transform the dip to px, according to the phone density
     *
     * @param context context
     * @param dpValue dp value
     * @return px value
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * transform the px to dip, according to the phone density
     *
     * @param context context
     * @param pxValue px value
     * @return dip value
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * transform the px to sp, according to the phone scaledDensity
     *
     * @param context context
     * @param pxValue the px value
     * @return sp value
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * transform the sp to px, according to the phone scaledDensity
     *
     * @param context context
     * @param spValue ap value
     * @return px value
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
