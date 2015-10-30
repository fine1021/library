package com.yxkang.android.view;

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

    private Paint paintNormal;
    private Paint paintOnTouch;
    private Paint paintInnerCycle;
    private Paint paintLines;
    private Paint paintKeyError;
    private Paint paintInnerError;

    private GesturePoint[] points = null;     // 9 points
    private boolean isDrawEnable = true;      // is allowed to draw
    private ArrayList<Integer> linePaths = new ArrayList<>();   // path of the gesture
    @SuppressWarnings("FieldCanBeLocal")
    private int firstPointID, secondPointID;
    private boolean isVerify;
    private GestureTouchListener listener;
    private String user_pwd;

    private Canvas canvas;
    private Bitmap bitmap;

    /**
     * default color values of different states
     */
    private int mNormalColor = Color.parseColor("#aaffffff");
    private int mOnTouchColor = Color.parseColor("#ff2db1e8");
    private int mLineColor = Color.parseColor("#ff2db1e8");
    private int mErrorColor = Color.parseColor("#ffff3030");

    /**
     * default width of different states
     */
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
     * init the paints
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
            bitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);   // set the size of bitmap
            canvas = new Canvas();
            canvas.setBitmap(bitmap);

            initPoints();
            invalidate();
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);     // Measure the height and width
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);        // draw the bitmap on the canvas
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!isDrawEnable) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                firstPointID = getPointAt(event.getX(), event.getY());
                if (firstPointID != -1) {
                    if (!points[firstPointID].isOnTouched()) {   // if the point has not been drawn
                        canvas.drawCircle(points[firstPointID].getCenterX(), points[firstPointID].getCenterY(),
                                points[firstPointID].getRadius(), paintOnTouch);
                        canvas.drawCircle(points[firstPointID].getCenterX(), points[firstPointID].getCenterY(),
                                points[firstPointID].getRadius() / 4, paintInnerCycle);
                        points[firstPointID].setOnTouch(true);
                    }
                    linePaths.add(firstPointID);    // add the point to path
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                clearScreenAndDrawLines();       // redraw

                secondPointID = getPointAt(event.getX(), event.getY());

                if (firstPointID == -1 && secondPointID == -1) {    // both the two points are on the outside
                    break;
                } else {
                    if (firstPointID == -1) {     // the first point is on the outside, but the second point is on the inside
                        if (!points[secondPointID].isOnTouched()) {  // if the point has not been drawn
                            canvas.drawCircle(points[secondPointID].getCenterX(), points[secondPointID].getCenterY(),
                                    points[secondPointID].getRadius(), paintOnTouch);
                            canvas.drawCircle(points[secondPointID].getCenterX(), points[secondPointID].getCenterY(),
                                    points[secondPointID].getRadius() / 4, paintInnerCycle);
                            points[secondPointID].setOnTouch(true);
                        }
                        linePaths.add(secondPointID);      // add the point to path

                        firstPointID = secondPointID;      // Assign the point
                    } else if (secondPointID == -1) {     //the first point is on the inside, but the second point is on the outside
                        GesturePoint boundaryPoint = GestureHelper.calculateBoundaryPoint(points[firstPointID], event.getX(), event.getY());
                        canvas.drawLine(boundaryPoint.getCenterX(), boundaryPoint.getCenterY(),
                                event.getX(), event.getY(), paintLines);
                    } else {             // both the two points are on the inside
                        if (firstPointID != secondPointID) {      // if the first point is not equal with the second

                            // Just to draw a point. call clearScreenAndDrawLines() method draw lines

                            // check the point between the two points
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

                            if (!points[secondPointID].isOnTouched()) {    // if the point has not been drawn
                                canvas.drawCircle(points[secondPointID].getCenterX(), points[secondPointID].getCenterY(),
                                        points[secondPointID].getRadius(), paintOnTouch);
                                canvas.drawCircle(points[secondPointID].getCenterX(), points[secondPointID].getCenterY(),
                                        points[secondPointID].getRadius() / 4, paintInnerCycle);
                                points[secondPointID].setOnTouch(true);
                            }
                            linePaths.add(secondPointID);    // add the point to path

                            firstPointID = secondPointID;    // Assign the point
                        }
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                clearScreenAndDrawLines();  // draw lines

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
     * init the 9 points
     */
    private void initPoints() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (GesturePoint point : points) {
            canvas.drawCircle(point.getCenterX(), point.getCenterY(), point.getRadius(), paintNormal);
            point.setOnTouch(false);
        }
    }

    /**
     * judge the given x/y is in which point of the nine points.
     * if found, return the id of the point, Otherwise return -1
     *
     * @param x x coordinates of a point
     * @param y y coordinates of a point
     * @return the id of the point
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

    /**
     * @param pointS the id of one point
     * @param pointE the id of another point
     * @return the id of the point, which is between the two points
     */
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
     * clear screen and draw the path lines
     */
    private void clearScreenAndDrawLines() {
        initPoints();
        int first, second;        // two points id
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
                second = lines.next();  // get the second point id, and draw line
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
     * draw the error path ,usually the color is red
     */
    private void drawErrorPath() {
        initPoints();
        int first, second;        // two points id
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
                second = lines.next();  // get the second point id, and draw line
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
     * set the listener
     *
     * @param isVerify if verify
     * @param user_pwd verify password
     * @param listener a callback
     */
    public void setOnTouchListener(boolean isVerify, String user_pwd, GestureTouchListener listener) {
        this.isVerify = isVerify;
        this.user_pwd = user_pwd;
        this.listener = listener;
    }

    /**
     * clear all the draws
     *
     * @param delay delay time
     */
    public void clearDrawWithDelay(long delay) {
        if (delay > 0) {
            isDrawEnable = false;
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
            linePaths.clear();          // clear the path
            clearScreenAndDrawLines();  // redraw
            isDrawEnable = true;
        }
    }

    /**
     * Interface definition for a callback to be invoked when this view is touched
     */
    public interface GestureTouchListener {

        /**
         * @param pwd password
         */
        void onDrawGesture(String pwd);

        /**
         * @param result the result of verify
         */
        void onVerifyGesture(boolean result);
    }
}
