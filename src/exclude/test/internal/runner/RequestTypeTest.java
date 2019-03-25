package internal.runner;

import com.ecfeed.core.junit5.message.schema.RequestChunkSchema;
import com.ecfeed.core.junit5.message.schema.RequestUpdateSchema;
import com.ecfeed.core.utils.TestCasesRequest;
import com.ecfeed.web.service.Application;
import general.RequestTypeService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Request type")
public class RequestTypeTest {

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
    @DisplayName("Request test stream")
    public void requestStreamTest() {
        List<String> response = new ArrayList<>();

        TestCasesRequest request = new TestCasesRequest();
        request.setMethod("test.Class1.randomized(int arg1, String arg2)");
        request.setModelName("TestUuid1");
        request.setUserData("{'dataSource':'static','testSuites':['default']}");

        new RequestTypeService(request, "requestData", response).run();

        assertThat(response)
                .as("The REST test suite response should consist of a fixed number of elements")
                .doesNotHaveDuplicates()
                .hasSize(10);
    }

    @Test
    @DisplayName("Update chunk")
    public void requestChunkSchemaTest() {

        List<String> response = new ArrayList<>();

        RequestChunkSchema request = new RequestChunkSchema();
        request.setId("mock");

        new RequestTypeService(request, "requestChunk", response).run();

        assertThat(response)
                .as("The server response should consist of a single JSON that ends the connection.")
                .filteredOn(e -> e.contains("\"status\":\"END_DATA\""))
                .hasSize(1);
    }

    @Test
    @DisplayName("Update confirmation")
    public void requestUpdateSchemaTest() {

        List<String> response = new ArrayList<>();

        RequestUpdateSchema request = new RequestUpdateSchema();
        request.setId("mock");

        new RequestTypeService(request, "requestUpdate", response).run();

        assertThat(response)
                .as("The server response should consist of a single JSON that acknowledges the update.")
                .filteredOn(e -> e.contains("\"status\":\"ACKNOWLEDGED\""))
                .hasSize(1);
    }

    @Test
    @DisplayName("Erroneous request name")
    public void requestErroneousNameTest() {

        List<String> response = new ArrayList<>();

        RequestUpdateSchema request = new RequestUpdateSchema();

        new RequestTypeService(request, "invalid", response).run();

        assertThat(response)
                .as("The request name is erroneous, and therefore, an exception should be thrown.")
                .filteredOn(e -> e.contains("\"error\":\"Unknown JSON request\""))
                .hasSize(1);
    }

    @Test
    @DisplayName("Erroneous request type")
    public void requestErroneousTypeTest() {

        List<String> response = new ArrayList<>();

        RequestUpdateSchema request = new RequestUpdateSchema();
        request.setId("mock");

        new RequestTypeService(request, "requestData", response).run();

        assertThat(response)
                .as("The request type is not compatible with the request name, and therefore, an exception should be thrown.")
                .filteredOn(e -> e.contains("\"error\":\"Cannot parse input parameters"))
                .hasSize(1);
    }

}
