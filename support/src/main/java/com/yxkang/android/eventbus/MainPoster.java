package com.yxkang.android.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by yexiaokang on 2016/12/22.
 */
final class MainPoster extends Handler {

    private EventBus eventBus;
    private PendingPostQueue queue;

    MainPoster(EventBus eventBus, Looper looper) {
        super(looper);
        this.eventBus = eventBus;
        this.queue = new PendingPostQueue();
    }

    void enqueue(Object event) {
        synchronized (this) {
            queue.offer(event);
            sendMessage(obtainMessage());
        }
    }

    @Override
    public void handleMessage(Message msg) {
        while (true) {
            Object event = queue.poll();
            if (event == null) {
                synchronized (this) {
                    event = queue.poll();
                    if (event == null) {
                        return;
                    }
                }
            }
            eventBus.notifySubscribers(event);
        }
    }
}
