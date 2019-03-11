package com.ecfeed.junit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.params.provider.Arguments;

public final class ArgumentsHelper {

	public static Parameter[] generateParametersString() {
		return generateParametersString("");
	}
	
	public static Parameter[] generateParametersString(String op1) {
		Method method = null;

		try {
			method = ArgumentsHelper.class.getMethod("generateParametersString", String.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return method.getParameters();
	}
	
	public static Parameter[] generateParametersChar() {
		return generateParametersChar('a');
	}
	
	public static Parameter[] generateParametersChar(char op1) {
		Method method = null;

		try {
			method = ArgumentsHelper.class.getMethod("generateParametersChar", char.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return method.getParameters();
	}
	
	public static Parameter[] generateParametersBoolean() {
		return generateParametersBoolean(true);
	}
	
	public static Parameter[] generateParametersBoolean(boolean op1) {
		Method method = null;

		try {
			method = ArgumentsHelper.class.getMethod("generateParametersBoolean", boolean.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return method.getParameters();
	}
	
	public static Parameter[] generateParametersInteger() {
		return generateParametersInteger((byte) 0, (short) 0, (int) 0, (long) 0);
	}
	
	public static Parameter[] generateParametersInteger(byte op1, short op2, int op3, long op4) {
		Method method = null;

		try {
			method = ArgumentsHelper.class.getMethod("generateParametersInteger", byte.class, short.class, int.class, long.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return method.getParameters();
	}
	
	public static Parameter[] generateParametersDouble() {
		return generateParametersFloatingPoint((float) 0, (double) 0);
	}
	
	public static Parameter[] generateParametersFloatingPoint(float op1, double op2) {
		Method method = null;

		try {
			method = ArgumentsHelper.class.getMethod("generateParametersFloatingPoint", float.class, double.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return method.getParameters();
	}

	public static boolean compareArguments(Arguments op1, Arguments op2) {
		
		if (op1.get().length != op2.get().length) {
			return false;
		}
		
		for (int i = 0 ; i < op1.get().length ; i ++) {
			if (!op1.get()[i].equals(op2.get()[i])) {
				return false;
			}
		}
		
		return true;		
	}

}
