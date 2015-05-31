package com.yxkang.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.yxkang.android.R;

/**
 * Created by fine on 2015/4/24.
 * based on girdview ,add the child view divider
 */
public class DividerGirdView extends GridView {

    private int mDividerColor = Color.parseColor("#ffc0c0c0");
    private float mDividerWidth = 1;
    private boolean mDividerExist = true;

    public DividerGirdView(Context context) {
        this(context, null);
    }

    public DividerGirdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DividerGirdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DividerGirdView, defStyleAttr, 0);
        mDividerColor = array.getColor(R.styleable.DividerGirdView_divider_color, mDividerColor);
        mDividerWidth = array.getDimension(R.styleable.DividerGirdView_divider_width, mDividerWidth);
        mDividerExist = array.getBoolean(R.styleable.DividerGirdView_divider_exist, mDividerExist);
        array.recycle();
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mDividerExist) {
            View localView = getChildAt(0);
            if (localView == null || localView.getWidth() == 0) return;
            int column = getWidth() / localView.getWidth();
            int childCount = getChildCount();
            Paint localPaint = new Paint();
            localPaint.setStyle(Paint.Style.STROKE);
            localPaint.setStrokeWidth(mDividerWidth);
            localPaint.setColor(mDividerColor);
            for (int i = 0; i < childCount; i++) {
                View cellView = getChildAt(i);
                if ((i + 1) % column == 0) {
                    canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
                } else if ((i + 1) > (childCount - (childCount % column))) {
                    canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
                } else {
                    canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
                    canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
                }
            }
            if (childCount % column != 0) {
                for (int j = 0; j < (column - childCount % column); j++) {
                    View lastView = getChildAt(childCount - 1);
                    canvas.drawLine(lastView.getRight() + lastView.getWidth() * j, lastView.getTop(), lastView.getRight() + lastView.getWidth() * j, lastView.getBottom(), localPaint);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
