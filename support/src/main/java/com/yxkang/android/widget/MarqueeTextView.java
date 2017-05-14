package com.yxkang.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by fine on 2017/5/14.
 */

public class MarqueeTextView extends TextView implements Runnable {

    private static final String TAG = "MarqueeTextView";
    private static final boolean LOG_DEBUG = true;
    private static final int DEFAULT_DURATION = 50;
    private static final int DEFAULT_INTERVAL = 1;
    private int mDuration = DEFAULT_DURATION;
    private int mInterval = DEFAULT_INTERVAL;
    private boolean mForcedMarquee = false;
    private boolean mPerformMarquee = false;
    private final int mOriginalScrollX;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mOriginalScrollX = getScrollX();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPerformMarquee && !mForcedMarquee) {
            CharSequence text = getText();
            float textWith = 0;
            if (!TextUtils.isEmpty(text)) {
                textWith = getPaint().measureText(text, 0, text.length());
            }
            if (textWith <= getWidth()) {
                stopScroll();
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public void run() {
        final int scrollX = getScrollX();
        final int scrollY = getScrollY();
        final int width = getWidth();
        int marqueeScrollX = scrollX;
        marqueeScrollX -= mInterval;
        scrollTo(marqueeScrollX, scrollY);
        if (marqueeScrollX <= -width) {
            scrollTo(width, scrollY);
            return;
        }
        if (mPerformMarquee) {
            postDelayed(this, mDuration);
        } else {
            scrollTo(mOriginalScrollX, scrollY);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(this);
        stopScroll();
        super.onDetachedFromWindow();
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getInterval() {
        return mInterval;
    }

    public void setInterval(int interval) {
        mInterval = interval;
    }

    public void startScroll(boolean forcedMarquee) {
        mForcedMarquee = forcedMarquee;
        mPerformMarquee = true;
        removeCallbacks(this);
        post(this);
    }

    public void stopScroll() {
        mPerformMarquee = false;
    }
}
