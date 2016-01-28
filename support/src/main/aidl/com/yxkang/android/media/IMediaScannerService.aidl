// IMediaScannerService.aidl
package com.yxkang.android.media;

import com.yxkang.android.media.IMediaScannerListener;

// Declare any non-default types here with import statements

interface IMediaScannerService {

   void scanFile(String path, int way);

   void scanDirectory(String path);

   void registerMediaScannerListener(in IMediaScannerListener listener);

   void unregisterMediaScannerListener(in IMediaScannerListener listener);
}
