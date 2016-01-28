package com.yxkang.android.util;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by yexiaokang on 2016/1/28.
 */
@SuppressWarnings("unused")
public class ThreadManager {

    private static final int KEEP_ALIVE = 5;

    private volatile ExecutorService mThreadPool = Executors.newFixedThreadPool(KEEP_ALIVE);

    private static ThreadManager manager = null;

    private ThreadManager() {
    }

    public static ThreadManager getInstance() {
        if (manager == null) {
            manager = new ThreadManager();
        }
        return manager;
    }

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's {@code get} method will
     * return {@code null} upon <em>successful</em> completion.
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *                                    scheduled for execution
     * @throws NullPointerException       if the task is null
     */
    public Future<?> submit(Runnable task) {
        return mThreadPool.submit(task);
    }

    /**
     * Submits a value-returning task for execution and returns a
     * Future representing the pending results of the task. The
     * Future's {@code get} method will return the task's result upon
     * successful completion.
     * <br>
     * If you would like to immediately block waiting
     * for a task, you can use constructions of the form
     * {@code result = exec.submit(aCallable).get();}
     * <br>
     * <p>Note: The {@link Executors} class includes a set of methods
     * that can convert some other common closure-like objects,
     * for example, {@link java.security.PrivilegedAction} to
     * {@link Callable} form so they can be submitted.
     *
     * @param task the task to submit
     * @param <T>  data type
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *                                    scheduled for execution
     * @throws NullPointerException       if the task is null
     */
    public <T> Future<T> submit(Callable<T> task) {
        return mThreadPool.submit(task);
    }

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     * <br>
     * <p>This method does not wait for previously submitted tasks to
     * complete execution.
     */
    public void shutdown() {
        mThreadPool.shutdown();
    }

    /**
     * Attempts to stop all actively executing tasks, halts the
     * processing of waiting tasks, and returns a list of the tasks
     * that were awaiting execution.
     * <br>
     * <p>This method does not wait for actively executing tasks to
     * terminate.
     * <br>
     * <p>There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks.  For example, typical
     * implementations will cancel via {@link Thread#interrupt}, so any
     * task that fails to respond to interrupts may never terminate.
     *
     * @return list of tasks that never commenced execution
     */
    public List<Runnable> shutdownNow() {
        return mThreadPool.shutdownNow();
    }

    /**
     * Returns {@code true} if this executor has been shut down.
     *
     * @return {@code true} if this executor has been shut down
     */
    public boolean isShutdown() {
        return mThreadPool.isShutdown();
    }

    /**
     * Returns {@code true} if all tasks have completed following shut down.
     * Note that {@code isTerminated} is never {@code true} unless
     * either {@code shutdown} or {@code shutdownNow} was called first.
     *
     * @return {@code true} if all tasks have completed following shut down
     */
    public boolean isTerminated() {
        return mThreadPool.isTerminated();
    }

    /**
     * Reset the thread pool, if {@code isShutdown} or {@code isTerminated}, it will
     * create a new thread pool. otherwise do nothing
     */
    public void reset() {
        if (isShutdown() || isTerminated()) {
            mThreadPool = Executors.newFixedThreadPool(KEEP_ALIVE);
        }
    }
}
