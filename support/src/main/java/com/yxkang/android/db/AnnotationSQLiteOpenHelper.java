package com.yxkang.android.db;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.Collection;

/**
 * Created by yexiaokang on 2016/2/1.
 */
public class AnnotationSQLiteOpenHelper extends SQLiteOpenHelper {

    private Class<?>[] classes;

    public AnnotationSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, Class<?>[] classes) {
        super(context, name, factory, version);
        this.classes = classes;
    }

    public AnnotationSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, Collection<Class<?>> collection) {
        super(context, name, factory, version);
        this.classes = new Class[collection.size()];
        collection.toArray(this.classes);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AnnotationSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler, Class<?>[] classes) {
        super(context, name, factory, version, errorHandler);
        this.classes = classes;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AnnotationSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler, Collection<Class<?>> collection) {
        super(context, name, factory, version, errorHandler);
        this.classes = new Class[collection.size()];
        collection.toArray(this.classes);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, classes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        dropTable(db, classes);
    }

    protected void debug(Class<?>[] classes) {
        TableUtil.createTable(null, classes);
    }

    protected void createTable(SQLiteDatabase db, Class<?>[] classes) {
        TableUtil.createTable(db, classes);
    }

    protected void deleteTable(SQLiteDatabase db, Class<?>[] classes) {
        TableUtil.deleteTable(db, classes);
    }

    protected void dropTable(SQLiteDatabase db, Class<?>[] classes) {
        TableUtil.dropTable(db, classes);
    }
}
