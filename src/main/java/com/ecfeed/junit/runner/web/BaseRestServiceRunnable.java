package com.ecfeed.junit.runner.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ecfeed.junit.message.schema.RequestChunkSchema;
import com.ecfeed.junit.message.schema.RequestUpdateSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseRestServiceRunnable implements Runnable {
	
static final private String COMMUNICATION_PROTOCOL = "TLSv1.2";
	
	static final private String NAME_CLIENT_VERSION = "clientVersion";
	static final private String NAME_CLIENT_TYPE = "clientType";
	static final private String NAME_CLIENT_REQUEST_TYPE = "requestType";
	
	static final String REQUEST_TEST_STREAM = "requestData";
	static final String REQUEST_UPDATE_CHUNK = "requestChunk";
	static final String REQUEST_UPDATE_CONFIRMATION = "requestUpdate";
	
	private Object fRequest;
	
	private WebTarget fWebTarget;
	private Client fClient;
	
	private BufferedReader fResponseBufferedReader;
	private int fResponseStatus;
	
	private ObjectMapper mapper;
	
	protected String fClientVersion = "1.0";
	protected String fClientType = "regular";
	
	protected String fCommunicationProtocol = COMMUNICATION_PROTOCOL;
	protected String fClientInitialReqeust = REQUEST_TEST_STREAM;
	protected String fTrustStorePath = "";
	protected String fKeyStorePath = "";
	
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

	abstract protected void lifecycleStart();

	abstract protected void lifecycleEnd();

	private void createConnection(String target) {
		fClient = createConnectionClient();
		fWebTarget = fClient.target(target);
	}

	private void startRestClient() {
		lifecycleStart();

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

		lifecycleEnd();
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
			securityContext.init(ServiceRestKeyManager.useKeyManagerCustom(fTrustStorePath), ServiceRestTrustManager.useTrustManagerCustom(fTrustStorePath), new SecureRandom());
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
		String value = null;
		
		try {
			value = mapper.writer().writeValueAsString(fRequest);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestJsonProcessingException"), e);
			exception.addSuppressed(e);
			handleException(exception);
		}
		
		try {
			Response response = fWebTarget
					.queryParam(NAME_CLIENT_VERSION, fClientVersion)
					.queryParam(NAME_CLIENT_TYPE, fClientType)
					.queryParam(NAME_CLIENT_REQUEST_TYPE, fClientInitialReqeust)
					.request()
					.post(Entity.entity(value, MediaType.APPLICATION_JSON));
			
			fResponseStatus = response.getStatus();
			fResponseBufferedReader = new BufferedReader(new InputStreamReader(response.readEntity(InputStream.class)));
		} catch (Exception e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestJsonConnectionException"), e);
			exception.addSuppressed(e);
			handleException(exception);
		}
	}
	
	private void getServerUpdateResponse() {
		closeBufferedReader();
		
		fRequest = sendUpdatedRequest();
		
		String value = null;
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
			value = mapper.writer().writeValueAsString(fRequest);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestJsonProcessingException"), e);
			exception.addSuppressed(e);
			handleException(exception);
		}

		Response response = fWebTarget
				.queryParam(NAME_CLIENT_REQUEST_TYPE, requestType)
				.request()
				.post(Entity.entity(value, MediaType.APPLICATION_JSON));
		
		fResponseStatus = response.getStatus();
		fResponseBufferedReader = new BufferedReader(new InputStreamReader(response.readEntity(InputStream.class)));
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
