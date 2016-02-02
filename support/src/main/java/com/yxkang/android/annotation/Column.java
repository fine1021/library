package com.yxkang.android.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yexiaokang on 2016/2/1.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * set the name of the column
     *
     * @return the field column name
     */
    String name() default "";

    /**
     * set the type of the column
     *
     * @return the field column type
     */
    String type() default "";

    /**
     * set the order of the field from left to right, order must be started with 1 and continuous
     *
     * @return the field order
     */
    int order();

    /**
     * set the primary key
     *
     * @return {@code true} if this field is primary key, otherwise {@code false}
     */
    boolean primary() default false;

    /**
     * set the not null
     *
     * @return {@code true} if this field is not allowed to be null, otherwise {@code false}
     */
    boolean notNull() default false;
}
