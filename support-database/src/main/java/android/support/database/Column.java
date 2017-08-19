package android.support.database;

import java.lang.reflect.Field;

/**
 * Created by yexiaokang on 2017/8/17.
 */

public interface Column {

    String getName();

    Class<?> getType();

    boolean isPrimaryKey();

    boolean isNotNull();

    boolean isAutoincrement();

    boolean isUnique();

    String getDefaultValue();

    Field getField();
}
