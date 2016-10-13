package com.yxkang.android.media;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import java.io.File;

/**
 * Created by yexiaokang on 2015/11/13.
 */
class MediaScannerService extends IMediaScannerService.Stub {

    private Context context;

    private final Object mMutex = new Object();

    private final RemoteCallbackList<IMediaScannerListener> callbackList = new RemoteCallbackList<>();

    MediaScannerService(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void scanFile(String path, int way) throws RemoteException {
        switch (way) {
            case 1:
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File file = new File(path);
                intent.setData(Uri.fromFile(file));
                context.sendBroadcast(intent);
                notifyScanOperationFinished();
                break;
            case 2:
                scan(new String[]{path}, null);
                break;
        }
    }

    @Override
    public void scanDirectory(String path) throws RemoteException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            context.sendBroadcast(intent);
            notifyScanOperationFinished();
        } else {
            File dir = new File(path);
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                int length = files.length;
                String[] paths = new String[length];
                for (int i = 0; i < length; i++) {
                    paths[i] = files[i].getAbsolutePath();
                }
                scan(paths, null);
            } else {
                notifyScanOperationFinished();
            }
        }
    }

    private void scan(String[] paths, String[] mimeTypes) {
        ClientProxy clientProxy = new ClientProxy(paths, mimeTypes);
        MediaScannerConnection connection = new MediaScannerConnection(context, clientProxy);
        clientProxy.mConnection = connection;
        connection.connect();
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

    private class ClientProxy implements MediaScannerConnection.MediaScannerConnectionClient {
        final String[] mPaths;
        final String[] mMimeTypes;
        MediaScannerConnection mConnection;
        int mNextPath;

        ClientProxy(String[] paths, String[] mimeTypes) {
            mPaths = paths;
            mMimeTypes = mimeTypes;
        }

        public void onMediaScannerConnected() {
            notifyMediaScannerConnected();
            scanNextPath();
        }

        public void onScanCompleted(String path, Uri uri) {
            notifyScanCompleted(path, uri);
            scanNextPath();
        }

        void scanNextPath() {
            if (mNextPath >= mPaths.length) {
                mConnection.disconnect();
                notifyMediaScannerDisConnected();
                notifyScanOperationFinished();
                return;
            }
            String mimeType = mMimeTypes != null ? mMimeTypes[mNextPath] : null;
            mConnection.scanFile(mPaths[mNextPath], mimeType);
            mNextPath++;
        }
    }
}
