package com.ecfeed.junit.annotation;

import com.ecfeed.junit.AnnotationDefaultValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Defines the certificate path.</p>
 *
 * The annotation is optional and can be used with (nested) classes and/or methods.
 * If it is not present, the algorithm uses the following locations (in that order):
 * <ol>
 *     <li>.ecfeed/security.p12</li>
 *     <li>ecfeed/security.p12</li>
 *     <li>${USER_HOME}/.ecfeed/security.p12</li>
 *     <li>${USER_HOME}/ecfeed/security.p12</li>
 *     <li>${JAVA_HOME}/lib/security/cacerts</li>
 * </ol>
 * The certificate is needed for on-line generation only. It can be created in the 'security' section of the EcFeed service.
 * <br/><br/>
 * The value can be defined using the {@link com.ecfeed.junit.annotation.EcFeed} meta-annotation.
 *
 * @see <a href="https://ecfeed.com/tutorials/junit-documentation#ecfeedkeystore">https://ecfeed.com/tutorials/junit-documentation</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface EcFeedKeyStore {
    /**
     * The path of the certificate.
     */
	String value() default AnnotationDefaultValue.DEFAULT_KEYSTORE;
}
