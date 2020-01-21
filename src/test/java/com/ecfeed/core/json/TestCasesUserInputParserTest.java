package com.ecfeed.core.json;

import com.ecfeed.core.utils.DataSource;
import com.ecfeed.core.utils.TestCasesUserInput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class TestCasesUserInputParserTest {

	@Test
	public void parseAllTags() {

		String request = 
				"{ " + 
						"'dataSource' : 'static', " + 
						"'method' : 'method1', " + 
						"'suiteSize' : 'suiteSize1', " + 
						"'properties':{" +
						"'coverage' : 'coverage1', " +
						"'n' : 'N1', " +
						"'duplicates' : 'duplicates1', " + 
						"'length' : 'length1', " +
						"'candidates' : 'candidates1' " +
						"}," +
						"'testSuites' : 'testSuites1', " + 
						"'constraints' : 'constraints1', " + 
						"'choices' : 'choices1' " + 
						"}";

		request = request.replace("'", "\"");

		TestCasesUserInput testCasesRequest = TestCasesUserInputParser.parseRequest(request);

		DataSource dataSource = null;
		try {
			dataSource = DataSource.parse(testCasesRequest.getDataSource());
		} catch (Exception e) {
			fail();
		}
		assertEquals(DataSource.STATIC, dataSource);
		assertEquals("method1", testCasesRequest.getMethod());
		assertEquals("suiteSize1", testCasesRequest.getSuiteSize());
		assertEquals("coverage1", testCasesRequest.getProperties().get("coverage"));
		assertEquals("N1", testCasesRequest.getProperties().get("n"));
		assertEquals("duplicates1", testCasesRequest.getProperties().get("duplicates"));
		assertEquals("length1", testCasesRequest.getProperties().get("length"));
		assertEquals("candidates1", testCasesRequest.getProperties().get("candidates"));
		assertEquals("testSuites1", testCasesRequest.getTestSuites());
		assertEquals("constraints1", testCasesRequest.getConstraints());
		assertEquals("choices1", testCasesRequest.getChoices());
	}


}