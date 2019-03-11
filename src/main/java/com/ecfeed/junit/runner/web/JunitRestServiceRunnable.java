package com.ecfeed.junit.runner.web;

import java.util.concurrent.BlockingQueue;

import com.ecfeed.core.utils.TestCasesRequest;
import com.ecfeed.junit.EcFeedExtensionStore;
import com.ecfeed.junit.message.schema.RequestChunkSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class JunitRestServiceRunnable extends BaseRestServiceRunnable {

	private volatile BlockingQueue<String> fResponseQueue;
	private volatile EcFeedExtensionStore fStore;
	
	public JunitRestServiceRunnable(BlockingQueue<String> responseQueue, TestCasesRequest request, String target, EcFeedExtensionStore store, String... customSettings) {
		super(request, target, customSettings);
		
		fResponseQueue = responseQueue;
		fStore = store;
	}
	
	@Override
	protected void adjustParameters(String... customSettings) {
		fTrustStorePath = customSettings[0];
		fKeyStorePath = customSettings[0];
		
		if (customSettings[1].equals("TestUuid1")) {
			fClientType = "localTestRunner";
		} 
		
	}

	@Override
	protected void lifecycleStart() {
		Logger.message("");
	}

	@Override
	protected void consumeReceivedMessage(String message) {
		fResponseQueue.offer(message);
	}

	@Override
	protected void lifecycleEnd() {
		fResponseQueue.offer("");
	}

	@Override
	protected boolean cancelExecution() {
		return fStore.getTerminate();
	}
	
	@Override
	protected void waitForStreamEnd() {
		
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

	@Override
	protected Object sendUpdatedRequest() {
		if (fStore.getCollectStats() && !fStore.getAcknowledge()) {
			fStore.setAcknowledge(false);
			return fStore.getTestResults();
		} 
			
		RequestChunkSchema request = new RequestChunkSchema();
		request.setId(fStore.getStreamId());
			
		return request;
	}

	@Override
	protected void handleException(Exception e) {
		Logger.exception(e);
		
		new RuntimeException(e);
	}
	
}