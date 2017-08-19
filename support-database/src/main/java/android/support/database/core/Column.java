package android.support.database.core;

import java.lang.reflect.Field;

/**
 * Created by yexiaokang on 2017/8/17.
 */

public class Column implements android.support.database.Column {

    private String mName;
    private Class<?> mType;
    private boolean mPrimaryKey;
    private boolean mNotNull;
    private boolean mAutoincrement;
    private boolean mUnique;
    private String mDefaultValue;
    private Field mField;

    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public Class<?> getType() {
        return mType;
    }

    public void setType(Class<?> type) {
        mType = type;
    }

    @Override
    public boolean isPrimaryKey() {
        return mPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        mPrimaryKey = primaryKey;
    }

    @Override
    public boolean isNotNull() {
        return mNotNull;
    }

    public void setNotNull(boolean notNull) {
        mNotNull = notNull;
    }

    @Override
    public boolean isAutoincrement() {
        return mAutoincrement;
    }

    public void setAutoincrement(boolean autoincrement) {
        mAutoincrement = autoincrement;
    }

    @Override
    public boolean isUnique() {
        return mUnique;
    }

    public void setUnique(boolean unique) {
        mUnique = unique;
    }

    @Override
    public String getDefaultValue() {
        return mDefaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        mDefaultValue = defaultValue;
    }

    @Override
    public Field getField() {
        return mField;
    }

    public void setField(Field field) {
        mField = field;
    }

    @Override
    public String toString() {
        return "Column{" +
                "mName='" + mName + '\'' +
                ", mType=" + mType +
                ", mPrimaryKey=" + mPrimaryKey +
                ", mNotNull=" + mNotNull +
                ", mAutoincrement=" + mAutoincrement +
                ", mUnique=" + mUnique +
                ", mDefaultValue='" + mDefaultValue + '\'' +
                '}';
    }
}
