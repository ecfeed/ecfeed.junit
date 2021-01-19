package com.ecfeed.junit;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.webservice.client.GenWebServiceClient;
import com.ecfeed.core.webservice.client.GenWebServiceClientType;
import com.ecfeed.core.webservice.client.IWebServiceClient;
import com.ecfeed.junit.runner.web.ServiceObjectMapper;
import com.ecfeed.junit.utils.TestDataSupplier;
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

        new Thread(createRunnable(context, store, new ExtLanguageManagerForJava())).start();

        Stream<Arguments> testStream =
                StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                                EcFeedArgumentsProviderIterator.create(dataBlockingQueue, context),
//                                EcFeedArgumentsProviderIteratorMock.create(dataBlockingQueue),
                                Spliterator.IMMUTABLE),
                        false);

        return testStream;
    }

    private Runnable createRunnable(
            ExtensionContext extensionContext,
            EcFeedExtensionStore ecFeedExtensionStore,
            IExtLanguageManager extLanguageManager) {

        TestCasesRequest restRequest = getTestCaseRequest(extensionContext);

        String serviceUrl = AnnotationProcessor.processService(extensionContext);
        Optional<String> keyStorePath = Optional.ofNullable(AnnotationProcessor.processKeyStore(extensionContext));
        String clientType = getCreateClientType(restRequest);

        return createJunitRestServiceRunnable(
                extensionContext, ecFeedExtensionStore,
                restRequest,
                serviceUrl,
                keyStorePath,
                clientType,
                extLanguageManager);
    }

    private Runnable createJunitRestServiceRunnable(
            ExtensionContext extensionContext,
            EcFeedExtensionStore ecFeedExtensionStore,
            TestCasesRequest testCasesRequest,
            String serviceUrl,
            Optional<String> keyStorePath,
            String clientType,
            IExtLanguageManager extLanguageManager) {

        if (!serviceUrl.equals(AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE)) {

            IWebServiceClient webServiceClient =
                    createWebServiceClient(serviceUrl, keyStorePath, clientType);

            ServiceObjectMapper serviceObjectMapper = new ServiceObjectMapper();

            return new JunitRestServiceRunnable(
                    webServiceClient, dataBlockingQueue, testCasesRequest,
                    ecFeedExtensionStore, serviceObjectMapper);
        }

        String model = testCasesRequest.getModel();

        if (model.equals("auto") || isModelFile(model)) {
            return createRunnableForAutoOrFileModel(extensionContext, model, extLanguageManager);
        }

        // TODO
        return createRunnableForDefaultServiceOnLocalhost(
                ecFeedExtensionStore,
                testCasesRequest,
                keyStorePath,
                clientType);
    }

    private Runnable createRunnableForDefaultServiceOnLocalhost(
            EcFeedExtensionStore ecFeedExtensionStore,
            TestCasesRequest testCasesRequest,
            Optional<String> keyStorePath,
            String clientType) {

        IWebServiceClient webServiceClient =
                createWebServiceClient(
                        AnnotationDefaultValue.DEFAULT_ECFEED_SERVICE_ON_LOCALHOST,
                        keyStorePath,
                        clientType);

        ServiceObjectMapper serviceObjectMapper = new ServiceObjectMapper();

        return new JunitRestServiceRunnable(
                webServiceClient,
                dataBlockingQueue, testCasesRequest,
                ecFeedExtensionStore, serviceObjectMapper);
    }

    private Runnable createRunnableForAutoOrFileModel(
            ExtensionContext extensionContext, String model, IExtLanguageManager extLanguageManager) {

        TestCasesUserInput restUserInput = getTestCasesUserInput(extensionContext);
        Method restMethod = extensionContext.getTestMethod().get();

        if (model.equals("auto")) { // TODO
            return new ServiceLocalDynamicRunnable(dataBlockingQueue, restMethod, restUserInput, model, extLanguageManager);
        }

        if (restUserInput.getDataSource().equalsIgnoreCase("static")) { // TODO
            return new ServiceLocalTestSuiteRunnable(dataBlockingQueue, restMethod, restUserInput, model, extLanguageManager);
        }

        return new ServiceLocalDynamicRunnable(dataBlockingQueue, restMethod, restUserInput, model, extLanguageManager);

    }

    private String getCreateClientType(TestCasesRequest restRequest) {

        String clientType = "regular";

        if (restRequest.getModel().equals(TestDataSupplier.testModelUuid)) {
            clientType = GenWebServiceClientType.LOCAL_TEST_RUNNER.toString();
        }

        return clientType;
    }

    private IWebServiceClient createWebServiceClient(
            String serviceUrl, Optional<String> keyStorePath, String clientType) {

        return new GenWebServiceClient(
                serviceUrl,
                GenWebServiceClient.getTestCasesEndPoint(),
                clientType,
                keyStorePath);
    }

    private TestCasesUserInput getTestCasesUserInput(ExtensionContext context) {
        return AnnotationProcessor.processInputSchema(context);
    }

    private TestCasesRequest getTestCaseRequest(ExtensionContext context) {
        TestCasesRequest restRequest = new TestCasesRequest();

        restRequest.setModelIdentificationStr(AnnotationProcessor.processModelName(context));
        restRequest.setMethod(AnnotationProcessor.extractMethodName(context));
        restRequest.setUserData("{" + AnnotationProcessor.processInput(context) + "}");

        return restRequest;
    }

    private boolean isModelFile(String filePath) {
        Path path = Paths.get(filePath);

        return Files.exists(path) && Files.isRegularFile(path) && Files.isReadable(path);
    }

}
