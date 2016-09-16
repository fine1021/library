package com.yxkang.android.loop;

/**
 * Created by fine on 2016/9/15.
 */
public abstract class LoopAction {

    /**
     * loop action status
     */
    private Status status;

    public LoopAction() {
        status = Status.PENDING;
    }

    /**
     * check the action status
     *
     * @return {@code true} if the current status is {@link Status#PENDING}, otherwise is {@code false}
     */
    protected boolean isReady() {
        return status == Status.PENDING;
    }

    /**
     * set the action status {@link Status#RUNNING}, used by {@link LoopHandler}
     */
    protected void preExecute() {
        status = Status.RUNNING;
    }

    /**
     * execute the action
     */
    public abstract void execute();

    /**
     * set the action status {@link Status#FINISHED}, used by {@link LoopHandler}
     */
    protected void postExecute() {
        status = Status.FINISHED;
    }

    /**
     * get the current action status
     *
     * @return the action status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * set the current action status
     *
     * @param status the action status
     */
    protected void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Indicates the current status of the action. Each status will be set only once
     * during the lifetime of a action.
     */
    public enum Status {
        /**
         * Indicates that the action has not been executed yet.
         */
        PENDING,
        /**
         * Indicates that the action is running.
         */
        RUNNING,
        /**
         * Indicates that the action has finished.
         */
        FINISHED
    }
}
