package com.github.liuanxin.api.annotation;

import java.lang.annotation.*;

/** want to ignore some return comment, use this */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiReturnIgnore {
}
