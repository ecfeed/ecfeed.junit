package com.ecfeed.junit.message;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.params.provider.Arguments;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.junit.message.schema.ChoiceSchema;
import com.ecfeed.junit.message.schema.ResultTestSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class ArgumentChainTypeParser {

	private ArgumentChainTypeParser() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
	public static Optional<Arguments> parseJUnit5(ResultTestSchema jsonResult, Parameter[] parameters) {
		
		validateArguments(jsonResult, Arrays.asList(parameters));
		
		Object[] arguments = new Object[parameters.length];
		ChoiceSchema[] choices = jsonResult.getTestCase();
		
		for (int i = 0 ; i < arguments.length ; i++) {
			arguments[i] = convertParameterType(choices[i], parameters[i]);
		}
		
		return Optional.of(Arguments.of(arguments));
	}
	
	public static Optional<List<ChoiceNode>> parseRap(ResultTestSchema jsonResult, List<AbstractParameterNode> parameters) {
		
		validateArguments(jsonResult, parameters);
		
		List<ChoiceNode> arguments = new ArrayList<>();
		ChoiceSchema[] choices = jsonResult.getTestCase();
		
		for (int i = 0 ; i < choices.length ; i++) {
			ChoiceNode choiceNode = parameters.get(i).getChoice(choices[i].getName());

			arguments.add(choiceNode);
		}
		
		return Optional.of(arguments);
	}
	
	private static void validateArguments(ResultTestSchema jsonResult, List<?> parameters) {
		
		if (jsonResult == null || jsonResult.getTestCase() == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("argumentChainTypeParserEmptyArgumentListError"));
			Logger.exception(exception);
			throw exception;
		}
		
		if (parameters == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("argumentChainTypeParserEmptyMethodArgumentListError"));
			Logger.exception(exception);
			throw exception;
		}
		
		if (jsonResult.getTestCase().length != parameters.size()) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("argumentChainTypeParserErroneousArgumentNumberError"));
			Logger.exception(exception);
			throw exception;
		}
		
	}
	
	private static Object convertParameterType(ChoiceSchema schemaParameter, Parameter singleParameter) {
		
		Type parameterType = singleParameter.getParameterizedType();
		
		try {
			switch(parameterType.getTypeName()) {
				case "java.lang.String" : return schemaParameter.getValue();
				case "char" : return parseChar(schemaParameter.getValue());
				case "boolean" : return parseBoolean(schemaParameter.getValue());
				case "byte" : return Byte.parseByte(schemaParameter.getValue());
				case "short" : return Short.parseShort(schemaParameter.getValue());
				case "int" : return Integer.parseInt(schemaParameter.getValue());
				case "long" : return Long.parseLong(schemaParameter.getValue());
				case "float" : return Float.parseFloat(schemaParameter.getValue());
				case "double" : return Double.parseDouble(schemaParameter.getValue());
				default : return schemaParameter.getValue();
			}
		} catch (NumberFormatException e) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("argumentChainTypeParserIllegalArgumentPositionError"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		} catch (IndexOutOfBoundsException e) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("argumentChainTypeParserIllegalCharConversionError"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}

	}
	
	private static char parseChar(String value) {
		
		if (value.length() > 1) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("argumentChainTypeParserIllegalCharConversionError"));
			Logger.exception(exception);
			throw exception;
		}
		
		return value.charAt(0);
	}
	
	private static boolean parseBoolean(String value) {
	
		if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false") && !value.equalsIgnoreCase("t") && !value.equalsIgnoreCase("f")) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("argumentChainTypeParserIllegalBooleanConversionError"));
			Logger.exception(exception);
			throw exception;
		}
		
		return Boolean.parseBoolean(value);
	}
	
}
