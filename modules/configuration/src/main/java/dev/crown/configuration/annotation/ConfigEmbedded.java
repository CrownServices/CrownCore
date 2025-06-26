package dev.crown.configuration.annotation;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation to indicate that the type annotated is an class that can be serialized and deserialized.
 * This is typically used for configuration classes that are embedded within other configuration classes.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ConfigEmbedded {

    //The class that is embedded
    Class<? extends Serializable> value();

}
