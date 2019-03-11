package com.ecfeed.junit.message.processor;

import java.util.Optional;

import org.junit.jupiter.params.provider.Arguments;

import com.ecfeed.junit.EcFeedExtensionStore;
import com.ecfeed.junit.message.ArgumentChainJUnit5;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class DefaultProcessorJUnit5 implements ArgumentChainJUnit5 {
	
	private final EcFeedExtensionStore fStore;
	
	public DefaultProcessorJUnit5(EcFeedExtensionStore store) {
		fStore = store;
	}
	
	@Override
	public Optional<Arguments> process(String json) {
		
		if (json == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("argumentChainErroneousJsonData"));
			Logger.exception(exception);
			throw exception;
		}
		
		Logger.message(Localization.bundle.getString("argumentChainUnknownCommandError") + " " + json);
		fStore.setTerminate(true);
		return Optional.empty();
	}
	
	@Override
	public String toString( ) {
		return getClass().getSimpleName();
	}
}
