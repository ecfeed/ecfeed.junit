package com.ecfeed.junit.annotation;

import com.ecfeed.junit.AnnotationDefaultValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Defines the address of the EcFeed service.</p>
 *
 * The annotation is optional and should be used with a local EcFeed environment only.
 * It can be used with (nested) classes and/or methods.
 * The default value points to https://gen.ecfeed.com, which (at the moment) is compliant with all use cases.
 * <br/><br/>
 * The value can be defined using the {@link com.ecfeed.junit.annotation.EcFeed} meta-annotation.
 *
 * @see <a href="https://ecfeed.com/tutorials/junit-documentation#ecfeedservice">https://ecfeed.com/tutorials/junit-documentation</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface EcFeedService {
    /**
     * The address of the EcFeed service.
     */
	String value() default AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE;
}
