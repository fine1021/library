package com.yxkang.android.db;

/**
 * Created by yexiaokang on 2016/2/2.
 */
public class AnnotationSQLException extends RuntimeException {

    public AnnotationSQLException() {

    }

    public AnnotationSQLException(String detailMessage) {
        super(detailMessage);
    }

    public AnnotationSQLException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AnnotationSQLException(Throwable throwable) {
        super(throwable);
    }
}
