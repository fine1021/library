package android.support.database;

/**
 * Created by yexiaokang on 2017/8/19.
 */

public interface Behaviour {

    /**
     * insert records
     */
    int INSERT = 0;

    /**
     * delete records
     */
    int DELETE = 1;

    /**
     * update records
     */
    int UPDATE = 2;

    /**
     * query records
     */
    int QUERY = 3;

    /**
     * query if a record exists
     */
    int EXIST = 4;
}
