package com.ecfeed.junit.main;

import com.ecfeed.core.evaluator.Sat4jEvaluator;
import com.ecfeed.core.generators.algorithms.*;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.json.TestCasesUserInputParser;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.MethodNode;

import com.ecfeed.core.utils.DataSource;
import com.ecfeed.core.model.RootNode;

import com.ecfeed.core.utils.SimpleProgressMonitor;
import com.ecfeed.core.utils.TestCasesUserInput;
import com.ecfeed.junit.main.processor.TupleProcessorDynamic;
import com.ecfeed.junit.main.processor.TupleProcessorStatic;
import com.ecfeed.junit.runner.UserInputHelper;
import com.ecfeed.junit.utils.Localization;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.ecfeed.junit.main.CommandLineConstants.*;

public class Main {

	private static Optional<Path> fFileInput;
	private static TestCasesUserInput fUserInput;
	private static boolean fVerbose;
	private static RootNode fModel;

	public static void main(String[] args) throws Exception {
		processConsoleInput(parseConsoleInput(args));
		fModel = UserInputHelper.loadEcFeedModelFromDirectory(fFileInput.map( p -> p.toAbsolutePath().toString() ));
		for(MethodNode methodNode : getAllMethodNodes(fUserInput)) {
			System.out.println(methodNode.getLongSignature());
			Optional<AbstractAlgorithm<ChoiceNode>> generator = initializeGenerator(fUserInput, methodNode);
			Optional<List<List<ChoiceNode>>> list = initializeList(fUserInput, methodNode);

			if (fVerbose) {
				new TupleProcessorDynamic(generator, System.out::println).process();
				new TupleProcessorStatic(list, System.out::println).process();
			} else {
				int[] counter = new int[2];
				new TupleProcessorDynamic(generator, t -> counter[0]++).process();
				new TupleProcessorStatic(list, t -> counter[1]++).process();
				System.out.println(counter[0] + " : " + counter[1]);
			}
		}

	}

	private static OptionSet parseConsoleInput(String[] args) {
		OptionParser parser = new OptionParser();

		parser.accepts(FILE_INPUT_LONG).withRequiredArg();
		parser.accepts(FILE_INPUT_SHORT).withRequiredArg();
		parser.accepts(USER_INPUT_LONG).withRequiredArg();
		parser.accepts(USER_INPUT_SHORT).withRequiredArg();
		parser.accepts(VERBOSE_LONG);
		parser.accepts(VERBOSE_SHORT);
		parser.accepts(TUPLE_ARITY_SHORT).withRequiredArg();
		parser.accepts(METHOD_LONG).withRequiredArg();
		parser.accepts(METHOD_SHORT).withRequiredArg();

		return parser.parse(args);
	}

	private static void processConsoleInput(OptionSet options) {
		fVerbose = InputProcessor.extractVerbose(options);

		fFileInput = InputProcessor.extractFileInputPath(options);

		String userDataString = InputProcessor.extractUserData(options).orElse(DEFAULT_USER_INPUT);
		userDataString = "{" + userDataString.replaceAll("'", "\"") + "}";

		fUserInput = TestCasesUserInputParser.parseRequest(userDataString);

		Optional<String> methodName = InputProcessor.extractMethod(options);
		if (methodName.isPresent())
			fUserInput.setMethod(methodName.get());
		else
			fUserInput.setMethod(DEFAULT_METHOD);

		if (fUserInput.getCoverage() == null)
			fUserInput.setCoverage(DEFAULT_COVERAGE);

		if (fUserInput.getDataSource() == null)
			fUserInput.setDataSource(DEFAULT_DATA_SOURCE);

		if (fUserInput.getConstraints() == null)
			fUserInput.setAllConstraints();

		Integer N = InputProcessor.extractN(options);
		if (N != null)
			fUserInput.setN(N);
	}

	private static Optional<AbstractAlgorithm<ChoiceNode>> initializeGenerator(TestCasesUserInput userData, MethodNode methodNode) throws Exception {
		Optional<AbstractAlgorithm<ChoiceNode>> generator = getGenerator(userData);

		if (generator.isPresent()) {
			setGenerator(generator.get(), userData, methodNode);
			return generator;
		}

		return Optional.empty();
	}

	private static Optional<List<List<ChoiceNode>>> initializeList(TestCasesUserInput userData, MethodNode methodNode) throws Exception {
		return Optional.of(UserInputHelper.getTestsFromEcFeedModel(methodNode, Optional.ofNullable(userData.getTestSuites())));
	}

	private static Optional<AbstractAlgorithm<ChoiceNode>> getGenerator(TestCasesUserInput userData) throws Exception {
		DataSource dataSource = DataSource.parse(userData.getDataSource());

		switch (dataSource) {
			case GEN_N_WISE :
				return Optional.of(new AwesomeNWiseAlgorithm<>(
						Integer.parseInt(userData.getN()),
						Integer.parseInt(userData.getCoverage())));
			case GEN_CARTESIAN :
				return Optional.of(new CartesianProductAlgorithm<>());
			case GEN_RANDOM:
				return Optional.of(new RandomAlgorithm<>(
						Integer.parseInt(userData.getLength()),
						Boolean.parseBoolean(userData.getDuplicates())));
			case GEN_ADAPTIVE_RANDOM :
				return Optional.of(new AdaptiveRandomAlgorithm<>(
						Integer.parseInt(userData.getDepth()),
						Integer.parseInt(userData.getCandidates()),
						Integer.parseInt(userData.getLength()),
						Boolean.parseBoolean(userData.getDuplicates())));
			case STATIC:
				return Optional.empty();
			default :
				RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceLocalInvalidGeneratorName"));
				throw exception;
		}

	}

	private static void setGenerator(AbstractAlgorithm<ChoiceNode> generator, TestCasesUserInput userData, MethodNode methodNode) throws Exception {
		Collection<Constraint> generatorDataConstraints = UserInputHelper.getConstraintsFromEcFeedModel(
				methodNode,
				Optional.ofNullable(userData.getConstraints()));
		IConstraintEvaluator<ChoiceNode> constraintEvaluator = new Sat4jEvaluator(generatorDataConstraints, methodNode);
		List<List<ChoiceNode>> generatorDataInput = UserInputHelper.getChoicesFromEcFeedModel(
				methodNode,
				Optional.ofNullable(userData.getChoices()));

		generator.initialize(generatorDataInput, constraintEvaluator, new SimpleProgressMonitor());
	}

	private static MethodNode getMethodNode(TestCasesUserInput userData) throws Exception {
		return UserInputHelper.getMethodNodeFromEcFeedModel(null, fModel
				, Optional.ofNullable(userData.getMethod()));
	}

	private static List<MethodNode> getAllMethodNodes(TestCasesUserInput userData)
	{
		return UserInputHelper.getAllMatchingMethodNodesFromEcFeedModel(fModel, userData.getMethod());
	}

}
