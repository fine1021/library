package com.yxkang.android.media;

import android.content.Context;
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

    class ListenerTransport extends IMediaScannerListener.Stub {

        private MediaScannerListener listener;

        public ListenerTransport(MediaScannerListener listener) {
            this.listener = listener;
        }

        @Override
        public void onMediaScannerConnected() throws RemoteException {
            listener.onMediaScannerConnected();
        }

        @Override
        public void onScanCompleted(String path) throws RemoteException {
            Log.d(TAG, path);
            listener.onScanCompleted(path);
        }

        @Override
        public void onMediaScannerDisConnected() throws RemoteException {
            listener.onMediaScannerDisConnected();
        }
    }

    public void registerMediaScannerListener(MediaScannerListener listener) {

        if (scannerService == null) {
            Log.e(TAG, "scannerService == null");
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
                mListeners.remove(transport);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
