package android.support.database.behaviour;

import android.support.database.Behaviour;

/**
 * Created by yexiaokang on 2017/8/19.
 */

public interface UpdateBehaviour extends Behaviour {

    String whereClause();

    String[] whereArgs();
}
