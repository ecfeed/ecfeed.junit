package com.ecfeed.junit.runner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.FixedChoiceValueFactory;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelConverter;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ParserException;
import com.ecfeed.junit.utils.Localization;

public class UserInputHelper {
	
	private static FixedChoiceValueFactory fFactory = new FixedChoiceValueFactory(null, false);
	
	public static MethodNode getMethodNodeFromEcFeedModel(Method testMethod, RootNode model, Optional<String> testName) throws GeneratorException {
		List<MethodNode> modelMethods = new ArrayList<>();
		
		for (ClassNode classNode : model.getClasses()) {
			for (MethodNode methodNode : classNode.getMethods()) {
				if (isMethodIdentical(testMethod, testName, methodNode)) {
					modelMethods.add(methodNode);
				}
			}
		}
		
		if (modelMethods.size() == 0) {
			GeneratorException.report(Localization.bundle.getString("userInputHelperMissingMethod"));
		}
		
		if (modelMethods.size() > 1) {
			GeneratorException.report(Localization.bundle.getString("userInputHelperMultipleMethod"));
		}
		
		return modelMethods.get(0);
	}

	public static List<MethodNode> getAllMatchingMethodNodesFromEcFeedModel(RootNode model, String methodPrefix)
	{
		List<MethodNode> modelMethods = new ArrayList<>();

		for (ClassNode classNode : model.getClasses()) {
			for (MethodNode methodNode : classNode.getMethods()) {
				if (isMethodNamePrefix(methodPrefix, methodNode)) {
					modelMethods.add(methodNode);
				}
			}
		}

		return modelMethods;
	}
	
	public static List<List<ChoiceNode>> getChoicesFromEcFeedModel(MethodNode methodNode, Optional<Object> choiceRestrictions) throws GeneratorException {
		List<List<ChoiceNode>> generatorChoices = new ArrayList<>();
		List<MethodParameterNode> modelMethodParameters = methodNode.getMethodParameters();
		Map<String, List<String>> modelMethodRestrictions = getChoiceRestrictionsFromUserInput(choiceRestrictions);
		
		validateChoiceRestrictionName(methodNode, modelMethodRestrictions);
		
		for (MethodParameterNode parameter : modelMethodParameters) {
			generatorChoices.add(adjustMethodParameter(parameter, adjustChoicesUsingRestrictions(parameter, modelMethodRestrictions)));
		}
		
		return generatorChoices;
	}
	
	public static List<List<ChoiceNode>> getTestsFromEcFeedModel(MethodNode methodNode, Optional<Object> testCases) throws GeneratorException {
		List<List<ChoiceNode>> testSuiteCollection = new ArrayList<>();
		 
		if (testCases.isPresent()) {
			Object testObject = testCases.get();
			List<TestCaseNode> testSuite;
			
			if (testObject instanceof String) {
				testSuite = getTestSuiteFromEcFeedModelString(methodNode, (String) testObject);
			} else if (testObject instanceof List<?>) {
				testSuite = getTestSuiteFromEcFeedModelList(methodNode, (List<?>) testObject);
			} else {
				GeneratorException.report(Localization.bundle.getString("userInputHelperUnknownTest"));
				return null;
			}
			
			for (TestCaseNode node : testSuite) {
				testSuiteCollection.add(adjustChoiceNode(node.getTestData()));
			}
			
			return testSuiteCollection;
		} else {
			return new ArrayList<>();
		}
		
	}
	
	public static List<Constraint> getConstraintsFromEcFeedModel(MethodNode methodNode, Optional<Object> constraintName) throws GeneratorException {
		
		if (constraintName.isPresent()) {
			Object constraintObject = constraintName.get();
			
			if (constraintObject instanceof String) {
				return getConstraintsFromEcFeedModelString(methodNode, (String) constraintObject);
			} else if (constraintObject instanceof List<?>) {
				return getConstraintsFromEcFeedModelList(methodNode, (List<?>) constraintObject);
			} else {
				GeneratorException.report(Localization.bundle.getString("userInputHelperUnknownConstraint"));
				return null;
			}
			
		} else {
			return new ArrayList<>();
		}

	}
	 
	private static List<ChoiceNode> adjustMethodParameter(MethodParameterNode parameter, List<ChoiceNode> choices) {
		List<ChoiceNode> choiceList = new ArrayList<>();
		
		if (parameter.isExpected()) {
		    ChoiceNode choice = new ChoiceNode("[e]" + parameter.getDefaultValue(), null, parameter.getDefaultValue());
		    choice.setParent(parameter);

		    choiceList.add(choice);

		    return adjustChoiceNode(choiceList);
		}
		
		choiceList.addAll(choices);

		return adjustChoiceNode(choiceList);
	}
	
	private static List<ChoiceNode> adjustChoiceNode(List<ChoiceNode> choiceNodeList) {
		List<ChoiceNode> choiceList = new ArrayList<>();
		
		for (ChoiceNode choice : choiceNodeList) {
			
			if (choice.isRandomizedValue() == false) {
				choice.setValueString(fFactory.createValue(choice) + "");
			}

			choiceList.add(choice);
		}
		
		return choiceList;
	}

	private static boolean isMethodIdentical(Method test, Optional<String> remoteName, MethodNode model) {
		
		if (remoteName.isPresent()) {

			if (test == null) {
				return remoteName.get().equals(model.getLongSignature());
			}

			String nameModel =  ((ClassNode) model.getParent()).getFullName() + "." + model.getFullName();

			return nameModel.equals(remoteName.get()) && isMethodParameterListIdentical(test, model);
		}
		
		return isMethodClassNameIdentical(test, model) && isMethodNameIdentical(test, model);
	}

	private static boolean isMethodNamePrefix(String methodName, MethodNode modelMethod)
	{
		return modelMethod.getLongSignature().startsWith(methodName);
	}
	
	private static boolean isMethodClassNameIdentical(Method test, MethodNode model) {
		String classTest = test.getDeclaringClass().getCanonicalName();
		String classModel = ((ClassNode) model.getParent()).getFullName();		
		
		return classTest.equals(classModel);
	}

 	private static boolean isMethodNameIdentical(Method test, MethodNode model) {
		String nameTest = test.getName();
		String nameModel = model.getFullName();
		
		if (nameTest.equals(nameModel)) {
			return isMethodParameterListIdentical(test, model);
		}
		
		return false;
	}
	
	private static boolean isMethodParameterListIdentical(Method test, MethodNode model) {
		Parameter[] parameterListTest = test.getParameters();
		List<AbstractParameterNode> parameterListModel = model.getParameters();
		
		if (parameterListTest.length != parameterListModel.size()) {
			return false;
		}
		
		for (int i = 0 ; i < parameterListTest.length ; i++) {
			String parsedName = parameterListModel.get(i).getType();
			
			if (parsedName.equals("String")) {
				parsedName = "java.lang.String";
			}
			
			if (parameterListTest[i].getType().getCanonicalName().equals(parsedName)) {
				continue;
			}
			
			return false;
		}
		
		return true;
	}

	private static List<TestCaseNode> getTestSuiteFromEcFeedModelString(MethodNode methodNode, String testData) throws GeneratorException {
		
		if (testData.equals("ALL")) {
			return methodNode.getTestCases();
		} else if (testData.equals("NONE")) {
			return new ArrayList<>();
		} else {
			GeneratorException.report(Localization.bundle.getString("userInputHelperUnknownTestString"));
			return null;
		}
	}
	
	private static List<TestCaseNode> getTestSuiteFromEcFeedModelList(MethodNode methodNode, List<?> testData) throws GeneratorException {
		List<TestCaseNode> testSuite = new ArrayList<>();
		
		for (Object testSuiteObject : testData) {
			if (!testSuite.addAll(methodNode.getTestCases(testSuiteObject.toString()))) {
				GeneratorException.report(Localization.bundle.getString("userInputHelperWrongTestSuite") + testSuiteObject.toString());
			}
			
		}
		
		return testSuite;
	}
	
	private static List<Constraint> getConstraintsFromEcFeedModelString(MethodNode methodNode, String constraintData) throws GeneratorException {

		if (constraintData.equals("ALL")) {
			return methodNode.getAllConstraints();
		} else if (constraintData.equals("NONE")) {
			return new ArrayList<>();
		} else {
			GeneratorException.report(Localization.bundle.getString("userInputHelperUnknownConstraintString"));
			return null;
		}
		
	}
	
	private static List<Constraint> getConstraintsFromEcFeedModelList(MethodNode methodNode, List<?> constraintData) throws GeneratorException {
		List<Constraint> constraint = new ArrayList<>();
		List<Constraint> constraintList = new ArrayList<>();
		
		for (Object constraintObject : constraintData) {
			for (Constraint constraintMethod : methodNode.getAllConstraints()) {
				if (((Constraint) constraintMethod).getName().equals(constraintObject.toString())) {
					constraintList.add(constraintMethod);
				}
			}
			
			if (constraintList.size() == 0) {
				GeneratorException.report(Localization.bundle.getString("userInputHelperWrongConstraintSuite") + constraintObject.toString());
			}
				
			constraint.addAll(constraintList);
			constraintList.clear();
		}
		
		return constraint;
	}
	
	private static Map<String, List<String>> getChoiceRestrictionsFromUserInput(Optional<Object> choiceRestrictions) throws GeneratorException {
		
		if (choiceRestrictions.isPresent()) {
			Object choiceRestrictionObject = choiceRestrictions.get();
			
			if (choiceRestrictionObject instanceof String) {
				return getChoiceRestrictionsFromUserInputString((String) choiceRestrictionObject);
			} else if (choiceRestrictionObject instanceof Map<?, ?>) {
				return getChoiceRestrictionsFromUserInputMap((Map<?, ?>) choiceRestrictionObject);
			} else {
				GeneratorException.report(Localization.bundle.getString("userInputHelperUnknownInput"));
				return null;
			}
			
		} else {
			return new HashMap<>();
		}
		
	}
	
	private static Map<String, List<String>> getChoiceRestrictionsFromUserInputString(String testData) throws GeneratorException {
		
		if (testData.equals("NONE")) {
			return new HashMap<>();
		} else {
			GeneratorException.report(Localization.bundle.getString("userInputHelperUnknownInputString"));
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String, List<String>> getChoiceRestrictionsFromUserInputMap(Map<?, ?> testMap) throws GeneratorException {
		return (Map<String, List<String>>) testMap;
	}
	 
	private static List<ChoiceNode> adjustChoicesUsingRestrictions(MethodParameterNode parameter, Map<String, List<String>> mapRestrictions) {
		
		List<String> choiceListUser = mapRestrictions.get(parameter.getFullName());
		List<ChoiceNode> choiceListParameter = extractChoices(new ArrayList<>(), parameter.getChoices());
		
		if (choiceListUser != null) {
			List<ChoiceNode> choiceListAdjusted = new ArrayList<>();
			
			for (String choiceUser : choiceListUser) {
				for (ChoiceNode choiceParameter : choiceListParameter) {		
					if (compareChoiceName(choiceParameter.getQualifiedName(), choiceUser)) {
						choiceListAdjusted.add(choiceParameter);
					}
				}
				
			}
			
			return choiceListAdjusted;
		}

		return choiceListParameter;
	}
	
	private static List<ChoiceNode> extractChoices(List<ChoiceNode> compound, List<ChoiceNode> choiceNodeList) {
		
		for (ChoiceNode node : choiceNodeList) {
			
			if (node.getChoices().size() != 0) {
				extractChoices(compound, node.getChoices());
				continue;
			}
			
			compound.add(node);
		}
		
		return compound;
	}
	
	private static boolean compareChoiceName(String choiceParameter, String choiceUser) {
		String[] choiceSplitParameter = choiceParameter.split(":");
		String[] choiceSplitUser = choiceUser.split(":");
		
		for (int i = 0 ; i < choiceSplitUser.length ; i++) {
			if (choiceSplitParameter[i].equals(choiceSplitUser[i])) {
				continue;
			}
			
			return false;
		}
		
		return true;
	}
	
	private static void validateChoiceRestrictionName(MethodNode methodNode, Map<String, List<String>> userRestriction) throws GeneratorException {
		Set<String> parameterNames = new HashSet<>(methodNode.getParametersNames());
		
		for (String key : userRestriction.keySet()) {
			if (parameterNames.contains(key)) {
				validateChoiceRestrictionArgument(methodNode.getMethodParameter(key), userRestriction.get(key));
			} else {
				GeneratorException.report(Localization.bundle.getString("userInputHelperWrongInputArgumentName") + key);
			}
		}
	}
	
	private static void validateChoiceRestrictionArgument(MethodParameterNode parameter, List<String> userRestrictionParameter) throws GeneratorException {
		List<ChoiceNode> choiceListParameter = extractChoices(new ArrayList<>(), parameter.getChoices());
		
		choiceRestrictionLoop:
		for (String choiceUser : userRestrictionParameter) {
			for (ChoiceNode choiceParameter : choiceListParameter) {		
				if (compareChoiceName(choiceParameter.getQualifiedName(), choiceUser)) {
					continue choiceRestrictionLoop;
				}
			}
			
			GeneratorException.report(Localization.bundle.getString("userInputHelperWrongInputChoiceName") + choiceUser);
		}
			
	}
	
 	public static RootNode loadEcFeedModelFromDirectory(Optional<String> path) throws GeneratorException {
		InputStream modelStream = null;

		try {
			if(path.isPresent())
				modelStream = Files.newInputStream(Paths.get(path.get()));
			else
				modelStream = System.in;
		} catch (IOException e) {
			GeneratorException.report(Localization.bundle.getString("userInputHelperWrongModel"));
		}
		
		RootNode model = null;
		
		try {
			ModelParser modelParser = new ModelParser();

			model = modelParser.parseModel(modelStream, null);
			model = ModelConverter.convertToCurrentVersion(model);
		} catch (ParserException e) {
			GeneratorException.report(Localization.bundle.getString("userInputHelperWrongModelParser"));
		} catch (ModelOperationException e) {
			GeneratorException.report(Localization.bundle.getString("userInputHelperWrongModelOperation"));
		}
		
		return model;
	}
 	
}
