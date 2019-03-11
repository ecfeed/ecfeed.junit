package com.ecfeed.core.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ecfeed.core.utils.TestCasesUserInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

@Tag("Annotation")
@DisplayName("Set input data")
public class AnnotationInputSetTest {

	private static ObjectMapper mapper = new ObjectMapper();
	private static String stringValue = "DEFAULT";
	
	TestCasesUserInput schema;
	
	@BeforeEach
	void beforeEach() {
		schema = new TestCasesUserInput();
	}
	
	@Test
	@DisplayName("Test suite")
	@SuppressWarnings("unchecked")
	void schemaTestSuiteTest() {
		List<String> testObject = Arrays.asList("suite 1", "suite 2", "suite 3");
		
		try {
			schema.setTestSuites(stringValue);
			String jsonText = mapper.writeValueAsString(schema);
			TestCasesUserInput resultSchema = mapper.reader().forType(TestCasesUserInput.class).readValue(jsonText);
			assertEquals(stringValue, resultSchema.getTestSuites(), () -> "The parsed JSON value is erroneous");
		} catch (IOException e) {
			fail(() -> "Could not manipulate JSON data");
		}
		
		try {
			schema.setTestSuites(testObject);
			String jsonText = mapper.writeValueAsString(schema);
			TestCasesUserInput resultSchema = mapper.reader().forType(TestCasesUserInput.class).readValue(jsonText);

			List<String> resultObject = (ArrayList<String>) resultSchema.getTestSuites();
			assertEquals(testObject, resultObject, () -> "The parsed JSON value is erroneous");
		} catch (IOException e) {
			fail(() -> "Could not manipulate JSON data");
		}
		
	}
	
	@Test
	@DisplayName("Constraints")
	@SuppressWarnings("unchecked")
	void schemaConstraintsTest() {
		List<String> testObject = Arrays.asList("constraint 1", "constraint 2", "constraint 3");
		
		try {
			schema.setConstraints(stringValue);
			String jsonText = mapper.writeValueAsString(schema);
			TestCasesUserInput resultSchema = mapper.reader().forType(TestCasesUserInput.class).readValue(jsonText);
			assertEquals(stringValue, resultSchema.getConstraints(), () -> "The parsed JSON value is erroneous");
		} catch (IOException e) {
			fail(() -> "Could not manipulate JSON data");
		}
		
		try {
			schema.setConstraints(testObject);
			String jsonText = mapper.writeValueAsString(schema);
			TestCasesUserInput resultSchema = mapper.reader().forType(TestCasesUserInput.class).readValue(jsonText);

			List<String> resultObject = (ArrayList<String>) resultSchema.getConstraints();
			assertLinesMatch(testObject, resultObject);
		} catch (IOException e) {
			fail(() -> "Could not manipulate JSON data");
		}
		
	}
	
	@Test
	@DisplayName("Choices")
	@SuppressWarnings("unchecked")
	void schemaChoicesTest() {
		Map<String, List<String>> testObject = new HashMap<>();
		
		testObject.put("Argument A", Arrays.asList("value 1A", "value 2A", "value 3A"));
		testObject.put("Argument B", Arrays.asList("value 1B", "value 2B", "value 3B"));
		testObject.put("Argument C", Arrays.asList("value 1C", "value 2C", "value 3C"));
		
		try {
			schema.setChoices(stringValue);
			String jsonText = mapper.writeValueAsString(schema);
			TestCasesUserInput resultSchema = mapper.reader().forType(TestCasesUserInput.class).readValue(jsonText);
			assertEquals(stringValue, resultSchema.getChoices(), () -> "The parsed JSON value is erroneous");
		} catch (IOException e) {
			fail(() -> "Could not manipulate JSON data");
		}
		
		try {
			schema.setChoices(testObject);
			String jsonText = mapper.writeValueAsString(schema);
			TestCasesUserInput resultSchema = mapper.reader().forType(TestCasesUserInput.class).readValue(jsonText);

			Map<String, List<String>> resultObject = (Map<String, List<String>>) resultSchema.getChoices();
			assertTrue(Maps.difference(testObject, resultObject).areEqual(), () -> "The parsed JSON value is erroneous");
		} catch (IOException e) {
			fail(() -> "Could not manipulate JSON data");
		}
		
	}
}
