package com.ecfeed.junit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import com.ecfeed.junit.EcFeedArgumentsProvider;
import com.ecfeed.junit.EcFeedResultVerifier;

/**
 * <p>Defines the EcFeed test method.</p>
 *
 * If no other annotations present, the test stream is generated using the default values for each argument.
 *
 * @see <a href="https://ecfeed.com/tutorials/junit-documentation#ecfeedtest">https://ecfeed.com/tutorials/junit-documentation</a>
 */
@ParameterizedTest
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ArgumentsSource(EcFeedArgumentsProvider.class)
@ExtendWith(EcFeedResultVerifier.class)
public @interface EcFeedTest {}
