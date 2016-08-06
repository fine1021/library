package com.yxkang.android.sample.db;

import android.content.Context;

/**
 * Created by fine on 2016/8/6.
 */
public class DatabaseManager {

    private DatabaseHelper databaseHelper;

    public DatabaseManager(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }
}
