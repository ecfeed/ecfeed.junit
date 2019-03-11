package com.ecfeed.junit.annotation;

import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class AnnotationDefaultValue {

	private AnnotationDefaultValue() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
	public static final String DEFAULT_ECFEED_SERVICE = "default"; 
	public static final String DEFAULT_ECFEED_SERVICE_ON_LOCALHOST = "https://prod-gen.ecfeed.com/testCaseService"; 
	public static final String DEFAULT_MODEL_NAME = "auto"; 
	public static final String DEFAULT_INPUT = "'dataSource':'genCartesian'"; 
	public static final String DEFAULT_KEYSTORE = ""; 
	
}
