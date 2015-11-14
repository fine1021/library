package com.yxkang.android.media;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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

        private static final int CONNECTED = 0x1;
        private static final int COMPLETED = 0x2;
        private static final int DISCONNECTED = 0x3;

        private final Handler mScannerHandler;

        public ListenerTransport(MediaScannerListener listener) {
            this.listener = listener;

            mScannerHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    _handleMessage(msg);
                }
            };
        }

        @Override
        public void onMediaScannerConnected() throws RemoteException {
            mScannerHandler.sendEmptyMessage(CONNECTED);
        }

        @Override
        public void onScanCompleted(String path) throws RemoteException {
            Log.d(TAG, path);
            mScannerHandler.sendMessage(mScannerHandler.obtainMessage(COMPLETED, path));
        }

        @Override
        public void onMediaScannerDisConnected() throws RemoteException {
            mScannerHandler.sendEmptyMessage(DISCONNECTED);
        }

        private void _handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED:
                    listener.onMediaScannerConnected();
                    break;
                case COMPLETED:
                    listener.onScanCompleted(msg.obj.toString());
                    break;
                case DISCONNECTED:
                    listener.onMediaScannerDisConnected();
                    break;
            }
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
                mListeners.remove(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
