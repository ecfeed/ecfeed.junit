package com.ecfeed.junit.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.ecfeed.core.json.TestCasesUserInputParser;
import com.ecfeed.core.utils.TestCasesUserInput;
import com.ecfeed.junit.runner.SecurityHelper;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class AnnotationProcessor {

	private AnnotationProcessor() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
	public static String extractMethodName(ExtensionContext context) {
		
		Optional<AnnotatedElement> element = context.getElement();
		
		if (element.isPresent()) {
			return element.get().toString();
		}
		
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("annotationProcessorNotResolvableMethodNameError"));
		Logger.exception(exception);
		throw exception;
	}

	public static Optional<EcFeedInput> extractUserInput(ExtensionContext context) {
		
		return getAnnotationFromRunner(context, EcFeedInput.class);
	}
	
	public static Optional<EcFeedModel> extractModel(ExtensionContext context) {
		
		return getAnnotationFromRunner(context, EcFeedModel.class);
	}
	
	public static Optional<EcFeed> extractEcFeed(ExtensionContext context) {
		
		return getAnnotationFromRunner(context, EcFeed.class);
	}
	
	public static Optional<EcFeedService> extractService(ExtensionContext context) {
		
		return getAnnotationFromRunner(context, EcFeedService.class);
	}
	
	public static Optional<EcFeedKeyStore> extractKeyStore(ExtensionContext context) {
		
		return getAnnotationFromRunner(context, EcFeedKeyStore.class);
	}
	
	public static String processInput(ExtensionContext context) {
		
		Optional<EcFeedInput> annotation = extractUserInput(context);
		
		if (annotation.isPresent()) {
			String extractedInput = annotation.get().value();
			Logger.message(Localization.bundle.getString("annotationProcessorInput") + " " + extractedInput);
			return extractedInput;
		}
		
		return processEcFeedInput(context);
	}	
	
	public static String processEcFeedInput(ExtensionContext context) {
		Optional<EcFeed> annotation = extractEcFeed(context);
		
		if (annotation.isPresent()) {
			String extractedInput = annotation.get().input();
			
			if (extractedInput.equals(AnnotationDefaultValue.DEFAULT_INPUT)) {
				Logger.message(
						Localization.bundle.getString("annotationProcessorInputDefault") 
						+ " " + AnnotationDefaultValue.DEFAULT_INPUT);
			} else {
				Logger.message(
						Localization.bundle.getString("annotationProcessorInput") 
						+ " " + extractedInput);
			}
			
			return extractedInput;
		}
		
		Logger.message(
				Localization.bundle.getString("annotationProcessorInputDefault") 
				+ " " + AnnotationDefaultValue.DEFAULT_INPUT);
		
		return AnnotationDefaultValue.DEFAULT_INPUT;
	}
	
	public static TestCasesUserInput processInputSchema(ExtensionContext context) {
		
		String extractedInput = "{" + processInput(context).replace("'", "\"") + "}";
		
		return TestCasesUserInputParser.parseRequest(extractedInput); 
	}

	public static String processModelName(ExtensionContext context) {
		
		Optional<EcFeedModel> annotation = extractModel(context);
		
		if (annotation.isPresent()) {
			String extractedModelName = annotation.get().value();
			Logger.message(Localization.bundle.getString("annotationProcessorModelName") + " " + extractedModelName);
			return extractedModelName;
		}
		
		return processEcFeedModelName(context);
	}
	
	public static String processEcFeedModelName(ExtensionContext context) {
		
		Optional<EcFeed> annotation = extractEcFeed(context);
		
		if (annotation.isPresent()) {
			String extractedModelName = annotation.get().value();
			
			if (extractedModelName.equals(AnnotationDefaultValue.DEFAULT_MODEL_NAME)) {
				Logger.message(
						Localization.bundle.getString("annotationProcessorModelNameDefault") 
						+ " " + AnnotationDefaultValue.DEFAULT_MODEL_NAME);
			} else {
				Logger.message(
						Localization.bundle.getString("annotationProcessorModelName") 
						+ " " + extractedModelName);
			}
			
			return extractedModelName;
		}
		
		Logger.message(
				Localization.bundle.getString("annotationProcessorModelNameDefault") 
				+ " " + AnnotationDefaultValue.DEFAULT_MODEL_NAME);
		
		return AnnotationDefaultValue.DEFAULT_MODEL_NAME;
	}	
	
	public static String processService(ExtensionContext context) {
		
		Optional<EcFeedService> annotation = extractService(context);
		
		if (annotation.isPresent()) {
			String extractedTarget = annotation.get().value();
			Logger.message(Localization.bundle.getString("annotationProcessorTarget") + " " + extractedTarget);
			return extractedTarget;
		}
		
		return processEcFeedService(context);
	}
	
	public static String processEcFeedService(ExtensionContext context) {
		
		Optional<EcFeed> annotation = extractEcFeed(context);
		
		if (annotation.isPresent()) {
			String extractedTarget = annotation.get().target();
			
			if (extractedTarget.equals(AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE)) {
				Logger.message(
						Localization.bundle.getString("annotationProcessorTargetDefault") 
						+ " " + AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE);
			} else {
				Logger.message(
						Localization.bundle.getString("annotationProcessorTarget") 
						+ " " + extractedTarget);
			}
			
			return extractedTarget;
		}
		
		Logger.message(
				Localization.bundle.getString("annotationProcessorTargetDefault") 
				+ " " + AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE);
		
		return AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE;
	}
	
	public static String processKeyStore(ExtensionContext context) {
		
		Optional<EcFeedKeyStore> annotation = extractKeyStore(context);
		
		if (annotation.isPresent()) {
			return annotation.get().value();
		}
		
		return processEcFeedKeyStore(context);
	}
	
	public static String processEcFeedKeyStore(ExtensionContext context) {
		
		Optional<EcFeed> annotation = extractEcFeed(context);
		
		if (annotation.isPresent()) {
			return annotation.get().keystore();
		}
		
		return AnnotationDefaultValue.DEFAULT_KEYSTORE;
	}

	public static PublicKey processPublicKey(ExtensionContext context) {
		PublicKey publicKey = null;
		
		try {
			SecurityHelper.getKeyStore(processKeyStore(context));
			publicKey = SecurityHelper.getPublicKey(null, SecurityHelper.ALIAS_CLIENT);
			
			Logger.message(
					Localization.bundle.getString("annotationProcessorPublicKeyRetrieved") 
					+ " " + DigestUtils.sha256Hex(publicKey.getEncoded()));
			
		} catch (IllegalArgumentException e) {
			
			RuntimeException exception = 
					new RuntimeException(
							Localization.bundle.getString("annotationProcessorPublicKeyNotRetrieved"), e);
			
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
		
		return publicKey;
	}
	
	public static PrivateKey processPrivateKey(ExtensionContext context) {
		PrivateKey privateKey = null;
			
		try {
			SecurityHelper.getKeyStore(processKeyStore(context));
			privateKey = SecurityHelper.getPrivateKey(SecurityHelper.ALIAS_CLIENT, SecurityHelper.UNIVERSAL_PASSWORD);
			Logger.message(
					Localization.bundle.getString("annotationProcessorPrivateKeyRetrieved") 
					+ " " + DigestUtils.sha256Hex(privateKey.getEncoded()));
			
		} catch (IllegalArgumentException e) {
			
			RuntimeException exception = 
					new RuntimeException(Localization.bundle.getString("annotationProcessorPrivateKeyNotRetrieved"), e);
			
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
			
		return privateKey;
	}
	
	private static <T extends Annotation> Optional<T> getAnnotationFromRunner(
			ExtensionContext context, Class<T> annotation) {
		
		Optional<T> annotatedElement;
		
		annotatedElement = getAnnotationFromMethod(context, annotation);
		
		if (!annotatedElement.isPresent()) {
			annotatedElement = getAnnotationFromClass(context, annotation);
		}
		
		if (annotatedElement.isPresent()) {
			return annotatedElement;
		}

		return Optional.empty();	
	}
	
	private static <T extends Annotation> Optional<T> getAnnotationFromMethod(
			ExtensionContext context, Class<T> annotation) {
		
		return getAnnotationFromMethod(context.getElement(), annotation);
	}
	
	private static <T extends Annotation> Optional<T> getAnnotationFromMethod(
			Optional<AnnotatedElement> context, Class<T> annotation) {
		
		if (context.isPresent()) {
			AnnotatedElement annotatedElement = context.get();
			return Optional.ofNullable((T) annotatedElement.getAnnotation(annotation));	
		}
		
		return Optional.empty();
	}
	
	private static <T extends Annotation> Optional<T> getAnnotationFromClass(
			ExtensionContext context, Class<T> annotation) {

		return getAnnotationFromClass(context.getTestClass(), annotation);
	}
	
	private static <T extends Annotation> Optional<T> getAnnotationFromClass(
			Optional<Class<?>> element, Class<T> annotation) {

		if (element.isPresent()) {
			Optional<T> annotatedElement = Optional.ofNullable(element.get().getAnnotation(annotation));
			
			if (annotatedElement.isPresent()) {
				return annotatedElement;
			}
			
			return getAnnotationFromClass(Optional.ofNullable(element.get().getEnclosingClass()), annotation);
		}
		
		return Optional.empty();
	}
	
}