package com.ecfeed.core.junit5;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.ecfeed.core.utils.ExtensionContextResolver;
import com.ecfeed.core.utils.TestCasesUserInput;
import com.ecfeed.junit.annotation.AnnotationProcessor;
import com.ecfeed.junit.annotation.EcFeedInput;
import com.google.common.collect.Maps;

@Tag("Annotation")
@DisplayName("Get input data")
@ExtendWith(ExtensionContextResolver.class)
public class AnnotationInputGetTest {

	@Test
	@DisplayName("Generator")
	@EcFeedInput("'dataSource':'default'")
	void extractGeneratorNameTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getDataSource(),
							() -> "The generator name could not be extracted");
				},
				() -> {
					assertEquals("default", request.getDataSource(),
							() -> "The generator name is erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Coverage")
	@EcFeedInput("'properties':{'coverage':'default'}")
	void extractCoverageTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getProperties().get("coverage"),
							() -> "The coverage could not be extracted");
				},
				() -> {
					assertEquals("default", request.getProperties().get("coverage"),
							() -> "The coverage is erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Order")
	@EcFeedInput("'properties':{'n':'default'}")
	void extractOrderTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getProperties().get("n"),
							() -> "The order could not be extracted");
				},
				() -> {
					assertEquals("default", request.getProperties().get("n"),
							() -> "The order is erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Method")
	@EcFeedInput("'method':'default'")
	void extractMethodTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getMethod(),
							() -> "The method could not be extracted");
				},
				() -> {
					assertEquals("default", request.getMethod(),
							() -> "The method is erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Duplicates")
	@EcFeedInput("'properties':{'duplicates':'default'}")
	void extractDuplicatesTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getProperties().get("duplicates"),
							() -> "The duplicate flag could not be extracted");
				},
				() -> {
					assertEquals("default", request.getProperties().get("duplicates"),
							() -> "The duplicate flag is erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Depth")
	@EcFeedInput("'properties':{'depth':'default'}")
	void extractDepthTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getProperties().get("depth"),
							() -> "The depth could not be extracted");
				},
				() -> {
					assertEquals("default", request.getProperties().get("depth"),
							() -> "The depth is erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Length")
	@EcFeedInput("'properties':{'length':'default'}")
	void extractLengthTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getProperties().get("length"),
							() -> "The number of samples could not be extracted");
				},
				() -> {
					assertEquals("default", request.getProperties().get("length"),
							() -> "The number od samples is erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Test suites (text)")
	@EcFeedInput("'testSuites':'default'")
	void extractTestSuitesTextTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getTestSuites(),
							() -> "Test suites could not be extracted");
				},
				() -> {
					assertEquals("default", request.getTestSuites(),
							() -> "Test suites are erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Test suites (object)")
	@EcFeedInput("'testSuites':['suite 1','suite 2','suite 3']")
	void extractTestSuitesObjectTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		List<String> testObject = Arrays.asList("suite 1", "suite 2", "suite 3");
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getTestSuites(),
							() -> "Test suites could not be extracted");
				},
				() -> {
					assertEquals(testObject, request.getTestSuites(),
							() -> "Test suites are erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Constraints (text)")
	@EcFeedInput("'constraints':'default'")
	void extractConstraintsTextTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getConstraints(),
							() -> "Constraints could not be extracted");
				},
				() -> {
					assertEquals("default", request.getConstraints(),
							() -> "Constraints are erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Constraints (object)")
	@EcFeedInput("'constraints':['constraint 1','constraint 2','constraint 3']")
	void extractConstraintsObjectTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		List<String> testObject = Arrays.asList("constraint 1", "constraint 2", "constraint 3");
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getConstraints(),
							() -> "Constraints could not be extracted");
				},
				() -> {
					assertEquals(testObject, request.getConstraints(),
							() -> "Constraints are erroneous");
				}
		);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Choices (object)")
	@EcFeedInput("'choices':{'Argument C':['value 1C','value 2C','value 3C'],'Argument A':['value 1A','value 2A','value 3A'],'Argument B':['value 1B','value 2B','value 3B']}")
	void extractChoicesObjectTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		Map<String, List<String>> testObject = new HashMap<>();
		
		testObject.put("Argument A", Arrays.asList("value 1A", "value 2A", "value 3A"));
		testObject.put("Argument B", Arrays.asList("value 1B", "value 2B", "value 3B"));
		testObject.put("Argument C", Arrays.asList("value 1C", "value 2C", "value 3C"));
		
		assertAll("Compound",
				() -> {
					assertNotNull(request.getChoices(),
							() -> "Choices could not be extracted");
				},
				() -> {
					assertTrue(Maps.difference(testObject, (Map<String, List<String>>) request.getChoices()).areEqual(),
							() -> "Constraints are erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("N-Wise")
	@EcFeedInput("'dataSource':'nwise', 'properties':{'coverage':'100', 'n':'2'}")
	void getNWiseTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertEquals("nwise", request.getDataSource(),
							() -> "The generator name is erroneous");
				},
				() -> {
					assertEquals("100", request.getProperties().get("coverage"),
							() -> "The coverage is erroneous");
				},
				() -> {
					assertEquals("2", request.getProperties().get("n"),
							() -> "The order is erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Random")
	@EcFeedInput("'dataSource':'random', 'properties':{'length':'100', 'duplicates':'false'}")
	void getSampleTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertEquals("random", request.getDataSource(),
							() -> "The generator name is erroneous");
				},
				() -> {
					assertEquals("100", request.getProperties().get("length"),
							() -> "The number of samples is erroneous");
				},
				() -> {
					assertEquals("false", request.getProperties().get("duplicates"),
							() -> "The duplicates flag is erroneous");
				}
		);
	}
	
	@Test
	@DisplayName("Random adaptive")
	@EcFeedInput("'dataSource':'random', 'properties':{'depth':'10', 'length':'2', 'duplicates':'false'}")
	void getSampleAdaptiveTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertEquals("random", request.getDataSource(),
							() -> "The generator name is erroneous");
				},
				() -> {
					assertEquals("10", request.getProperties().get("depth"),
							() -> "The depth is erroneous");
				},
				() -> {
					assertEquals("2", request.getProperties().get("length"),
							() -> "The number of samples is erroneous");
				},
				() -> {
					assertEquals("false", request.getProperties().get("duplicates"),
							() -> "The duplicates flag is erroneous");
				}
		);
	}
	
	@Test 
	@DisplayName("Cartesian") 
	@EcFeedInput("'dataSource':'cartesian'")
	void getCartesianTest(ExtensionContext extensionContext) {
		TestCasesUserInput request = AnnotationProcessor.processInputSchema(extensionContext);
		
		assertAll("Compound",
				() -> {
					assertEquals("cartesian", request.getDataSource(),
							() -> "The generator name is erroneous");
				}
		);
	}
	
}
