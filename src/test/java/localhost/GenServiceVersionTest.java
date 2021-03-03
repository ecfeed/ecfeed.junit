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

        IWebServiceClient genWebServiceClient =
                TestHelper.createWebServiceClient(
                        GenWebServiceClient.getGenServiceVersionEndPoint(),
                        GenWebServiceClientType.LOCAL_TEST_RAP);

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

}
