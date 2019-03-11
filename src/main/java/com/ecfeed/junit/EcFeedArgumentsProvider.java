package com.ecfeed.junit;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import com.ecfeed.core.utils.TestCasesRequest;
import com.ecfeed.core.utils.TestCasesUserInput;
import com.ecfeed.junit.annotation.AnnotationDefaultValue;
import com.ecfeed.junit.annotation.AnnotationProcessor;
import com.ecfeed.junit.runner.local.ServiceLocalDynamicRunnable;
import com.ecfeed.junit.runner.local.ServiceLocalTestSuiteRunnable;
import com.ecfeed.junit.runner.web.JunitRestServiceRunnable;

public class EcFeedArgumentsProvider implements ArgumentsProvider {

	private volatile BlockingQueue<String> dataBlockingQueue = new LinkedBlockingQueue<>();

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

		EcFeedExtensionStore store = new EcFeedExtensionStore();
		context.getStore(Namespace.create("ecFeed")).put("ecFeedStore", store);

		new Thread(createRunnable(context, store)).start();

		Stream<Arguments> testStream = 
				StreamSupport.stream(
						Spliterators.spliteratorUnknownSize(
								EcFeedArgumentsProviderIterator.create(dataBlockingQueue, context), 
								Spliterator.IMMUTABLE), 
						false);

		return testStream;
	}

	private Runnable createRunnable(ExtensionContext context, EcFeedExtensionStore store) {

		TestCasesRequest restRequest = getTestCaseRequest(context);
		String targetAnnotation = AnnotationProcessor.processService(context);
		String restKeyStore = AnnotationProcessor.processKeyStore(context);

		if (!targetAnnotation.equals(AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE)) {
			return new JunitRestServiceRunnable(dataBlockingQueue, restRequest, targetAnnotation, store, restKeyStore, restRequest.getModel());
		}

		if (restRequest.getModel().equals("auto") || isModelFile(restRequest.getModel())) { // TODO

			TestCasesUserInput restUserInput = getTestCasesUserInput(context);
			Method restMethod = context.getTestMethod().get();

			if (restRequest.getModel().equals("auto")) { // TODO
				return 
						new ServiceLocalDynamicRunnable(
								dataBlockingQueue, restMethod, restUserInput, restRequest.getModel());
			}

			if (restUserInput.getDataSource().equalsIgnoreCase("static")) { // TODO
				return 
						new ServiceLocalTestSuiteRunnable(
								dataBlockingQueue, restMethod, restUserInput, restRequest.getModel());
			} else {
				return 
						new ServiceLocalDynamicRunnable(
								dataBlockingQueue, restMethod, restUserInput, restRequest.getModel());
			}

		} else {
			return 
					new JunitRestServiceRunnable(
							dataBlockingQueue, restRequest, 
							AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE_ON_LOCALHOST, store, restKeyStore, restRequest.getModel());
		}
	}

	private TestCasesUserInput getTestCasesUserInput(ExtensionContext context) {
		return AnnotationProcessor.processInputSchema(context);
	}

	private TestCasesRequest getTestCaseRequest(ExtensionContext context) {
		TestCasesRequest restRequest = new TestCasesRequest();

		restRequest.setModelName(AnnotationProcessor.processModelName(context));
		restRequest.setMethod(AnnotationProcessor.extractMethodName(context));
		restRequest.setUserData("{" + AnnotationProcessor.processInput(context) + "}");

		return restRequest;
	}

	private boolean isModelFile(String filePath) {
		Path path = Paths.get(filePath);

		return Files.exists(path) && Files.isRegularFile(path) && Files.isReadable(path);
	}

}
