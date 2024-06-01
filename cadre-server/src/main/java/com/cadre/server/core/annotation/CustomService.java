package com.cadre.server.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.cadre.server.core.entity.ServiceLoadMode;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
//service interface
public @interface CustomService {

	Class<?> serviceId();
	String properties() default "";
	ServiceLoadMode loadMode() default ServiceLoadMode.LAZY;
	boolean register() default true;
	boolean loadOnStartApp() default false;
}
