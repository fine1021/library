package com.yxkang.android.util;

import android.util.Log;

/**
 * Logger is a none-static class. use for local log print
 */
public class Logger {

    /**
     * Priority constant for the log method;
     */
    public static final int VERBOSE = 0x00;
    public static final int DEBUG = 0x01;
    public static final int INFO = 0x02;
    public static final int WARN = 0x03;
    public static final int ERROR = 0x04;

    private int level = VERBOSE;

    public Logger(int level) {
        this.level = level;
    }

    public void v(String tag, String msg) {
        if (VERBOSE >= level) {
            Log.v(tag, msg);
        }
    }

    public void v(String tag, String msg, Throwable tr) {
        if (VERBOSE >= level) {
            Log.v(tag, msg, tr);
        }
    }

    public void d(String tag, String msg) {
        if (DEBUG >= level) {
            Log.d(tag, msg);
        }
    }

    public void d(String tag, String msg, Throwable tr) {
        if (DEBUG >= level) {
            Log.d(tag, msg, tr);
        }
    }

    public void i(String tag, String msg) {
        if (INFO >= level) {
            Log.i(tag, msg);
        }
    }

    public void i(String tag, String msg, Throwable tr) {
        if (INFO >= level) {
            Log.i(tag, msg, tr);
        }
    }

    public void w(String tag, String msg) {
        if (WARN >= level) {
            Log.w(tag, msg);
        }
    }

    public void w(String tag, String msg, Throwable tr) {
        if (WARN >= level) {
            Log.w(tag, msg, tr);
        }
    }

    public void e(String tag, String msg) {
        if (ERROR >= level) {
            Log.e(tag, msg);
        }
    }

    public void e(String tag, String msg, Throwable tr) {
        if (ERROR >= level) {
            Log.e(tag, msg, tr);
        }
    }

    /**
     * Global is a static class. use for global log print
     */
    public static class Global {

        private static int level = VERBOSE;

        public static void setLevel(int level) {
            Global.level = level;
        }

        public static void v(String tag, String msg) {
            if (VERBOSE >= level) {
                Log.v(tag, msg);
            }
        }

        public static void v(String tag, String msg, Throwable tr) {
            if (VERBOSE >= level) {
                Log.v(tag, msg, tr);
            }
        }

        public static void d(String tag, String msg) {
            if (DEBUG >= level) {
                Log.d(tag, msg);
            }
        }

        public static void d(String tag, String msg, Throwable tr) {
            if (DEBUG >= level) {
                Log.d(tag, msg, tr);
            }
        }

        public static void i(String tag, String msg) {
            if (INFO >= level) {
                Log.i(tag, msg);
            }
        }

        public static void i(String tag, String msg, Throwable tr) {
            if (INFO >= level) {
                Log.i(tag, msg, tr);
            }
        }

        public static void w(String tag, String msg) {
            if (WARN >= level) {
                Log.w(tag, msg);
            }
        }

        public static void w(String tag, String msg, Throwable tr) {
            if (WARN >= level) {
                Log.w(tag, msg, tr);
            }
        }

        public static void e(String tag, String msg) {
            if (ERROR >= level) {
                Log.e(tag, msg);
            }
        }

        public static void e(String tag, String msg, Throwable tr) {
            if (ERROR >= level) {
                Log.e(tag, msg, tr);
            }
        }
    }
}
