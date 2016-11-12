package com.yxkang.android.sample.media;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.yxkang.android.media.MediaScannerListener;
import com.yxkang.android.media.MediaScannerManager;
import com.yxkang.android.sample.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * MediaScannerService
 */
public class MediaScannerService extends IntentService {

    public static final int SCAN_FILE = 0;
    public static final int SCAN_DIR = 1;

    public static final String EXTRA_SCAN_TYPE = "scan_type";
    public static final String EXTRA_SCAN_PATH = "scan_path";

    private static final String TAG = "MediaScannerService";

    private MediaScannerManager scannerManager;
    private ScannerListener scannerListener;


    public MediaScannerService() {
        this("MediaScannerService");
    }

    public MediaScannerService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        scannerManager = new MediaScannerManager(this);
        scannerListener = new ScannerListener();
        scannerManager.registerMediaScannerListener(scannerListener);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int type = intent.getIntExtra(EXTRA_SCAN_TYPE, -1);
            String path = intent.getStringExtra(EXTRA_SCAN_PATH);
            if (TextUtils.isEmpty(path)) {
                return;
            }
            Log.i(TAG, "path = " + path);
            switch (type) {
                case SCAN_FILE:
                    scanFile(path, 2);
                    break;
                case SCAN_DIR:
                    scanDirectory(path);
                    break;
                default:
                    break;
            }
        }
    }

    private void scanFile(String path, int way) {
        scannerManager.scanFile(path, way);
    }

    private void scanDirectory(String path) {
        scannerManager.scanDirectory(path);
    }

    private class ScannerListener implements MediaScannerListener {

        @Override
        public void onMediaScannerConnected() {
            Log.i(TAG, "onMediaScannerConnected");
        }

        @Override
        public void onMediaScannerDisConnected() {
            Log.i(TAG, "onMediaScannerDisConnected");
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            if (!TextUtils.isEmpty(path)) {
                Log.i(TAG, path);
            }
        }

        @Override
        public void onScanOperationFinished() {
            Log.i(TAG, "onScanOperationFinished");
            scannerManager.unregisterMediaScannerListener(scannerListener);
            EventBus.getDefault().post(new MessageEvent(MessageEvent.SCAN_MEDIA_COMPLETE));
        }
    }
}
