package com.yxkang.android.loop;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

/**
 * Created by fine on 2016/9/16.
 * <p>LoopHandler used for Multi-loop and Multi-action,
 * it also provides many convenient methods for Single-loop and Single-action</p>
 */
public final class LoopHandler {

    private static final String TAG = "LoopHandler";

    private final SparseBooleanArray array = new SparseBooleanArray();
    private final SparseArray<LoopAction> actions = new SparseArray<>();

    private LoopHandler() {
        array.clear();
        actions.clear();
    }

    /**
     * check the first action status is ready for next loop action
     *
     * @return {@code true} if ready, otherwise {@code false}
     */
    public boolean isPresent() {
        return isPresent(0);
    }

    /**
     * check the key action status is ready for next loop action
     *
     * @param key the key
     * @return {@code true} if ready, otherwise {@code false}
     */
    public boolean isPresent(int key) {
        LoopAction action = getAction(key);
        return action == null || action.getStatus() == LoopAction.Status.FINISHED;
    }

    /**
     * check the first loop flag
     *
     * @return loop flag value, {@code true} or {@code false}
     */
    public boolean isLoop() {
        return isLoop(0);
    }

    /**
     * check the loop of the key flag
     *
     * @param key the key
     * @return loop flag value, {@code true} or {@code false}
     */
    public boolean isLoop(int key) {
        return array.get(key);
    }

    /**
     * set the loop flag of the first loop
     *
     * @param loop the loop flag
     */
    public void setLoop(boolean loop) {
        setLoop(0, loop);
    }

    /**
     * set the loop flag of the loop of the key
     *
     * @param key  the key
     * @param loop the loop flag
     */
    public void setLoop(int key, boolean loop) {
        array.put(key, loop);
    }

    /**
     * get the loop action of the first loop
     *
     * @return the loop action, or {@code null} if not exists
     */
    @Nullable
    public LoopAction getAction() {
        return getAction(0);
    }

    /**
     * get the loop action of the key
     *
     * @param key the key
     * @return the loop action, or {@code null} if not exists
     */
    @Nullable
    public LoopAction getAction(int key) {
        return actions.get(key);
    }

    /**
     * set the first loop a new loop action
     *
     * @param action the loop action
     */
    public void setAction(LoopAction action) {
        setAction(0, action);
    }

    /**
     * set the loop of the key a new loop action
     *
     * @param key    the key
     * @param action the loop action
     */
    public void setAction(int key, LoopAction action) {
        actions.put(key, action);
    }

    /**
     * notify the first loop has been finished
     */
    public void loopFinished() {
        loopFinished(0);
    }

    /**
     * notify the loop of the key has been finished
     *
     * @param key the key
     */
    public void loopFinished(int key) {
        LoopAction action = getAction(key);
        if (action != null && action.isReady()) {
            action.preExecute();
            action.execute();
            action.postExecute();
            actions.delete(key);
        } else {
            if (action == null) {
                Log.v(TAG, "loopFinished: action == null");
            } else {
                Log.w(TAG, "loopFinished: action status = " + action.getStatus().name());
            }
        }
    }

    /**
     * sleep all the time, until the {@link #wake()} has been called by others
     *
     * @see #wait()
     */
    public void sleep() {
        synchronized (this) {
            try {
                wait();
            } catch (Exception e) {
                Log.e(TAG, "sleep", e);
            }
        }
    }

    /**
     * sleep the millis time, until the {@link #wake()} has been called by others or time up
     *
     * @param millis the maximum time to sleep in milliseconds.
     * @see #wait(long)
     */
    public void sleep(long millis) {
        synchronized (this) {
            try {
                wait(millis);
            } catch (Exception e) {
                Log.e(TAG, "sleep", e);
            }
        }
    }

    /**
     * wake all the threads that is waiting this object
     *
     * @see #notifyAll()
     */
    public void wake() {
        synchronized (this) {
            try {
                notifyAll();
            } catch (Exception e) {
                Log.e(TAG, "wake", e);
            }
        }
    }

    @NonNull
    static LoopHandler newInstance() {
        return new LoopHandler();
    }
}
