package android.support.database.behaviour;

/**
 * Created by yexiaokang on 2017/8/19.
 */

public abstract class AbstractDeleteBehaviour implements DeleteBehaviour {

    @Override
    public String whereClause() {
        return null;
    }

    @Override
    public String[] whereArgs() {
        return null;
    }
}
