package com.ecfeed.junit.runner.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import com.ecfeed.junit.message.schema.RequestChunkSchema;
import com.ecfeed.junit.message.schema.RequestUpdateSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseRestServiceRunnable implements Runnable {

    static final private String COMMUNICATION_PROTOCOL = "TLSv1.2";

    static final String REQUEST_TEST_STREAM = "requestData";
    static final String REQUEST_UPDATE_CHUNK = "requestChunk";
    static final String REQUEST_UPDATE_CONFIRMATION = "requestUpdate";

    private Object fRequest;

    private Client fClient;
    private WebServiceClient fWebServiceClient;

    private BufferedReader fResponseBufferedReader;
    private int fResponseStatus;

    private ObjectMapper mapper;

    private String fClientVersion = "1.0";
    private String fClientType = "regular";

    private String fCommunicationProtocol = COMMUNICATION_PROTOCOL;
    private String fRequestType = REQUEST_TEST_STREAM;
    private String fKeyStorePath = "";

    public BaseRestServiceRunnable(Object request, String target, String... customSettings) {
        mapper = new ObjectMapper();

        fRequest = request;

        adjustParameters(customSettings);

        createConnection(target);
    }

    @Override
    final public void run() {
        startRestClient();
    }

    abstract protected void consumeReceivedMessage(String message);

    abstract protected boolean cancelExecution();

    abstract protected Object sendUpdatedRequest();

    abstract protected void handleException(Exception e);

    abstract protected void adjustParameters(String... customSettings);

    abstract protected void waitForStreamEnd();

    abstract protected void startLifeCycle();

    abstract protected void finishLifeCycle();

    protected void setClientType(String clientType) {
        fClientType = clientType;
    }

    protected void setKeyStorePath(String keyStorePath) {
        fKeyStorePath = keyStorePath;
    }

    private void createConnection(String target) {
        fClient = createConnectionClient();
        fWebServiceClient = new WebServiceClient(fClient, target);
    }

    private void startRestClient() {
        startLifeCycle();

        getServerResponse();

        try {
            processTestStream();
        } catch (Exception e) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestConnectionLost"), e);
            exception.addSuppressed(e);
            handleException(exception);
        } finally {
            closeBufferedReader();
            closeClient();
        }

        finishLifeCycle();
    }

    private Client createConnectionClient() {
        ClientBuilder client = ClientBuilder.newBuilder();

        client.hostnameVerifier(ServiceWebHostnameVerifier.noSecurity());
        client.sslContext(createConnectionClientSecurityContext());

        return client.build();
    }

    private SSLContext createConnectionClientSecurityContext() {
        SSLContext securityContext = null;

        try {
            securityContext = SSLContext.getInstance(fCommunicationProtocol);
            securityContext.init(ServiceRestKeyManager.useKeyManagerCustom(fKeyStorePath), ServiceRestTrustManager.useTrustManagerCustom(fKeyStorePath), new SecureRandom());
        } catch (KeyManagementException e) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestSecureConnectionError"), e);
            exception.addSuppressed(e);
            handleException(exception);

        } catch (NoSuchAlgorithmException e) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestNoProtocolProvider"), e);
            exception.addSuppressed(e);
            handleException(exception);
        }

        return securityContext;
    }

    private void getServerResponse() {
        String requestText = null;

        try {
            requestText = mapper.writer().writeValueAsString(fRequest);
        } catch (JsonProcessingException e) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestJsonProcessingException"), e);
            exception.addSuppressed(e);
            handleException(exception);
        }

        try {
            ResponseData responseData =
                    fWebServiceClient.getServerResponse(
                            fClientType, fClientVersion,fRequestType, requestText);

            fResponseStatus = responseData.getResponseStatus();
            fResponseBufferedReader = responseData.getResponseBufferedReader();
        } catch (Exception e) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestJsonConnectionException"), e);
            exception.addSuppressed(e);
            handleException(exception);
        }
    }

    private void getServerUpdateResponse() {
        closeBufferedReader();

        fRequest = sendUpdatedRequest();

        String requestText = null;
        String requestType = null;

        if (fRequest instanceof RequestChunkSchema) {
            requestType = REQUEST_UPDATE_CHUNK;
        } else if (fRequest instanceof RequestUpdateSchema) {
            requestType = REQUEST_UPDATE_CONFIRMATION;
        } else {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestNotRecognizedRequestType"));
            handleException(exception);
        }

        try {
            requestText = mapper.writer().writeValueAsString(fRequest);
        } catch (JsonProcessingException e) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestJsonProcessingException"), e);
            exception.addSuppressed(e);
            handleException(exception);
        }

        ResponseData responseData =
                fWebServiceClient.getServerResponse(
                        fClientVersion, fClientVersion, requestType, requestText);

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
                handleException(exception);
            }
        }

    }

    private void closeClient() {

        if (fClient != null) {
            fClient.close();
        }

    }

}
