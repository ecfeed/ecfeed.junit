// TODO - RAP-GEN
//package com.ecfeed.junit5.message.processor;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
//import com.ecfeed.core.junit5.message.schema.ResultErrorSchema;
//import com.ecfeed.core.junit5.utils.Localization;
//import com.ecfeed.core.model.ChoiceNode;
//import com.ecfeed.junit5.message.ArgumentChainUI;
//import com.ecfeed.ui.dialogs.RestProgressMonitor;
//
//public class ResultErrorProcessorUI implements ArgumentChainUI {
//	
//	private final ArgumentChainUI fChain;
//	
//	public ResultErrorProcessorUI(ArgumentChainUI chain) {
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
//			ResultErrorSchema response = fMapper.reader().forType(ResultErrorSchema.class).readValue(json);
//			
//			throw new RuntimeException(response.getError());
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
