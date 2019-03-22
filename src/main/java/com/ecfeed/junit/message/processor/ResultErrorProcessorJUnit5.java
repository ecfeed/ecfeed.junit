package com.ecfeed.junit.message.processor;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.params.provider.Arguments;

import com.ecfeed.junit.message.ArgumentChainJUnit5;
import com.ecfeed.core.genservice.protocol.schema.ResultErrorSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public class ResultErrorProcessorJUnit5 implements ArgumentChainJUnit5 {
	
	private final ArgumentChainJUnit5 fChain;
	
	public ResultErrorProcessorJUnit5(ArgumentChainJUnit5 chain) {
		fChain = chain;
	}
	
	@Override
	public Optional<Arguments> process(String json) {
		
		if (json == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("argumentChainErroneousJsonData"));
			Logger.exception(exception);
			throw exception;
		}
		
		try {
			ResultErrorSchema errorMessage = fMapper.reader().forType(ResultErrorSchema.class).readValue(json);
			
			RuntimeException exception = new RuntimeException(errorMessage.getError());
			Logger.exception(exception);
			throw exception;
		} catch (IOException e) {
			return fChain.process(json);
		}
		
	}
	
	@Override
	public String toString( ) {
		return getClass().getSimpleName();
	}
	
}
