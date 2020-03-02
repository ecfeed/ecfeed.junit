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
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GenerationWithExportTest {

    @Test
    public void generateWithExportTest() {

        IWebServiceClient genWebServiceClient =
                TestHelper.createWebServiceClient(GenWebServiceClient.getTestCasesEndPoint());

        String request = "{" +
                "\"model\":\"TestUuid11\"," +
                "\"method\":\"test.Class1.testMethod(java.lang.String,java.lang.String)\"," +
                "\"userData\":\"{'dataSource':'genCartesian'}\"" +
                "}";

        WebServiceResponse webServiceResponse = genWebServiceClient.sendPostRequest(TestHelper.REQUEST_EXPORT, request);

        verifyResponse(webServiceResponse);
    }

    private void verifyResponse(WebServiceResponse webServiceResponse) {

        if (!webServiceResponse.isResponseStatusOk()) {
            fail();
        }

        JSONObject jsonObject = convertToJsonObject(webServiceResponse);

        // get array of test cases

        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("testCases");
        } catch (JSONException e) {
            fail();
        }

        // check test cases

        JSONObject jsonObjectRow = null;
        try {
            for (int testCaseIndex = 0; testCaseIndex < jsonArray.length(); testCaseIndex++) {

                jsonObjectRow = jsonArray.getJSONObject(testCaseIndex);
                verifyResponseRow(testCaseIndex, jsonObjectRow);
            }
        } catch (JSONException e) {
            fail();
        }
    }

    private void verifyResponseRow(int testCaseIndex, JSONObject jsonObjectRow) throws JSONException {

        String testCaseIndexStr = jsonObjectRow.getString("index"); // TODO - convert index in JSON to number instead of String
        int resultTestCaseIndex = Integer.parseInt(testCaseIndexStr);
        assertEquals(testCaseIndex, resultTestCaseIndex);

        String arg1 = jsonObjectRow.getString("arg1");
        if (!arg1.startsWith("V")) {
            fail();
        }

        String arg2 = jsonObjectRow.getString("arg2");
        if (!arg2.startsWith("V")) {
            fail();
        }
    }

    private JSONObject convertToJsonObject(WebServiceResponse webServiceResponse) {

        BufferedReader bufferedReader = webServiceResponse.getResponseBufferedReader();
        String result = bufferedReader.lines().collect(Collectors.joining());

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException err) {
            fail();
        }
        return jsonObject;
    }

}
