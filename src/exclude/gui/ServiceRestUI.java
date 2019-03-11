// TODO - RAP-GEN
//package com.ecfeed.junit5.runner;
//
//import java.lang.reflect.InvocationTargetException;
//import java.util.List;
//import java.util.Optional;
//
////import org.eclipse.core.runtime.IProgressMonitor;
////import org.eclipse.jface.operation.IRunnableWithProgress;
//
//import com.ecfeed.core.junit5.EcFeedExtensionStore;
//import com.ecfeed.core.junit5.annotation.AnnotationDefaultValue;
//import com.ecfeed.core.junit5.message.schema.RequestChunkSchema;
//import com.ecfeed.core.junit5.runner.web.ServiceRestTemplate;
//import com.ecfeed.core.junit5.utils.Localization;
//import com.ecfeed.core.junit5.utils.Logger;
//import com.ecfeed.core.model.AbstractParameterNode;
//import com.ecfeed.core.model.ChoiceNode;
//import com.ecfeed.core.utils.TestCasesRequest;
//import com.ecfeed.junit5.message.ArgumentChainUI;
//import com.ecfeed.ui.common.utils.SwtObjectHelper;
//import com.ecfeed.ui.dialogs.RestProgressMonitor;
//import com.ecfeed.utils.SaveHelper;
//
//public final class ServiceRestUI extends ServiceRestTemplate implements IRunnableWithProgress 
//{
//
//	private RestProgressMonitor fMonitor;
//	private List<AbstractParameterNode> fParameters;
//	List<List<ChoiceNode>> fGeneratedData;
//
//	EcFeedExtensionStore fStore;
//
//	public ServiceRestUI(
//			RestProgressMonitor monitor, 
//			List<AbstractParameterNode> parameters, TestCasesRequest request, List<List<ChoiceNode>> generatedData) {
//		super(request, AnnotationDefaultValue.DEFAULT_TARGET_ECFEED);
//
//		SaveHelper.saveCurrentState();
//		
//		fGeneratedData = generatedData;
//		fParameters = parameters;
//		fMonitor = monitor;
//
//		fStore = new EcFeedExtensionStore();
//
//		fClientVersion = "1.0";
//		fClientType = "RAP";
//	}
//	
//	@Override
//	protected void consumeReceivedMessage(String message) {
//		Optional<List<ChoiceNode>> testCase = 
//				ArgumentChainUI.extract(message, fParameters, fMonitor, fStore);
//
//		if (testCase.isPresent()) {
//			fGeneratedData.add(testCase.get());
//		}
//
//	}
//
//	@Override
//	protected void lifecycleEnd() {
//		SwtObjectHelper.updateProgressMonitor(fMonitor.getProgressMonitor(), IProgressMonitor::done);
//	}
//
//	@Override
//	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//		startRestClient();
//	}
//
//	@Override
//	protected boolean cancelExecution() {
//		return fMonitor.isCanceled() || fMonitor.getProgressMonitor().isCanceled();
//	}
//
//	@Override
//	protected void waitForStreamEnd() {
//
//		if (fStore.getChunkProgress()) {
//			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestFlagEndMissingError"));
//			Logger.exception(exception);
//			throw exception;
//		}
//
//	}
//
//	@Override
//	protected Object sendUpdatedRequest() {
//
//		RequestChunkSchema request = new RequestChunkSchema();
//		request.setId(fStore.getStreamId());
//
//		return request;
//	}
//
//	@Override
//	protected void handleException(Exception e) {
//		SwtObjectHelper.showErrorDialog("Exception", e);
//
//		new RuntimeException(e);
//	}
//
//}
