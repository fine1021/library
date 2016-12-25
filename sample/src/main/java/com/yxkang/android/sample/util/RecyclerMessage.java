package com.yxkang.android.sample.util;

/**
 * Created by yexiaokang on 2016/12/21.
 */

@SuppressWarnings("ALL")
public final class RecyclerMessage {

    private RecyclerMessage next;

    private static RecyclerMessage sPool;
    private static final Object sPoolSync = new Object();
    private static final int MAX_POOL_SIZE = 50;
    private static int sPoolSize = 0;

    public static RecyclerMessage obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                RecyclerMessage m = sPool;
                sPool = m.next;
                m.next = null;
                sPoolSize--;
                return m;
            }
        }
        return new RecyclerMessage();
    }

    public static void evictAll() {
        synchronized (sPoolSync) {
            while (sPool != null) {
                RecyclerMessage m = sPool;
                sPool = m.next;
                m.next = null;      // Help the GC (for performance)
                sPoolSize--;
            }
        }
    }

    public void recycle() {
        clearForRecycle();

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    private void clearForRecycle() {

    }
}
