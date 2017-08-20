package android.support.database.behaviour;

/**
 * Created by yexiaokang on 2017/8/19.
 */

public abstract class AbstractInsertBehaviour implements InsertBehaviour {

    @Override
    public int conflictAlgorithm() {
        return CONFLICT_NONE;
    }

    @Override
    public boolean includeAutoincrement() {
        return false;
    }
}
