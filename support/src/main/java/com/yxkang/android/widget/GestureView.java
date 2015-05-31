package com.yxkang.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.yxkang.android.R;
import com.yxkang.android.util.GestureHelper;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by fine on 2015/4/8.
 */
public class GestureView extends View {

    private static final String TAG = "GestureContentView";

    private Paint paintNormal;                // 绘制普通未点击的圆的画笔
    private Paint paintOnTouch;               // 绘制点击之后的圆的画笔
    private Paint paintInnerCycle;            // 绘制点击之后内部的实心圆的画笔
    private Paint paintLines;                 // 绘制直线的画笔
    private Paint paintKeyError;              // 绘制验证时错误的圆的画笔
    private Paint paintInnerError;            // 绘制验证时错误的内部的实心圆的画笔

    private GesturePoint[] points = null;     // 9宫格的9个点集合
    private boolean isDrawEnable = true;      // 是否允许绘制
    private ArrayList<Integer> linePaths = new ArrayList<>();   // 记录手势的路径
    @SuppressWarnings("FieldCanBeLocal")
    private int firstPointID, secondPointID;
    private boolean isVerify;
    private GestureTouchListener listener;
    private String user_pwd;

    private Canvas canvas;                    // 画布
    private Bitmap bitmap;                    // 位图

    /**
     * 不同状态下的色值
     */
    private int mNormalColor = Color.parseColor("#aaffffff");
    private int mOnTouchColor = Color.parseColor("#ff2db1e8");
    private int mLineColor = Color.parseColor("#ff2db1e8");
    private int mErrorColor = Color.parseColor("#ffff3030");

    private float mNormalWidth = 2;
    private float mOnTouchWidth = 2;
    private float mLineWidth = 5;
    private float mErrorWidth = 2;


    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Gesture, defStyleAttr, 0);
        mNormalColor = array.getColor(R.styleable.Gesture_normal_color, mNormalColor);
        mNormalWidth = array.getDimension(R.styleable.Gesture_normal_width, mNormalWidth);
        mOnTouchColor = array.getColor(R.styleable.Gesture_ontouch_color, mOnTouchColor);
        mOnTouchWidth = array.getDimension(R.styleable.Gesture_ontouch_width, mOnTouchWidth);
        mLineColor = array.getColor(R.styleable.Gesture_line_color, mLineColor);
        mLineWidth = array.getDimension(R.styleable.Gesture_line_width, mLineWidth);
        mErrorColor = array.getColor(R.styleable.Gesture_error_color, mErrorColor);
        mErrorWidth = array.getDimension(R.styleable.Gesture_error_width, mErrorWidth);
        array.recycle();
        initPaints();
    }

    public GestureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureView(Context context) {
        this(context, null);
    }

    /**
     * 初始化各种画笔
     */
    private void initPaints() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        BlurMaskFilter bmf = new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL);

        paintNormal = new Paint();
        paintNormal.setAntiAlias(true);
        paintNormal.setStyle(Paint.Style.STROKE);
        paintNormal.setStrokeWidth(mNormalWidth);
        paintNormal.setColor(mNormalColor);
        paintNormal.setMaskFilter(bmf);

        paintOnTouch = new Paint();
        paintOnTouch.setAntiAlias(true);
        paintOnTouch.setStyle(Paint.Style.STROKE);
        paintOnTouch.setStrokeWidth(mOnTouchWidth);
        paintOnTouch.setColor(mOnTouchColor);
        paintOnTouch.setMaskFilter(bmf);

        paintInnerCycle = new Paint();
        paintInnerCycle.setAntiAlias(true);
        paintInnerCycle.setStyle(Paint.Style.FILL);
        paintInnerCycle.setColor(mOnTouchColor);
        paintInnerCycle.setMaskFilter(bmf);

        paintLines = new Paint();
        paintLines.setAntiAlias(true);
        paintLines.setStyle(Paint.Style.STROKE);
        paintLines.setStrokeWidth(mLineWidth);
        paintLines.setColor(mLineColor);
        paintLines.setMaskFilter(bmf);

        paintKeyError = new Paint();
        paintKeyError.setAntiAlias(true);
        paintKeyError.setStyle(Paint.Style.STROKE);
        paintKeyError.setStrokeWidth(mErrorWidth);
        paintKeyError.setColor(mErrorColor);
        paintKeyError.setMaskFilter(bmf);

        paintInnerError = new Paint();
        paintInnerError.setAntiAlias(true);
        paintInnerError.setStyle(Paint.Style.FILL);
        paintInnerError.setColor(mErrorColor);
        paintInnerError.setMaskFilter(bmf);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int perSize = getWidth() / 6;
        if (points == null && perSize > 0) {
            points = new GesturePoint[9];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    GesturePoint point = new GesturePoint();
                    point.setId(i * 3 + j);
                    point.setCenterX(perSize * (2 * j + 1));
                    point.setCenterY(perSize * (2 * i + 1));
                    point.setRadius(perSize * 0.5f);
                    point.setOnTouch(false);
                    points[i * 3 + j] = point;
                }
            }

            Log.d(TAG, getWidth() + "/" + perSize);
            bitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);  // 设置位图的宽高
            canvas = new Canvas();
            canvas.setBitmap(bitmap);

            initPoints();
            invalidate();
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);  // 重新调整大小，让其为正方形
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);        // 把我们画好的图形绘制到界面画布上
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!isDrawEnable) {  // 处理结果的时候不允许绘制
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                firstPointID = getPointAt(event.getX(), event.getY());
                if (firstPointID != -1) {           // 用户按下的位置在某个点内，则认为选中了该点
                    if (!points[firstPointID].isOnTouched()) {   // 如果第一个点没有被画出来
                        canvas.drawCircle(points[firstPointID].getCenterX(), points[firstPointID].getCenterY(),
                                points[firstPointID].getRadius(), paintOnTouch);
                        canvas.drawCircle(points[firstPointID].getCenterX(), points[firstPointID].getCenterY(),
                                points[firstPointID].getRadius() / 4, paintInnerCycle);
                        points[firstPointID].setOnTouch(true);
                    }
                    linePaths.add(firstPointID);    // 把该点加入到路径中去
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                clearScreenAndDrawLines();       // 重绘图形

                secondPointID = getPointAt(event.getX(), event.getY());

                // 代表当前用户手指处于点与点之前，第一次按下的位置和移动的位置均在点的范围外
                if (firstPointID == -1 && secondPointID == -1) {
                    break;
                } else {
                    if (firstPointID == -1) {     // 第一次按下的位置在点的范围外，移动到了某个点的范围内，认为点击该点
                        if (!points[secondPointID].isOnTouched()) {  // 如果该点没有被画出来
                            canvas.drawCircle(points[secondPointID].getCenterX(), points[secondPointID].getCenterY(),
                                    points[secondPointID].getRadius(), paintOnTouch);
                            canvas.drawCircle(points[secondPointID].getCenterX(), points[secondPointID].getCenterY(),
                                    points[secondPointID].getRadius() / 4, paintInnerCycle);
                            points[secondPointID].setOnTouch(true);
                        }
                        linePaths.add(secondPointID);      // 把该点加入到路径中去

                        firstPointID = secondPointID;      // 赋值，把移动的那个点重新作为第一个点
                    } else if (secondPointID == -1) {     // 移动的位置在点的范围外，跟随移动的位置划线
                        GesturePoint boundaryPoint = GestureHelper.calculateBoundaryPoint(points[firstPointID], event.getX(), event.getY());
                        canvas.drawLine(boundaryPoint.getCenterX(), boundaryPoint.getCenterY(),
                                event.getX(), event.getY(), paintLines);
                    } else {             // 第一次按下的位置和移动的位置均在点的范围内
                        if (firstPointID != secondPointID) {      // 如果两个点不为同一点则连线，否则就略过

                            // 每次移动的时候就会重绘界面，此处仅仅只是描点，划线则交给重绘函数完成 clearScreenAndDrawLines()

                            // 处理两个点之间的点，该点也要加入到路径中
                            Integer between = getPointBetween2(firstPointID, secondPointID);
                            if (between != -1) {
                                if (!points[between].isOnTouched()) {
                                    canvas.drawCircle(points[between].getCenterX(), points[between].getCenterY(),
                                            points[secondPointID].getRadius(), paintOnTouch);
                                    canvas.drawCircle(points[between].getCenterX(), points[between].getCenterY(),
                                            points[secondPointID].getRadius() / 4, paintInnerCycle);
                                    points[between].setOnTouch(true);
                                }
                                linePaths.add(between);
                            }

                            Log.d(TAG, "middle : " + between);

                            if (!points[secondPointID].isOnTouched()) {    // 如果第二个点没有被画出来
                                canvas.drawCircle(points[secondPointID].getCenterX(), points[secondPointID].getCenterY(),
                                        points[secondPointID].getRadius(), paintOnTouch);
                                canvas.drawCircle(points[secondPointID].getCenterX(), points[secondPointID].getCenterY(),
                                        points[secondPointID].getRadius() / 4, paintInnerCycle);
                                points[secondPointID].setOnTouch(true);
                            }
                            linePaths.add(secondPointID);    // 把移动的点加入到路径中去，第一个点理论上已经加进去了

                            firstPointID = secondPointID;    // 赋值，把移动的那个点重新作为第一个点
                        }
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                clearScreenAndDrawLines();  // 重绘图形

                if (isVerify) {
                    if (listener != null) listener.onVerifyGesture(matchUserPwd());
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (Integer line : linePaths) {
                        sb.append(line);
                    }
                    if (listener != null) listener.onDrawGesture(sb.toString());
                }
                break;
        }
        return true;
    }

    /**
     * 初始化九宫格界面，绘制9个点
     */
    private void initPoints() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (GesturePoint point : points) {
            canvas.drawCircle(point.getCenterX(), point.getCenterY(), point.getRadius(), paintNormal);
            point.setOnTouch(false);
        }
    }

    /**
     * 获取当前位置在那个点的范围内
     *
     * @return 如果找到了该点的ID，没有则返回-1
     */
    private int getPointAt(float x, float y) {
        int id = -1;
        for (GesturePoint point : points) {
            if (point.isPointIn(x, y)) {
                id = point.getId();
                break;
            }
        }
        return id;
    }

    private int getPointBetween2(int pointS, int pointE) {
        int id = -1;
        int abs = Math.abs(pointE - pointS);
        if ((abs == 1) || (abs == 3)) {
            return id;
        }
        float x = (points[pointS].getCenterX() + points[pointE].getCenterX()) / 2;
        float y = (points[pointS].getCenterY() + points[pointE].getCenterY()) / 2;
        for (GesturePoint point : points) {
            if ((point.getId() == pointE) || (point.getId() == pointS)) continue;
            if (point.isPointIn(x, y)) {
                id = point.getId();
                break;
            }
        }
        return id;
    }

    /**
     * 清空屏幕的其他划痕，并且重绘已经保存的路径
     */
    private void clearScreenAndDrawLines() {
        initPoints();
        int first, second;        // 用来保存前后两个点的ID
        Iterator<Integer> lines = linePaths.iterator();
        if (lines.hasNext()) {
            first = lines.next();
            if (!points[first].isOnTouched()) {
                canvas.drawCircle(points[first].getCenterX(), points[first].getCenterY(),
                        points[first].getRadius(), paintOnTouch);
                canvas.drawCircle(points[first].getCenterX(), points[first].getCenterY(),
                        points[first].getRadius() / 4, paintInnerCycle);
                points[first].setOnTouch(true);
            }
            while (lines.hasNext()) {
                second = lines.next();  // 获取第二个点，绘制直线
                GesturePoint[] boundaryPoints = GestureHelper.calculateBoundaryPoints(points[first], points[second]);
                canvas.drawLine(boundaryPoints[0].getCenterX(), boundaryPoints[0].getCenterY(),
                        boundaryPoints[1].getCenterX(), boundaryPoints[1].getCenterY(), paintLines);
                if (!points[second].isOnTouched()) {
                    canvas.drawCircle(points[second].getCenterX(), points[second].getCenterY(),
                            points[second].getRadius(), paintOnTouch);
                    canvas.drawCircle(points[second].getCenterX(), points[second].getCenterY(),
                            points[second].getRadius() / 4, paintInnerCycle);
                    points[second].setOnTouch(true);
                }
                first = second;
            }
        }
        invalidate();
    }

    /**
     * 作为验证是用，绘制红色的路径改告诉用户
     */
    private void drawErrorPath() {
        initPoints();
        int first, second;        // 用来保存前后两个点的ID
        Iterator<Integer> lines = linePaths.iterator();
        if (lines.hasNext()) {
            first = lines.next();
            if (!points[first].isOnTouched()) {
                canvas.drawCircle(points[first].getCenterX(), points[first].getCenterY(),
                        points[first].getRadius(), paintKeyError);
                canvas.drawCircle(points[first].getCenterX(), points[first].getCenterY(),
                        points[first].getRadius() / 4, paintInnerError);
                points[first].setOnTouch(true);
            }
            while (lines.hasNext()) {
                second = lines.next();  // 获取第二个点，绘制直线
                GesturePoint[] boundaryPoints = GestureHelper.calculateBoundaryPoints(points[first], points[second]);
                canvas.drawLine(boundaryPoints[0].getCenterX(), boundaryPoints[0].getCenterY(),
                        boundaryPoints[1].getCenterX(), boundaryPoints[1].getCenterY(), paintKeyError);
                if (!points[second].isOnTouched()) {
                    canvas.drawCircle(points[second].getCenterX(), points[second].getCenterY(),
                            points[second].getRadius(), paintKeyError);
                    canvas.drawCircle(points[second].getCenterX(), points[second].getCenterY(),
                            points[second].getRadius() / 4, paintInnerError);
                    points[second].setOnTouch(true);
                }
                first = second;
            }
        }
        invalidate();
    }

    private boolean matchUserPwd() {
        StringBuilder sb = new StringBuilder();
        for (Integer line : linePaths) {
            sb.append(line);
        }
        return sb.toString().equals(user_pwd);
    }

    /**
     * 可获取绘图的图案编码和验证图案的结果
     *
     * @param isVerify 是否为验证图案
     * @param user_pwd 在验图案的时候传入的验证密码，如果不是验证图案可随意
     * @param listener 触摸监听器
     */
    public void setOnTouchListener(boolean isVerify, String user_pwd, GestureTouchListener listener) {
        this.isVerify = isVerify;
        this.user_pwd = user_pwd;
        this.listener = listener;
    }

    /**
     * 清空所绘画的图案
     *
     * @param delay 是否延迟一段时间后在清除所绘画的图案，该期间可用于显示错误的图案
     */
    public void clearDrawWithDelay(long delay) {
        if (delay > 0) {
            isDrawEnable = false;       // 此时暂时不允许绘制图形
            drawErrorPath();
        }
        postDelayed(new clearRunnable(), delay);
    }

    /**
     *
     */
    private class clearRunnable implements Runnable {
        @Override
        public void run() {
            linePaths.clear();          // 本次任务结束，清空路径
            clearScreenAndDrawLines();  // 重绘图形
            isDrawEnable = true;
        }
    }

    /**
     * Interface definition for a callback to be invoked when this view is on touch
     */
    public interface GestureTouchListener {

        /**
         * 对于首次设置密码后的回调
         *
         * @param pwd 返回用户设置的密码
         */
        void onDrawGesture(String pwd);

        /**
         * 校验用户输入的密码正确与否
         *
         * @param result 检验的结果
         */
        void onVerifyGesture(boolean result);
    }
}
