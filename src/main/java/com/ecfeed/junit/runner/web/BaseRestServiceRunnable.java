package com.ecfeed.junit.runner.web;

import java.io.BufferedReader;
import java.io.IOException;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.junit.message.schema.RequestChunkSchema;
import com.ecfeed.junit.message.schema.RequestUpdateSchema;
//import com.ecfeed.junit.utils.Localization;

public abstract class BaseRestServiceRunnable implements Runnable {

    private static final String REQUEST_DATA = "requestData";
    private static final String REQUEST_CHUNK = "requestChunk";
    private static final String REQUEST_UPDATE = "requestUpdate";


    private IWebServiceClient fWebServiceClient;
    ServiceObjectMapper fServiceObjectMapper;

    private String fRequestStr;
    private String fRequestType = REQUEST_DATA;

    public BaseRestServiceRunnable(
            IWebServiceClient webServiceClient,
            Object request,
            ServiceObjectMapper serviceObjectMapper) {

        fWebServiceClient = webServiceClient;
        fServiceObjectMapper = serviceObjectMapper;
        fRequestStr = fServiceObjectMapper.mapRequestToString(request);
    }

    @Override
    final public void run() {

        startRestClient(fRequestStr);
    }

    abstract protected void consumeReceivedMessage(String message);

    abstract protected boolean cancelExecution();

    abstract protected Object sendUpdatedRequest();

    abstract protected void waitForStreamEnd();

    abstract protected void startLifeCycle();

    abstract protected void finishLifeCycle();

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
