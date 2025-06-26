package dev.crown.configuration.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation to provide comments for configuration properties.
 * This annotation can be used to add descriptive comments to configuration fields.
 * It is retained at runtime and can be processed by tools or frameworks that support it.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ConfigComment {

    String[] value(); // The comment text for the configuration property

}
