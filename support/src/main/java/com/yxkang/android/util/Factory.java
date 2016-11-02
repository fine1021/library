package com.yxkang.android.util;

/**
 * Created by yexiaokang on 2016/11/2.
 * A {@link Provider} implementation that memorizes the result of another {@link Provider}
 */

public final class Factory<T> implements Provider<T> {

    private Provider<T> delegate;

    @Override
    public T get() {
        if (delegate == null) {
            throw new IllegalStateException("delegate provider is null");
        }
        return delegate.get();
    }

    public void setDelegatedProvider(Provider<T> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate provider is null");
        }
        if (this.delegate != null) {
            throw new IllegalStateException("delegate provider has been set");
        }
        this.delegate = delegate;
    }
}
