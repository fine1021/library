package com.yxkang.android.sample.db;

import android.content.Context;

import com.yxkang.android.db.AnnotationSQLiteOpenHelper;

/**
 * Created by yexiaokang on 2016/2/1.
 */
class DatabaseHelper extends AnnotationSQLiteOpenHelper {

    private static final String NAME = "test.db";
    private static final int VERSION = 1;
    private static final Class<?>[] CLASSES = new Class[]{SettingsInfo.class};

    public DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION, CLASSES);
        debug(CLASSES);
    }
}
