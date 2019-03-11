package com.ecfeed.core.json;


import com.ecfeed.core.utils.TestCasesRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestCasesRequestParserTest {

	@Test
	public void parseRequest() {

		String request = "{ 'method' : 'METHOD1', 'model' : 'MODEL1', 'userData' : 'DATA1' }";
		request = request.replace("'", "\"");

		TestCasesRequest testCasesRequest = TestCasesRequestParser.parseRequest(request);

		assertEquals("METHOD1", testCasesRequest.getMethod());
		assertEquals("MODEL1", testCasesRequest.getModel());
		assertEquals("DATA1", testCasesRequest.getUserData());
	}

}