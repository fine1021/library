package com.yxkang.android.util;


import com.yxkang.android.widget.GesturePoint;

/**
 * Created by fine on 2015/6/2. GestureHelper
 */
public class GestureHelper {

    private static GesturePoint temp = new GesturePoint();

    /**
     * 以起始点作为中心，建立直角坐标系，以X轴的正方向为0，按照逆时针方向一起加1
     *
     * @param start 起始点
     * @param end   终止点
     * @return 终止点相对于起始点的方位
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
     *
     * @param start  起始点
     * @param x  终止点的X坐标
     * @param y  终止点的Y坐标
     * @return  返回以起始点为圆心的圆，与起始点和终止点连线的交点
     */
    public static GesturePoint calculateBoundaryPoint(GesturePoint start, float x, float y) {
        temp.setCenterX(x);
        temp.setCenterY(y);
        return calculateBoundaryPoint(start, temp);
    }

    /**
     * @param start 起始点
     * @param end   终止点
     * @return 返回以起始点为圆心的圆，与起始点和终止点连线的交点
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
     * @param start 起始点
     * @param end   终止点
     * @return 返回以起始点为圆心的圆、以终止点为圆心的圆，与起始点和终止点连线的2个交点
     */
    public static GesturePoint[] calculateBoundaryPoints(GesturePoint start, GesturePoint end) {
        GesturePoint[] points = new GesturePoint[2];
        points[0] = calculateBoundaryPoint(start, end);
        points[1] = calculateBoundaryPoint(end, start);
        return points;
    }
}
