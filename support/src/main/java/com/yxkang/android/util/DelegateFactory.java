package com.yxkang.android.util;

import com.yxkang.android.base.Preconditions;

/**
 * Created by yexiaokang on 2016/11/2.
 * A {@link Factory} implementation that memorizes the result of another {@link Provider}
 */

public final class DelegateFactory<T> implements Factory<T> {

    private Provider<T> delegate;

    private DelegateFactory(Provider<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T get() {
        return delegate.get();
    }

    public static <T> Provider<T> provider(Provider<T> delegate) {
        Preconditions.checkNotNull(delegate);
        return new DelegateFactory<>(delegate);
    }
}
