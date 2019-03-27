package com.ecfeed.junit.runner.web;

import java.util.HashMap;
import java.util.Map;

import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class ServiceRestErrorCodes {

	private ServiceRestErrorCodes() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
//	private final static Map<Integer, String> codeMap;
//
//	static {
//		codeMap = new HashMap<>();
//
//		codeSuccess(codeMap);
//		codeRedirection(codeMap);
//		codeClientError(codeMap);
//		codeServerError(codeMap);
//	}
//
//	private static void codeSuccess(Map<Integer, String> codeMap) {
//		codeMap.put(200, Localization.bundle.getString("serviceRestErrorCodesSuccess200"));
//		codeMap.put(201, Localization.bundle.getString("serviceRestErrorCodesSuccess201"));
//		codeMap.put(202, Localization.bundle.getString("serviceRestErrorCodesSuccess202"));
//		codeMap.put(204, Localization.bundle.getString("serviceRestErrorCodesSuccess204"));
//	}
//
//	private static void codeRedirection(Map<Integer, String> codeMap) {
//		codeMap.put(301, Localization.bundle.getString("serviceRestErrorCodesRedirection301"));
//		codeMap.put(302, Localization.bundle.getString("serviceRestErrorCodesRedirection302"));
//	}
//
//	private static void codeClientError(Map<Integer, String> codeMap) {
//		codeMap.put(401, Localization.bundle.getString("serviceRestErrorCodesClientError401"));
//		codeMap.put(404, Localization.bundle.getString("serviceRestErrorCodesClientError404"));
//		codeMap.put(406, Localization.bundle.getString("serviceRestErrorCodesClientError406"));
//		codeMap.put(415, Localization.bundle.getString("serviceRestErrorCodesClientError415"));
//	}
//
//	private static void codeServerError(Map<Integer, String> codeMap) {
//		codeMap.put(500, Localization.bundle.getString("serviceRestErrorCodesServerError500"));
//		codeMap.put(503, Localization.bundle.getString("serviceRestErrorCodesServerError503"));
//	}
//
//	public static String getCode(int code) {
//		return codeMap.get(code);
//	}

}
