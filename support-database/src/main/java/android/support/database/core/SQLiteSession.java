package android.support.database.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.database.Behaviour;
import android.support.database.Column;
import android.support.database.Session;
import android.support.database.sqlite.AnnotationSQLiteHelper;
import android.support.database.strategy.Delete;
import android.support.database.strategy.Insert;
import android.support.database.strategy.Query;
import android.support.database.strategy.Update;
import android.support.database.util.TableFetcher;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yexiaokang on 2017/8/18.
 */

public class SQLiteSession implements Session {

    private static final String TAG = "SQLiteSession";

    private final AnnotationSQLiteHelper mHelper;

    public SQLiteSession(AnnotationSQLiteHelper helper) {
        mHelper = helper;
    }

    @Override
    public <T> void insert(List<T> entries) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            if (entries != null && !entries.isEmpty()) {
                for (T entry : entries) {
                    insert(entry, Insert.NONE);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public <T> long insert(T entry) {
        return insert(entry, Insert.NONE);
    }

    @Override
    public <T> long insert(T entry, Behaviour behaviour) {
        final TableFetcher fetcher = TableFetcher.getInstance();
        android.support.database.Table table = fetcher.getTable(entry.getClass());
        if (table != null) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = getContentValues(entry, true);
            return Transform.INSERT.transform(db, table, behaviour, values);
        } else {
            return -1;
        }
    }

    @Override
    public <T> void replace(List<T> entries) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            if (entries != null && !entries.isEmpty()) {
                for (T entry : entries) {
                    insert(entry, Insert.REPLACE);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public <T> long replace(T entry) {
        return insert(entry, Insert.REPLACE);
    }

    @Override
    public <T> int delete(Class<T> clazz) {
        return delete(clazz, Delete.ALL);
    }

    @Override
    public <T> int delete(Class<T> clazz, Behaviour behaviour) {
        final TableFetcher fetcher = TableFetcher.getInstance();
        android.support.database.Table table = fetcher.getTable(clazz);
        if (table != null) {
            SQLiteDatabase db = getWritableDatabase();
            return Transform.DELETE.transform(db, table, behaviour);
        } else {
            return 0;
        }
    }

    @Override
    public <T> int update(T entry) {
        return update(entry, Update.PRIMARY_KEY);
    }

    @Override
    public <T> int update(T entry, Behaviour behaviour) {
        final TableFetcher fetcher = TableFetcher.getInstance();
        android.support.database.Table table = fetcher.getTable(entry.getClass());
        if (table != null) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = getContentValues(entry, true);
            return (int) Transform.UPDATE.transform(db, table, behaviour, values);
        } else {
            return -1;
        }
    }

    @Override
    public <T> List<T> query(Class<T> clazz) {
        return query(clazz, Query.ALL);
    }

    @Override
    public <T> List<T> query(Class<T> clazz, Behaviour behaviour) {
        final TableFetcher fetcher = TableFetcher.getInstance();
        android.support.database.Table table = fetcher.getTable(clazz);
        if (table != null) {
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor = Transform.QUERY.transform(db, table, behaviour);
            if (cursor != null) {
                List<T> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    try {
                        T entry = clazz.newInstance();
                        List<android.support.database.Column> columns = table.getColumns();
                        if (columns != null && !columns.isEmpty()) {
                            for (Column column : columns) {
                                String columnName = column.getName();
                                int columnIndex = cursor.getColumnIndex(columnName);
                                if (columnIndex >= 0) {
                                    setColumnValue(entry, column, cursor, columnIndex);
                                }
                            }
                        }
                        list.add(entry);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
                return list;
            }
        }
        return null;
    }

    @Override
    public <T> boolean exist(T entry) {
        final TableFetcher fetcher = TableFetcher.getInstance();
        android.support.database.Table table = fetcher.getTable(entry.getClass());
        if (table != null) {
            String tableName = table.getName();
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = getContentValues(entry, false);
            StringBuilder sql = new StringBuilder(120);
            int setValuesSize = values.size();
            String[] selectionArgs = new String[setValuesSize];
            int i = 0;
            for (String colName : values.keySet()) {
                sql.append((i > 0) ? " AND " : "");
                sql.append(colName);
                selectionArgs[i++] = values.getAsString(colName);
                sql.append("=?");
            }
            String selection = sql.toString();
            Cursor cursor = db.query(tableName, null, selection, selectionArgs, null, null, null);
            boolean exist = false;
            if (cursor != null) {
                exist = cursor.getCount() > 0;
                cursor.close();
            }
            return exist;
        }
        return false;
    }

    @Override
    public <T> boolean exist(Class<T> clazz, Behaviour behaviour) {
        final TableFetcher fetcher = TableFetcher.getInstance();
        android.support.database.Table table = fetcher.getTable(clazz);
        if (table != null) {
            SQLiteDatabase db = getWritableDatabase();

        }
        return false;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return mHelper.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return mHelper.getReadableDatabase();
    }

    @Override
    public void close() {
        mHelper.close();
    }

    private static <T> void setColumnValue(T entry, Column column, Cursor cursor, int columnIndex) {
        Class<?> clazz = column.getType();
        Field field = column.getField();
        field.setAccessible(true);
        if (clazz == String.class) {
            try {
                String value = cursor.getString(columnIndex);
                field.set(entry, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == int.class || clazz == Integer.class) {
            try {
                int value = cursor.getInt(columnIndex);
                field.set(entry, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == float.class || clazz == Float.class) {
            try {
                float value = cursor.getFloat(columnIndex);
                field.set(entry, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            try {
                String value = cursor.getString(columnIndex);
                field.set(entry, ofBoolean(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == byte.class || clazz == Byte.class) {
            try {
                byte value = (byte) cursor.getInt(columnIndex);
                field.set(entry, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == short.class || clazz == Short.class) {
            try {
                short value = cursor.getShort(columnIndex);
                field.set(entry, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == long.class || clazz == Long.class) {
            try {
                long value = cursor.getLong(columnIndex);
                field.set(entry, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == double.class || clazz == Double.class) {
            try {
                double value = cursor.getDouble(columnIndex);
                field.set(entry, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == char.class || clazz == Character.class) {
            try {
                String value = cursor.getString(columnIndex);
                field.set(entry, TextUtils.isEmpty(value) ? null : value.charAt(0));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == Date.class) {
            try {
                String value = cursor.getString(columnIndex);
                long time = ofLong(value);
                if (time > 0) {
                    field.set(entry, new Date(time));
                } else {
                    field.set(entry, null);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            Log.w(TAG, "setColumnValue: clazz = " + clazz.toString());
            try {
                String value = cursor.getString(columnIndex);
                field.set(entry, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static <T> ContentValues getContentValues(T entry, boolean includeNullValue) {
        Class<?> clazz = entry.getClass();
        final TableFetcher fetcher = TableFetcher.getInstance();
        android.support.database.Table table = fetcher.getTable(clazz);
        ContentValues values = new ContentValues();
        List<android.support.database.Column> columns = table.getColumns();
        if (columns != null && !columns.isEmpty()) {
            for (Column column : columns) {
                getColumnValue(entry, column, values, includeNullValue);
            }
        }
        return values;
    }

    private static <T> void getColumnValue(T entry, Column column, ContentValues values,
                                           boolean includeNullValue) {
        Class<?> clazz = column.getType();
        String columnName = column.getName();
        Field field = column.getField();
        field.setAccessible(true);
        if (clazz == String.class) {
            try {
                String value = (String) field.get(entry);
                if (value != null) {
                    values.put(columnName, value);
                } else {
                    if (includeNullValue) {
                        values.put(columnName, "");
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == int.class) {
            try {
                int value = field.getInt(entry);
                values.put(columnName, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == float.class) {
            try {
                float value = field.getFloat(entry);
                values.put(columnName, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == boolean.class) {
            try {
                boolean value = field.getBoolean(entry);
                values.put(columnName, value ? "1" : "0");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == byte.class) {
            try {
                byte value = field.getByte(entry);
                values.put(columnName, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == short.class) {
            try {
                short value = field.getShort(entry);
                values.put(columnName, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == long.class) {
            try {
                long value = field.getLong(entry);
                values.put(columnName, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == double.class) {
            try {
                double value = field.getDouble(entry);
                values.put(columnName, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == char.class) {
            try {
                char value = field.getChar(entry);
                values.put(columnName, String.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == Integer.class) {
            try {
                Integer value = (Integer) field.get(entry);
                if (value != null) {
                    values.put(columnName, value);
                } else {
                    if (includeNullValue) {
                        values.put(columnName, 0);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == Float.class) {
            try {
                Float value = (Float) field.get(entry);
                if (value != null) {
                    values.put(columnName, value);
                } else {
                    if (includeNullValue) {
                        values.put(columnName, (float) 0);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == Boolean.class) {
            try {
                Boolean value = (Boolean) field.get(entry);
                if (value != null) {
                    values.put(columnName, value ? "1" : "0");
                } else {
                    if (includeNullValue) {
                        values.put(columnName, "0");
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == Byte.class) {
            try {
                Byte value = (Byte) field.get(entry);
                if (value != null) {
                    values.put(columnName, value);
                } else {
                    if (includeNullValue) {
                        values.put(columnName, (byte) 0);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == Short.class) {
            try {
                Short value = (Short) field.get(entry);
                if (value != null) {
                    values.put(columnName, value);
                } else {
                    if (includeNullValue) {
                        values.put(columnName, (short) 0);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == Long.class) {
            try {
                Long value = (Long) field.get(entry);
                if (value != null) {
                    values.put(columnName, value);
                } else {
                    if (includeNullValue) {
                        values.put(columnName, (long) 0);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == Double.class) {
            try {
                Double value = (Double) field.get(entry);
                if (value != null) {
                    values.put(columnName, value);
                } else {
                    if (includeNullValue) {
                        values.put(columnName, (double) 0);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == Character.class) {
            try {
                Character value = (Character) field.get(entry);
                if (value != null) {
                    values.put(columnName, String.valueOf(value));
                } else {
                    if (includeNullValue) {
                        values.put(columnName, String.valueOf('\u0000'));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (clazz == Date.class) {
            try {
                Date value = (Date) field.get(entry);
                if (value != null) {
                    values.put(columnName, String.valueOf(value.getTime()));
                } else {
                    if (includeNullValue) {
                        values.put(columnName, 0);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            Log.w(TAG, "getColumnValue: clazz = " + clazz.toString());
            try {
                String value = (String) field.get(entry);
                values.put(columnName, ofNullable(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean ofBoolean(String value) {
        return "1".equals(value) ||
                "true".equalsIgnoreCase(value) ||
                "yes".equalsIgnoreCase(value) ||
                "on".equalsIgnoreCase(value);
    }

    private static long ofLong(String value) {
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private static String ofNullable(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }
}
