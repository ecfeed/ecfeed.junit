package com.ecfeed.junit.annotation;

import com.ecfeed.junit.AnnotationDefaultValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Defines the generation options.</p>
 *
 * The annotation is optional and can be used with (nested) classes and/or methods.
 * Its value resembles the JSON format, however, double quotes should be replaced with single quotes.
 * The list of all possible options (which is being constantly updated) can be found on the EcFeed page.
 * <br/><br/>
 * The value can be defined using the {@link com.ecfeed.junit.annotation.EcFeed} meta-annotation.
 *
 * @see <a href="https://ecfeed.com/tutorials/junit-documentation#ecfeedinput">https://ecfeed.com/tutorials/junit-documentation</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface EcFeedInput {
    /**
     * The generation options.
     */
	String value() default AnnotationDefaultValue.DEFAULT_INPUT;
}
