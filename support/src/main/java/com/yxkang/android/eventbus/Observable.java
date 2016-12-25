package com.yxkang.android.eventbus;

import java.util.ArrayList;

/**
 * Created by yexiaokang on 2016/12/23.
 */

class Observable {

    private static final Object sSubscriberSync = new Object();
    private ArrayList<Subscriber> subscribers;

    Observable() {
        subscribers = new ArrayList<>();
    }

    public void register(Subscriber subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("subscriber == null");
        }
        synchronized (sSubscriberSync) {
            if (!subscribers.contains(subscriber)) {
                subscribers.add(subscriber);
            }
        }
    }

    public void unregister(Subscriber subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("subscriber == null");
        }
        synchronized (sSubscriberSync) {
            subscribers.remove(subscriber);
        }
    }

    void notifySubscribers(Object event) {
        synchronized (sSubscriberSync) {
            for (Subscriber subscriber : subscribers) {
                subscriber.dispatchEvent(event);
            }
        }
    }

    public int size() {
        synchronized (sSubscriberSync) {
            return subscribers.size();
        }
    }
}
