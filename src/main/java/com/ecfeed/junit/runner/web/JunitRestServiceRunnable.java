package com.ecfeed.junit.runner.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.core.utils.TestCasesRequest;
import com.ecfeed.junit.EcFeedExtensionStore;
import com.ecfeed.junit.message.schema.RequestChunkSchema;
import com.ecfeed.junit.message.schema.RequestUpdateSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;


public class JunitRestServiceRunnable implements Runnable {

    private static final String REQUEST_DATA = "requestData";
    private static final String REQUEST_CHUNK = "requestChunk";
    private static final String REQUEST_UPDATE = "requestUpdate";


    private IWebServiceClient fWebServiceClient;
    ServiceObjectMapper fServiceObjectMapper;

    private String fRequestStr;
    private String fRequestType = REQUEST_DATA;

    private volatile BlockingQueue<String> fResponseQueue;
    private volatile EcFeedExtensionStore fStore;


    public JunitRestServiceRunnable(
            IWebServiceClient webServiceClient, BlockingQueue<String> responseQueue,
            TestCasesRequest request, EcFeedExtensionStore store, ServiceObjectMapper serviceObjectMapper)

    {

        fWebServiceClient = webServiceClient;
        fServiceObjectMapper = serviceObjectMapper;
        fRequestStr = fServiceObjectMapper.mapRequestToString(request);

        fResponseQueue = responseQueue;
        fStore = store;
    }

    private void startLifeCycle() {
        Logger.message("");
    }

    private void consumeReceivedMessage(String message) {
        fResponseQueue.offer(message);
    }

    private void finishLifeCycle() {
        fResponseQueue.offer("");
    }

    private boolean cancelExecution() {
        return fStore.getTerminate();
    }

    private void waitForStreamEnd() {

        while(fResponseQueue.size() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (fStore.getChunkProgress()) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestFlagEndMissingError"));
            Logger.exception(exception);
            throw exception;
        }

    }

    private Object sendUpdatedRequest() {
        if (fStore.getCollectStats() && !fStore.getAcknowledge()) {
            fStore.setAcknowledge(false);
            return fStore.getTestResults();
        }

        RequestChunkSchema request = new RequestChunkSchema();
        request.setId(fStore.getStreamId());

        return request;
    }

    @Override
    final public void run() {

        startRestClient(fRequestStr);
    }

    private void startRestClient(String requestText) {

        startLifeCycle();

        WebServiceResponse webServiceResponse = getServerResponse(requestText);

        if (!webServiceResponse.isResponseStatusOk()) {
            ExceptionHelper.reportRuntimeException(
                    "Request failed. Response status: " + webServiceResponse.getResponseStatus());
        }

        try {
            processTestStream(webServiceResponse.getResponseBufferedReader());
        } finally {
            closeBufferedReader(webServiceResponse.getResponseBufferedReader());
            closeClient();
        }

        finishLifeCycle();
    }

    private WebServiceResponse getServerResponse(String requestText) {

        return fWebServiceClient.postRequest(fRequestType, requestText);
    }

    private void getServerUpdateResponse(BufferedReader responseBufferedReader) {

        closeBufferedReader(responseBufferedReader);

        Object request = sendUpdatedRequest(); // TODO - are we sending anything here?

        String requestType = getRequestType(request);
        if (requestType == null) {
            return;
        }

        String requestText = fServiceObjectMapper.mapRequestToString(request);
        WebServiceResponse webServiceResponse = fWebServiceClient.postRequest(requestType, requestText);

        if (!webServiceResponse.isResponseStatusOk()) {
            ExceptionHelper.reportRuntimeException("Failed to send update request.");
        }
    }

    private String getRequestType(Object request) {
        String requestType;

        if (request instanceof RequestChunkSchema) {
            requestType = REQUEST_CHUNK;
        } else if (request instanceof RequestUpdateSchema) {
            requestType = REQUEST_UPDATE;
        } else {
            ExceptionHelper.reportRuntimeException("Request type not recognized.");
            return null;
        }
        return requestType;
    }

    private void processTestStream(BufferedReader responseBufferedReader) {

        while (true) {
            if (processTestSuite(responseBufferedReader)) {
                break;
            } else {
                getServerUpdateResponse(responseBufferedReader);
            }
        }
    }

    private boolean processTestSuite(BufferedReader responseBufferedReader) {

        String line;

        while ((line = readLine(responseBufferedReader)) != null) {
            consumeReceivedMessage(line);

            if (cancelExecution()) {
                return true;
            }
        }

        waitForStreamEnd();

        if (cancelExecution()) {
            return true;
        }

        return false;
    }

    private String readLine(BufferedReader responseBufferedReader) {

        try {
            return responseBufferedReader.readLine();
        } catch (IOException e) {
            ExceptionHelper.reportRuntimeException("Cannot read line from response.", e);
        }
        return null;
    }

    private void closeBufferedReader(BufferedReader responseBufferedReader) {

        if (responseBufferedReader == null) {
            return;
        }

        try {
            responseBufferedReader.close();
        } catch (IOException e) {
            SystemLogger.logCatch("Cannot close response stream.");
        }
    }

    private void closeClient() {

        fWebServiceClient.close();
    }

}
