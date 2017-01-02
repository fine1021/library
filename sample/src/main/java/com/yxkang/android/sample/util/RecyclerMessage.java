package com.yxkang.android.sample.util;

/**
 * Created by yexiaokang on 2016/12/21.
 */

@SuppressWarnings("ALL")
public final class RecyclerMessage {

    static final int FLAG_NONE = 0;
    static final int FLAG_IN_USE = 1;

    private RecyclerMessage next;
    private int flags = FLAG_NONE;

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
                m.flags = FLAG_NONE;    // clear in-use flag
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
        if (isInUse()) {
            return;
        }

        clearForRecycle();

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    boolean isInUse() {
        return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
    }

    void clearForRecycle() {
        flags = FLAG_IN_USE;
    }
}
