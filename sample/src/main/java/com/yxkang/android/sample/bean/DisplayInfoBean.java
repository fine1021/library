package com.yxkang.android.sample.bean;

import android.content.Context;

/**
 * Created by fine on 2015/8/11.
 */
public class DisplayInfoBean {

    private float density;
    private int densityDpi;
    private int heightPixels;
    private int widthPixels;
    private float scaledDensity;
    private float xdpi;
    private float ydpi;

    public DisplayInfoBean(Context context) {
        xdpi = context.getResources().getDisplayMetrics().xdpi;
        ydpi = context.getResources().getDisplayMetrics().ydpi;
        density = context.getResources().getDisplayMetrics().density;
        densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        heightPixels = context.getResources().getDisplayMetrics().heightPixels;
        scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("xdpi : " + xdpi + "\n");
        builder.append("ydpi : " + ydpi + "\n");
        builder.append("density : " + density + "\n");
        builder.append("densityDpi : " + densityDpi + "\n");
        builder.append("widthPixels : " + widthPixels + "\n");
        builder.append("heightPixels : " + heightPixels + "\n");
        builder.append("scaledDensity : " + scaledDensity + "\n");
        return builder.toString();
    }
}
