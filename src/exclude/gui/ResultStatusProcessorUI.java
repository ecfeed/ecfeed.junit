// // TODO - RAP-GEN
//package com.ecfeed.junit5.message.processor;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
//import com.ecfeed.core.junit5.EcFeedExtensionStore;
//import com.ecfeed.core.junit5.message.schema.ResultStatusSchema;
//import com.ecfeed.core.junit5.utils.Localization;
//import com.ecfeed.core.junit5.utils.Logger;
//import com.ecfeed.core.model.ChoiceNode;
//import com.ecfeed.junit5.message.ArgumentChainUI;
//import com.ecfeed.ui.dialogs.RestProgressMonitor;
//
//public class ResultStatusProcessorUI implements ArgumentChainUI {
//	
//	private final ArgumentChainUI fChain;
//	private final EcFeedExtensionStore fStore;
//	
//	public ResultStatusProcessorUI(ArgumentChainUI chain, EcFeedExtensionStore store) {
//		fChain = chain;
//		fStore = store;
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
//			ResultStatusSchema response = fMapper.reader().forType(ResultStatusSchema.class).readValue(json);
//
//			if (response.getStatus().equals("BEG_DATA")) {
//				fStore.setStreamId(response.getId());
//			}
//			
//			if (response.getStatus().equals("END_DATA")) {
//				monitor.setCanceled(true);
//			}
//			
//			if (response.getStatus().equals("BEG_CHUNK")) {
//				
//				if (response.getId().equals(fStore.getStreamId())) {
//					fStore.setChunkProgress(true);
//				} else {
//					RuntimeException exception = new RuntimeException(Localization.bundle.getString("argumentChainWrongChunkIdentifier"));
//					Logger.exception(exception);
//					throw exception;
//				}
//				
//			}
//				
//			if (response.getStatus().equals("END_CHUNK")) {
//				fStore.setChunkProgress(false);
//			}
//			
//			if (response.getStatus().equals("ACKNOWLEDGED")) {
//			}
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
