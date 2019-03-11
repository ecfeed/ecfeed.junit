package com.ecfeed.junit.runner.local;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import com.ecfeed.core.generators.DataSourceHelper;
import com.ecfeed.core.generators.algorithms.AbstractAlgorithm;
import com.ecfeed.core.generators.algorithms.AdaptiveRandomAlgorithm;
import com.ecfeed.core.generators.algorithms.CartesianProductAlgorithm;
import com.ecfeed.core.generators.algorithms.RandomAlgorithm;
import com.ecfeed.core.generators.algorithms.RandomizedNWiseAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.TestCasesUserInput;
import com.ecfeed.junit.message.MessageHelper;
import com.ecfeed.junit.runner.UserInputHelper;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public class ServiceLocalDynamicRunnable implements Runnable {
	private volatile BlockingQueue<String> fResponseQueue;
	
	private AbstractAlgorithm<ChoiceNode> fAlgorithm;
	private TestCasesUserInput fRequest;
	private String fModel;
	
	public ServiceLocalDynamicRunnable(BlockingQueue<String> responseQueue, Method testMethod, TestCasesUserInput request, String model) {
		fModel = model;
		fRequest = request;
		fResponseQueue = responseQueue;
		
		getGeneratorType();
		getGeneratorData(testMethod);
	}
	
	private void getGeneratorType() {
		
		switch (fRequest.getDataSource()) {
			case DataSourceHelper.dataSourceGenNWise :
				fAlgorithm = new RandomizedNWiseAlgorithm<>(Integer.parseInt(fRequest.getN()), Integer.parseInt(fRequest.getCoverage()));
				break;
			case DataSourceHelper.dataSourceGenCartesian :
				fAlgorithm = new CartesianProductAlgorithm<>();
				break;
			case DataSourceHelper.dataSourceGenRandom :
				fAlgorithm = new RandomAlgorithm<>(Integer.parseInt(fRequest.getLength()), Boolean.parseBoolean(fRequest.getDuplicates()));
				break;
			case DataSourceHelper.dataSourceGenAdaptiveRandom :
				fAlgorithm = new AdaptiveRandomAlgorithm<>(Integer.parseInt(fRequest.getDepth()), Integer.parseInt(fRequest.getCandidates()), Integer.parseInt(fRequest.getLength()), Boolean.parseBoolean(fRequest.getDuplicates()));
				break;
			case DataSourceHelper.dataSourceStaticContent : 
				fAlgorithm = null;
				break;
			default :
				RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceLocalInvalidGeneratorName"));
				Logger.exception(exception);
				throw exception;
			}
		
	}
	
	private void getGeneratorData(Method testMethod) {
		Collection<IConstraint<ChoiceNode>> generatorDataConstraints;
		List<List<ChoiceNode>> generatorDataInput;
		
		try {
			
			if (fModel.equals("auto")) {
				generatorDataConstraints = new ArrayList<>();
				generatorDataInput = ServiceLocalChoice.getInputChoices(testMethod);
			} else {
				MethodNode methodNode = UserInputHelper.getMethodNodeFromEcFeedModel(testMethod, fModel, Optional.ofNullable(fRequest.getMethod()));
				
				generatorDataConstraints = UserInputHelper.getConstraintsFromEcFeedModel(methodNode, Optional.ofNullable(fRequest.getConstraints()));
				generatorDataInput = UserInputHelper.getChoicesFromEcFeedModel(methodNode, Optional.ofNullable(fRequest.getChoices()));
			}
			
			fAlgorithm.initialize(generatorDataInput, generatorDataConstraints, null);
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
		
		List<ChoiceNode> tuple = null;

		try { 
			tuple = fAlgorithm.getNext();
		} catch (GeneratorException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceLocalFirstTupleError"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
		
		do {
			
			if (iteration % 10 == 0) {
				fResponseQueue.offer(MessageHelper.resultProgressSchema(iteration));
			}
			
			fResponseQueue.offer(MessageHelper.resultTestSchema(tuple, "" + iteration));
			
			try {
				tuple = (List<ChoiceNode>) fAlgorithm.getNext();
			} catch (GeneratorException e) {
				fResponseQueue.offer(MessageHelper.resultErrorSchema(Localization.bundle.getString("serviceLocalTupleError")));
			}
			
			iteration++;
		} while (tuple != null);
		
		fResponseQueue.offer(MessageHelper.resultEndChunkSchema());
		fResponseQueue.offer(MessageHelper.resultEndDataSchema("LOCAL"));
		fResponseQueue.offer("");		
	}

}
