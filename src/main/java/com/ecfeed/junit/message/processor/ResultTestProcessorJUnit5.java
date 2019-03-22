package com.ecfeed.junit.message.processor;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Optional;

import org.junit.jupiter.params.provider.Arguments;

import com.ecfeed.junit.EcFeedExtensionStore;
import com.ecfeed.junit.message.ArgumentChainJUnit5;
import com.ecfeed.junit.message.ArgumentChainTypeParser;
import com.ecfeed.core.genservice.protocol.schema.ResultTestSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class ResultTestProcessorJUnit5 implements ArgumentChainJUnit5 {

	private final EcFeedExtensionStore fStore;
	private final ArgumentChainJUnit5 fChain;
	private final Parameter[] fParameters;
	
	public ResultTestProcessorJUnit5(ArgumentChainJUnit5 chain, Parameter[] parameters, EcFeedExtensionStore store) {
		fChain = chain;
		fStore = store;
		fParameters = parameters;
	}
	
	@Override
	public Optional<Arguments> process(String json) {
		
		try {
			ResultTestSchema response = fMapper.reader().forType(ResultTestSchema.class).readValue(json);
			
			if (!fStore.getChunkProgress()) {
				RuntimeException exception = new RuntimeException(Localization.bundle.getString("argumentChainFlagStartMissingError"));
				Logger.exception(exception);
				throw exception;
			}
			
			if (response.getId() != null) {
				fStore.setTestId(response.getId());
			}
			
			return ArgumentChainTypeParser.parseJUnit5(response, fParameters);
		} catch (IOException e) {
			return fChain.process(json);
		}
		
	}
	
	@Override
	public String toString( ) {
		return getClass().getSimpleName();
	}

}
