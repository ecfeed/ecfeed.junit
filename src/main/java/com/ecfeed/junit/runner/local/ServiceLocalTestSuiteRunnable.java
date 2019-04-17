package com.ecfeed.junit.runner.local;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.TestCasesUserInput;
import com.ecfeed.junit.message.MessageHelper;
import com.ecfeed.junit.runner.UserInputHelper;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public class ServiceLocalTestSuiteRunnable implements Runnable {
	
	private volatile BlockingQueue<String> fResponseQueue;
	
	private List<List<ChoiceNode>> testCases;

	public ServiceLocalTestSuiteRunnable(BlockingQueue<String> responseQueue, Method testMethod, TestCasesUserInput request, String modelPath) {
		fResponseQueue = responseQueue;
		
		try {
			System.out.println("aaaaaa");
			RootNode model = UserInputHelper.loadEcFeedModelFromDirectory(Optional.ofNullable(modelPath));
			MethodNode methodNode = UserInputHelper.getMethodNodeFromEcFeedModel(testMethod, model, Optional.ofNullable(request.getMethod()));
			testCases = UserInputHelper.getTestsFromEcFeedModel(methodNode, Optional.ofNullable(request.getTestSuites()));
		} catch (GeneratorException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("generatorInitializationError"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
	@Override
	public void run() {
		Logger.message("");

		int iteration = 0;
		
		fResponseQueue.offer(MessageHelper.resultStartDataSchema("LOCAL", false));
		fResponseQueue.offer(MessageHelper.resultStartChunkSchema("LOCAL"));
		
		for (List<ChoiceNode> tuple : testCases) {
			
			if (iteration % 10 == 0) {
				fResponseQueue.offer(MessageHelper.resultProgressSchema(iteration));
			}
			
			fResponseQueue.offer(MessageHelper.resultTestSchema(tuple, "" + iteration));
			
			iteration++;	
		}
		
		fResponseQueue.offer(MessageHelper.resultEndChunkSchema());
		fResponseQueue.offer(MessageHelper.resultEndDataSchema("LOCAL"));
		fResponseQueue.offer("");		
	}
	
}
