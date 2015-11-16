// IMediaScannerListener.aidl
package com.yxkang.android.media;

// Declare any non-default types here with import statements
import android.net.Uri;

interface IMediaScannerListener {

    void onMediaScannerConnected();

    void onScanCompleted(String path, in Uri uri);

    void onMediaScannerDisConnected();

    void onScanOperationFinished();
}
