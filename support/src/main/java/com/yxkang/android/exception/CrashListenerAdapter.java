package com.yxkang.android.exception;

/**
 * This adapter class provides empty implementations of the methods from {@link CrashListener}.
 * Any custom listener that cares only about a subset of the methods of this listener can
 * simply subclass this adapter class instead of implementing the interface directly.
 */
public abstract class CrashListenerAdapter implements CrashListener {

    @Override
    public void crashMessage(String message) {

    }

    @Override
    public void crashException(Throwable throwable) {

    }

    @Override
    public void afterCrash() {

    }
}
