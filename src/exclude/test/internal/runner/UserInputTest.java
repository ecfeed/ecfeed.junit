package internal.runner;


import com.ecfeed.core.utils.TestCasesRequest;
import com.ecfeed.web.service.Application;
import general.UserInputServiceTest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User input")
public class UserInputTest {

    private static ConfigurableApplicationContext fConfigurableApplicationContext = null;

    @BeforeAll
    public static void beforeAll() {
        fConfigurableApplicationContext = SpringApplication.run(Application.class);
    }

    @AfterAll
    public static void afterAll() {
        fConfigurableApplicationContext.close();
    }

    private static TestCasesRequest getTestCaseRequest() {
        TestCasesRequest request = new TestCasesRequest();

        request.setModelName("TestUuid1");
        request.setMethod("test.Class1.randomized(int arg1, String arg2)");
        request.setUserData("{'dataSource':'static','testSuites':['default']}");
        return request;
    }

    // TODO - match display names with method names in all tests

    @Test
    @DisplayName("Input data")
    public void parameterInputDataTest() {
        List<String> response = new ArrayList<>();

        TestCasesRequest request = getTestCaseRequest();
        request.setUserData("{invalid}");

        new UserInputServiceTest(request, response).run();

        assertThat(response)
                .as("The input data is erroneous, and therefore, an exception should be thrown.")
                .doesNotHaveDuplicates()
                .filteredOn(e -> e.contains("\"error\":"))
                .hasSize(1);
    }

    @Test
    @DisplayName("Method (main)")
    public void parameterMethodTest() {
        List<String> response = new ArrayList<>();

        TestCasesRequest request = getTestCaseRequest();
        request.setMethod("test.Class1.invalid(int arg1, String arg2)");

        new UserInputServiceTest(request, response).run();

        assertThat(response)
                .as("The method name is erroneous, and therefore, an exception should be thrown.")
                .doesNotHaveDuplicates()
                .filteredOn(e -> e.contains("\"error\":"))
                .hasSize(1);
    }

    @Nested
    @DisplayName("User input")
    class UserInputDataCheck {

        @Nested
        @DisplayName("Generator")
        class GeneratorCheck {

            @Test
            @DisplayName("Static")
            public void generatorStaticTest() {
                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();

                new UserInputServiceTest(request, response).run();

                assertThat(response)
                        .as("The REST response should contain info tag.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"info\":"))
                        .isNotEmpty();

                assertThat(response)
                        .as("The REST test  suite response should consist of a fixed number of elements")
                        .doesNotHaveDuplicates()
                        .hasSize(10);
            }

            @Test
            @DisplayName("N-Wise")
            public void generatorNWiseTest() {
                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();
                request.setUserData("{'dataSource':'genNWise','coverage':'100','N':'2'}");

                new UserInputServiceTest(request, response).run();

                assertThat(response)
                        .as("The REST response should not contain errors and each element should be unique.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .isEmpty();
            }

            @Test
            @DisplayName("Cartesian")
            public void generatorCartesianTest() {
                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();
                request.setUserData("{'dataSource':'genCartesian'}");

                new UserInputServiceTest(request, response).run();

                assertThat(response)
                        .as("The REST response should not contain errors and each element should be unique.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .isEmpty();
            }

            @Test
            @DisplayName("Random")
            public void generatorRandomTest() {
                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();
                request.setUserData("{'dataSource':'genRandom', 'length':'10', 'duplicates':'false'}");

                new UserInputServiceTest(request, response).run();

                assertThat(response)
                        .as("The REST response should not contain errors.")
                        .filteredOn(e -> e.contains("\"error\":"))
                        .isEmpty();
            }

            @Test
            @DisplayName("Adaptive random")
            public void generatorRandomAdaptiveTest() {
                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();
                request.setUserData("{'dataSource':'genAdaptiveRandom', 'depth':'10', 'length':'10', 'candidates':'10', 'duplicates':'false'}");

                new UserInputServiceTest(request, response).run();

                assertThat(response)
                        .as("The REST response should not contain errors.")
                        .filteredOn(e -> e.contains("\"error\":"))
                        .isEmpty();
            }

        }

        @Nested
        @DisplayName("Parameter validity")
        class ParameterValidityCheck {

            @Test
            @DisplayName("method (alternative)")
            public void parameterMethod() {
                SoftAssertions softly = new SoftAssertions();

                List<String> response = new ArrayList<>();

                TestCasesRequest request = new TestCasesRequest();

                request.setModelName("TestUuid1");
                request.setMethod("test.Class1.alternative1(String arg1, int arg2)");
                request.setUserData("{'dataSource':'genCartesian','method':'test.Class1.alternative2'}");

                response.clear();
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The alternative method name is correct.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .isEmpty();

                softly.assertThat(response)
                        .as("Choice names should be different while using the alternative method.")
                        .filteredOn(e -> e.contains("\"alt2p1c1\""))
                        .isNotEmpty();

                response.clear();
                request.setUserData("{'dataSource':'genCartesian','method':'invalid'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The name of the alternative method is erroneous, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                softly.assertAll();
            }

            @Test
            @DisplayName("coverage")
            public void parameterCoverage() { // TODO - passes with invalid token
                SoftAssertions softly = new SoftAssertions();

                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();

                response.clear();
                request.setUserData("{'dataSource':'genNWise','coverage':'-1','N':'2'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The coverage is negative, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'genNWise','coverage':'101','N':'2'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The coverage is greater than 100, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'genNWise','coverage':'invalid','N':'2'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The coverage is non-numeric, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                softly.assertAll();
            }

            @Test
            @DisplayName("n")
            public void parameterN() { // TODO - passes with invalid token
                SoftAssertions softly = new SoftAssertions();

                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();

                response.clear();
                request.setUserData("{'dataSource':'genNWise','coverage':'100','N':'-1'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The N value is negative, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'genNWise','coverage':'100','N':'3'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The N value is greater than the number of arguments, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'genNWise','coverage':'100','N':'invalid'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The N value is non-numeric, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                softly.assertAll();
            }

            @Test
            @DisplayName("duplicates")
            public void parameterDuplicates() { // TODO - passes with invalid token
                SoftAssertions softly = new SoftAssertions();

                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();
                request.setUserData("{'dataSource':'genRandom', 'length':'10', 'duplicates':'invalid'}");

                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The duplicates argument is erroneous, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                softly.assertAll();
            }

            @Test
            @DisplayName("length")
            public void parameterSuiteSize() { // TODO - passes with invalid token
                SoftAssertions softly = new SoftAssertions();

                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();

                response.clear();
                request.setUserData("{'dataSource':'genRandom', 'length':'-1', 'duplicates':'false'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The length is negative, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'genRandom', 'length':'invalid', 'duplicates':'false'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The length is non-numeric, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                softly.assertAll();
            }

            @Test
            @DisplayName("depth")
            public void parameterDepth() { // TODO - passes with invalid token
                SoftAssertions softly = new SoftAssertions();

                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();

                response.clear();
                request.setUserData("{'dataSource':'genAdaptiveRandom', 'depth':'-12541', 'length':'10', 'candidates':'10', 'duplicates':'false'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The depth is smaller than -1, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'genAdaptiveRandom', 'depth':'invalid', 'length':'10', 'candidates':'10', 'duplicates':'false'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The depth is non-numeric, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                softly.assertAll();
            }

            @Test
            @DisplayName("candidates")
            public void parameterCandidates() { // TODO - passes with invalid token
                SoftAssertions softly = new SoftAssertions();

                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();

                response.clear();
                request.setUserData("{'dataSource':'genAdaptiveRandom', 'depth':'10', 'length':'10', 'candidates':'-1', 'duplicates':'false'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The number of candidates is negative, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'genAdaptiveRandom', 'depth':'10', 'length':'10', 'candidates':'invalid', 'duplicates':'false'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The number of candidates is non-numeric, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                softly.assertAll();
            }

            @Test
            @DisplayName("dataSource")
            public void parameterDataSourceTest() { // TODO - passes with invalid token
                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();
                request.setUserData("{'dataSource':'invalid','coverage':'100','N':'2'}");

                new UserInputServiceTest(request, response).run();

                assertThat(response)
                        .as("The dataSource argument is erroneous, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);
            }

            @Test
            @DisplayName("static testSuites")
            public void parameterTestSuites() {
                SoftAssertions softly = new SoftAssertions();

                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();
                request.setMethod("test.Class1.testMethod(String arg1, String arg2)");

                response.clear();
                request.setUserData("{'dataSource':'static','testSuites':['first','second']}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The number of response messages is invalid.")
                        .hasSize(15);

                response.clear();
                request.setUserData("{'dataSource':'static','testSuites':['first','invalid']}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The test suite 'invalid' is erroneous, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'static','testSuites':[]}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The list of test suites is empty, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'static','testSuites':'ALL'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The value 'ALL' is correct, and therefore, all test suites should be included.")
                        .hasSize(15);

                softly.assertAll();
            }

            @Test
            @DisplayName("constraints")
            public void parameterConstraintsTest() { // TODO - passes with invalid token
                SoftAssertions softly = new SoftAssertions();

                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();
                request.setMethod("test.Class1.testMethod(int arg1, int arg2)");

                response.clear();
                request.setUserData("{'dataSource':'genCartesian','constraints':['c1']}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The constraint name 'c1' is correct, and therefore, it should be included in the generator data.")
                        .filteredOn(e -> e.contains("choice12"))
                        .isEmpty();

                response.clear();
                request.setUserData("{'dataSource':'genCartesian','constraints':['c2']}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The constraint name 'c2' is correct, and therefore, it should be included in the generator data.")
                        .filteredOn(e -> e.contains("choice21"))
                        .isEmpty();

                response.clear();
                request.setUserData("{'dataSource':'genCartesian','constraints':['c1','c2']}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The constraint names 'c1', 'c2' are correct, and therefore, they both should be included in the generator data.")
                        .filteredOn(e -> e.contains("choice12") || e.contains("choice21"))
                        .isEmpty();

                response.clear();
                request.setUserData("{'dataSource':'genCartesian','constraints':['invalid']}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The constraint name is erroneous, and therefore, an exception should be thrown.")
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                softly.assertAll();

                response.clear();
                request.setUserData("{'dataSource':'genCartesian','constraints':[]}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The list of constraints is empty, and therefore, an exception should be thrown.")
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'genCartesian','constraints':'ALL'}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The value 'ALL' is correct, and therefore, all constraints should be included.")
                        .filteredOn(e -> e.contains("choice12") || e.contains("choice21"))
                        .isEmpty();

                softly.assertAll();
            }

            @Test // TODO - add tests for multiple choices
            @DisplayName("choices")
            public void parameterChoicesTest() {
                SoftAssertions softly = new SoftAssertions();

                List<String> response = new ArrayList<>();

                TestCasesRequest request = getTestCaseRequest();
                request.setMethod("test.Class1.testMethod(String arg1, String arg2)");

                response.clear();
                request.setUserData("{'dataSource':'genCartesian','choices':{'arg1':['choice11']}}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("Arguments and choices are valid, and therefore, no exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .isEmpty();

                response.clear();
                request.setUserData("{'dataSource':'genCartesian','choices':{'arg1':['choice00']}}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The choice 'choice00' is erroneous, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'genCartesian','choices':{'arg0':['choice11']}}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The argument 'arg0' is erroneous, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                response.clear();
                request.setUserData("{'dataSource':'genCartesian','choices':{'arg1':[]}}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The list of choices is empty, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);


                response.clear();
                request.setUserData("{'dataSource':'genCartesian','choices':{}}");
                new UserInputServiceTest(request, response).run();

                softly.assertThat(response)
                        .as("The list of parameters is empty, and therefore, an exception should be thrown.")
                        .doesNotHaveDuplicates()
                        .filteredOn(e -> e.contains("\"error\":"))
                        .hasSize(1);

                softly.assertAll();
            }

        }

    }

}
