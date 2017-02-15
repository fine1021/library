package com.yxkang.android.eventbus;

/**
 * Created by yexiaokang on 2016/12/23.
 */

public class PendingPost {

    private static final int FLAG_NONE = 0;
    private static final int FLAG_IN_USE = 1;

    Object event;
    Subscription subscription;

    private int flags = FLAG_NONE;
    private PendingPost next;
    private static PendingPost sPool;
    private static final Object sPoolSync = new Object();
    private static final int MAX_POOL_SIZE = 100;
    private static int sPoolSize = 0;

    private PendingPost(Object event, Subscription subscription) {
        this.event = event;
        this.subscription = subscription;
    }

    static PendingPost obtain(Subscription subscription, Object event) {
        synchronized (sPoolSync) {
            if (sPool != null) {
                PendingPost m = sPool;
                sPool = m.next;
                m.next = null;
                sPoolSize--;

                m.subscription = subscription;
                m.event = event;
                return m;
            }
            return new PendingPost(event, subscription);
        }
    }

    void recycle() {
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

    private boolean isInUse() {
        return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
    }

    private void clearForRecycle() {
        flags = FLAG_IN_USE;
    }
}
