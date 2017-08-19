package android.support.database.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.database.Behaviour;
import android.support.database.Column;
import android.support.database.behaviour.DeleteBehaviour;
import android.support.database.behaviour.InsertBehaviour;
import android.support.database.behaviour.QueryBehaviour;
import android.support.database.behaviour.UpdateBehaviour;
import android.support.database.strategy.Delete;
import android.support.database.strategy.Insert;
import android.support.database.strategy.Query;
import android.support.database.strategy.Update;

import java.util.List;

/**
 * Created by yexiaokang on 2017/8/19.
 */

@SuppressWarnings("unchecked")
enum Transform {

    INSERT(Behaviour.INSERT),

    DELETE(Behaviour.DELETE),

    UPDATE(Behaviour.UPDATE),

    QUERY(Behaviour.QUERY);

    private int type;

    Transform(int type) {
        this.type = type;
    }


    public <T> T transform(SQLiteDatabase db, android.support.database.Table table, Behaviour behaviour) {
        T result = null;
        String tableName = table.getName();
        switch (type) {
            case Behaviour.DELETE:
                if (behaviour == null || behaviour == Delete.ALL) {
                    result = (T) Integer.valueOf(db.delete(tableName, null, null));
                } else {
                    DeleteBehaviour deleteBehaviour = (DeleteBehaviour) behaviour;
                    String whereClause = deleteBehaviour.whereClause();
                    String[] whereArgs = deleteBehaviour.whereArgs();
                    return (T) Integer.valueOf(db.delete(tableName, whereClause, whereArgs));
                }
                break;
            case Behaviour.QUERY:
                if (behaviour == null || behaviour == Query.ALL) {
                    result = (T) db.query(tableName, null, null, null, null, null, null);
                } else {
                    QueryBehaviour queryBehaviour = (QueryBehaviour) behaviour;
                    boolean distinct = queryBehaviour.distinct();
                    String[] columns = queryBehaviour.columns();
                    String selection = queryBehaviour.selection();
                    String[] selectionArgs = queryBehaviour.selectionArgs();
                    String groupBy = queryBehaviour.groupBy();
                    String having = queryBehaviour.having();
                    String orderBy = queryBehaviour.orderBy();
                    String limit = queryBehaviour.limit();
                    result = (T) db.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
                }
                break;
        }
        return result;
    }

    public long transform(SQLiteDatabase db, android.support.database.Table table, Behaviour behaviour, ContentValues values) {
        long result = -1;
        String tableName = table.getName();
        switch (type) {
            case Behaviour.INSERT:
                if (behaviour == null || behaviour == Insert.NONE) {
                    result = db.insert(tableName, null, values);
                } else if (behaviour == Insert.REPLACE) {
                    result = db.replace(tableName, null, values);
                } else {
                    InsertBehaviour insertBehaviour = (InsertBehaviour) behaviour;
                    int conflictAlgorithm = insertBehaviour.conflictAlgorithm();
                    result = db.insertWithOnConflict(tableName, null, values, conflictAlgorithm);
                }
                break;
            case Behaviour.UPDATE:
                if (behaviour == null || behaviour == Update.PRIMARY_KEY) {
                    List<android.support.database.Column> columns = table.getColumns();
                    android.support.database.Column c = null;
                    for (Column column : columns) {
                        if (column.isPrimaryKey()) {
                            c = column;
                            break;
                        }
                    }
                    if (c == null) {
                        throw new SQLiteException("need a PRIMARY KEY for update");
                    }
                    String columnName = c.getName();
                    String columnValue = values.getAsString(columnName);
                    String whereClause = columnName + "=?";
                    String[] whereArgs = new String[]{columnValue};
                    result = db.update(tableName, values, whereClause, whereArgs);
                } else {
                    UpdateBehaviour updateBehaviour = (UpdateBehaviour) behaviour;
                    String whereClause = updateBehaviour.whereClause();
                    String[] whereArgs = updateBehaviour.whereArgs();
                    result = db.update(tableName, values, whereClause, whereArgs);
                }
                break;
        }
        return result;
    }
}
