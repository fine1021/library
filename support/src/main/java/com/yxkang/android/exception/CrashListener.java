package com.yxkang.android.exception;

/**
 * CrashListener
 */
public interface CrashListener {

    void handleException(Throwable throwable);

    void beforeKillProcess();
}
