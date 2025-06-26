package dev.crown.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface DoNotUse {
    String reason() default "";
}
