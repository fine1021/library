package com.yxkang.android.exception;

/**
 * CrashListener
 */
public interface CrashListener {

    void crashMessage(String message);

    void crashException(Throwable throwable);

    void afterCrash();
}
