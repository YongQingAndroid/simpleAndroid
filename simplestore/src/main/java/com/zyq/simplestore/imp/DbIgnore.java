package com.zyq.simplestore.imp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * orm对象忽略字段
 * **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DbIgnore {

}
