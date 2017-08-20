package android.support.database.log;

import android.util.Log;

/**
 * StatusLogger, used by internal log
 */

public class StatusLogger implements Logger {

    private static final String TAG = "StatusLogger";
    private static final StatusLogger STATUS_LOGGER = new StatusLogger();

    private boolean mTraceEnabled = false;
    private boolean mDebugEnabled = false;
    private boolean mInfoEnabled = false;
    private boolean mWarnEnabled = true;
    private boolean mErrorEnabled = true;

    private Logger mLogger;

    public static StatusLogger getLogger() {
        return STATUS_LOGGER;
    }

    @Override
    public String getName() {
        return TAG;
    }

    public void setTraceEnabled(boolean traceEnabled) {
        mTraceEnabled = traceEnabled;
    }

    @Override
    public boolean isTraceEnabled() {
        return mTraceEnabled;
    }

    @Override
    public void trace(String msg) {
        if (mLogger != null) {
            mLogger.trace(msg);
        } else {
            if (mTraceEnabled) {
                Log.v(TAG, msg);
            }
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (mLogger != null) {
            mLogger.trace(format, arg);
        } else {
            if (mTraceEnabled) {
                Log.v(TAG, String.format(format, arg));
            }
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (mLogger != null) {
            mLogger.trace(format, arg1, arg2);
        } else {
            if (mTraceEnabled) {
                Log.v(TAG, String.format(format, arg1, arg2));
            }
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (mLogger != null) {
            mLogger.trace(format, arguments);
        } else {
            if (mTraceEnabled) {
                Log.v(TAG, String.format(format, arguments));
            }
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (mLogger != null) {
            mLogger.trace(msg, t);
        } else {
            if (mTraceEnabled) {
                Log.v(TAG, msg, t);
            }
        }
    }

    public void setDebugEnabled(boolean debugEnabled) {
        mDebugEnabled = debugEnabled;
    }

    @Override
    public boolean isDebugEnabled() {
        return mDebugEnabled;
    }

    @Override
    public void debug(String msg) {
        if (mLogger != null) {
            mLogger.debug(msg);
        } else {
            if (mDebugEnabled) {
                Log.d(TAG, msg);
            }
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (mLogger != null) {
            mLogger.debug(format, arg);
        } else {
            if (mDebugEnabled) {
                Log.d(TAG, String.format(format, arg));
            }
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (mLogger != null) {
            mLogger.debug(format, arg1, arg2);
        } else {
            if (mDebugEnabled) {
                Log.d(TAG, String.format(format, arg1, arg2));
            }
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (mLogger != null) {
            mLogger.debug(format, arguments);
        } else {
            if (mDebugEnabled) {
                Log.d(TAG, String.format(format, arguments));
            }
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (mLogger != null) {
            mLogger.debug(msg, t);
        } else {
            if (mDebugEnabled) {
                Log.d(TAG, msg, t);
            }
        }
    }

    public void setInfoEnabled(boolean infoEnabled) {
        mInfoEnabled = infoEnabled;
    }

    @Override
    public boolean isInfoEnabled() {
        return mInfoEnabled;
    }

    @Override
    public void info(String msg) {
        if (mLogger != null) {
            mLogger.info(msg);
        } else {
            if (mInfoEnabled) {
                Log.i(TAG, msg);
            }
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (mLogger != null) {
            mLogger.info(format, arg);
        } else {
            if (mInfoEnabled) {
                Log.i(TAG, String.format(format, arg));
            }
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (mLogger != null) {
            mLogger.info(format, arg1, arg2);
        } else {
            if (mInfoEnabled) {
                Log.i(TAG, String.format(format, arg1, arg2));
            }
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (mLogger != null) {
            mLogger.info(format, arguments);
        } else {
            if (mInfoEnabled) {
                Log.i(TAG, String.format(format, arguments));
            }
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (mLogger != null) {
            mLogger.info(msg, t);
        } else {
            if (mInfoEnabled) {
                Log.i(TAG, msg, t);
            }
        }
    }

    public void setWarnEnabled(boolean warnEnabled) {
        mWarnEnabled = warnEnabled;
    }

    @Override
    public boolean isWarnEnabled() {
        return mWarnEnabled;
    }

    @Override
    public void warn(String msg) {
        if (mLogger != null) {
            mLogger.warn(msg);
        } else {
            if (mWarnEnabled) {
                Log.w(TAG, msg);
            }
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (mLogger != null) {
            mLogger.warn(format, arg);
        } else {
            if (mWarnEnabled) {
                Log.w(TAG, String.format(format, arg));
            }
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (mLogger != null) {
            mLogger.warn(format, arguments);
        } else {
            if (mWarnEnabled) {
                Log.w(TAG, String.format(format, arguments));
            }
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (mLogger != null) {
            mLogger.warn(format, arg1, arg2);
        } else {
            if (mWarnEnabled) {
                Log.w(TAG, String.format(format, arg1, arg2));
            }
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (mLogger != null) {
            mLogger.warn(msg, t);
        } else {
            if (mWarnEnabled) {
                Log.w(TAG, msg, t);
            }
        }
    }

    public void setErrorEnabled(boolean errorEnabled) {
        mErrorEnabled = errorEnabled;
    }

    @Override
    public boolean isErrorEnabled() {
        return mErrorEnabled;
    }

    @Override
    public void error(String msg) {
        if (mLogger != null) {
            mLogger.error(msg);
        } else {
            if (mErrorEnabled) {
                Log.e(TAG, msg);
            }
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (mLogger != null) {
            mLogger.error(format, arg);
        } else {
            if (mErrorEnabled) {
                Log.e(TAG, String.format(format, arg));
            }
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (mLogger != null) {
            mLogger.error(format, arg1, arg2);
        } else {
            if (mErrorEnabled) {
                Log.e(TAG, String.format(format, arg1, arg2));
            }
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (mLogger != null) {
            mLogger.error(format, arguments);
        } else {
            if (mErrorEnabled) {
                Log.e(TAG, String.format(format, arguments));
            }
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (mLogger != null) {
            mLogger.error(msg, t);
        } else {
            if (mErrorEnabled) {
                Log.e(TAG, msg, t);
            }
        }
    }

    public void setLogger(Logger logger) {
        mLogger = logger;
    }
}
