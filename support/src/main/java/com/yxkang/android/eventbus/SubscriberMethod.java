package com.yxkang.android.eventbus;

import java.lang.reflect.Method;

/**
 * Created by yexiaokang on 2016/10/20.
 */

class SubscriberMethod {

    final int priority;
    final Class<?> eventType;
    final Method method;

    SubscriberMethod(int priority, Class<?> eventType, Method method) {
        this.priority = priority;
        this.eventType = eventType;
        this.method = method;
    }
}
