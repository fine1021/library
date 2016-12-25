package com.yxkang.android.eventbus;

/**
 * Created by yexiaokang on 2016/12/22.
 */
final class BackgroundPoster implements Runnable {

    private EventBus eventBus;
    private PendingPostQueue queue;
    private volatile boolean executorRunning;

    BackgroundPoster(EventBus eventBus) {
        this.eventBus = eventBus;
        this.queue = new PendingPostQueue();
    }

    void enqueue(Object event) {
        synchronized (this) {
            queue.offer(event);
            if (!executorRunning) {
                executorRunning = true;
                eventBus.getExecutorService().submit(this);
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object event = queue.poll();
                if (event == null) {
                    synchronized (this) {
                        event = queue.poll();
                        if (event == null) {
                            executorRunning = false;
                            return;
                        }
                    }
                }
                eventBus.notifySubscribers(event);
            }
        } finally {
            executorRunning = false;
        }
    }
}
