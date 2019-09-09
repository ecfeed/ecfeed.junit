package com.ecfeed.junit.annotation;

import com.ecfeed.junit.AnnotationDefaultValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Defines the model which should be used to generate the test stream.</p>
 *
 * The annotation is optional and can be used with (nested) classes and/or methods.
 * To use a model stored in the on-line database, its UUID must be provided (the format is XXX-XXXX-XXXX-XXXX-XXXX).
 * It can also be used with a local file. In this case its path (absolute or relative) is needed.
 * If the annotation is not present, the test stream is generated using the default values for each argument.
 * <br/><br/>
 * The value can be defined using the {@link com.ecfeed.junit.annotation.EcFeed} meta-annotation.
 *
 * @see <a href="https://ecfeed.com/tutorials/junit-documentation#ecfeedmodel">https://ecfeed.com/tutorials/junit-documentation</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface EcFeedModel {
    /**
     * The address/path of the EcFeed model.
     */
	String value() default AnnotationDefaultValue.DEFAULT_MODEL_NAME;
}
