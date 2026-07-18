package com.osmantusx.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an event listener.
 *
 * <p>Annotated methods must be public and accept exactly one parameter whose
 * type extends {@link Event}. The {@link EventBus} discovers these methods via
 * reflection when an object is registered.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    /** Higher priority listeners are invoked first. */
    int priority() default Priority.NORMAL;

    /** Standard priority values. */
    final class Priority {
        public static final int HIGHEST = 200;
        public static final int HIGH = 100;
        public static final int NORMAL = 0;
        public static final int LOW = -100;
        public static final int LOWEST = -200;

        private Priority() {
        }
    }
}
