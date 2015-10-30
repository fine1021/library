package com.yxkang.android.util;


import com.yxkang.android.view.GesturePoint;

/**
 * Created by fine on 2015/6/2. GestureHelper
 */
public class GestureHelper {

    private static GesturePoint temp = new GesturePoint();

    /**
	 * Positive direction of X axis is 0, contrarotate
     *
     * @param start start point
     * @param end   end point
     * @return  direction relative to start point
     */
    private static int calculateDirection(GesturePoint start, GesturePoint end) {
        if (start.getCenterY() == end.getCenterY()) {
            float value = start.getCenterX() - end.getCenterX();
            if (value > 0) return 4;
            else return 0;
        } else if (start.getCenterX() == end.getCenterX()) {
            float value = start.getCenterY() - end.getCenterY();
            if (value > 0) return 6;
            else return 2;
        } else if (start.getCenterX() < end.getCenterX()) {
            float value = start.getCenterY() - end.getCenterY();
            if (value > 0) return 7;
            else return 1;
        } else {
            float value = start.getCenterY() - end.getCenterY();
            if (value > 0) return 5;
            else return 3;
        }
    }

    /**
     * The starting point as the center of the circle
	 *
     * @param start  start point
     * @param x  end point X coordinates
     * @param y  end point Y coordinates
     * @return  the intersection of circle and line
     */
    public static GesturePoint calculateBoundaryPoint(GesturePoint start, float x, float y) {
        temp.setCenterX(x);
        temp.setCenterY(y);
        return calculateBoundaryPoint(start, temp);
    }

    /**
     * The starting point as the center of the circle
     *
     * @param start  start point
     * @param end  end point
     * @return  the intersection of circle and line
     */
    public static GesturePoint calculateBoundaryPoint(GesturePoint start, GesturePoint end) {
        GesturePoint point = new GesturePoint();
        int direction = calculateDirection(start, end);
        double sqrt, xabs = 0, yabs = 0;
        if (direction % 2 == 1) {
            sqrt = Math.sqrt((end.getCenterX() - start.getCenterX()) * (end.getCenterX() - start.getCenterX()) + (end.getCenterY() - start.getCenterY()) * (end.getCenterY() - start.getCenterY()));
            xabs = (start.getRadius() * Math.abs(end.getCenterX() - start.getCenterX())) / sqrt;
            yabs = (start.getRadius() * Math.abs(end.getCenterY() - start.getCenterY())) / sqrt;
        }
        switch (direction) {
            case 0:
                point.setCenterX(start.getCenterX() + start.getRadius());
                point.setCenterY(start.getCenterY());
                break;
            case 1:
                point.setCenterX(start.getCenterX() + (float) xabs);
                point.setCenterY(start.getCenterY() + (float) yabs);
                break;
            case 2:
                point.setCenterX(start.getCenterX());
                point.setCenterY(start.getCenterY() + start.getRadius());
                break;
            case 3:
                point.setCenterX(start.getCenterX() - (float) xabs);
                point.setCenterY(start.getCenterY() + (float) yabs);
                break;
            case 4:
                point.setCenterX(start.getCenterX() - start.getRadius());
                point.setCenterY(start.getCenterY());
                break;
            case 5:
                point.setCenterX(start.getCenterX() - (float) xabs);
                point.setCenterY(start.getCenterY() - (float) yabs);
                break;
            case 6:
                point.setCenterX(start.getCenterX());
                point.setCenterY(start.getCenterY() - start.getRadius());
                break;
            case 7:
                point.setCenterX(start.getCenterX() + (float) xabs);
                point.setCenterY(start.getCenterY() - (float) yabs);
                break;
            default:
                break;
        }
        return point;
    }

    /**
     * The starting point as the center of the circle, the end point as the center of the another circle
     *
     * @param start  start point
     * @param end   end point
     * @return  the intersection of circles and line, there are two points
     */
    public static GesturePoint[] calculateBoundaryPoints(GesturePoint start, GesturePoint end) {
        GesturePoint[] points = new GesturePoint[2];
        points[0] = calculateBoundaryPoint(start, end);
        points[1] = calculateBoundaryPoint(end, start);
        return points;
    }
}
