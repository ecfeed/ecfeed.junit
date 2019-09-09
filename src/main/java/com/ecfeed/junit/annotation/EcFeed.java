package com.ecfeed.junit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ecfeed.junit.AnnotationDefaultValue;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * <p>Meta-annotation defining all EcFeed options.</p>
 *
 * The annotation is optional and can be used with (nested) classes only.
 * It might be used to group other annotations.
 *
 * @see <a href="https://ecfeed.com/tutorials/junit-documentation#ecfeed">https://ecfeed.com/tutorials/junit-documentation</a>
 */
@ParameterizedTest
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EcFeed {

    /**
     * The address/path of the EcFeed model.
     *
     * @see com.ecfeed.junit.annotation.EcFeedModel
     */
	String value() default AnnotationDefaultValue.DEFAULT_MODEL_NAME;

    /**
     * The generation options.
     *
     * @see com.ecfeed.junit.annotation.EcFeedInput
     */
	String input() default AnnotationDefaultValue.DEFAULT_INPUT;

    /**
     * The address of the EcFeed service.
     *
     * @see com.ecfeed.junit.annotation.EcFeedService
     */
	String target() default AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE;

    /**
     * The path of the certificate.
     *
     * @see com.ecfeed.junit.annotation.EcFeedKeyStore
     */
	String keystore() default AnnotationDefaultValue.DEFAULT_KEYSTORE;
}
