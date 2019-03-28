package com.ecfeed.junit.message.processor;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.params.provider.Arguments;

import com.ecfeed.junit.message.ArgumentChainJUnit5;
import com.ecfeed.core.genservice.schema.ResultProgressSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class ResultProgressProcessorJUnit5 implements ArgumentChainJUnit5 {

	private final ArgumentChainJUnit5 fChain;
	
	public ResultProgressProcessorJUnit5(ArgumentChainJUnit5 chain) {
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
			Logger.message(Localization.bundle.getString("argumentChainErroneousJsonDataInfo") + " " + fMapper.reader().forType(ResultProgressSchema.class).readValue(json));
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
