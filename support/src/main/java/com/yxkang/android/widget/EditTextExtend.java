package com.yxkang.android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by yexiaokang on 2015/11/11.
 */
public class EditTextExtend extends EditText {

    private Drawable left, top, right, bottom;
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
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int offset;
        if (left != null) {
            offset = left.getBounds().width();
            if (x < offset && x > 0) {
                notifyBoundaryDrawableClick(0);
            }
        }
        if (top != null) {
            offset = top.getBounds().height();
            if (y < offset && y > 0) {
                notifyBoundaryDrawableClick(1);
            }
        }
        if (right != null) {
            offset = getWidth() - right.getBounds().width();
            if (x > offset && x < getWidth()) {
                notifyBoundaryDrawableClick(2);
            }
        }
        if (bottom != null) {
            offset = getHeight() - bottom.getBounds().height();
            if (y > offset && y < getHeight()) {
                notifyBoundaryDrawableClick(3);
            }
        }
        return super.onTouchEvent(event);
    }

    public interface OnBoundaryDrawableClickListener {
        /**
         * call back on boundary drawable click
         *
         * @param direction 0-left, 1-top, 2-right, 3-bottom
         */
        void onBoundaryDrawableClick(int direction);
    }

    public void addBoundaryDrawableClickListener(OnBoundaryDrawableClickListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    public void removeBoundaryDrawableClickListener(OnBoundaryDrawableClickListener listener) {
        if (listeners != null) {
            int i = listeners.indexOf(listener);
            if (i >= 0) {
                listeners.remove(i);
            }
        }
    }

    private void notifyBoundaryDrawableClick(int direction) {
        if (listeners != null) {
            for (OnBoundaryDrawableClickListener listener : listeners) {
                listener.onBoundaryDrawableClick(direction);
            }
        }
    }
}
