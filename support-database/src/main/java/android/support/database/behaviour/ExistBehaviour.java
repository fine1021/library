package android.support.database.behaviour;

import android.support.database.Behaviour;

/**
 * Created by fine on 2017/8/19.
 */

public interface ExistBehaviour extends Behaviour {

    boolean distinct();

    String[] columns();

    String selection();

    String[] selectionArgs();

    String groupBy();

    String having();

    String orderBy();

    String limit();
}
