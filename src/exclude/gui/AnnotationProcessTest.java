// TODO - RAP-GEN
//package com.ecfeed.core.junit5;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assumptions.assumeTrue;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.api.extension.ExtensionContext;
//
//import com.ecfeed.core.junit5.annotation.AnnotationDefaultValue;
//import com.ecfeed.core.junit5.annotation.AnnotationProcessor;
//import com.ecfeed.core.junit5.annotation.EcFeed;
//import com.ecfeed.core.junit5.annotation.EcFeedInput;
//import com.ecfeed.core.junit5.annotation.EcFeedKeyStore;
//import com.ecfeed.core.junit5.annotation.EcFeedModel;
//import com.ecfeed.core.junit5.annotation.EcFeedTarget;
//import com.ecfeed.core.junit5.util.extension.ExtensionContextResolver;
//
//@Tag("Annotation")
//@DisplayName("Process annotations")
//@ExtendWith(ExtensionContextResolver.class)
//public class AnnotationProcessTest {
//
//	@Nested
//	@DisplayName("Not available")
//	class MissingAnnotation {
//
//		@Test
//		@DisplayName("Target")
//		void ecFeedConnectionTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processTarget(extensionContext);
//			assertEquals(annotation, AnnotationDefaultValue.DEFAULT_TARGET,
//					() -> "The annotation is missing, and therefore, the default value should be returned");
//		}
//
//		@Test
//		@DisplayName("Model")
//		void ecFeedModelTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processModelName(extensionContext);
//			assertEquals(annotation, AnnotationDefaultValue.DEFAULT_MODEL_NAME,
//					() -> "The annotation is missing, and therefore, the default value should be returned");
//		}
//
//		@Test
//		@DisplayName("Input")
//		void ecFeedUserDataTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processInput(extensionContext);
//			assertEquals(annotation, AnnotationDefaultValue.DEFAULT_INPUT,
//					() -> "The annotation is missing, and therefore, the default value should be returned");
//		}
//
//		@Test
//		@DisplayName("KeyStore")
//		void ecFeedStoreTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processKeyStore(extensionContext);
//			assertEquals(annotation, AnnotationDefaultValue.DEFAULT_KEYSTORE,
//					() -> "The annotation is missing, and therefore, the default value should be returned");
//		}
//
//	}
//
//	@Nested
//	@DisplayName("EcFeed level")
//	@EcFeed(value = "Default model", target = "Default target", input = "Default input", keystore = "Default keystore")
//	class EcFeedLevel {
//
//		@Test
//		@DisplayName("Target")
//		void ecFeedConnectionTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processTarget(extensionContext);
//			assertEquals(annotation, "Default target",
//					() -> "The class is annotated, and therefore, the annotation value should be available");
//		}
//
//		@Test
//		@DisplayName("Model")
//		void ecFeedModelTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processModelName(extensionContext);
//			assertEquals(annotation, "Default model",
//					() -> "The class is annotated, and therefore, the annotation value should be available");
//		}
//
//		@Test
//		@DisplayName("Input")
//		void ecFeedUserDataTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processInput(extensionContext);
//			assertEquals(annotation, "Default input",
//					() -> "The class is annotated, and therefore, the annotation value should be available");
//		}
//
//		@Test
//		@DisplayName("KeyStore")
//		void ecFeedStoreTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processKeyStore(extensionContext);
//			assertEquals(annotation, "Default keystore",
//					() -> "The class is annotated, and therefore, the annotation value should be available");
//		}
//
//	}
//
//	@Nested
//	@DisplayName("EcFeed priority")
//	@EcFeed(value = "Default model erroneous", target = "Default target erroneous", input = "Default input erroneous", keystore = "Default keystore erroneous")
//	class PriorityLevel {
//
//		@Test
//		@DisplayName("Target")
//		@EcFeedTarget("Default target")
//		void ecFeedConnectionTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processTarget(extensionContext);
//			assertEquals(annotation, "Default target",
//					() -> "The class is annotated, and therefore, the annotation value should be available");
//		}
//
//		@Test
//		@DisplayName("Model")
//		@EcFeedModel("Default model")
//		void ecFeedModelTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processModelName(extensionContext);
//			assertEquals(annotation, "Default model",
//					() -> "The class is annotated, and therefore, the annotation value should be available");
//		}
//
//		@Test
//		@DisplayName("Input")
//		@EcFeedInput("Default input")
//		void ecFeedUserDataTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processInput(extensionContext);
//			assertEquals(annotation, "Default input",
//					() -> "The class is annotated, and therefore, the annotation value should be available");
//		}
//
//		@Test
//		@DisplayName("KeyStore")
//		@EcFeedKeyStore("Default keystore")
//		void ecFeedStoreTest(ExtensionContext extensionContext) {
//			String annotation = AnnotationProcessor.processKeyStore(extensionContext);
//			assertEquals(annotation, "Default keystore",
//					() -> "The class is annotated, and therefore, the annotation value should be available");
//		}
//
//	}
//
//	@Test
//	@EcFeedKeyStore()
//	@DisplayName("Public key extraction")
//	public void extractPublicKey(ExtensionContext extensionContext) {
//		assumeTrue(isStoreAvailable(), 
//				() -> "The public key could not be found using the default path, the tests could not be executed");
//
//		PublicKey annotation = AnnotationProcessor.processPublicKey(extensionContext);
//
//		assertNotNull(annotation, 
//				() -> "The public key could not be retrieved");
//	}
//
//	@Test
//	@EcFeedKeyStore()
//	@DisplayName("Private key extraction")
//	public void extractPrivateKey(ExtensionContext extensionContext) {
//		assumeTrue(isStoreAvailable(), 
//				() -> "The private key could not be found using the default path, the tests could not be executed");
//
//		PrivateKey annotation = AnnotationProcessor.processPrivateKey(extensionContext);
//
//		assertNotNull(annotation,  
//				() -> "The private key could not be retrieved");
//	}
//
//	private static boolean isStoreAvailable() {
//		Path filePath = Paths.get(".ecFeedTest/security");
//
//		if (!Files.exists(filePath)) {
//			return false;
//		}
//
//		if (!Files.isReadable(filePath)) {
//			return false;
//		}
//
//		if (!Files.isRegularFile(filePath)) {
//			return false;
//		}
//
//		return true;
//	}
//
//}
