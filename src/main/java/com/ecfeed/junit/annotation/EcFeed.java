package com.ecfeed.junit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.ParameterizedTest;

@ParameterizedTest
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EcFeed {
	String value() default AnnotationDefaultValue.DEFAULT_MODEL_NAME;
	String input() default AnnotationDefaultValue.DEFAULT_INPUT;
	String target() default AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE;
	String keystore() default AnnotationDefaultValue.DEFAULT_KEYSTORE;
}
