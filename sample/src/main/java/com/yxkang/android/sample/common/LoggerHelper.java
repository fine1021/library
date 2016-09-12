package com.yxkang.android.sample.common;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yexiaokang on 2016/9/12.
 */
public final class LoggerHelper {

    /**
     * Holds the global level for all Loggers
     */
    private static final Level LEVEL = Level.ALL;

    public static void initLogger(Logger logger) {
        logger.setUseParentHandlers(false);
        logger.setLevel(LEVEL);
    }
}
