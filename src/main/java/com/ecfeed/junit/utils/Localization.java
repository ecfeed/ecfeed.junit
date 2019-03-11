package com.ecfeed.junit.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Localization {

	public static final ResourceBundle bundle;
	
	static {	
		bundle = ResourceBundle.getBundle("localization/ecfeed", new Locale("en", "US"));
	}
	
	private Localization() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
}
