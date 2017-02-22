package com.yxkang.android.loop;

import android.util.SparseArray;

/**
 * Created by fine on 2016/9/15.
 */
public final class LoopManager {

    private static final SparseArray<LoopHandler> sLoopArray = new SparseArray<>();

    private LoopManager() {
    }

    public static LoopHandler myLoopHandler(int key) {
        LoopHandler handler = sLoopArray.get(key);
        if (handler == null) {
            synchronized (LoopManager.class) {
                handler = LoopHandler.newInstance();
                sLoopArray.put(key, handler);
            }
        }
        return handler;
    }

    public static void clearCache() {
        synchronized (LoopManager.class) {
            sLoopArray.clear();
        }
    }
}
