package com.ecfeed.junit;

import com.ecfeed.junit.message.ArgumentChainJUnit5;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class EcFeedArgumentsProviderIteratorMock implements Iterator<Arguments>  {
	private final BlockingQueue<String> fDataBlockingQueue;

	private Arguments fTestCaseArguments;

	private EcFeedArgumentsProviderIteratorMock(BlockingQueue<String> dataBlockingQueue) {
		fDataBlockingQueue = dataBlockingQueue;
		fTestCaseArguments = getTestCase();
	}
	
	public static EcFeedArgumentsProviderIteratorMock create(BlockingQueue<String> dataBlockingQueue) {
		return new EcFeedArgumentsProviderIteratorMock(dataBlockingQueue);
	}

	@Override
	public boolean hasNext() {

		return true;
	}

	@Override
	public Arguments next() {

        return fTestCaseArguments;
	}
	
	private Arguments getTestCase() {
		Object[] arguments = new Object[10];

		for (int i = 0 ; i < 10 ; i++) {
			arguments[i] = new Integer(0);
		}

		return Arguments.of(arguments);
	}
	
}
