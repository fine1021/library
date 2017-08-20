package android.support.database.behaviour;

import android.database.sqlite.SQLiteDatabase;
import android.support.database.Behaviour;

/**
 * Created by yexiaokang on 2017/8/19.
 */

public interface InsertBehaviour extends Behaviour {

    /**
     * When a constraint violation occurs, an immediate ROLLBACK occurs,
     * thus ending the current transaction, and the command aborts with a
     * return code of SQLITE_CONSTRAINT. If no transaction is active
     * (other than the implied transaction that is created on every command)
     * then this algorithm works the same as ABORT.
     */
    int CONFLICT_ROLLBACK = SQLiteDatabase.CONFLICT_ROLLBACK;

    /**
     * When a constraint violation occurs,no ROLLBACK is executed
     * so changes from prior commands within the same transaction
     * are preserved. This is the default behavior.
     */
    int CONFLICT_ABORT = SQLiteDatabase.CONFLICT_ABORT;

    /**
     * When a constraint violation occurs, the command aborts with a return
     * code SQLITE_CONSTRAINT. But any changes to the database that
     * the command made prior to encountering the constraint violation
     * are preserved and are not backed out.
     */
    int CONFLICT_FAIL = SQLiteDatabase.CONFLICT_FAIL;

    /**
     * When a constraint violation occurs, the one row that contains
     * the constraint violation is not inserted or changed.
     * But the command continues executing normally. Other rows before and
     * after the row that contained the constraint violation continue to be
     * inserted or updated normally. No error is returned.
     */
    int CONFLICT_IGNORE = SQLiteDatabase.CONFLICT_IGNORE;

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
    int CONFLICT_REPLACE = SQLiteDatabase.CONFLICT_REPLACE;

    /**
     * Use the following when no conflict action is specified.
     */
    int CONFLICT_NONE = SQLiteDatabase.CONFLICT_NONE;

    int conflictAlgorithm();

    /**
     * A column declared <tt>INTEGER PRIMARY KEY</tt> will autoincrement.
     * <p>
     * Note that the integer key is one greater than the largest key that was in the table
     * just prior to the insert. The new key will be unique over all keys currently in the table,
     * but it might overlap with keys that have been previously deleted from the table.
     * To create keys that are unique over the lifetime of the table,
     * add the <tt>AUTOINCREMENT</tt> keyword to the <tt>INTEGER PRIMARY KEY</tt> declaration.
     * Then the key chosen will be one more than the largest key that has ever existed
     * in that table. If the largest possible key has previously existed in that table,
     * then the <tt>INSERT</tt> will fail with an <tt>SQLITE_FULL</tt> error code.
     * <p>
     *
     * @return {@code true} include the integer auto-increment field value when insert a record,
     * otherwise {@code false}
     */
    boolean includeAutoincrement();
}
