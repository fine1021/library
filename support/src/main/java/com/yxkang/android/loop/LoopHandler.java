package com.yxkang.android.loop;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by fine on 2016/9/16.
 */
public final class LoopHandler {

    private static final String TAG = "LoopHandler";

    private LoopAction action;

    private boolean loop = false;

    private LoopHandler() {
    }

    public boolean isPresent() {
        return action == null || action.getStatus() == LoopAction.Status.FINISHED;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public LoopAction getAction() {
        return action;
    }

    public void setAction(LoopAction action) {
        this.action = action;
    }

    public void loopFinished() {
        if (action != null && action.isReady()) {
            action.preExecute();
            action.execute();
            action.postExecute();
        }
    }

    public void sleep() {
        synchronized (this) {
            try {
                wait();
            } catch (Exception e) {
                Log.e(TAG, "sleep", e);
            }
        }
    }

    public void sleep(long millis) {
        synchronized (this) {
            try {
                wait(millis);
            } catch (Exception e) {
                Log.e(TAG, "sleep", e);
            }
        }
    }

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
