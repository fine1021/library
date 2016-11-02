package com.yxkang.android.util;

import com.yxkang.android.base.Preconditions;

/**
 * A {@link Provider} implementation that memorizes the result of another {@link Provider} using
 * the double-checked lock pattern.
 */

@SuppressWarnings({"unchecked", "WeakerAccess"})
public final class Singleton<T> implements Provider<T> {

    private static final Object UNINITIALIZED = new Object();

    private volatile Provider<T> provider;
    private volatile Object instance = UNINITIALIZED;

    private Singleton(Provider<T> provider) {
        this.provider = provider;
    }

    @Override
    public T get() {
        Object result = instance;
        if (result == UNINITIALIZED) {
            synchronized (this) {
                result = instance;
                if (result == UNINITIALIZED) {
                    result = provider.get();
                    Object currentInstance = instance;
                    if (currentInstance != UNINITIALIZED && currentInstance != result) {
                        throw new IllegalStateException("provider was invoked recursively returning "
                                + "different results: " + currentInstance + " & " + result);
                    }
                    instance = result;
                    /* Null out the reference to the provider. We are never going to need it again, so we
                     * can make it eligible for GC. */
                    provider = null;
                }
            }
        }
        return (T) result;
    }

    /**
     * return a {@link Provider} that caches the value from the given delegate provider
     *
     * @param delegate the delegate provider
     * @param <T>      the class model
     * @return a {@link Provider}
     */
    public static <T> Provider<T> provider(Provider<T> delegate) {
        Preconditions.checkNotNull(delegate);
        if (delegate instanceof Singleton) {
            return delegate;
        }
        return new Singleton<>(delegate);
    }
}
