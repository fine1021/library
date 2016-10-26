package com.yxkang.android.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by yexiaokang on 2016/10/24.
 */

public final class IoUtils {

    /**
     * close the generic class which implements {@link Closeable}, with giving a prompt if failed
     *
     * @param closeable the generic class
     */
    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * close the generic class which implements {@link Closeable}, without giving a prompt if failed
     *
     * @param closeable the generic class
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}
