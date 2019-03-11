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
			choices.add(new ChoiceNode("Byte 1", null, "" + Byte.MAX_VALUE));
			choices.add(new ChoiceNode("Byte 2", null, "" + Byte.MIN_VALUE));
			choices.add(new ChoiceNode("Byte 3", null, "0"));
			break;
		case "char" :
			choices.add(new ChoiceNode("Char 1", null, "A"));
			choices.add(new ChoiceNode("Char 2", null, "Z"));
			choices.add(new ChoiceNode("Char 3", null, "0"));
			break;
		case "short" :
			choices.add(new ChoiceNode("Short 1", null, "" + Short.MAX_VALUE));
			choices.add(new ChoiceNode("Short 2", null, "" + Short.MIN_VALUE));
			choices.add(new ChoiceNode("Short 3", null, "0"));
			break;
		case "int" :
			choices.add(new ChoiceNode("Integer 1", null, "" + Integer.MAX_VALUE));
			choices.add(new ChoiceNode("Integer 2", null, "" + Integer.MIN_VALUE));
			choices.add(new ChoiceNode("Integer 3", null, "0"));
			break;
		case "long" :
			choices.add(new ChoiceNode("Long 1", null, "" + Long.MAX_VALUE));
			choices.add(new ChoiceNode("Long 2", null, "" + Long.MIN_VALUE));
			choices.add(new ChoiceNode("Long 3", null, "0"));
			break;
		case "float" :
			choices.add(new ChoiceNode("Float 1", null, "" + Float.MAX_VALUE));
			choices.add(new ChoiceNode("Float 2", null, "" + -Float.MAX_VALUE));
			choices.add(new ChoiceNode("Float 3", null, "" + Float.MIN_VALUE));
			choices.add(new ChoiceNode("Float 4", null, "" + -Float.MIN_VALUE));
			choices.add(new ChoiceNode("Float 5", null, "" + Float.POSITIVE_INFINITY));
			choices.add(new ChoiceNode("Float 6", null, "" + Float.NEGATIVE_INFINITY));
			choices.add(new ChoiceNode("Float 7", null, "" + Float.NaN));
			choices.add(new ChoiceNode("Float 8", null, "0"));
			break;
		case "double" :
			choices.add(new ChoiceNode("Double 1", null, "" + Double.MAX_VALUE));
			choices.add(new ChoiceNode("Double 2", null, "" + -Double.MAX_VALUE));
			choices.add(new ChoiceNode("Double 3", null, "" + Double.MIN_VALUE));
			choices.add(new ChoiceNode("Double 4", null, "" + -Double.MIN_VALUE));
			choices.add(new ChoiceNode("Double 5", null, "" + Double.POSITIVE_INFINITY));
			choices.add(new ChoiceNode("Double 6", null, "" + Double.NEGATIVE_INFINITY));
			choices.add(new ChoiceNode("Double 7", null, "" + Double.NaN));
			choices.add(new ChoiceNode("Double 8", null, "0"));
			break;
		case "boolean" :
			choices.add(new ChoiceNode("Boolean 1", null, "true"));
			choices.add(new ChoiceNode("Boolean 2", null, "false"));
			break;
		case "java.lang.String" :
			choices.add(new ChoiceNode("String 1", null, "default"));
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
			choices.add(new ChoiceNode(enumName + " " + (i + 1), null, enumFields[i].getName()));
		}
		
		return choices;
	}
	
}
