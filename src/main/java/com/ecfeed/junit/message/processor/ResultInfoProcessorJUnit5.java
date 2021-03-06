package com.ecfeed.junit.message.processor;

import java.io.IOException;
import java.util.Optional;

import com.ecfeed.core.genservice.util.GenServiceProtocolHelper;
import org.junit.jupiter.params.provider.Arguments;

import com.ecfeed.junit.message.ArgumentChainJUnit5;
import com.ecfeed.core.genservice.schema.ResultInfoSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public class ResultInfoProcessorJUnit5 implements ArgumentChainJUnit5 {
	
	private final ArgumentChainJUnit5 fChain;
	
	public ResultInfoProcessorJUnit5(ArgumentChainJUnit5 chain) {
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
			ResultInfoSchema infoMessage = GenServiceProtocolHelper.parseInfo(json);
			
			Logger.message(Localization.bundle.getString("argumentChainJsonDataInfo") + " " + infoMessage);
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
