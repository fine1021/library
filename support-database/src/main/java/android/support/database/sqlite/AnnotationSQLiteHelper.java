package android.support.database.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.database.Session;
import android.support.database.core.SQLiteSession;
import android.support.database.util.TableUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yexiaokang on 2017/8/17.
 */

public abstract class AnnotationSQLiteHelper extends SQLiteOpenHelper {

    private Class<?>[] mClasses;
    private Session mSession;
    private final Lock mLock = new ReentrantLock();

    public AnnotationSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
                                  Class<?>[] classes) {
        super(context, name, factory, version);
        mClasses = classes;
    }

    public AnnotationSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
                                  DatabaseErrorHandler errorHandler, Class<?>[] classes) {
        super(context, name, factory, version, errorHandler);
        mClasses = classes;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableUtil.createTables(db, mClasses);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TableUtil.dropTables(db, mClasses);
    }

    public Session getSession() {
        final Lock lock = this.mLock;
        lock.lock();
        try {
            if (mSession == null) {
                mSession = new SQLiteSession(this);
            }
            return mSession;
        } finally {
            lock.unlock();
        }
    }

    protected final void debug(Class<?>[] classes) {
        TableUtil.createTables(null, classes);
    }
}
