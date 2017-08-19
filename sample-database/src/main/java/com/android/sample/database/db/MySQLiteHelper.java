package com.android.sample.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.database.sqlite.AnnotationSQLiteHelper;
import android.util.Log;

/**
 * Created by yexiaokang on 2017/8/17.
 */

public class MySQLiteHelper extends AnnotationSQLiteHelper {

    private static final String TAG = "MySQLiteHelper";
    private static final String NAME = "user.db";
    private static final int VERSION = 1;
    private static final Class<?>[] CLASSES = new Class[]{User.class};

    public MySQLiteHelper(Context context) {
        super(context, NAME, null, VERSION, CLASSES);
        debug(CLASSES);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        Log.i(TAG, "onCreate: OK");
    }
}
