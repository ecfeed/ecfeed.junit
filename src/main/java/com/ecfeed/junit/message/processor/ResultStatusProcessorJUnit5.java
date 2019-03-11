package com.ecfeed.junit.message.processor;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.params.provider.Arguments;

import com.ecfeed.junit.EcFeedExtensionStore;
import com.ecfeed.junit.message.ArgumentChainJUnit5;
import com.ecfeed.junit.message.schema.ResultStatusSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class ResultStatusProcessorJUnit5 implements ArgumentChainJUnit5 {

	private final ArgumentChainJUnit5 fChain;
	private final EcFeedExtensionStore fStore;
	
	public ResultStatusProcessorJUnit5(ArgumentChainJUnit5 chain, EcFeedExtensionStore store) {
		fChain = chain;
		fStore = store;
	}
	
	@Override
	public Optional<Arguments> process(String json) {
		
		if (json == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("argumentChainErroneousJsonData"));
			Logger.exception(exception);
			throw exception;
		}
		
		try {
			ResultStatusSchema response = fMapper.reader().forType(ResultStatusSchema.class).readValue(json);
				
			if (response.getStatus().equals("BEG_DATA")) {
				fStore.setStreamId(response.getId());
				fStore.setCollectStats(response.getCollectStats());
			}
				
			if (response.getStatus().equals("END_DATA")) {
				fStore.setTerminate(true);
			}
				
			if (response.getStatus().equals("BEG_CHUNK")) {
				
				if (response.getId().equals(fStore.getStreamId())) {
					fStore.setChunkProgress(true);
					fStore.getTestResults().getTestReport().clear();
				} else {
					RuntimeException exception = new RuntimeException(Localization.bundle.getString("argumentChainWrongChunkIdentifier"));
					Logger.exception(exception);
					throw exception;
				}
				
			}
				
			if (response.getStatus().equals("END_CHUNK")) {
				fStore.setChunkProgress(false);
			}
			
			if (response.getStatus().equals("ACKNOWLEDGED")) {
				fStore.setAcknowledge(true);
			}
			
			Logger.message(Localization.bundle.getString("argumentChainErroneousJsonDataInfo") + " " + response);
			return Optional.empty();
		} catch (IOException e) {
			return fChain.process(json);
		}
		
	}
	
	@Override
	public String toString( ) {
		return getClass().getSimpleName();
	}
	
}
