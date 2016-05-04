package com.yxkang.android.util;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yexiaokang on 2016/5/4.
 */
public class ThreadPoolFactory {

    private static final int nThreads = 5;
    private static final String TAG = "ThreadPoolFactory";
    private static volatile ExecutorService sCachedThreadPool;
    private static volatile ExecutorService sSingleThreadExecutor;
    private static volatile ExecutorService sFixedThreadPool;

    /**
     * get a cached thread pool, if the ExecutorService is shutdown it will create a new instance
     *
     * @return ExecutorService
     */
    public static ExecutorService getCachedThreadPool() {
        if (sCachedThreadPool == null) {
            synchronized (ThreadPoolFactory.class) {
                if (sCachedThreadPool == null) {
                    Log.i(TAG, "newCachedThreadPool");
                    sCachedThreadPool = Executors.newCachedThreadPool();
                }
            }
        }
        if (sCachedThreadPool.isShutdown()) {
            synchronized (ThreadPoolFactory.class) {
                Log.i(TAG, "the old thread pool is shutdown, newCachedThreadPool");
                sCachedThreadPool = Executors.newCachedThreadPool();
            }
        }
        return sCachedThreadPool;
    }

    /**
     * get a single thread executor, if the ExecutorService is shutdown it will create a new instance
     *
     * @return ExecutorService
     */
    public static ExecutorService getSingleThreadExecutor() {
        if (sSingleThreadExecutor == null) {
            synchronized (ThreadPoolFactory.class) {
                if (sSingleThreadExecutor == null) {
                    Log.i(TAG, "newSingleThreadExecutor");
                    sSingleThreadExecutor = Executors.newSingleThreadExecutor();
                }
            }
        }
        if (sSingleThreadExecutor.isShutdown()) {
            synchronized (ThreadPoolFactory.class) {
                Log.i(TAG, "the old thread pool is shutdown, newSingleThreadExecutor");
                sSingleThreadExecutor = Executors.newSingleThreadExecutor();
            }
        }
        return sSingleThreadExecutor;
    }

    /**
     * get a fixed thread pool, the number of threads is limited at 5, if the ExecutorService is shutdown it will create a new instance
     *
     * @return ExecutorService
     */
    public static ExecutorService getFixedThreadPool() {
        if (sFixedThreadPool == null) {
            synchronized (ThreadPoolFactory.class) {
                if (sFixedThreadPool == null) {
                    Log.i(TAG, "newFixedThreadPool");
                    sFixedThreadPool = Executors.newFixedThreadPool(nThreads);
                }
            }
        }
        if (sFixedThreadPool.isShutdown()) {
            synchronized (ThreadPoolFactory.class) {
                Log.i(TAG, "the old thread pool is shutdown, newFixedThreadPool");
                sFixedThreadPool = Executors.newFixedThreadPool(nThreads);
            }
        }
        return sFixedThreadPool;
    }
}
