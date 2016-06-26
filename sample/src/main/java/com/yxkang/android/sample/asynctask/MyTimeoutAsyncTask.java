package com.yxkang.android.sample.asynctask;

import com.yxkang.android.os.TimeoutAsyncTask;

/**
 * Created by yexiaokang on 2016/6/23.
 */
public class MyTimeoutAsyncTask extends TimeoutAsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        return null;
    }

    @Override
    protected long getTimeout() {
        return 3000;
    }
}
