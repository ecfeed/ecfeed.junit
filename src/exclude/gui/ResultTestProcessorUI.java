// TODO - RAP-GEN
//package com.ecfeed.junit5.message.processor;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
//import com.ecfeed.core.junit5.EcFeedExtensionStore;
//import com.ecfeed.core.junit5.message.ArgumentChainTypeParser;
//import com.ecfeed.core.junit5.message.schema.ResultTestSchema;
//import com.ecfeed.core.junit5.utils.Localization;
//import com.ecfeed.core.junit5.utils.Logger;
//import com.ecfeed.core.model.AbstractParameterNode;
//import com.ecfeed.core.model.ChoiceNode;
//import com.ecfeed.junit5.message.ArgumentChainUI;
//import com.ecfeed.ui.common.utils.SwtObjectHelper;
//import com.ecfeed.ui.dialogs.RestProgressMonitor;
//
//public class ResultTestProcessorUI implements ArgumentChainUI {
//	
//	private final EcFeedExtensionStore fStore;
//	private final ArgumentChainUI fChain;
//	private final List<AbstractParameterNode> fParameters;
//	
//	public ResultTestProcessorUI(ArgumentChainUI chain, List<AbstractParameterNode> parameters, EcFeedExtensionStore store) {
//		 fChain = chain;
//		fStore = store;
//		fParameters = parameters;
//	}
//	
//	@Override
//	public Optional<List<ChoiceNode>> process(String json, RestProgressMonitor monitor) {
//		
//		try {
//			ResultTestSchema response = fMapper.reader().forType(ResultTestSchema.class).readValue(json);
//
//			if (!fStore.getChunkProgress()) {
//				RuntimeException exception = new RuntimeException(Localization.bundle.getString("argumentChainFlagStartMissingError"));
//				Logger.exception(exception);
//				throw exception;
//			}
//			
//			 SwtObjectHelper.updateProgressMonitor(monitor.getProgressMonitor(), m -> m.worked(1));
//			
//			return ArgumentChainTypeParser.parseRap(response, fParameters);
//		} catch (IOException e) {
//			return fChain.process(json, monitor);
//			return null;
//		}
//		
//	}
//	
//	@Override
//	public String toString( ) {
//		return getClass().getSimpleName();
//	}
//	
//}
