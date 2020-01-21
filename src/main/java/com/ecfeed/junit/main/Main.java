package com.ecfeed.junit.main;

import com.ecfeed.core.evaluator.SatSolverConstraintEvaluator;
import com.ecfeed.core.generators.GeneratorFactoryWithCodes;
import com.ecfeed.core.generators.NWiseGenerator;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.generators.api.ParameterConverter;
import com.ecfeed.core.json.TestCasesUserInputParser;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.MethodNode;

import com.ecfeed.core.utils.DataSource;
import com.ecfeed.core.model.RootNode;

import com.ecfeed.core.utils.GeneratorType;
import com.ecfeed.core.utils.SimpleProgressMonitor;
import com.ecfeed.core.utils.TestCasesUserInput;
import com.ecfeed.junit.main.processor.TupleProcessorDynamic;
import com.ecfeed.junit.main.processor.TupleProcessorStatic;
import com.ecfeed.junit.runner.UserInputHelper;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.ecfeed.core.utils.DataSource.STATIC;
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
			Optional<IGenerator<ChoiceNode>> generator = initializeGenerator(fUserInput, methodNode);
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

		if (fUserInput.getDataSource() == null)
			fUserInput.setDataSource(DEFAULT_DATA_SOURCE);

		if (fUserInput.getConstraints() == null)
			fUserInput.setAllConstraints();

		String N = InputProcessor.extractN(options);
		if (N != null)
			fUserInput.getProperties().put(NWiseGenerator.PARAMETER_NAME_N,N);
	}

	private static Optional<IGenerator<ChoiceNode>> initializeGenerator(TestCasesUserInput userData, MethodNode methodNode) throws Exception {
		DataSource dataSource = DataSource.parse(userData.getDataSource());

		if(dataSource == STATIC)
			return Optional.empty();

		GeneratorType generatorType = dataSource.toGeneratorType();

		IGenerator<ChoiceNode> generator = new GeneratorFactoryWithCodes().createGenerator(generatorType);




		Collection<Constraint> generatorDataConstraints = UserInputHelper.getConstraintsFromEcFeedModel(
				methodNode,
				Optional.ofNullable(userData.getConstraints()));
		List<List<ChoiceNode>> generatorDataInput = UserInputHelper.getChoicesFromEcFeedModel(
				methodNode,
				Optional.ofNullable(userData.getChoices()));

		generator.initialize(generatorDataInput,
				new SatSolverConstraintEvaluator(generatorDataConstraints, methodNode),
				ParameterConverter.deserialize(userData.getProperties(), generator.getParameterDefinitions()),
				new SimpleProgressMonitor());

		return Optional.of(generator);

	}

	private static Optional<List<List<ChoiceNode>>> initializeList(TestCasesUserInput userData, MethodNode methodNode) throws Exception {
		return Optional.of(UserInputHelper.getTestsFromEcFeedModel(methodNode, Optional.ofNullable(userData.getTestSuites())));
	}

//	private static MethodNode getMethodNode(TestCasesUserInput userData) throws Exception {
//		return UserInputHelper.getMethodNodeFromEcFeedModel(null, fModel
//				, Optional.ofNullable(userData.getMethod()));
//	}

	private static List<MethodNode> getAllMethodNodes(TestCasesUserInput userData)
	{
		return UserInputHelper.getAllMatchingMethodNodesFromEcFeedModel(fModel, userData.getMethod());
	}

}
