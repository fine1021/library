package com.yxkang.android.image.core.ref;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * RefView
 */
@SuppressWarnings("ALL")
public abstract class RefView<T extends View> implements ViewAction {

    public static final String WARN_CANT_SET_DRAWABLE = "Can't set a drawable into view. You should call ImageLoader on UI thread for it.";
    public static final String WARN_CANT_SET_BITMAP = "Can't set a bitmap into view. You should call ImageLoader on UI thread for it.";
    private static final String TAG = RefView.class.getSimpleName();
    private WeakReference<T> reference;

    public RefView(T reference) {
        this.reference = new WeakReference<T>(reference);
    }

    @Override
    public boolean setImageDrawable(Drawable drawable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            T t = reference.get();
            if (t != null) {
                setImageDrawable(drawable, t);
                return true;
            }
        } else {
            Log.e(TAG, WARN_CANT_SET_DRAWABLE);
        }
        return false;
    }

    @Override
    public boolean setImageBitmap(Bitmap bitmap) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            T t = reference.get();
            if (t != null) {
                setImageBitmap(bitmap, t);
                return true;
            }
        } else {
            Log.e(TAG, WARN_CANT_SET_BITMAP);
        }
        return false;
    }

    protected abstract void setImageDrawable(Drawable drawable, T reference);

    protected abstract void setImageBitmap(Bitmap bitmap, T reference);
}
