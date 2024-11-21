package me.autobot.playerdoll.api.wrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A utility class for constructing a wrapper from anonymous implementation class
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Wrapper {
    /**
     * @return A custom name for further matching if same Wrapper base exist.
     */
    String name() default "";

    /**
     * @return The class being Wrapped.
     */
    Class<?> wrapping();

    /**
     * @return The Wrapping Method name.
     */
    String method();


}
