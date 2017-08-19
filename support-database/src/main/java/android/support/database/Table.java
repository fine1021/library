package android.support.database;

import java.util.List;

/**
 * Created by yexiaokang on 2017/8/17.
 */

public interface Table {

    String getName();

    int getPrimaryKeyCount();

    List<Column> getColumns();
}
