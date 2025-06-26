package dev.crown.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface VisibleForTesting {

}
