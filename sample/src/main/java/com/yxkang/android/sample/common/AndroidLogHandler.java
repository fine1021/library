package com.yxkang.android.sample.common;

import android.text.TextUtils;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Implements a {@link java.util.logging.Logger} handler that writes to the Android log. The
 * implementation is rather straightforward. The name of the logger serves as
 * the log tag. Only the log levels need to be converted appropriately. For
 * this purpose, the following mapping is being used:
 * <p/>
 * <table>
 * <tr>
 * <th>logger level</th>
 * <th>Android level</th>
 * </tr>
 * <tr>
 * <td>
 * SEVERE
 * </td>
 * <td>
 * ERROR
 * </td>
 * </tr>
 * <tr>
 * <td>
 * WARNING
 * </td>
 * <td>
 * WARN
 * </td>
 * </tr>
 * <tr>
 * <td>
 * INFO
 * </td>
 * <td>
 * INFO
 * </td>
 * </tr>
 * <tr>
 * <td>
 * CONFIG
 * </td>
 * <td>
 * DEBUG
 * </td>
 * </tr>
 * <tr>
 * <td>
 * FINE, FINER, FINEST
 * </td>
 * <td>
 * VERBOSE
 * </td>
 * </tr>
 * </table>
 */
public class AndroidLogHandler extends Handler {

    /**
     * Holds the formatter for all Android log handlers.
     */
    private static final Formatter FORMATTER = new Formatter() {
        @Override
        public String format(LogRecord record) {
            String message = formatMessage(record);
            //noinspection ThrowableResultOfMethodCallIgnored
            Throwable thrown = record.getThrown();
            if (thrown != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                sw.write(message);
                sw.write("\n");
                thrown.printStackTrace(pw);
                pw.flush();
                message = sw.toString();
            }
            return TextUtils.isEmpty(message) ? "null" : message;
        }
    };

    /**
     * Constructs a new instance of the Android log handler.
     */
    public AndroidLogHandler() {
        setFormatter(FORMATTER);
    }

    @Override
    public void publish(LogRecord record) {
        int priority = getAndroidLevel(record.getLevel());
        String tag = record.getLoggerName();
        if (TextUtils.isEmpty(tag)) {
            tag = "unknown";
        }
        String msg = getFormatter().format(record);
        //noinspection WrongConstant
        Log.println(priority, tag, msg);
    }

    @Override
    public void flush() {
        // No need to flush, but must implement abstract method.
    }

    @Override
    public void close() throws SecurityException {
        // No need to close, but must implement abstract method.
    }

    /**
     * Converts a {@link java.util.logging.Logger} logging level into an Android one.
     *
     * @param level The {@link java.util.logging.Logger} logging level.
     * @return The resulting Android logging level.
     */
    static int getAndroidLevel(Level level) {
        int value = level.intValue();
        if (value >= Level.SEVERE.intValue()) {             // SEVERE
            return Log.ERROR;
        } else if (value >= Level.WARNING.intValue()) {     // WARNING
            return Log.WARN;
        } else if (value >= Level.INFO.intValue()) {        // INFO
            return Log.INFO;
        } else if (value >= Level.CONFIG.intValue()) {      // CONFIG
            return Log.DEBUG;
        } else {
            return Log.VERBOSE;
        }
    }
}
