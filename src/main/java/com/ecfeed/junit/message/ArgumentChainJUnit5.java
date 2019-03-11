package com.ecfeed.junit.message;

import java.lang.reflect.Parameter;
import java.util.Optional;

import org.junit.jupiter.params.provider.Arguments;

import com.ecfeed.junit.EcFeedExtensionStore;
import com.ecfeed.junit.message.processor.DefaultProcessorJUnit5;
import com.ecfeed.junit.message.processor.RequestUpdateProcessorJUnit5;
import com.ecfeed.junit.message.processor.ResultErrorProcessorJUnit5;
import com.ecfeed.junit.message.processor.ResultInfoProcessorJUnit5;
import com.ecfeed.junit.message.processor.ResultProgressProcessorJUnit5;
import com.ecfeed.junit.message.processor.ResultStatusProcessorJUnit5;
import com.ecfeed.junit.message.processor.ResultTestProcessorJUnit5;
import com.ecfeed.junit.message.processor.ResultTotalProgressProcessorJUnit5;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface ArgumentChainJUnit5 {
	
	public static ObjectMapper fMapper = new ObjectMapper(); // TODO remove public

	public static Optional<Arguments> extract(String json, Parameter[] parameters, EcFeedExtensionStore store) {
		
		ArgumentChainJUnit5 processor;
		
		processor = new DefaultProcessorJUnit5(store);
		processor = new RequestUpdateProcessorJUnit5(processor, store);
		processor = new ResultInfoProcessorJUnit5(processor);
		processor = new ResultErrorProcessorJUnit5(processor);
		processor = new ResultTotalProgressProcessorJUnit5(processor);
		processor = new ResultProgressProcessorJUnit5(processor);
		processor = new ResultStatusProcessorJUnit5(processor, store);
		processor = new ResultTestProcessorJUnit5(processor, parameters, store);
		
		return processor.process(json);
	}
	
	public Optional<Arguments> process(String json);
	
}
