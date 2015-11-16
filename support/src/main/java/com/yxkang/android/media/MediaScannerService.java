package com.yxkang.android.media;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by yexiaokang on 2015/11/13.
 */
class MediaScannerService extends IMediaScannerService.Stub {

    private static final String TAG = "IMediaScannerService";

    private Context context;

    private int mTotal = 0;

    private int mCurrent = 0;

    private MediaScannerConnection connection = null;

    private final Object mMutex = new Object();

    private final RemoteCallbackList<IMediaScannerListener> callbackList = new RemoteCallbackList<>();

    public MediaScannerService(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void scanFile(String path, int way) throws RemoteException {
        File file = new File(path);
        switch (way) {
            case 1:
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
                context.sendBroadcast(intent);
                break;
            case 2:
                connection = new MediaScannerConnection(context, new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        notifyMediaScannerConnected();
                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(TAG, path);
                        notifyScanCompleted(path, uri);
                        try {
                            disconnect();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
                connection.connect();
                connection.scanFile(file.getAbsolutePath(), null);
                break;
        }
    }

    @Override
    public void scanDirectory(String path) throws RemoteException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            context.sendBroadcast(intent);
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
                mTotal = list.size();
                mCurrent = 1;
                MediaScannerConnection.scanFile(context, paths, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i(TAG, path);
                                notifyScanCompleted(path, uri);
                                if (mCurrent < mTotal) {
                                    mCurrent++;
                                } else {
                                    notifyScanOperationFinished();
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void disconnect() throws RemoteException {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            connection = null;
            notifyMediaScannerDisConnected();
            notifyScanOperationFinished();
        }
    }

    @Override
    public void registerMediaScannerListener(IMediaScannerListener listener) throws RemoteException {
        synchronized (mMutex) {
            callbackList.register(listener);
        }
    }

    @Override
    public void unregisterMediaScannerListener(IMediaScannerListener listener) throws RemoteException {
        synchronized (mMutex) {
            callbackList.unregister(listener);
        }
    }


    private void notifyMediaScannerConnected() {
        synchronized (mMutex) {
            int count = callbackList.beginBroadcast();
            for (int i = 0; i < count; i++) {
                try {
                    callbackList.getBroadcastItem(i).onMediaScannerConnected();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            callbackList.finishBroadcast();
        }
    }

    private void notifyScanCompleted(String path, Uri uri) {
        synchronized (mMutex) {
            int count = callbackList.beginBroadcast();
            for (int i = 0; i < count; i++) {
                try {
                    callbackList.getBroadcastItem(i).onScanCompleted(path, uri);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            callbackList.finishBroadcast();
        }
    }

    private void notifyMediaScannerDisConnected() {
        synchronized (mMutex) {
            int count = callbackList.beginBroadcast();
            for (int i = 0; i < count; i++) {
                try {
                    callbackList.getBroadcastItem(i).onMediaScannerDisConnected();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            callbackList.finishBroadcast();
        }
    }

    private void notifyScanOperationFinished() {
        synchronized (mMutex) {
            int count = callbackList.beginBroadcast();
            for (int i = 0; i < count; i++) {
                try {
                    callbackList.getBroadcastItem(i).onScanOperationFinished();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            callbackList.finishBroadcast();
        }
    }
}
