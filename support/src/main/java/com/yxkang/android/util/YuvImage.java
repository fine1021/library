package com.yxkang.android.util;

/**
 * Created by yexiaokang on 2016/10/18.
 */

public final class YuvImage {

    public static void yuv420spRotate90(byte[] src, byte[] des, int width, int height) {
        int wh = width * height;
        int uvHeight = height / 2;
        // Rotate Y
        int k = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                des[k] = src[width * (height - j - 1) + i];
                k++;
            }
        }
        // Rotate UV
        for (int i = 0; i < width; i += 2) {
            for (int j = 0; j < uvHeight; j++) {
                des[k] = src[wh + width * (uvHeight - j - 1) + i];
                des[k + 1] = src[wh + width * (uvHeight - j - 1) + i + 1];
                k += 2;
            }
        }
    }

    public static void yuv420spRotate90Image(byte[] src, byte[] des, int width, int height) {
        int wh = width * height;
        // Rotate Y
        int k = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                des[k] = src[width * j + i];
                k++;
            }
        }
        // Rotate UV
        for (int i = 0; i < width; i += 2) {
            for (int j = 0; j < height / 2; j++) {
                des[k] = src[wh + width * j + i];
                des[k + 1] = src[wh + width * j + i + 1];
                k += 2;
            }
        }
    }
}
