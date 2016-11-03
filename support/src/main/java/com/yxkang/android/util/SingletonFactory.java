package com.yxkang.android.util;

/**
 * Created by yexiaokang on 2016/11/3.
 * Singleton helper class for lazily initialization
 */

public abstract class SingletonFactory<T> implements Factory<T> {

    private volatile T instance;

    protected abstract T create();

    @Override
    public T get() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = create();
                }
            }
        }
        return instance;
    }
}
