package com.ecfeed.junit;

import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.params.provider.Arguments;

import com.ecfeed.junit.message.ArgumentChainJUnit5;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public class EcFeedArgumentsProviderIterator implements Iterator<Arguments>  {
	private final BlockingQueue<String> fDataBlockingQueue;
	private final EcFeedExtensionStore fStore;
	private final Parameter[] fParameters;
	
	private Arguments fTestCaseArguments;

	private EcFeedArgumentsProviderIterator(BlockingQueue<String> dataBlockingQueue, ExtensionContext context) {
		fStore = (EcFeedExtensionStore) context.getStore(Namespace.create("ecFeed")).get("ecFeedStore");
		fParameters = context.getTestMethod().get().getParameters();
		fDataBlockingQueue = dataBlockingQueue;
	}
	
	public static EcFeedArgumentsProviderIterator create(BlockingQueue<String> dataBlockingQueue, ExtensionContext context) {
		if (dataBlockingQueue == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("ecFeedArgumentsProviderIteratorBlockingQueueError"));
			Logger.exception(exception);
			throw exception;
		}
		
		return new EcFeedArgumentsProviderIterator(dataBlockingQueue, context);
	}
	
	@Override
	public boolean hasNext() {
		return (fTestCaseArguments = getTestCase(fDataBlockingQueue)) != null;
	}

	@Override
	public Arguments next() {
		return fTestCaseArguments; 
	}
	
	private Arguments getTestCase(BlockingQueue<String> dataBlockingQueue) {
		String testCaseMessage;
		Optional<Arguments> testCaseArguments;
		
		try {
			
			do {
				testCaseMessage = dataBlockingQueue.take();
				
				if (testCaseMessage.equals("")) {
					return null;
				}
				
				testCaseArguments = ArgumentChainJUnit5.extract(testCaseMessage, fParameters, fStore);
			} while (!testCaseArguments.isPresent());
			
			
		} catch (InterruptedException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("ecFeedArgumentsProviderIteratorBlockingQueueCorruptedError"));
			Logger.exception(exception);
			throw exception;
		}
		
		return testCaseArguments.get();
	}
	
}
