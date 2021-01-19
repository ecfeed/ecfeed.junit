package com.ecfeed.junit.runner.local;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public class ServiceLocalChoice {

	private ServiceLocalChoice() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
	static List<List<ChoiceNode>> getInputChoices(Method method) {
		List<List<ChoiceNode>> choices = new ArrayList<>();
		
		for (Parameter parameter : method.getParameters()) {
			choices.add(getInputParameterChoices(parameter.getType()));
		}
		
		return choices;
	}
	
	static List<ChoiceNode> getInputParameterChoices(Class<?> type) {
		List<ChoiceNode> choices = new ArrayList<>();

		if (type.isEnum()) {
			return getInputParameterChoicesEnum(type);
		}
		
		switch (type.getName()) {
		case "byte" :
			choices.add(new ChoiceNode("Byte 1", "" + Byte.MAX_VALUE, null));
			choices.add(new ChoiceNode("Byte 2", "" + Byte.MIN_VALUE, null));
			choices.add(new ChoiceNode("Byte 3", "0", null));
			break;
		case "char" :
			choices.add(new ChoiceNode("Char 1", "A", null));
			choices.add(new ChoiceNode("Char 2", "Z", null));
			choices.add(new ChoiceNode("Char 3", "0", null));
			break;
		case "short" :
			choices.add(new ChoiceNode("Short 1", "" + Short.MAX_VALUE, null));
			choices.add(new ChoiceNode("Short 2", "" + Short.MIN_VALUE, null));
			choices.add(new ChoiceNode("Short 3", "0", null));
			break;
		case "int" :
			choices.add(new ChoiceNode("Integer 1", "" + Integer.MAX_VALUE, null));
			choices.add(new ChoiceNode("Integer 2", "" + Integer.MIN_VALUE, null));
			choices.add(new ChoiceNode("Integer 3", "0", null));
			break;
		case "long" :
			choices.add(new ChoiceNode("Long 1", "" + Long.MAX_VALUE, null));
			choices.add(new ChoiceNode("Long 2", "" + Long.MIN_VALUE, null));
			choices.add(new ChoiceNode("Long 3", "0", null));
			break;
		case "float" :
			choices.add(new ChoiceNode("Float 1", "" + Float.MAX_VALUE, null));
			choices.add(new ChoiceNode("Float 2", "" + -Float.MAX_VALUE, null));
			choices.add(new ChoiceNode("Float 3", "" + Float.MIN_VALUE, null));
			choices.add(new ChoiceNode("Float 4", "" + -Float.MIN_VALUE, null));
			choices.add(new ChoiceNode("Float 5", "" + Float.POSITIVE_INFINITY, null));
			choices.add(new ChoiceNode("Float 6", "" + Float.NEGATIVE_INFINITY, null));
			choices.add(new ChoiceNode("Float 7", "" + Float.NaN, null));
			choices.add(new ChoiceNode("Float 8", "0", null));
			break;
		case "double" :
			choices.add(new ChoiceNode("Double 1", "" + Double.MAX_VALUE, null));
			choices.add(new ChoiceNode("Double 2", "" + -Double.MAX_VALUE, null));
			choices.add(new ChoiceNode("Double 3", "" + Double.MIN_VALUE, null));
			choices.add(new ChoiceNode("Double 4", "" + -Double.MIN_VALUE, null));
			choices.add(new ChoiceNode("Double 5", "" + Double.POSITIVE_INFINITY, null));
			choices.add(new ChoiceNode("Double 6", "" + Double.NEGATIVE_INFINITY, null));
			choices.add(new ChoiceNode("Double 7", "" + Double.NaN, null));
			choices.add(new ChoiceNode("Double 8", "0", null));
			break;
		case "boolean" :
			choices.add(new ChoiceNode("Boolean 1", "true", null));
			choices.add(new ChoiceNode("Boolean 2", "false", null));
			break;
		case "java.lang.String" :
			choices.add(new ChoiceNode("String 1", "default", null));
			choices.add(new ChoiceNode("String 2", null, null));
			break;
		}
		
		return choices;
	}
	
	static List<ChoiceNode> getInputParameterChoicesEnum(Class<?> type) {
		List<ChoiceNode> choices = new ArrayList<>();
		
		String enumName;
		enumName = type.getCanonicalName();
		enumName = enumName.substring(enumName.lastIndexOf(".") + 1);

		Field[] enumFields = type.getFields();
		for (int i = 0 ; i < enumFields.length ; i++) {
			choices.add(new ChoiceNode(enumName + " " + (i + 1), enumFields[i].getName(), null));
		}
		
		return choices;
	}
	
}
