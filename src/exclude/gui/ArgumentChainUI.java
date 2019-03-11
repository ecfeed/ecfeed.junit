// TODO - RAP-GEN
//package com.ecfeed.junit5.message;
//
//import java.util.List;
//import java.util.Optional;
//
//import com.ecfeed.core.junit5.EcFeedExtensionStore;
//import com.ecfeed.core.model.AbstractParameterNode;
//import com.ecfeed.core.model.ChoiceNode;
//import com.ecfeed.junit5.message.processor.DefaultProcessorUI;
//import com.ecfeed.junit5.message.processor.ResultErrorProcessorUI;
//import com.ecfeed.junit5.message.processor.ResultProgressProcessorUI;
//import com.ecfeed.junit5.message.processor.ResultStatusProcessorUI;
//import com.ecfeed.junit5.message.processor.ResultTestProcessorUI;
//import com.ecfeed.junit5.message.processor.ResultTotalProgressProcessorUI;
////import com.ecfeed.ui.dialogs.RestProgressMonitor;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public interface ArgumentChainUI {
//
//	public static ObjectMapper fMapper = new ObjectMapper();
//
//	public static Optional<List<ChoiceNode>> extract(
//			String json, List<AbstractParameterNode> parameters, 
//			RestProgressMonitor monitor, 
//			EcFeedExtensionStore store) {
//		ArgumentChainUI processor;
//
//		processor = new DefaultProcessorUI();
//		processor = new ResultErrorProcessorUI(processor);
//		processor = new ResultTotalProgressProcessorUI(processor);
//		processor = new ResultProgressProcessorUI(processor);
//		processor = new ResultStatusProcessorUI(processor, store);
//		processor = new ResultTestProcessorUI(processor, parameters, store);
//
//		return processor.process(json, monitor);
//	}
//
//	public Optional<List<ChoiceNode>> process(
//				String json, RestProgressMonitor monitor);
//
//}
