package com.yxkang.android.loop;

/**
 * Created by fine on 2016/9/16.
 */
public abstract class RepeatLoopAction extends LoopAction {

    @Override
    protected boolean isReady() {
        return true;
    }

    @Override
    protected void postExecute() {
        super.postExecute();
        setStatus(Status.PENDING);
    }
}
