package android.support.database.util;

import android.support.database.Column;
import android.support.database.Table;
import android.support.database.log.Logger;
import android.support.database.log.StatusLogger;
import android.support.database.strategy.TableMonitor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yexiaokang on 2017/8/17.
 */

public class TableFetcher {

    private static final Logger LOGGER = StatusLogger.getLogger();

    private static Map<Class<?>, Table> sTableMap = new HashMap<>();

    private static TableFetcher sTableFetcher;

    private TableMonitor mTableMonitor;

    public static TableFetcher getInstance() {
        if (sTableFetcher == null) {
            synchronized (TableFetcher.class) {
                if (sTableFetcher == null) {
                    sTableFetcher = new TableFetcher();
                }
            }
        }
        return sTableFetcher;
    }

    public Table getTable(Class<?> clazz) {
        Table table = sTableMap.get(clazz);
        if (table != null) {
            return table;
        }
        if (clazz.isAnnotationPresent(android.support.database.annotation.Table.class)) {
            android.support.database.annotation.Table t = clazz.getAnnotation(
                    android.support.database.annotation.Table.class);
            android.support.database.core.Table table1 = new android.support.database.core.Table();
            table1.setName(TableUtils.getTableName(clazz, t));
            Field[] fields = clazz.getDeclaredFields();
            List<Column> columns = new ArrayList<>();
            int primaryKeyCount = 0;
            for (Field field : fields) {
                if (field.isAnnotationPresent(android.support.database.annotation.Column.class)) {
                    android.support.database.annotation.Column column = field.getAnnotation(
                            android.support.database.annotation.Column.class);
                    columns.add(TableUtils.getColumn(field, column));
                    if (column.primaryKey()) {
                        primaryKeyCount++;
                    }
                }
            }
            if (mTableMonitor != null) {
                columns = mTableMonitor.onTableFetch(table1.getName(), columns);
            }
            table1.setPrimaryKeyCount(primaryKeyCount);
            table1.setColumns(columns);
            sTableMap.put(clazz, table1);
            return table1;
        } else {
            LOGGER.error("%s don't have a Table annotation", clazz.toString());
            return null;
        }
    }

    public void setTableMonitor(TableMonitor tableMonitor) {
        mTableMonitor = tableMonitor;
    }

    TableMonitor getTableMonitor() {
        return mTableMonitor;
    }

    public void clearTableCache() {
        sTableMap.clear();
    }
}
