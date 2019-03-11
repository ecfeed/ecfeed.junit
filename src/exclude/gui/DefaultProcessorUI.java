// TODO - RAP-GEN
//package com.ecfeed.junit5.message.processor;
//
//import java.util.List;
//import java.util.Optional;
//
//import com.ecfeed.core.junit5.utils.Localization;
//import com.ecfeed.core.model.ChoiceNode;
//import com.ecfeed.junit5.message.ArgumentChainUI;
//import com.ecfeed.ui.dialogs.RestProgressMonitor;
//
//public class DefaultProcessorUI implements ArgumentChainUI {
//
//	@Override
//	public Optional<List<ChoiceNode>> process(
//			String json, RestProgressMonitor monitor) {
//		
//		if (json == null) {
//			throw new NullPointerException(Localization.bundle.getString("argumentChainErroneousJsonData"));
//		}
//		
//		return Optional.empty();
//	}
//	
//	@Override
//	public String toString( ) {
//		return getClass().getSimpleName();
//	}
//}
