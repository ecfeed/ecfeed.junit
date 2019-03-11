// TODO - RAP-GEN
//package com.ecfeed.junit5.message.processor;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
//import com.ecfeed.core.junit5.message.schema.ResultTotalProgressSchema;
//import com.ecfeed.core.junit5.utils.Localization;
//import com.ecfeed.core.model.ChoiceNode;
//import com.ecfeed.junit5.message.ArgumentChainUI;
//import com.ecfeed.ui.common.utils.SwtObjectHelper;
//import com.ecfeed.ui.dialogs.RestProgressMonitor;
//
//public class ResultTotalProgressProcessorUI implements ArgumentChainUI {
//	
//	private final ArgumentChainUI fChain;
//	
//	public ResultTotalProgressProcessorUI(ArgumentChainUI chain) {
//		fChain = chain;
//	}
//	
//	@Override
//	public Optional<List<ChoiceNode>> process(String json, RestProgressMonitor monitor) {
//		
//		if (json == null) {
//			throw new NullPointerException(Localization.bundle.getString("argumentChainErroneousJsonData"));
//		}
//		
//		try {
//			ResultTotalProgressSchema response = fMapper.reader().forType(ResultTotalProgressSchema.class).readValue(json);
//			
//			SwtObjectHelper.updateProgressMonitor(
//					monitor.getProgressMonitor(), 
//					m -> m.beginTask("Generating test data", response.getTotalProgress()));
//			
//			return Optional.empty();
//		} catch (IOException e) {
//			return fChain.process(json, monitor);
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
