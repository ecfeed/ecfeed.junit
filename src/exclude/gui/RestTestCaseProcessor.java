// TODO - RAP-GEN
//package com.ecfeed.junit5.runner;
//
//import java.lang.reflect.InvocationTargetException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import com.ecfeed.application.ApplicationContext;
//import com.ecfeed.application.PlatformDependentDataStore;
//import com.ecfeed.core.generators.DataSourceHelper;
//import com.ecfeed.core.json.JsonHelper;
//import com.ecfeed.core.model.ChoiceNode;
//import com.ecfeed.core.model.Constraint;
//import com.ecfeed.core.model.MethodNode;
//import com.ecfeed.core.model.MethodNodeHelper;
//import com.ecfeed.core.utils.TestCasesRequest;
//import com.ecfeed.core.utils.TestCasesUserInput;
//import com.ecfeed.ui.common.utils.SwtObjectHelper;
//import com.ecfeed.ui.dialogs.GeneratorSetupDialog;
//import com.ecfeed.ui.dialogs.RestProgressMonitor;
//import com.ecfeed.view.JavaViewHelper;
//
//public class RestTestCaseProcessor {
//
//	private MethodNode fMethodNode;
//
//	private GeneratorSetupDialog fDialog;
//	private RestProgressMonitor fProgressMonitor;
//
//	private boolean fCanceled;
//	private List<List<ChoiceNode>> fGeneratedData;
//
//	public RestTestCaseProcessor(MethodNode methodNode, GeneratorSetupDialog dialog) {
//		this.fMethodNode = methodNode;
//		this.fDialog = dialog;
//
//		fProgressMonitor = new RestProgressMonitor(SwtObjectHelper.getActiveShell());
//		fGeneratedData = new ArrayList<List<ChoiceNode>>();
//		fCanceled = false;
//	}
//
//	public List<List<ChoiceNode>> getGeneretedData() {
//		return fGeneratedData;
//	}
//
//	public boolean getCanceled() {
//		return fCanceled;
//	}
//
//	public RestProgressMonitor getProgressMonitor() {
//		return fProgressMonitor; 
//	}
//
//	public void process() {
//		generateTestSuiteUsingWebService(getWebRequest());
//	}
//
//	private TestCasesRequest getWebRequest() {
//		Map<String, Object> parameterMap = fDialog.getGeneratorParameters();
//		Map<String, Object> parameterMap = null;
//
//		TestCasesUserInput userInput = new TestCasesUserInput();
//		userInput.setDataSource(getGenerator(fDialog.getSelectedGeneratorName())); 
//
//		Object element;
//
//		element = parameterMap.get("Length");
//		if (element != null) {
//			userInput.setLength(element.toString());
//		}
//
//		element = parameterMap.get("Duplicates");
//		if (element != null) {
//			userInput.setDuplicates(element.toString());
//		}
//
//		element = parameterMap.get("Coverage");
//		if (element != null) {
//			userInput.setCoverage(element.toString());
//		}
//
//		element = parameterMap.get("N");
//		if (element != null) {
//			userInput.setN(element.toString());
//		}
//
//		element = parameterMap.get("Candidate set size");
//		if (element != null) {
//			userInput.setCandidates(element.toString());
//		}
//
//		element = parameterMap.get("Depth");
//		if (element != null) {
//			userInput.setDepth(element.toString());
//		}
//
//		element = fDialog.getConstraints().stream().map(Constraint::getName).collect(Collectors.toList());
//		if (element != null) {
//			userInput.setConstraints(element);
//		}
//
//		element = fDialog.getAlgorithmInputMap();
//		if (element != null) {
//			userInput.setChoices(element);
//		}
//
//		TestCasesRequest request = new TestCasesRequest();
//		request.setUserData(getUserData(userInput));
//
//		if (ApplicationContext.isApplicationTypeRemoteRap()) {
//			request.setModelName(PlatformDependentDataStore.getCurrentModelUuid());
//		} else {
//			request.setModelName(fMethodNode.getParent().getParent().getFullName());
//		}
//		request.setMethod(getMethodName());
//		return request;
//	}
//
//	private String getGenerator(String generatorName) {
//		switch (generatorName) {
//		case "N-wise generator" : return DataSourceHelper.dataSourceGenNWise; // TODO
//		case "Cartesian Product generator" : return DataSourceHelper.dataSourceGenCartesian; // TODO
//		case "Adaptive random generator" : return DataSourceHelper.dataSourceGenAdaptiveRandom; // TODO
//		case "Random generator" : return DataSourceHelper.dataSourceGenRandom; // TODO
//		}
//
//		throw new RuntimeException("Invalid generator name."); 
//	}
//
//	private String getModelName() {
//		return fMethodNode.getParent().getParent().getFullName();
//	}
//
//	private String getMethodName() {
//		StringBuilder builder = new StringBuilder();
//
//		builder.append(fMethodNode.getParent().getFullName());
//		builder.append(".");
//		builder.append(MethodNodeHelper.getSimpleName(JavaViewHelper.switchToJavaView(fMethodNode.makeClone())));
//
//		return  builder.toString();
//	}
//
//	private String getUserData(TestCasesUserInput userInput) {
//		String userData = JsonHelper.parseObject(userInput);
//
//		return userData;
//	}
//
//	private void generateTestSuiteUsingWebService(TestCasesRequest request) {
//
//		try {
//			ServiceRestUI runnable = new ServiceRestUI(fProgressMonitor, fMethodNode.getParameters(), request, fGeneratedData);
//
//			fProgressMonitor.getProgressMonitorDialog().open();
//			fProgressMonitor.getProgressMonitorDialog().run(true, true, runnable);
//		} catch (InvocationTargetException e ) {
//			fCanceled = true;
//			SwtObjectHelper.showErrorDialog("Exception", new Exception("Could not connect to the server"));
//		} catch (Exception e) {
//			fCanceled = true;
//			SwtObjectHelper.showErrorDialog("Exception", e);
//		}
//
//		fCanceled |= fProgressMonitor.getProgressMonitor().isCanceled();
//
//		if (fCanceled) {
//			fGeneratedData =  new ArrayList<List<ChoiceNode>>();
//		}
//	}
//
//}
