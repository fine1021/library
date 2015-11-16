package com.yxkang.android.media;

import android.net.Uri;

/**
 * Created by yexiaokang on 2015/11/13.
 */
public interface MediaScannerListener {

    void onMediaScannerConnected();

    void onScanCompleted(String path, Uri uri);

    void onMediaScannerDisConnected();

    void onScanOperationFinished();
}
