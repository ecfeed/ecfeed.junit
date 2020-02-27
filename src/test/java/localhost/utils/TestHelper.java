package localhost.utils;

import com.ecfeed.core.webservice.client.GenWebServiceClient;
import com.ecfeed.core.webservice.client.GenWebServiceClientType;
import com.ecfeed.core.webservice.client.IWebServiceClient;

import java.util.Optional;

public class TestHelper {

    public static final String GEN_SERVICE_URL_ON_LOCALHOST = "https://localhost:8090";

    public static IWebServiceClient createWebServiceClient(String endpoint) {

        Optional<String> keyStorePath = Optional.of("src/test/resources/security");

        return new GenWebServiceClient(
                TestHelper.GEN_SERVICE_URL_ON_LOCALHOST,
                endpoint,
                GenWebServiceClientType.LOCAL_TEST_RUNNER.toString(),
                keyStorePath);
    }

}

