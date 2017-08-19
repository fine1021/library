package android.support.database.core;

import android.support.database.Column;

import java.util.List;

/**
 * Created by yexiaokang on 2017/8/17.
 */

public class Table implements android.support.database.Table {

    private String mName;
    private int mPrimaryKeyCount;
    private List<Column> mColumns;

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public int getPrimaryKeyCount() {
        return mPrimaryKeyCount;
    }

    public void setPrimaryKeyCount(int primaryKeyCount) {
        mPrimaryKeyCount = primaryKeyCount;
    }

    public void setColumns(List<Column> columns) {
        mColumns = columns;
    }

    @Override
    public List<Column> getColumns() {
        return mColumns;
    }

    @Override
    public String toString() {
        return "Table{" +
                "mName='" + mName + '\'' +
                ", mPrimaryKeyCount=" + mPrimaryKeyCount +
                ", mColumns=" + mColumns +
                '}';
    }
}
