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
    public void generateWithExportTest() {

        IWebServiceClient genWebServiceClient =
                TestHelper.createWebServiceClient(GenWebServiceClient.getTestCasesEndPoint());

        // TODO - remove tag: method ?
        String request = "{\"method\":\"public void localhost.ShouldGenerateWithCartesianGenerator.test(java.lang.String,java.lang.String)\",\"model\":\"TestUuid11\",\"userData\":\"{'dataSource':'genCartesian', 'method':'test.Class1.testMethod'}\",\"sessionId\":null}";

        WebServiceResponse webServiceResponse = genWebServiceClient.sendPostRequest(TestHelper.REQUEST_EXPORT, request);

        if (!webServiceResponse.isResponseStatusOk()) {
            fail();
        }

        BufferedReader bufferedReader = webServiceResponse.getResponseBufferedReader();

        try {

            for(;;) {
                String line = bufferedReader.readLine();

                if (line == null) {
                    break;
                }

                System.out.println(line);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

}
