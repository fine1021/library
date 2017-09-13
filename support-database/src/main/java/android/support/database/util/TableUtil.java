package android.support.database.util;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.database.Column;
import android.support.database.Table;
import android.support.database.log.Logger;
import android.support.database.log.StatusLogger;
import android.support.database.strategy.TableMonitor;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * TableUtil
 */

@SuppressWarnings("WeakerAccess")
public final class TableUtil {

    private static final Logger LOGGER = StatusLogger.getLogger();

    private static final String INTEGER = "INTEGER";
    private static final String REAL = "REAL";
    private static final String TEXT = "TEXT";
    private static final String BLOB = "BLOB";
    private static final String VARCHAR = "VARCHAR(16)";


    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String CRLF = "\n";
    private static final String PRIMARY_KEY = "PRIMARY KEY";
    private static final String NOT_NULL = "NOT NULL";
    private static final String AUTOINCREMENT = "AUTOINCREMENT";
    private static final String UNIQUE = "UNIQUE";
    private static final String DEFAULT = "DEFAULT";

    private static final String CREATE = "CREATE TABLE IF NOT EXISTS";
    private static final String DROP = "DROP TABLE IF EXISTS";
    private static final String DELETE = "DELETE * FROM";

    static String getTableName(Class<?> clazz, android.support.database.annotation.Table table) {
        String tableName = table.name();
        if (TextUtils.isEmpty(tableName)) {
            tableName = clazz.getSimpleName();
        }
        return tableName;
    }

    static Column getColumn(Field field, android.support.database.annotation.Column column) {
        String columnName = column.name();
        if (TextUtils.isEmpty(columnName)) {
            columnName = field.getName();
        }
        Class<?> columnType = field.getType();
        boolean isPrimaryKey = column.primaryKey();
        boolean isNotNull = column.notNull();
        boolean isAutoincrement = column.autoincrement();
        boolean isUnique = column.unique();
        String defaultValue = column.defaultValue();
        android.support.database.core.Column column1 = new android.support.database.core.Column();
        column1.setName(columnName);
        column1.setType(columnType);
        column1.setPrimaryKey(isPrimaryKey);
        column1.setNotNull(isNotNull);
        column1.setAutoincrement(isAutoincrement);
        column1.setUnique(isUnique);
        column1.setDefaultValue(defaultValue);
        column1.setField(field);
        return column1;
    }

    public static void createTables(SQLiteDatabase db, Class<?>[] classes) {
        if (null != classes && classes.length > 0) {
            for (Class<?> clazz : classes) {
                createTable(db, clazz);
            }
        }
    }

    public static void createTable(SQLiteDatabase db, Class<?> clazz) {
        final TableFetcher fetcher = TableFetcher.getInstance();
        Table table = fetcher.getTable(clazz);
        if (table != null) {
            TableMonitor monitor = fetcher.getTableMonitor();
            String sql = null;
            if (monitor != null) {
                sql = monitor.onBuildSQLStatement(table);
            }
            if (TextUtils.isEmpty(sql)) {
                StringBuilder builder = new StringBuilder();
                buildSQLStatement(table, builder);
                sql = builder.toString();
            }
            LOGGER.info("createTable: sql = %s", sql);
            if (db != null) {
                db.execSQL(sql);
            }
        }
    }

    private static void buildSQLStatement(Table table, StringBuilder builder) {
        String tableName = table.getName();
        builder.append(CREATE).append(SPACE).append(tableName).append(SPACE).append("(");
        List<Column> columns = table.getColumns();
        if (columns == null || columns.isEmpty()) {
            throw new SQLiteException("table " + tableName + " has no column");
        }
        final boolean compositeKeys = table.getPrimaryKeyCount() > 1;
        int primaryKeyCount = 0;
        StringBuilder keysBuilder = new StringBuilder();
        for (int i = 0, N = columns.size(); i < N; i++) {
            if (i > 0) {
                builder.append(COMMA);
            }
            Column column = columns.get(i);
            buildColumnStatement(table, column, builder);
            if (compositeKeys && column.isPrimaryKey()) {
                if (primaryKeyCount > 0) {
                    keysBuilder.append(COMMA);
                }
                keysBuilder.append(column.getName());
                primaryKeyCount++;
            }
        }
        if (compositeKeys) {
            builder.append(COMMA).append(CRLF);
            builder.append(PRIMARY_KEY).append(SPACE).append("(");
            builder.append(keysBuilder.toString()).append(")");
        }
        builder.append(");");
    }

    public static void buildColumnStatement(Table table, Column column, StringBuilder builder) {
        String columnName = column.getName();
        String columnType = columnType(column.getType());
        boolean compositeKeys = table.getPrimaryKeyCount() > 1;
        boolean isPrimaryKey = column.isPrimaryKey();
        boolean isNotNull = column.isNotNull();
        boolean isAutoincrement = column.isAutoincrement();
        boolean isUnique = column.isUnique();
        String defaultValue = column.getDefaultValue();
        builder.append(CRLF).append(columnName).append(SPACE).append(columnType);
        if (isAutoincrement) {
            if (!INTEGER.equals(columnType)) {
                throw new SQLiteException("AUTOINCREMENT constraint can only use on Integer type");
            }
            if (isPrimaryKey) {
                if (compositeKeys) {
                    throw new SQLiteException("AUTOINCREMENT constraint is conflict with composite primary keys");
                } else {
                    builder.append(SPACE).append(PRIMARY_KEY);
                    builder.append(SPACE).append(AUTOINCREMENT);
                }
            } else {
                throw new SQLiteException("AUTOINCREMENT constraint can only use on PRIMARY KEY");
            }
        } else {
            if (isPrimaryKey && !compositeKeys) {
                builder.append(SPACE).append(PRIMARY_KEY);
            }
        }
        if (isNotNull) {
            builder.append(SPACE).append(NOT_NULL);
        }
        if (isUnique) {
            builder.append(SPACE).append(UNIQUE);
        }
        if (!TextUtils.isEmpty(defaultValue)) {
            if (isPrimaryKey) {
                throw new SQLiteException("DEFAULT constraint can't use on PRIMARY KEY");
            }
            builder.append(SPACE).append(DEFAULT).append(defaultValue);
        }
    }

    public static void deleteTables(SQLiteDatabase db, Class<?>[] classes) {
        if (null != classes && classes.length > 0) {
            for (Class<?> clazz : classes) {
                deleteTable(db, clazz);
            }
        }
    }

    public static void deleteTable(SQLiteDatabase db, Class<?> clazz) {
        final TableFetcher fetcher = TableFetcher.getInstance();
        Table table = fetcher.getTable(clazz);
        if (table != null) {
            String tableName = table.getName();
            String sql = DELETE + SPACE + tableName + ";";
            LOGGER.info("deleteTable: sql = %s", sql);
            if (null != db) {
                db.execSQL(sql);
            }
        }
    }

    public static void dropTables(SQLiteDatabase db, Class<?>[] classes) {
        if (null != classes && classes.length > 0) {
            for (Class<?> clazz : classes) {
                dropTable(db, clazz);
            }
        }
    }

    public static void dropTable(SQLiteDatabase db, Class<?> clazz) {
        final TableFetcher fetcher = TableFetcher.getInstance();
        Table table = fetcher.getTable(clazz);
        if (table != null) {
            String tableName = table.getName();
            String sql = DROP + SPACE + tableName + ";";
            LOGGER.info("dropTable: sql = %s", sql);
            if (null != db) {
                db.execSQL(sql);
            }
        }
    }

    private static String columnType(Class<?> clazz) {
        if (clazz == String.class) {
            return TEXT;
        } else if (clazz == int.class || clazz == Integer.class) {
            return INTEGER;
        } else if (clazz == byte.class || clazz == Byte.class) {
            return INTEGER;
        } else if (clazz == short.class || clazz == Short.class) {
            return INTEGER;
        } else if (clazz == long.class || clazz == Long.class) {
            return INTEGER;
        } else if (clazz == float.class || clazz == Float.class) {
            return REAL;
        } else if (clazz == double.class || clazz == Double.class) {
            return REAL;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return VARCHAR;
        } else if (clazz == char.class || clazz == Character.class) {
            return VARCHAR;
        } else if (clazz == Date.class) {
            return TEXT;
        } else if (clazz == byte[].class) {
            return BLOB;
        } else {
            throw new SQLiteException("Not supported clazz type " + clazz.toString());
        }
    }

    public static boolean isAutoincrement(Column column) {
        return column.isPrimaryKey() && isIntegerType(column.getType());
    }

    public static boolean isIntegerType(Class<?> clazz) {
        return clazz == int.class || clazz == Integer.class;
    }
}
