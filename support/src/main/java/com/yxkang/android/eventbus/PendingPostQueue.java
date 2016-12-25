package com.yxkang.android.eventbus;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by yexiaokang on 2016/12/22.
 */
final class PendingPostQueue {

    private final ConcurrentLinkedQueue<Object> queue;

    PendingPostQueue() {
        this.queue = new ConcurrentLinkedQueue<Object>();
    }

    int size() {
        return queue.size();
    }

    boolean offer(Object o) {
        return queue.offer(o);
    }

    Object poll() {
        return queue.poll();
    }

    Object peek() {
        return queue.peek();
    }

    boolean isEmpty() {
        return queue.isEmpty();
    }
}
