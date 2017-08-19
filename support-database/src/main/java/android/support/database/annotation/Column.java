package android.support.database.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
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
    Class<?> type() default void.class;

    /**
     * set the primary key
     *
     * @return {@code true} if this field is primary key, otherwise {@code false}
     */
    boolean primaryKey() default false;

    /**
     * set the not null
     *
     * @return {@code true} if this field is not allowed to be null, otherwise {@code false}
     */
    boolean notNull() default false;

    /**
     * set the autoincrement
     *
     * @return {@code true} if this field is autoincrement, otherwise {@code false}
     */
    boolean autoincrement() default false;

    /**
     * set the unique
     *
     * @return {@code true} if this field is unique, otherwise {@code false}
     */
    boolean unique() default false;

    /**
     * set the default value
     *
     * @return the default value
     */
    String defaultValue() default "";
}
