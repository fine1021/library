package android.support.database.behaviour;

/**
 * Created by yexiaokang on 2017/8/19.
 */

public abstract class AbstractQueryBehaviour implements QueryBehaviour {

    @Override
    public boolean distinct() {
        return false;
    }

    @Override
    public String[] columns() {
        return null;
    }

    @Override
    public String selection() {
        return null;
    }

    @Override
    public String[] selectionArgs() {
        return null;
    }

    @Override
    public String groupBy() {
        return null;
    }

    @Override
    public String having() {
        return null;
    }

    @Override
    public String orderBy() {
        return null;
    }

    @Override
    public String limit() {
        return null;
    }
}
