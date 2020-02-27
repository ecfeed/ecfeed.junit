package localhost;

import com.ecfeed.core.webservice.client.GenWebServiceClient;
import com.ecfeed.core.webservice.client.IWebServiceClient;
import com.ecfeed.core.webservice.client.WebServiceResponse;
import localhost.utils.TestHelper;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;

import static localhost.utils.TestHelper.REQUEST_DATA;
import static org.junit.jupiter.api.Assertions.fail;

public class GenerationWithExportTest {

    @Test
    public void getVersionTest() {

        IWebServiceClient genWebServiceClient =
                TestHelper.createWebServiceClient(GenWebServiceClient.getTestCasesEndPoint());

        String request = "{ 'dataSource':'genCartesian', 'method':'test.Class1.testMethod' }";
        request = request.replace("'", "\""); // TODO - to helper

        WebServiceResponse webServiceResponse = genWebServiceClient.sendPostRequest(TestHelper.REQUEST_DATA, request);

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
