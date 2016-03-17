package com.yxkang.android.sample.bean;

/**
 * Created by yexiaokang on 2016/3/17.
 */
public class MessageEvent {

    public static final int LOAD_MEDIA_INFO = 0x01;

    public static final int SHOW_DIALOG = 0x02;

    public static final int DISMISS_DIALOG = 0x03;

    public final int eventType;

    public MessageEvent(int eventType) {
        this.eventType = eventType;
    }
}
