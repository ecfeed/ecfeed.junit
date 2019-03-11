package com.ecfeed.junit.message.processor;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.params.provider.Arguments;

import com.ecfeed.junit.EcFeedExtensionStore;
import com.ecfeed.junit.message.ArgumentChainJUnit5;
import com.ecfeed.junit.message.schema.RequestUpdateSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public class RequestUpdateProcessorJUnit5 implements ArgumentChainJUnit5 {

	private final EcFeedExtensionStore fStore;
	private final ArgumentChainJUnit5 fChain;
	
	public RequestUpdateProcessorJUnit5(ArgumentChainJUnit5 chain, EcFeedExtensionStore store) {
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
			RequestUpdateSchema response = fMapper.reader().forType(RequestUpdateSchema.class).readValue(json);
			
			response = fStore.getTestResults();
			
			Logger.message(response.toString());
			
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
