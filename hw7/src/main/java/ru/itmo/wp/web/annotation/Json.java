package ru.itmo.wp.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//применяем аннотацию к методу
@Retention(RetentionPolicy.RUNTIME) //аннотация джейсон должна быть в рантайме
public @interface Json {
}
