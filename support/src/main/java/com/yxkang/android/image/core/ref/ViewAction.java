package com.yxkang.android.image.core.ref;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * ViewAction
 */
public interface ViewAction {

    boolean setImageDrawable(Drawable drawable);

    boolean setImageBitmap(Bitmap bitmap);
}
