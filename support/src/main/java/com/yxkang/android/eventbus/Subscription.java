package com.yxkang.android.eventbus;

/**
 * Created by yexiaokang on 2016/10/20.
 */

final class Subscription {

    final Object subscriber;
    final SubscriberMethod subscriberMethod;
    volatile boolean active;

    public Subscription(Object subscriber, SubscriberMethod subscriberMethod) {
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
        this.active = true;
    }
}
