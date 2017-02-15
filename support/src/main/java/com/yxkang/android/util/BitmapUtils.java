package com.yxkang.android.util;

import android.graphics.Bitmap;
import android.os.Build;

/**
 * Created by yexiaokang on 2017/2/13.
 */

public final class BitmapUtils {

    /**
     * get the bitmap byte count
     *
     * @param bitmap the bitmap
     * @return bitmap byte count
     */
    public static int getByteCount(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }
}
