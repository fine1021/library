package com.yxkang.android.eventbus;

import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yexiaokang on 2016/12/22.
 */
@SuppressWarnings("WeakerAccess")
public class EventBus extends Observable {

    private final MainPoster mainPoster;
    private final BackgroundPoster backgroundPoster;
    private final AsyncPoster asyncPoster;
    private final ExecutorService executorService;
    private final SubscriberMethodFinder subscriberMethodFinder;

    private static EventBus sEventBus;

    private EventBus() {
        mainPoster = new MainPoster(this, Looper.getMainLooper());
        backgroundPoster = new BackgroundPoster(this);
        asyncPoster = new AsyncPoster(this);
        executorService = Executors.newCachedThreadPool();
        subscriberMethodFinder = new SubscriberMethodFinder();
    }

    public static EventBus getInstance() {
        if (sEventBus == null) {
            synchronized (EventBus.class) {
                if (sEventBus == null) {
                    sEventBus = new EventBus();
                }
            }
        }
        return sEventBus;
    }

    public final void post(Object event) {
        post(event, ThreadMode.POST);
    }

    public final void postMainThread(Object event) {
        post(event, ThreadMode.MAIN);
    }

    public final void postBackground(Object event) {
        post(event, ThreadMode.BACKGROUND);
    }

    public final void postAsync(Object event) {
        post(event, ThreadMode.ASYNC);
    }

    public final void post(Object event, ThreadMode mode) {
        boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
        postToSubscriber(event, mode, isMainThread);
    }

    private void postToSubscriber(Object event, ThreadMode mode, boolean isMainThread) {
        switch (mode) {
            case POST:
                notifySubscribers(event);
                break;
            case MAIN:
                if (isMainThread) {
                    notifySubscribers(event);
                } else {
                    mainPoster.enqueue(event);
                }
                break;
            case BACKGROUND:
                if (isMainThread) {
                    backgroundPoster.enqueue(event);
                } else {
                    notifySubscribers(event);
                }
                break;
            case ASYNC:
                asyncPoster.enqueue(event);
                break;
            default:
                break;
        }
    }

    ExecutorService getExecutorService() {
        return executorService;
    }

    void invokeSubscriber(Subscription subscription, Object event) {
        try {
            subscription.subscriberMethod.method.invoke(subscription.subscriber, event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
