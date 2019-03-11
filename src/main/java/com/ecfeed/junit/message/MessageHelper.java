package com.ecfeed.junit.message;

import java.util.List;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.FixedChoiceValueFactory;
import com.ecfeed.junit.message.schema.ChoiceSchema;
import com.ecfeed.junit.message.schema.RequestUpdateSchema;
import com.ecfeed.junit.message.schema.ResultErrorSchema;
import com.ecfeed.junit.message.schema.ResultInfoSchema;
import com.ecfeed.junit.message.schema.ResultProgressSchema;
import com.ecfeed.junit.message.schema.ResultStatusSchema;
import com.ecfeed.junit.message.schema.ResultTestSchema;
import com.ecfeed.junit.message.schema.ResultTotalProgressSchema;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class MessageHelper {

	private static ObjectMapper fMapper = new ObjectMapper();
	private static FixedChoiceValueFactory fFactory = new FixedChoiceValueFactory(null, false);
	
	private MessageHelper() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
	public static String resultStartDataSchema(String sessionId, boolean collectStats) {
		ResultStatusSchema result = new ResultStatusSchema();
		result.setStatus("BEG_DATA");
		result.setId(sessionId);
		result.setCollectStats(collectStats);

		try {
			return fMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("messageHelperStartData"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
	public static String resultStartChunkSchema(String sessionId) {
		ResultStatusSchema result = new ResultStatusSchema();
		result.setStatus("BEG_CHUNK");
		result.setId(sessionId);

		try {
			return fMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("messageHelperStartChunk"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
	public static String resultEndDataSchema(String sessionId) {
		ResultStatusSchema result = new ResultStatusSchema();
		result.setStatus("END_DATA");
		result.setId(sessionId);

		try {
			return fMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("messageHelperEndData"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
	public static String resultEndChunkSchema() {
		ResultStatusSchema result = new ResultStatusSchema();
		result.setStatus("END_CHUNK");

		try {
			return fMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("messageHelperEndChunk"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
	public static String resultErrorSchema(String error) {
		ResultErrorSchema result = new ResultErrorSchema();
		result.setError(error);
		
		try {
			return fMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("messageHelperError"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
	public static String resultInfoSchema(String info) {
		ResultInfoSchema result = new ResultInfoSchema();
		result.setInfo(info);
		
		try {
			return fMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("messageHelperError"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
	public static String resultAcknowledgeSchema() {
		ResultStatusSchema result = new ResultStatusSchema();
		result.setStatus("ACKNOWLEDGED");
		
		try {
			return fMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("messageHelperAcknowledge"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
	public static String resultTotalProgressSchema(int totalProgress) {
		ResultTotalProgressSchema result = new ResultTotalProgressSchema();
		result.setTotalProgress(totalProgress);
		
		try {
			return fMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("messageHelperTotalProgress"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
	public static String resultProgressSchema(int progress) {
		ResultProgressSchema result = new ResultProgressSchema();
		result.setProgress(progress);
		
		try {
			return fMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("messageHelperProgress"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
	public static String resultUpdateSchema() {
		RequestUpdateSchema result = new RequestUpdateSchema();
		
		try {
			return fMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("messageHelperUpdate"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
	public static String resultTestSchema(List<ChoiceNode> tuple, String parameterId) {
		ResultTestSchema result = new ResultTestSchema();
		
		ChoiceSchema[] parameter = new ChoiceSchema[tuple.size()];
		
		for (int i = 0 ; i < tuple.size() ; i++) {
			ChoiceNode choiceNode = tuple.get(i);
			
			parameter[i] = new ChoiceSchema();
			parameter[i].setName(choiceNode.getFullName());
			
			if (choiceNode.isRandomizedValue()) {
				parameter[i].setValue(fFactory.createValue(choiceNode) + "");
			} else {
				parameter[i].setValue(choiceNode.getValueString());
			}
			
		}
		
		result.setTestCase(parameter);
		result.setId(parameterId);
		
		try {
			return fMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("messageHelperTest"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
	}
	
}
