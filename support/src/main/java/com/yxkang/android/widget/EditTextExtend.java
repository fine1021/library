package com.yxkang.android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Created by yexiaokang on 2015/11/11.
 */
public class EditTextExtend extends EditText {

    @IntDef({DIRECTION_LEFT, DIRECTION_UP, DIRECTION_RIGHT, DIRECTION_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
    }

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_BOTTOM = 3;

    private Drawable[] drawables;
    private ArrayList<OnBoundaryDrawableClickListener> listeners;

    public EditTextExtend(Context context) {
        super(context);
    }

    public EditTextExtend(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextExtend(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        drawables = getCompoundDrawables();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (drawables == null) {
                    break;
                }
                float x = event.getX();
                float y = event.getY();
                int offset;
                Drawable drawable = drawables[0];
                if (drawable != null) {
                    offset = drawable.getBounds().width();
                    if (x < offset && x > 0) {
                        notifyBoundaryDrawableClick(DIRECTION_LEFT);
                    }
                }
                drawable = drawables[1];
                if (drawable != null) {
                    offset = drawable.getBounds().height();
                    if (y < offset && y > 0) {
                        notifyBoundaryDrawableClick(DIRECTION_UP);
                    }
                }
                drawable = drawables[2];
                if (drawable != null) {
                    offset = getWidth() - drawable.getBounds().width();
                    if (x > offset && x < getWidth()) {
                        notifyBoundaryDrawableClick(DIRECTION_RIGHT);
                    }
                }
                drawable = drawables[3];
                if (drawable != null) {
                    offset = getHeight() - drawable.getBounds().height();
                    if (y > offset && y < getHeight()) {
                        notifyBoundaryDrawableClick(DIRECTION_BOTTOM);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface OnBoundaryDrawableClickListener {
        /**
         * call back on boundary drawable click
         *
         * @param direction such as {@link #DIRECTION_UP}
         */
        void onBoundaryDrawableClick(@Direction int direction);
    }

    public void addBoundaryDrawableClickListener(OnBoundaryDrawableClickListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    public void removeBoundaryDrawableClickListener(OnBoundaryDrawableClickListener listener) {
        if (listeners != null) {
            if (listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }
    }

    private void notifyBoundaryDrawableClick(@Direction int direction) {
        if (listeners != null) {
            for (OnBoundaryDrawableClickListener listener : listeners) {
                listener.onBoundaryDrawableClick(direction);
            }
        }
    }
}
