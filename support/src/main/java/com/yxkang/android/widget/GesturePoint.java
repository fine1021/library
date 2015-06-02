package com.yxkang.android.widget;

/**
 * Created by fine on 2015/4/8.
 */
public class GesturePoint {

    private float centerX;     
    private float centerY;     
    private float radius;      
    private int id = -1;       
    private boolean onTouch;   

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOnTouched() {
        return onTouch;
    }

    public void setOnTouch(boolean onTouch) {
        this.onTouch = onTouch;
    }

    public boolean isPointIn(float x, float y) {
        double distance = Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
        return distance < radius;
    }

    @Override
    public String toString() {
        return "ID:" + getId() + " CenterX:" + getCenterX() + " CenterY:" + getCenterY() + " Radius:" + getRadius();
    }
}
