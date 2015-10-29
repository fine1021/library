package com.yxkang.android.sample.media;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * MediaScannerService
 */
public class MediaScannerService extends IntentService {

    public static final int SCAN_FILE = 0;
    public static final int SCAN_DIR = 1;

    public static final String EXTRA_SCAN_TYPE = "scan_type";
    public static final String EXTRA_SCAN_PATH = "scan_path";

    private static final String TAG = "MediaScannerService";

    private MediaScannerConnection connection = null;

    public MediaScannerService() {
        this("MediaScannerService");
    }

    public MediaScannerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int type = intent.getIntExtra(EXTRA_SCAN_TYPE, -1);
            String path = intent.getStringExtra(EXTRA_SCAN_PATH);
            if (TextUtils.isEmpty(path)) {
                return;
            }
            switch (type) {
                case SCAN_FILE:
                    scanFile(path, 1);
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
        File file = new File(path);
        switch (way) {
            case 1:
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
                sendBroadcast(intent);
                break;
            case 2:
                connection = new MediaScannerConnection(getApplicationContext(),
                        new MediaScannerConnection.MediaScannerConnectionClient() {
                            @Override
                            public void onMediaScannerConnected() {

                            }

                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i(TAG, path);
                                disconnect();
                            }
                        });
                connection.connect();
                connection.scanFile(file.getAbsolutePath(), null);
                break;
        }
    }

    private void disconnect() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
    }

    private void scanDirectory(String path) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            sendBroadcast(intent);
        } else {
            File dir = new File(path);
            File[] files = dir.listFiles();
            if (files != null) {
                ArrayList<String> list = new ArrayList<>();
                for (File f : files) {
                    list.add(f.getAbsolutePath());
                }
                String[] paths = new String[list.size()];
                list.toArray(paths);
                MediaScannerConnection.scanFile(getApplicationContext(), paths, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i(TAG, path);
                            }
                        });
            }
        }
    }
}
