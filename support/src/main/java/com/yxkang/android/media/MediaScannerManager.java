package com.yxkang.android.media;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by yexiaokang on 2015/11/13.
 */
public class MediaScannerManager {

    private static final String TAG = "MediaScannerManager";

    private final HashMap<MediaScannerListener, ListenerTransport> mListeners = new HashMap<>();

    private IMediaScannerService scannerService;

    public MediaScannerManager(Context context) {
        scannerService = new MediaScannerService(context);
    }

    public void scanFile(String path, int way) {
        if (scannerService != null) {
            try {
                scannerService.scanFile(path, way);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void scanDirectory(String path) {
        if (scannerService != null) {
            try {
                scannerService.scanDirectory(path);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private class ListenerTransport extends IMediaScannerListener.Stub {

        private MediaScannerListener listener;


        ListenerTransport(MediaScannerListener listener) {
            this.listener = listener;
        }

        @Override
        public void onMediaScannerConnected() throws RemoteException {
            listener.onMediaScannerConnected();
        }

        @Override
        public void onScanCompleted(String path, Uri uri) throws RemoteException {
            listener.onScanCompleted(path, uri);
        }

        @Override
        public void onMediaScannerDisConnected() throws RemoteException {
            listener.onMediaScannerDisConnected();
        }

        @Override
        public void onScanOperationFinished() throws RemoteException {
            listener.onScanOperationFinished();
        }
    }

    public void registerMediaScannerListener(MediaScannerListener listener) {

        if (scannerService == null) {
            Log.e(TAG, "scannerService == null");
            return;
        }
        if (mListeners.containsKey(listener)) {
            Log.w(TAG, "listener has already been added");
            return;
        }

        ListenerTransport transport = new ListenerTransport(listener);
        try {
            scannerService.registerMediaScannerListener(transport);
            mListeners.put(listener, transport);
            Log.i(TAG, "registerMediaScannerListener");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void unregisterMediaScannerListener(MediaScannerListener listener) {

        if (scannerService == null) {
            Log.e(TAG, "scannerService == null");
            return;
        }

        ListenerTransport transport = mListeners.get(listener);
        if (transport != null) {
            try {
                scannerService.unregisterMediaScannerListener(transport);
                mListeners.remove(listener);
                Log.i(TAG, "unregisterMediaScannerListener");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
