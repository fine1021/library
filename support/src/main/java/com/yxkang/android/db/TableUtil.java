package com.yxkang.android.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.yxkang.android.annotation.Column;
import com.yxkang.android.annotation.Table;

import java.lang.reflect.Field;
import java.sql.Blob;

/**
 * Created by yexiaokang on 2016/2/1.
 */
public class TableUtil {

    private static final String TAG = "TableUtil";
    // filed type
    private static final String INTEGER = "INTEGER";
    private static final String TEXT = "TEXT";
    private static final String BIGINT = "BIGINT";
    private static final String INT = "INT";
    private static final String FLOAT = "FLOAT";
    private static final String DOUBLE = "DOUBLE";
    private static final String BLOB = "BLOB";

    private static final int START = 1;
    private static final String SPACE = " ";
    private static final String CRLF = "\n";
    private static final String PRIMARY_KEY = "PRIMARY KEY";
    private static final String NOT_NULL = "NOT NULL";
    private static final String AUTOINCREMENT = "AUTOINCREMENT";

    private static final String CREATE = "CREATE TABLE IF NOT EXISTS";
    private static final String DROP = "DROP TABLE IF EXISTS";
    private static final String DELETE = "DELETE * FROM";

    private static final SparseArray<ColumnInfo> sColumns = new SparseArray<>();

    static void createTable(SQLiteDatabase db, Class<?>[] classes) {
        if (null != classes && classes.length > 0) {
            for (Class<?> clazz : classes) {
                createTable(db, clazz);
            }
        }
    }

    private static void createTable(SQLiteDatabase db, Class<?> clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = clazz.getAnnotation(Table.class);
            String tableName = TextUtils.isEmpty(table.name()) ? clazz.getSimpleName() : table.name();
            Log.i(TAG, "table = " + tableName);
            int[] fieldCount = getFieldCount(clazz);
            if (fieldCount[0] < 1) {
                throw new AnnotationSQLException("need more than one annotation field");
            }
            if (fieldCount[0] != sColumns.size()) {
                throw new AnnotationSQLException("more than one field has the same order");
            }
            if (fieldCount[1] > 1) {
                throw new AnnotationSQLException("only allow no more than one primary key");
            }
            StringBuilder builder = new StringBuilder();
            builder.append(CREATE).append(SPACE).append(tableName).append(SPACE).append("(");
            buildSQLStatement(builder);
            builder.deleteCharAt(builder.length() - 1).append(");");
            String sql = builder.toString();
            Log.i(TAG, "SQLStatement = " + sql);
            if (null != db) {
                db.execSQL(sql);
            }
        }
    }

    private static String fieldType(Field field) {
        if (field.getType() == String.class) {
            return TEXT;
        }
        if (field.getType() == int.class || field.getType() == Integer.class) {
            return INTEGER;
        }
        if (field.getType() == byte.class || field.getType() == Byte.class) {
            return INT;
        }
        if (field.getType() == short.class || field.getType() == Short.class) {
            return INT;
        }
        if (field.getType() == long.class || field.getType() == Long.class) {
            return BIGINT;
        }
        if (field.getType() == float.class || field.getType() == Float.class) {
            return FLOAT;
        }
        if (field.getType() == double.class || field.getType() == Double.class) {
            return DOUBLE;
        }
        if (field.getType() == Blob.class) {
            return BLOB;
        }
        return TEXT;
    }

    private static int[] getFieldCount(Class<?> clazz) {
        int annotationCount = 0;
        int primaryCount = 0;
        sColumns.clear();
        ColumnInfo info;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                annotationCount++;
                Column column = field.getAnnotation(Column.class);
                info = new ColumnInfo();
                info.name = TextUtils.isEmpty(column.name()) ? field.getName() : column.name();
                info.type = TextUtils.isEmpty(column.type()) ? fieldType(field) : column.type();
                info.order = column.order();
                info.primary = column.primary();
                info.notNull = column.notNull();
                sColumns.put(info.order, info);
                if (info.primary) primaryCount++;
            }
        }
        return new int[]{annotationCount, primaryCount};
    }

    private static void buildSQLStatement(StringBuilder builder) {
        int size = sColumns.size() + START;
        for (int i = START; i < size; i++) {
            ColumnInfo info = sColumns.get(i);
            if (info != null) {
                builder.append(CRLF).append(info.name).append(SPACE).append(info.type);
                if (info.notNull) {
                    builder.append(SPACE).append(NOT_NULL);
                }
                if (info.primary) {
                    builder.append(SPACE).append(PRIMARY_KEY);
                }
                builder.append(",");
            } else {
                throw new AnnotationSQLException("field order must be continuous");
            }
        }
        sColumns.clear();
    }

    static void deleteTable(SQLiteDatabase db, Class<?>[] classes) {
        if (null != classes && classes.length > 0) {
            for (Class<?> clazz : classes) {
                deleteTable(db, clazz);
            }
        }
    }

    private static void deleteTable(SQLiteDatabase db, Class<?> clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = clazz.getAnnotation(Table.class);
            String tableName = TextUtils.isEmpty(table.name()) ? clazz.getSimpleName() : table.name();
            String sql = DELETE + SPACE + tableName + ";";
            if (null != db) {
                db.execSQL(sql);
            }
        }
    }

    static void dropTable(SQLiteDatabase db, Class<?>[] classes) {
        if (null != classes && classes.length > 0) {
            for (Class<?> clazz : classes) {
                dropTable(db, clazz);
            }
        }
    }

    private static void dropTable(SQLiteDatabase db, Class<?> clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = clazz.getAnnotation(Table.class);
            String tableName = TextUtils.isEmpty(table.name()) ? clazz.getSimpleName() : table.name();
            String sql = DROP + SPACE + tableName + ";";
            if (null != db) {
                db.execSQL(sql);
            }
        }
    }

    public static boolean isColumnExist(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            result = (cursor != null && cursor.getColumnIndex(columnName) != -1);
        } catch (Exception e) {
            Log.e(TAG, "isColumnExist" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }
}
