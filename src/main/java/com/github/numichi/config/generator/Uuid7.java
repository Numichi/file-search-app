package com.github.numichi.config.generator;

import org.hibernate.annotations.IdGeneratorType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType(Uuid7Generator.class)
@Retention(RUNTIME)
@Target({FIELD})
public @interface Uuid7 {
}