package com.ecfeed.junit.runner.web;

import java.io.BufferedReader;
import java.io.IOException;

import com.ecfeed.junit.message.schema.RequestChunkSchema;
import com.ecfeed.junit.message.schema.RequestUpdateSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseRestServiceRunnable implements Runnable {

    static final String REQUEST_TEST_STREAM = "requestData";
    static final String REQUEST_UPDATE_CHUNK = "requestChunk";
    static final String REQUEST_UPDATE_CONFIRMATION = "requestUpdate";


    private IWebServiceClient fWebServiceClient;
    private Object fRequest;

    private BufferedReader fResponseBufferedReader;
    private int fResponseStatus;
    private ObjectMapper fMapper;

    private String fRequestType = REQUEST_TEST_STREAM;

    public BaseRestServiceRunnable(IWebServiceClient webServiceClient, Object request) {

        fMapper = new ObjectMapper();
        fRequest = request;
        fWebServiceClient = webServiceClient;
    }

    @Override
    final public void run() {
        startRestClient();
    }

    abstract protected void consumeReceivedMessage(String message);

    abstract protected boolean cancelExecution();

    abstract protected Object sendUpdatedRequest();

    abstract protected void waitForStreamEnd();

    abstract protected void startLifeCycle();

    abstract protected void finishLifeCycle();

    private void startRestClient() {
        startLifeCycle();

        getServerResponse();

        try {
            processTestStream();
        } catch (Exception e) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestConnectionLost"), e);
            exception.addSuppressed(e);
            throw exception;
        } finally {
            closeBufferedReader();
            closeClient();
        }

        finishLifeCycle();
    }

    private void getServerResponse() {
        String requestText = null;

        try {
            requestText = fMapper.writer().writeValueAsString(fRequest);
        } catch (JsonProcessingException e) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestJsonProcessingException"), e);
            exception.addSuppressed(e);
            throw exception;
        }

        try {
            WebServiceResponse responseData =
                    fWebServiceClient.postRequest(fRequestType, requestText);

            fResponseStatus = responseData.getResponseStatus();
            fResponseBufferedReader = responseData.getResponseBufferedReader();
        } catch (Exception e) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestJsonConnectionException"), e);
            exception.addSuppressed(e);
            throw exception;
        }
    }

    private void getServerUpdateResponse() {
        closeBufferedReader();

        fRequest = sendUpdatedRequest();

        String requestType;

        if (fRequest instanceof RequestChunkSchema) {
            requestType = REQUEST_UPDATE_CHUNK;
        } else if (fRequest instanceof RequestUpdateSchema) {
            requestType = REQUEST_UPDATE_CONFIRMATION;
        } else {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestNotRecognizedRequestType"));
            throw exception;
        }

        String requestText;

        try {
            requestText = fMapper.writer().writeValueAsString(fRequest);
        } catch (JsonProcessingException e) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestJsonProcessingException"), e);
            exception.addSuppressed(e);
            throw exception;
        }

        WebServiceResponse responseData =
                fWebServiceClient.postRequest(requestType, requestText);

        fResponseStatus = responseData.getResponseStatus();
        fResponseBufferedReader = responseData.getResponseBufferedReader();
    }

    private void processTestStream() throws IOException {

        while (true) {
            if (processTestSuite()) {
                break;
            } else {
                getServerUpdateResponse();
            }
        }

    }

    private boolean processTestSuite() throws IOException {
        String message;

        if (isServerResponseCorrect()) {

            while ((message = fResponseBufferedReader.readLine()) != null) {
                consumeReceivedMessage(message);

                if (cancelExecution()) {
                    return true;
                }

            }

            waitForStreamEnd();

            if (cancelExecution()) {
                return true;
            }

        } else {
            Logger.message(Localization.bundle.getString("serviceRestServerResponse") + " " + ServiceRestErrorCodes.getCode(fResponseStatus));
            return true;
        }

        return false;
    }

    private boolean isServerResponseCorrect() {
        return (fResponseStatus / 100) == 2;
    }

    private void closeBufferedReader() {

        if (fResponseBufferedReader != null) {
            try {
                fResponseBufferedReader.close();
            } catch (IOException e) {
                Exception exception = new Exception(Localization.bundle.getString("serviceRestConnctionCloseError"), e);
                exception.addSuppressed(e);
            }
        }

    }

    private void closeClient() {

        fWebServiceClient.close();
    }

}
