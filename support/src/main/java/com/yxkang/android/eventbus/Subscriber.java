package com.yxkang.android.eventbus;

/**
 * Created by yexiaokang on 2016/12/22.
 */

public abstract class Subscriber {

    void dispatchEvent(Object event) {
        if (accept(event)) {
            onSubscribe(event);
        }
    }

    protected boolean accept(Object event) {
        return true;    // do nothing by default
    }

    protected abstract void onSubscribe(Object event);
}
