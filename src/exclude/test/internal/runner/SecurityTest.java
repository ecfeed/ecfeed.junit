package internal.runner;

import com.ecfeed.core.utils.TestCasesRequest;
import com.ecfeed.web.service.Application;
import general.SecurityServiceTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SecurityTest")
public class SecurityTest {

    private static ConfigurableApplicationContext fConfigurableApplicationContext = null;

    @BeforeAll
    public static void beforeAll() {
        fConfigurableApplicationContext = SpringApplication.run(Application.class);
    }

    @AfterAll
    public static void afterAll() {
        fConfigurableApplicationContext.close();
    }

    @Test
    @DisplayName("Certificate valid")
    public void certificateCorrectTest() {
        List<String> response = new ArrayList<>();

        TestCasesRequest request = new TestCasesRequest();
        request.setMethod("test.Class1.randomized(int arg1, String arg2)");
        request.setModelName(TestDataSupplier.testModelUuid);
        request.setUserData("{'dataSource':'static','testSuites':['default']}");

        try {
            new SecurityServiceTest(request, response, "src/test/resources/security", "TLSv1.2").run();
        } catch (RuntimeException e) {
            response.add(e.getMessage());
        }

        assertThat(response)
                .as("The certificate is correct and the connection should be established.")
                .filteredOn(e -> e.contains("\"error\":"))
                .isEmpty();
    }

    @Test
    @Disabled("The valid certificate can be retrieved from the user keystore.")
    @DisplayName("Certificate invalid server")
    public void certificateInvalidServerTest() {
        List<String> response = new ArrayList<>();

        TestCasesRequest request = new TestCasesRequest();
        request.setMethod("test.Class1.randomized(int arg1, String arg2)");
        request.setModelName(TestDataSupplier.testModelUuid);
        request.setUserData("{'dataSource':'static','testSuites':['default']}");

        try {
            new SecurityServiceTest(request, response, "src/test/resources/securityInvalidServer", "TLSv1.2").run();
        } catch (RuntimeException e) {
            response.add(e.getMessage());
        }

        assertThat(response)
                .as("The certificate is erroneous, and therefore, an exception should be thrown.")
                .filteredOn(e -> e.contains("Error"))
                .hasSize(1);
    }

    @Test
    @DisplayName("Certificate invalid client")
    public void certificateInvalidClientTest() {
        List<String> response = new ArrayList<>();

        TestCasesRequest request = new TestCasesRequest();
        request.setMethod("test.Class1.randomized(int arg1, String arg2)");
        request.setModelName(TestDataSupplier.testModelUuid);
        request.setUserData("{'dataSource':'static','testSuites':['default']}");

        try {
            new SecurityServiceTest(request, response, "src/test/resources/securityInvalidClient", "TLSv1.2").run();
        } catch (RuntimeException e) {
            response.add(e.getMessage());
        }

        assertThat(response)
                .as("The certificate is erroneous, and therefore, an exception should be thrown.")
                .filteredOn(e -> e.contains("Exception"))
                .hasSize(1);
    }

    @Test
    @DisplayName("Connection invalid protocol")
    public void connectionInvalidProtocolTest() {
        List<String> response = new ArrayList<>();

        TestCasesRequest request = new TestCasesRequest();
        request.setMethod("test.Class1.randomized(int arg1, String arg2)");
        request.setModelName(TestDataSupplier.testModelUuid);
        request.setUserData("{'dataSource':'static','testSuites':['default']}");

        try {
            new SecurityServiceTest(request, response, "src/test/resources/security", "invalid").run();
        } catch (RuntimeException e) {
            response.add(e.getMessage());
        }

        assertThat(response)
                .as("The protocol is invalid, and therefore, an exception should be thrown.")
                .filteredOn(e -> e.contains("Exception"))
                .hasSize(1);
    }

}
