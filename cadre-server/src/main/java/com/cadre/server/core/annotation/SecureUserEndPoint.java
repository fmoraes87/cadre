package com.cadre.server.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cadre.server.core.entity.DatabaseOperation;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SecureUserEndPoint {
	DatabaseOperation databaseOperation() default DatabaseOperation.READ;
	
}
