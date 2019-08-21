package localhost;

import com.ecfeed.core.webservice.client.GenWebServiceClient;
import com.ecfeed.core.webservice.client.GenWebServiceClientType;
import com.ecfeed.core.webservice.client.IWebServiceClient;
import com.ecfeed.core.webservice.client.WebServiceResponse;
import localhost.utils.TestHelper;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

public class GenServiceVersionTest {

    @Test
    public void getVersionTest() {

        IWebServiceClient genWebServiceClient = createWebServiceClient();

        WebServiceResponse webServiceResponse = genWebServiceClient.sendGetRequest();

        if (!webServiceResponse.isResponseStatusOk()) {
            fail();
        }

        BufferedReader bufferedReader = webServiceResponse.getResponseBufferedReader();

        try {
            bufferedReader.readLine();
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }


    private IWebServiceClient createWebServiceClient() { // TODO - similar method in RemoteTCProviderTest

        Optional<String> keyStorePath = Optional.of("src/test/resources/security");

        return new GenWebServiceClient(
                "https://localhost:8090", // TODO
                GenWebServiceClient.getGenServiceVersionEndPoint(),
                GenWebServiceClientType.LOCAL_TEST_RUNNER.toString(),
                keyStorePath);
    }

}
