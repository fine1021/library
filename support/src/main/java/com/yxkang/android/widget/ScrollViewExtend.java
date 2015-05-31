package com.yxkang.android.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 能够兼容ViewPager的ScrollView
 * Created by fine on 2014/10/18.
 */
public class ScrollViewExtend extends ScrollView {

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    public ScrollViewExtend(Context context) {
        super(context);
    }

    public ScrollViewExtend(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewExtend(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * 事件机制是从父View传向子View,先问是否需要截获(InterceptTouchEvent)
     * 再从子View向父View询问是否需要处理(TouchEvent)
     *
     * @param event 移动事件
     * @return true 表示截获事件
     */
    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = event.getX();
                yLast = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = event.getX();
                final float curY = event.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(event);
    }
}
