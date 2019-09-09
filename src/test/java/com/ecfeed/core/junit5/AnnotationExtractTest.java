package com.ecfeed.core.junit5;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.ecfeed.core.utils.ExtensionContextResolver;
import com.ecfeed.junit.AnnotationProcessor;
import com.ecfeed.junit.annotation.EcFeedInput;
import com.ecfeed.junit.annotation.EcFeedKeyStore;
import com.ecfeed.junit.annotation.EcFeedModel;
import com.ecfeed.junit.annotation.EcFeedService;

@Tag("Annotation")
@DisplayName("Extract annotations")
@ExtendWith(ExtensionContextResolver.class)
public class AnnotationExtractTest {
	
	@Nested
	@DisplayName("Not available")
	class MissingAnnotation {
		
		@Test
		@DisplayName("Service")
		void ecFeedConnectionTest(ExtensionContext extensionContext) {
			Optional<EcFeedService> annotation = AnnotationProcessor.extractService(extensionContext);
			assertEquals(annotation.isPresent(), false,
					() -> "The annotation is missing, and therefore, its value should not be available");
		}
		
		@Test
		@DisplayName("Model")
		void ecFeedModelTest(ExtensionContext extensionContext) {
			Optional<EcFeedModel> annotation = AnnotationProcessor.extractModel(extensionContext);
			assertEquals(annotation.isPresent(), false,
					() -> "The annotation is missing, and therefore, its value should not be available");
		}
		
		@Test
		@DisplayName("Input")
		void ecFeedUserDataTest(ExtensionContext extensionContext) {
			Optional<EcFeedInput> annotation = AnnotationProcessor.extractUserInput(extensionContext);
			assertEquals(annotation.isPresent(), false,
					() -> "The annotation is missing, and therefore, its value should not be available");
		}
		
		@Test
		@DisplayName("KeyStore")
		void ecFeedStoreTest(ExtensionContext extensionContext) {
			Optional<EcFeedKeyStore> annotation = AnnotationProcessor.extractKeyStore(extensionContext);
			assertEquals(annotation.isPresent(), false,
					() -> "The annotation is missing, and therefore, its value should not be available");
		}
		
	}
	
	@Nested
	@DisplayName("Method level")
	class MethodLevel {
		
		@Test
		@DisplayName("Target")
		@EcFeedService("Default target")
		void ecFeedConnectionTest(ExtensionContext extensionContext) {
			Optional<EcFeedService> annotation = AnnotationProcessor.extractService(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The method is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default target",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
		@Test
		@DisplayName("Model")
		@EcFeedModel("Default model")
		void ecFeedModelTest(ExtensionContext extensionContext) {
			Optional<EcFeedModel> annotation = AnnotationProcessor.extractModel(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The method is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default model",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
		@Test
		@DisplayName("Input")
		@EcFeedInput("Default input")
		void ecFeedUserDataTest(ExtensionContext extensionContext) {
			Optional<EcFeedInput> annotation = AnnotationProcessor.extractUserInput(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The method is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default input",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
		@Test
		@DisplayName("KeyStore")
		@EcFeedKeyStore("Default store")
		void ecFeedStoreTest(ExtensionContext extensionContext) {
			Optional<EcFeedKeyStore> annotation = AnnotationProcessor.extractKeyStore(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The method is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default store",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
	}
	
	@Nested
	@DisplayName("Class level")
	@EcFeedService("Default target")
	@EcFeedModel("Default model")
	@EcFeedInput("Default input")
	@EcFeedKeyStore("Default keystore")
	class ClassLevel {
		
		@Test
		@DisplayName("Target")
		void ecFeedConnectionTest(ExtensionContext extensionContext) {
			Optional<EcFeedService> annotation = AnnotationProcessor.extractService(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The class is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default target",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
		@Test
		@DisplayName("Model")
		void ecFeedModelTest(ExtensionContext extensionContext) {
			Optional<EcFeedModel> annotation = AnnotationProcessor.extractModel(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The class is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default model",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
		@Test
		@DisplayName("Input")
		void ecFeedUserDataTest(ExtensionContext extensionContext) {
			Optional<EcFeedInput> annotation = AnnotationProcessor.extractUserInput(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The class is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default input",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
		@Test
		@DisplayName("KeyStore")
		void ecFeedStoreTest(ExtensionContext extensionContext) {
			Optional<EcFeedKeyStore> annotation = AnnotationProcessor.extractKeyStore(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The method is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default keystore",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
	}
	
	@Nested
	@DisplayName("Class priority")
	@EcFeedService("Default target erroneous")
	@EcFeedModel("Default model erroneous")
	@EcFeedInput("Default input erroneous")
	@EcFeedKeyStore("Default keystore erroneous")
	class PriorityLevel {
		
		@Test
		@DisplayName("Target")
		@EcFeedService("Default target")
		void ecFeedConnectionTest(ExtensionContext extensionContext) {
			Optional<EcFeedService> annotation = AnnotationProcessor.extractService(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The method/class is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default target",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
		@Test
		@DisplayName("Model")
		@EcFeedModel("Default model")
		void ecFeedModelTest(ExtensionContext extensionContext) {
			Optional<EcFeedModel> annotation = AnnotationProcessor.extractModel(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The method/class is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default model",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
		@Test
		@DisplayName("Input")
		@EcFeedInput("Default input")
		void ecFeedUserDataTest(ExtensionContext extensionContext) {
			Optional<EcFeedInput> annotation = AnnotationProcessor.extractUserInput(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The method/class is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default input",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
		@Test
		@DisplayName("KeyStore")
		@EcFeedKeyStore("Default keystore")
		void ecFeedStoreTest(ExtensionContext extensionContext) {
			Optional<EcFeedKeyStore> annotation = AnnotationProcessor.extractKeyStore(extensionContext);
			assertAll("Compound", 
				() -> { 
					assertEquals(annotation.isPresent(), true, 
							() -> "The method/class is annotated, and therefore, the annotation value should be available");
				}, 
				() -> {
					assertEquals(annotation.get().value(), "Default keystore",
							() -> "The annotation value is erroneous");
				}
			);
		}
		
	}
	
}