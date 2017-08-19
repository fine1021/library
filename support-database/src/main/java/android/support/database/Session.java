package android.support.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by yexiaokang on 2017/8/17.
 */

public interface Session {

    <T> void insert(List<T> entries);

    <T> long insert(T entry);

    <T> long insert(T entry, Behaviour behaviour);

    <T> void replace(List<T> entries);

    <T> long replace(T entry);

    <T> int delete(Class<T> clazz);

    <T> int delete(Class<T> clazz, Behaviour behaviour);

    <T> int update(T entry);

    <T> int update(T entry, Behaviour behaviour);

    <T> List<T> query(Class<T> clazz);

    <T> List<T> query(Class<T> clazz, Behaviour behaviour);

    <T> boolean exist(T entry);

    <T> boolean exist(Class<T> clazz, Behaviour behaviour);

    SQLiteDatabase getWritableDatabase();

    SQLiteDatabase getReadableDatabase();

    void close();
}
