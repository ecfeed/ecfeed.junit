package com.ecfeed.junit;

import java.util.ArrayList;

import com.ecfeed.core.genservice.schema.RequestUpdateErrorSchema;
import com.ecfeed.core.genservice.schema.RequestUpdateSchema;

public final class EcFeedExtensionStore { // TODO - rename ? does function match name ?
	
	private volatile String fStreamId = "";
	private volatile String fTestId = "";
	private volatile boolean fTerminate = false;
	private volatile boolean fAcknowledge = false;
	private volatile boolean fCollectStats = false;
	private volatile boolean fChunkProgress = false;
	
	private RequestUpdateSchema requestUpdateSchema;
	
	public EcFeedExtensionStore() {
		requestUpdateSchema = new RequestUpdateSchema();
		requestUpdateSchema.setTestReport(new ArrayList<RequestUpdateErrorSchema>());
		
		setTerminate(false);
	}
	
	public void setTestId(String id) {
		fTestId = id;	
	}
	
	public String getTestId() {
		return fTestId;	
	}
	
	public void setStreamId(String id) {
		fStreamId = id;
	}
	
	public String getStreamId() {
		return fStreamId;	
	}
	
	public void setCollectStats(boolean collectStats) {
		fCollectStats = collectStats;
	}
	
	public boolean getCollectStats() {
		return fCollectStats;	
	}
	
	public void setChunkProgress(boolean chunkProgress) {
		fChunkProgress = chunkProgress;
	}
	
	public boolean getChunkProgress() {
		return fChunkProgress;	
	}
	
	public void setTerminate(boolean terminate) {
		fTerminate = terminate;
	}
	
	public boolean getTerminate() {
		return fTerminate;
	}
	
	public void setAcknowledge(boolean acknowledge) {
		fAcknowledge = acknowledge;
	}
	
	public boolean getAcknowledge() {
		return fAcknowledge;
	}
	
	public synchronized void updateTestResults(RequestUpdateErrorSchema result) {
		requestUpdateSchema.getTestReport().add(result);
	}
	
	public RequestUpdateSchema getTestResults() {
		return requestUpdateSchema;
	}

}
