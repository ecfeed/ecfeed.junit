package com.ecfeed.core.junit5;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import com.ecfeed.junit.ArgumentsHelper;
import com.ecfeed.junit.EcFeedExtensionStore;
import com.ecfeed.junit.message.ArgumentChainJUnit5;
import com.ecfeed.junit.message.processor.DefaultProcessorJUnit5;
import com.ecfeed.junit.message.processor.ResultErrorProcessorJUnit5;
import com.ecfeed.junit.message.processor.ResultProgressProcessorJUnit5;
import com.ecfeed.junit.message.processor.ResultStatusProcessorJUnit5;
import com.ecfeed.junit.message.processor.ResultTestProcessorJUnit5;
import com.ecfeed.junit.message.processor.ResultTotalProgressProcessorJUnit5;

@Tag("ArgumentProcessor")
@DisplayName("Process arguments")
public class ArgumentProcessTest {

	@Nested
	@DisplayName("Unknown type")
	class ProcessorDefault {
		ArgumentChainJUnit5 processor;

		@BeforeEach
		void beforeEach() {
			processor = new DefaultProcessorJUnit5(new EcFeedExtensionStore());
		}

		@Test
		@DisplayName("Success")
		void processorTest() {
			String input = "{\"default\"}";
			Optional<Arguments> output = processor.process(input);
			assertEquals(output, Optional.empty(), 
					() -> "For the 'unknown type' the list of arguments must be empty");
		}

		@Test
		@DisplayName("Null argument")
		void processorTestNull() {		
			assertThrows(NullPointerException.class, 
					() -> processor.process(null), 
					() -> "Undefined input must always cause an exception (regardless of the processor type)");
		}

	}

	@Nested
	@DisplayName("Error type")
	class ProcessorError {
		ArgumentChainJUnit5 processor;

		@BeforeEach
		void beforeEach() {
			processor = new ResultErrorProcessorJUnit5(null);
		}

		@Test
		@DisplayName("Success")
		void processorTest() {
			String input = "{\"error\":\"Test error\"}";

			RuntimeException exception = assertThrows(RuntimeException.class, () -> {
				processor.process(input);
			});

			assertTrue(exception.getMessage().contains("Test error"), 
					() -> "The 'error type' processor must return a message in a specific format");
		}

		@Test
		@DisplayName("Wrong argument format")
		void processorTestWrongFormat() {
			String input = "{\"default\"}";
			assertThrows(NullPointerException.class, 
					() -> processor.process(input),
					() -> "Input not associated with the correct processor should not be parsed");
		}

		@Test
		@DisplayName("Null argument")
		void processorTestNull() {
			assertThrows(NullPointerException.class, 
					() -> processor.process(null),
					() -> "Undefined input must always throw an exception (regardless of the processor type)");
		}

	}

	@Nested
	@DisplayName("Total progress type")
	class ProcessorTotalProgress {
		ArgumentChainJUnit5 processor;

		@BeforeEach
		void beforeEach() {
			processor = new ResultTotalProgressProcessorJUnit5(null);
		}

		@Test
		@DisplayName("Success")
		void processorTest() {
			String input = "{\"totalProgress\":50}";
			Optional<Arguments> output = processor.process(input);
			assertEquals(output, Optional.empty(),
					() -> "The 'total progress' processor must return an empty list of arguments");
		}

		@Test
		@DisplayName("Wrong argument format")
		void processorTestWrongFormat() {
			String input = "{\"default\"}";
			assertThrows(NullPointerException.class, 
					() -> processor.process(input),
					() -> "Input not associated with the correct processor should not be parsed");
		}

		@Test
		@DisplayName("Null argument")
		void processorTestNull() {
			assertThrows(NullPointerException.class, 
					() -> processor.process(null),
					() -> "Undefined input must always throw an exception (regardless of the processor type)");
		}

	}

	@Nested
	@DisplayName("Progress type")
	class ProcessorProgress {
		ArgumentChainJUnit5 processor;

		@BeforeEach
		void beforeEach() {
			processor = new ResultProgressProcessorJUnit5(null);
		}

		@Test
		@DisplayName("Success")
		void processorTest() {
			String input = "{\"progress\":50}";
			Optional<Arguments> output = processor.process(input);
			assertEquals(output, Optional.empty(),
					() -> "The 'progress' processor must return an empty list of arguments");
		}

		@Test
		@DisplayName("Wrong argument format")
		void processorTestWrongFormat() {
			String input = "{\"default\"}";
			assertThrows(NullPointerException.class, 
					() -> processor.process(input),
					() -> "Input not associated with the correct processor should not be parsed");
		}

		@Test
		@DisplayName("Null argument")
		void processorTestNull() {
			assertThrows(NullPointerException.class, 
					() -> processor.process(null),
					() -> "Undefined input must always throw an exception (regardless of the processor type)");
		}

	}

	@Nested
	@DisplayName("Status type")
	class ProcessorStatus {
		ArgumentChainJUnit5 processor;

		@BeforeEach
		void beforeEach() {
			processor = new ResultStatusProcessorJUnit5(null, new EcFeedExtensionStore());
		}

		@Test
		@DisplayName("Success")
		void processorTest() {

			assertAll("Compound", 
					() -> {
						String input = "{\"status\":\"BEG\"}";
						Optional<Arguments> output = processor.process(input);
						assertEquals(output, Optional.empty(),
								() -> "The 'status' processor must return an empty list of arguments");
					},

					() -> {
						String input = "{\"status\":\"END\"}";
						Optional<Arguments> output = processor.process(input);
						assertEquals(output, Optional.empty(),
								() -> "The 'status' processor must return an empty list of arguments");
					}
					);	
		}

		@DisplayName("Wrong argument format")
		void processorTestWrongFormat() {
			String input = "{\"default\"}";
			assertThrows(NullPointerException.class, 
					() -> processor.process(input),
					() -> "Input not associated with the correct processor should not be parsed");
		}

		@Test
		@DisplayName("Null argument")
		void processorTestNull() {
			assertThrows(NullPointerException.class, 
					() -> processor.process(null),
					() -> "Undefined input must always throw an exception (regardless of the processor type)");
		}

	}

	@Nested
	@DisplayName("Test type")
	class ProcessorTest {
		ArgumentChainJUnit5 processor;
		EcFeedExtensionStore store;

		@BeforeEach
		void beforeEach() {
			store = new EcFeedExtensionStore();
			processor = 
					new ResultTestProcessorJUnit5(
							null, 
							ArgumentsHelper.generateParametersDouble(),
							store);
		}

		@Test
		@DisplayName("Not enough arguments")
		void processorTestNotEnoughParameters() {
			assertThrows(IllegalArgumentException.class, 
					() -> {
						String input = "{\"testCase\":[{\"name\":\"N-1\",\"value\":\"1\"}]}";
						store.setChunkProgress(true);
						processor.process(input);
					},
					() -> "An incorrect number of arguments should throw an exception");
		}

		@Test
		@DisplayName("Too many arguments")
		void processorTestTooManyArguments() {
			assertThrows(IllegalArgumentException.class, 
					() -> {
						String input = "{\"testCase\":[{\"name\":\"N-1\",\"value\":\"1\"}, {\"name\":\"N-2\",\"value\":\"2\"}, {\"name\":\"N-3\",\"value\":\"3\"}]}";
						store.setChunkProgress(true);
						processor.process(input);
					},
					() -> "An incorrect number of arguments should throw an exception");
		}

		@Test
		@DisplayName("Missing argument value")
		void processorTestMissingValue() {
			assertThrows(IllegalArgumentException.class, 
					() -> {
						String input = "{\"testCase\":[{\"name\":\"N-1\"}]}";
						store.setChunkProgress(true);
						processor.process(input);
					},
					() -> "Erroneous data (missing value) should throw an exception");
		}

		@Test
		@DisplayName("Missing argument name")
		void processorTestMissingName() {
			assertThrows(IllegalArgumentException.class, 
					() -> {
						String input = "{\"testCase\":[{\"value\":\"1\"}]}";
						store.setChunkProgress(true);
						processor.process(input);
					},
					() -> "Erroneous data (missing name) should throw an exception");
		}

		@Test
		@DisplayName("Wrong argument format")
		void processorTestWrongFormat() {
			String input = "{\"default\"}";
			ArgumentChainJUnit5 processor = 
					new ResultTestProcessorJUnit5(
							null, 
							ArgumentsHelper.generateParametersBoolean(),
							store);
			assertThrows(NullPointerException.class, 
					() -> processor.process(input),
					() -> "Input not associated with the correct processor should not be parsed\"");
		}

		@Test
		@DisplayName("Null argument")
		void processorTestNull() {
			ArgumentChainJUnit5 processor = new ResultTestProcessorJUnit5(
					null, 
					ArgumentsHelper.generateParametersBoolean(),
					new EcFeedExtensionStore());
			assertThrows(NullPointerException.class, 
					() -> processor.process(null),
					() -> "Undefined input must always throw an exception (regardless of the processor type)");
		}

		@Nested
		@DisplayName("Char")
		class TypeChar {
			ArgumentChainJUnit5 processor;

			@BeforeEach
			void beforeEach() {
				processor = new ResultTestProcessorJUnit5(null, ArgumentsHelper.generateParametersChar(), store);
			}

			@Test
			@DisplayName("Success")
			void processorTest() {
				String input = "{\"testCase\":[{\"name\":\"N\",\"value\":\"V\"}]}";
				store.setChunkProgress(true);
				Optional<Arguments> output = processor.process(input);
				assertTrue(ArgumentsHelper.compareArguments(Arguments.of('V'), output.get()),
						() -> "The list of parsed arguments is erroneous");
			}

			@Test
			@DisplayName("Invalid argument format")
			void processorTestInvalidFormat() {
				String input = "{\"testCase\":[{\"name\":\"N\",\"value\":\"V-1\"}]}";
				store.setChunkProgress(true);
				assertThrows(IllegalArgumentException.class, 
						() -> processor.process(input),
						() -> "Values composed of more than one character shoud not be parsed to 'char'");
			}

		}

		@Nested
		@DisplayName("Boolean")
		class TypeBoolean {
			ArgumentChainJUnit5 processor;

			@BeforeEach
			void beforeEach() {
				processor = new ResultTestProcessorJUnit5(
						null, 
						ArgumentsHelper.generateParametersBoolean(), 
						store);
			}

			@Test
			@DisplayName("Success")
			void processorTest() {

				assertAll("Compound",
						() -> {
							String input = "{\"testCase\":[{\"name\":\"N\",\"value\":\"true\"}]}";
							store.setChunkProgress(true);
							Optional<Arguments> output = processor.process(input);
							assertTrue(ArgumentsHelper.compareArguments(Arguments.of(Boolean.valueOf("true")), output.get()),
									() -> "The list of parsed arguments is erroneous");
						},

						() -> {
							String input = "{\"testCase\":[{\"name\":\"N\",\"value\":\"false\"}]}";
							store.setChunkProgress(true);
							Optional<Arguments> output = processor.process(input);
							assertTrue(ArgumentsHelper.compareArguments(Arguments.of(Boolean.valueOf("false")), output.get()),
									() -> "The list of parsed arguments is erroneous");
						},

						() -> {
							String input = "{\"testCase\":[{\"name\":\"N\",\"value\":\"t\"}]}";
							store.setChunkProgress(true);
							Optional<Arguments> output = processor.process(input);
							assertTrue(ArgumentsHelper.compareArguments(Arguments.of(Boolean.valueOf("t")), output.get()),
									() -> "The list of parsed arguments is erroneous");
						},

						() -> {
							String input = "{\"testCase\":[{\"name\":\"N\",\"value\":\"f\"}]}";
							store.setChunkProgress(true);
							Optional<Arguments> output = processor.process(input);
							assertTrue(ArgumentsHelper.compareArguments(Arguments.of(Boolean.valueOf("f")), output.get()),
									() -> "The list of parsed arguments is erroneous");
						}
						);
			}

			@Test
			@DisplayName("Invalid argument format")
			void processorTestInvalidFormat() {
				String input = "{\"testCase\":[{\"name\":\"N\",\"value\":\"default\"}]}";
				store.setChunkProgress(true);
				assertThrows(IllegalArgumentException.class, () -> processor.process(input),
						() -> "The value 'default' should not be converted to 'boolean'");
			}

		}

		@Nested
		@DisplayName("String")
		class TypeString {
			ArgumentChainJUnit5 processor;

			@BeforeEach
			void beforeEach() {
				processor = new ResultTestProcessorJUnit5(
						null, 
						ArgumentsHelper.generateParametersString(),
						store);
			}

			@Test
			@DisplayName("Success")
			void processorTest() {
				String input = "{\"testCase\":[{\"name\":\"N\",\"value\":\"V-1\"}]}";
				store.setChunkProgress(true);
				Optional<Arguments> output = processor.process(input);
				assertTrue(ArgumentsHelper.compareArguments(Arguments.of("V-1"), output.get()),
						() -> "The list of parsed arguments is erroneous");
			}
		}

		@Nested
		@DisplayName("Integer")
		class TypeInteger {
			ArgumentChainJUnit5 processor;

			@BeforeEach
			void beforeEach() {
				processor = new ResultTestProcessorJUnit5(null, ArgumentsHelper.generateParametersInteger(), store);
			}

			@Test
			@DisplayName("Success")
			void processorTest() {
				String input = "{\"testCase\":[{\"name\":\"N-1\",\"value\":\"1\"}, {\"name\":\"N-2\",\"value\":\"2\"}, {\"name\":\"N-3\",\"value\":\"3\"}, {\"name\":\"N-4\",\"value\":\"4\"}]}";
				store.setChunkProgress(true);
				Optional<Arguments> output = processor.process(input);
				assertTrue(ArgumentsHelper.compareArguments(Arguments.of(Byte.valueOf("1"), Short.valueOf("2"), Integer.valueOf("3"), Long.valueOf("4")), output.get()),
						() -> "The list of parsed arguments is erroneous");
			}

			@Test
			@DisplayName("Empty field")
			void processorTestEmpty() {
				String input = "{\"testCase\":[{\"name\":\"N-1\",\"value\":\"\"}, {\"name\":\"N-2\",\"value\":\"\"}, {\"name\":\"N-3\",\"value\":\"\"}, {\"name\":\"N-4\",\"value\":\"\"}]}";
				store.setChunkProgress(true);
				assertThrows(IllegalArgumentException.class, () -> processor.process(input),
						() -> "Empty fields should not be converted to integer types");
			}

			@Test
			@DisplayName("Wrong argument format")
			void processorTestWrongFormat() {
				String input = "{\"testCase\":[{\"name\":\"N-1\",\"value\":\"default\"}, {\"name\":\"N-2\",\"value\":\"default\"}, {\"name\":\"N-3\",\"value\":\"default\"}, {\"name\":\"N-4\",\"value\":\"default\"}]}";
				store.setChunkProgress(true);
				assertThrows(IllegalArgumentException.class, () -> processor.process(input),
						() -> "The value 'default' should not be converted to integer types");
			}

			@Test
			@DisplayName("Overflow")
			void processorTestOverflow() {
				String input = "{\"testCase\":[{\"name\":\"N-1\",\"value\":\"1000000\"}, {\"name\":\"N-2\",\"value\":\"2\"}, {\"name\":\"N-3\",\"value\":\"3\"}, {\"name\":\"N-4\",\"value\":\"4\"}]}";
				store.setChunkProgress(true);
				assertThrows(IllegalArgumentException.class, () -> processor.process(input),
						() -> "The values which could cause the 'overflow' error should not be converted to integer types");
			}
		}

		@Nested
		@DisplayName("Floating point")
		class TypeFloatingPoint {
			ArgumentChainJUnit5 processor;

			@BeforeEach
			void beforeEach() {
				processor = new ResultTestProcessorJUnit5(null, ArgumentsHelper.generateParametersDouble(), store);
			}

			@Test
			@DisplayName("Success")
			void processorTest() {
				String input = "{\"testCase\":[{\"name\":\"N-1\",\"value\":\"1\"}, {\"name\":\"N-2\",\"value\":\"2\"}]}";
				store.setChunkProgress(true);
				Optional<Arguments> output = processor.process(input);
				assertTrue(ArgumentsHelper.compareArguments(Arguments.of(Float.valueOf("1"), Double.valueOf("2")), output.get()),
						() -> "The list of parsed arguments is erroneous");
			}

			@Test
			@DisplayName("Empty field")
			void processorTestEmpty() {
				String input = "{\"testCase\":[{\"name\":\"N-1\",\"value\":\"\"}, {\"name\":\"N-2\",\"value\":\"\"}]}";
				store.setChunkProgress(true);
				assertThrows(IllegalArgumentException.class, () -> processor.process(input),
						() -> "Empty fields should not be converted to floating-point types");
			}

			@Test
			@DisplayName("Wrong argument format")
			void processorTestWrongFormat() {
				String input = "{\"testCase\":[{\"name\":\"N-1\",\"value\":\"number\"}, {\"name\":\"N-2\",\"value\":\"number\"}]}";
				store.setChunkProgress(true);
				assertThrows(IllegalArgumentException.class, () -> processor.process(input),
						() -> "The value 'default' should not be converted to floating-point types");
			}

		}

	}

}
