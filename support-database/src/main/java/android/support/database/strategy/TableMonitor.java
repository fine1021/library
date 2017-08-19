package android.support.database.strategy;

import android.support.database.Column;
import android.support.database.Table;

import java.util.List;

/**
 * Created by yexiaokang on 2017/8/17.
 */

public interface TableMonitor {

    List<Column> onTableFetch(String tableName, List<Column> columns);

    String onBuildSQLStatement(Table table);
}
