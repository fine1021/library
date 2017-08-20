package android.support.database.strategy;

import android.support.database.Behaviour;
import android.support.database.behaviour.InsertBehaviour;

/**
 * Created by yexiaokang on 2017/8/19.
 */

public enum Insert implements Behaviour {

    /**
     * Use the following when no conflict action is specified.
     * This is the normal insert behaviour
     */
    NONE,

    /**
     * When a UNIQUE constraint violation occurs, the pre-existing rows that
     * are causing the constraint violation are removed prior to inserting
     * or updating the current row. Thus the insert or update always occurs.
     * The command continues executing normally. No error is returned.
     * If a NOT NULL constraint violation occurs, the NULL value is replaced
     * by the default value for that column. If the column has no default
     * value, then the ABORT algorithm is used. If a CHECK constraint
     * violation occurs then the IGNORE algorithm is used. When this conflict
     * resolution strategy deletes rows in order to satisfy a constraint,
     * it does not invoke delete triggers on those rows.
     * This behavior might change in a future release.
     */
    REPLACE,

    /**
     * Same as {@link Insert#REPLACE}.
     * <p>When replace a record, it will exclude the autoincrement field
     *
     * @see InsertBehaviour#includeAutoincrement()
     */
    REPLACE_EXCLUDE_AUTOINCREMENT
}
