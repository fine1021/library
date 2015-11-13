package com.yxkang.android.media;

/**
 * Created by yexiaokang on 2015/11/13.
 */
public interface MediaScannerListener {

    void onMediaScannerConnected();

    void onScanCompleted(String path);

    void onMediaScannerDisConnected();
}
