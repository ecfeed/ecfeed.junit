package localhost;

import com.ecfeed.core.webservice.client.GenWebServiceClient;
import com.ecfeed.core.webservice.client.IWebServiceClient;
import com.ecfeed.core.webservice.client.WebServiceResponse;
import localhost.utils.TestHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GenerationWithFixedFormatTest {

    @Test
    public void generateWithFixedFormat() {

        IWebServiceClient genWebServiceClient =
                TestHelper.createWebServiceClient(GenWebServiceClient.getTestCasesEndPoint());

        String request = "{" +
                "\"model\":\"TestUuid11\"," +
                "\"method\":\"test.Class1.testMethod(java.lang.String,java.lang.String)\"," +
                "\"userData\":\"{'dataSource':'genCartesian'}\"" +
                "}";

        WebServiceResponse webServiceResponse = genWebServiceClient.sendPostRequest(TestHelper.REQUEST_DATA, request);

        final String expectedResponse =
                "{\"status\":\"BEG_DATA\"}|" +
                        "{\"info\":\"{'method':'test.Class1.testMethod(String arg1, String arg2)'}\"}|" +
                        "{\"status\":\"BEG_CHUNK\"}|" +
                        "{\"totalProgress\":4}|" +
                        "{\"testCase\":[{\"name\":\"choice11\",\"value\":\"V11\"},{\"name\":\"choice21\",\"value\":\"V21\"}]}|" +
                        "{\"testCase\":[{\"name\":\"choice11\",\"value\":\"V11\"},{\"name\":\"choice22\",\"value\":\"V22\"}]}|" +
                        "{\"testCase\":[{\"name\":\"choice12\",\"value\":\"V12\"},{\"name\":\"choice21\",\"value\":\"V21\"}]}|" +
                        "{\"testCase\":[{\"name\":\"choice12\",\"value\":\"V12\"},{\"name\":\"choice22\",\"value\":\"V22\"}]}|" +
                        "{\"status\":\"END_CHUNK\"}|" +
                        "{\"status\":\"END_DATA\"}|";

        TestHelper.verifyResponse(webServiceResponse, expectedResponse);
    }

}
