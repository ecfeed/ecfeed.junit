package com.ecfeed.core.json;

import com.ecfeed.core.generators.DataSourceHelper;
import com.ecfeed.core.utils.TestCasesUserInput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestCasesUserInputParserTest {

	@Test
	public void parseAllTags() {

		String request = 
				"{ " + 
						"'dataSource' : 'static', " + 
						"'method' : 'method1', " + 
						"'suiteSize' : 'suiteSize1', " + 
						"'coverage' : 'coverage1', " + 
						"'N' : 'N1', " + 
						"'duplicates' : 'duplicates1', " + 
						"'depth' : 'depth1', " + 
						"'length' : 'length1', " + 
						"'candidates' : 'candidates1', " + 
						"'testSuites' : 'testSuites1', " + 
						"'constraints' : 'constraints1', " + 
						"'choices' : 'choices1' " + 
						"}";

		request = request.replace("'", "\"");

		TestCasesUserInput testCasesRequest = TestCasesUserInputParser.parseRequest(request);

		assertEquals(DataSourceHelper.dataSourceStaticContent, testCasesRequest.getDataSource());
		assertEquals("method1", testCasesRequest.getMethod());
		assertEquals("suiteSize1", testCasesRequest.getSuiteSize());
		assertEquals("coverage1", testCasesRequest.getCoverage());
		assertEquals("N1", testCasesRequest.getN());
		assertEquals("duplicates1", testCasesRequest.getDuplicates());
		assertEquals("depth1", testCasesRequest.getDepth());
		assertEquals("length1", testCasesRequest.getLength());
		assertEquals("candidates1", testCasesRequest.getCandidates());
		assertEquals("testSuites1", testCasesRequest.getTestSuites());
		assertEquals("constraints1", testCasesRequest.getConstraints());
		assertEquals("choices1", testCasesRequest.getChoices());
	}


}