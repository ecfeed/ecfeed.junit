package com.ecfeed.junit.utils;

public final class Logger { // TODO - REMOVE AND USE SystemLogger

	volatile private static boolean printToStream = true;
	
	private Logger() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
	public static void message(String info) {
		
		if (printToStream) {
	//		System.out.println(info);
		}
		
	}
	
	public static void exception(Exception e) {
		System.out.println(e.getMessage());
	}
	
	public static boolean isPrintToStream() {
		return printToStream;
	}

	public static void setPrintToStream(boolean printToStream) {
		Logger.printToStream = printToStream;
	}
	
}
