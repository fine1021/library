package com.yxkang.android.sample.asynctask;

import android.util.Log;

import com.yxkang.android.os.AsyncTask;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fine on 2015/7/2.
 */
public class MyAsyncTask extends AsyncTask<Void, Void, Boolean> {

    public static final String TAG = "MyAsyncTask";
    private static final AtomicInteger mAsyncTaskID = new AtomicInteger(1);

    private int id = 0;

    public MyAsyncTask() {
        this.id = mAsyncTaskID.getAndIncrement();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        synchronized (MyAsyncTask.this) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, id + " execute ");
        }
        return false;
    }
}
