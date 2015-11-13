// IMediaScannerListener.aidl
package com.yxkang.android.media;

// Declare any non-default types here with import statements

interface IMediaScannerListener {

    void onMediaScannerConnected();

    void onScanCompleted(String path);

    void onMediaScannerDisConnected();

}
