package com.yxkang.android.eventbus;

/**
 * Created by yexiaokang on 2016/12/22.
 */
class AsyncPoster implements Runnable {

    private EventBus eventBus;
    private PendingPostQueue queue;

    AsyncPoster(EventBus eventBus) {
        this.eventBus = eventBus;
        this.queue = new PendingPostQueue();
    }

    void enqueue(Object event) {
        queue.offer(event);
        eventBus.getExecutorService().submit(this);
    }

    @Override
    public void run() {
        Object event = queue.poll();
        if (event == null) {
            return;
        }
        eventBus.notifySubscribers(event);
    }
}
