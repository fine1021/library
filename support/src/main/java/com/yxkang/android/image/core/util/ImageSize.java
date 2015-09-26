package com.yxkang.android.image.core.util;

import android.content.Context;

/**
 * ImageSize
 */
public class ImageSize {

    private Context context;
    private int width;
    private int height;

    public ImageSize(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        int dpi = context.getResources().getDisplayMetrics().densityDpi;
        if (dpi < 300) {
            this.width = 200;
            this.height = 150;
        } else if (dpi < 400) {
            this.width = 250;
            this.height = 200;
        } else {
            this.width = 300;
            this.height = 250;
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
