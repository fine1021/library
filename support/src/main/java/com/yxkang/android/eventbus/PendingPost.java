package com.yxkang.android.eventbus;

/**
 * Created by yexiaokang on 2016/12/23.
 */

public class PendingPost {

    Object event;
    Subscription subscription;


    private static final Object sPoolSync = new Object();
    private static final int MAX_POOL_SIZE = 100;
    private static int sPoolSize = 0;

    static PendingPost obtain(Subscription subscription, Object event){
        synchronized (sPoolSync) {
            if (sPool != null) {
                RecyclerMessage m = sPool;
                sPool = m.next;
                m.next = null;
                sPoolSize--;
                return m;
            }
        }
    }
}
