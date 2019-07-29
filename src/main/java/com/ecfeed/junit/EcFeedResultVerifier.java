package com.ecfeed.junit;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import com.ecfeed.core.genservice.schema.RequestUpdateErrorSchema;

public class EcFeedResultVerifier implements AfterTestExecutionCallback {

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {
		Optional<Throwable> throwable = context.getExecutionException();
		
		EcFeedExtensionStore store = (EcFeedExtensionStore) context.getStore(Namespace.create("ecFeed")).get("ecFeedStore");

		if (store.getCollectStats()) {
			if (throwable.isPresent()) {
				RequestUpdateErrorSchema error = new RequestUpdateErrorSchema();

				error.setId(store.getTestId());
				error.setErrorClass(throwable.get().getClass() + "");
				error.setErrorMessage(throwable.get().getMessage());
				
				store.updateTestResults(error);
			}
		}

	}

}
