package dev.crown.configuration.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation to mark a configuration key.
 * This annotation can be used to indicate that a field or method is a configuration key.
 * It is typically used in conjunction with configuration management systems.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ConfigKey {

    String value();

}
