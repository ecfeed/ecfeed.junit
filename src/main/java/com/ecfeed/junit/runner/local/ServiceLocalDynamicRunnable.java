package com.ecfeed.junit.runner.local;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import com.ecfeed.core.evaluator.SatSolverConstraintEvaluator;
import com.ecfeed.core.generators.GeneratorFactoryWithCodes;
import com.ecfeed.core.generators.algorithms.*;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;


//import com.ecfeed.core.model.ChoiceNode;
//import com.ecfeed.core.model.Constraint;

import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.generators.api.ParameterConverter;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.*;

import com.ecfeed.core.model.*;


import com.ecfeed.junit.message.MessageHelper;
import com.ecfeed.junit.runner.UserInputHelper;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

import static com.ecfeed.core.utils.DataSource.STATIC;

public class ServiceLocalDynamicRunnable implements Runnable {
	private volatile BlockingQueue<String> fResponseQueue;
	
	private IGenerator<ChoiceNode> fAlgorithm;
	private TestCasesUserInput fRequest;
	private String fModel;
	
	public ServiceLocalDynamicRunnable(
			BlockingQueue<String> responseQueue,
			Method testMethod,
			TestCasesUserInput request,
			String model, IExtLanguageManager extLanguageManager) {

		fModel = model;
		fRequest = request;
		fResponseQueue = responseQueue;

		try {
			initializeGenerator(testMethod, extLanguageManager);
		}
		catch (GeneratorException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("generatorInitializationError"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}

	}
	
	private void initializeGenerator(Method testMethod, IExtLanguageManager extLanguageManager) throws GeneratorException {
		DataSource dataSource = DataSource.parse(fRequest.getDataSource());

		if(dataSource == STATIC)
		{
			fAlgorithm = null;
			return;
		}

		GeneratorType generatorType = dataSource.toGeneratorType();

		fAlgorithm = new GeneratorFactoryWithCodes().createGenerator(generatorType);

		Collection<Constraint> generatorDataConstraints;
		List<List<ChoiceNode>> generatorDataInput;


		MethodNode methodNode = null;

		if (fModel.equals("auto")) {
			generatorDataConstraints = new ArrayList<>();
			generatorDataInput = ServiceLocalChoice.getInputChoices(testMethod);
			methodNode = new MethodNode("methodnode", null);
			Type[] typeList = testMethod.getGenericParameterTypes();

			for(int i=0;i<generatorDataInput.size();i++) {

				MethodParameterNode node =
						new MethodParameterNode(
								"arg"+i, typeList[i].getTypeName(), null,
								false, false, null, null);
				node.addChoices(generatorDataInput.get(i));
				methodNode.addParameter(node);
			}
		} else {
			RootNode model = UserInputHelper.loadEcFeedModelFromDirectory(Optional.ofNullable(fModel));
			methodNode = UserInputHelper.getMethodNodeFromEcFeedModel(testMethod, model, Optional.ofNullable(fRequest.getMethod()), extLanguageManager);
			generatorDataConstraints = UserInputHelper.getConstraintsFromEcFeedModel(methodNode, Optional.ofNullable(fRequest.getConstraints()));
			generatorDataInput = UserInputHelper.getChoicesFromEcFeedModel(methodNode, Optional.ofNullable(fRequest.getChoices()), extLanguageManager);
		}

		SimpleProgressMonitor simpleProgressMonitor = new SimpleProgressMonitor();

		IConstraintEvaluator<ChoiceNode> constraintEvaluator = new SatSolverConstraintEvaluator(generatorDataConstraints, methodNode);
		fAlgorithm.initialize(generatorDataInput, constraintEvaluator,
				ParameterConverter.deserialize(fRequest.getProperties(), fAlgorithm.getParameterDefinitions()),
				simpleProgressMonitor);

	}
	

	@Override
	public void run() {
		Logger.message("");

		int iteration = 0;
		
		fResponseQueue.offer(MessageHelper.resultStartDataSchema("LOCAL", false));
		fResponseQueue.offer(MessageHelper.resultStartChunkSchema("LOCAL"));
		
		List<ChoiceNode> tuple = null;

		try { 
			tuple = fAlgorithm.next();
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
				tuple = (List<ChoiceNode>) fAlgorithm.next();
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
