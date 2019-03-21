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

import com.ecfeed.junit.runner.web.GenWebServiceClient;
import com.ecfeed.junit.runner.web.IWebServiceClient;
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

	private Runnable createRunnable(
			ExtensionContext extensionContext,
			EcFeedExtensionStore ecFeedExtensionStore) {

		TestCasesRequest restRequest = getTestCaseRequest(extensionContext);
		String serviceUrl = AnnotationProcessor.processService(extensionContext);
		String keyStorePath = AnnotationProcessor.processKeyStore(extensionContext);

		String COMMUNICATION_PROTOCOL = "TLSv1.2";


		String fClientType = "regular";

		if (restRequest.getModel().equals("TestUuid1")) {
			fClientType = "localTestRunner"; // TODO
		}

		String fClientVersion = "1.0";

		if (!serviceUrl.equals(AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE)) {

			IWebServiceClient webServiceClient = null;

			webServiceClient = // TODO
				new GenWebServiceClient(
						serviceUrl, COMMUNICATION_PROTOCOL, keyStorePath, fClientType, fClientVersion);

			return new JunitRestServiceRunnable(
					webServiceClient, dataBlockingQueue, restRequest,
					serviceUrl, ecFeedExtensionStore, keyStorePath, restRequest.getModel());
		}

		if (restRequest.getModel().equals("auto") || isModelFile(restRequest.getModel())) { // TODO

			TestCasesUserInput restUserInput = getTestCasesUserInput(extensionContext);
			Method restMethod = extensionContext.getTestMethod().get();

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

			IWebServiceClient webServiceClient = null;

			webServiceClient = // TODO
					new GenWebServiceClient(
							serviceUrl, COMMUNICATION_PROTOCOL, keyStorePath, fClientType, fClientVersion);

			return

					new JunitRestServiceRunnable(
							webServiceClient,
							dataBlockingQueue, restRequest, 
							AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE_ON_LOCALHOST, ecFeedExtensionStore, keyStorePath, restRequest.getModel());
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
