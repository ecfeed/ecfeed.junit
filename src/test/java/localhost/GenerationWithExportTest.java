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

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GenerationWithExportTest {

    @Test
    public void generateWithDefaultExportFormat() {

        IWebServiceClient genWebServiceClient =
                TestHelper.createWebServiceClient(GenWebServiceClient.getTestCasesEndPoint());

        String request = "{" +
                "\"model\":\"TestUuid11\"," +
                "\"method\":\"test.Class1.testMethod(java.lang.String,java.lang.String)\"," +
                "\"userData\":\"{'dataSource':'genCartesian'}\"" +
                "}";

        WebServiceResponse webServiceResponse = genWebServiceClient.sendPostRequest(TestHelper.REQUEST_EXPORT, request);

        verifyJsonTemplateResponse(webServiceResponse);
    }

    @Test
    public void generateWithDefaultJsonTemplate() {

        IWebServiceClient genWebServiceClient =
                TestHelper.createWebServiceClient(GenWebServiceClient.getTestCasesEndPoint());

        String request = "{" +
                "\"model\":\"TestUuid11\"," +
                "\"method\":\"test.Class1.testMethod(java.lang.String,java.lang.String)\"," +
                "\"userData\":\"{'dataSource':'genCartesian'}\"," +
                "\"template\":\"JSON\"" +
                "}";

        WebServiceResponse webServiceResponse = genWebServiceClient.sendPostRequest(TestHelper.REQUEST_EXPORT, request);

        verifyJsonTemplateResponse(webServiceResponse);
    }

    @Test
    public void generateWithDefaultCsvTemplate() {

        IWebServiceClient genWebServiceClient =
                TestHelper.createWebServiceClient(GenWebServiceClient.getTestCasesEndPoint());

        String request = "{" +
                "\"model\":\"TestUuid11\"," +
                "\"method\":\"test.Class1.testMethod(java.lang.String,java.lang.String)\"," +
                "\"userData\":\"{'dataSource':'genCartesian'}\"," +
                "\"template\":\"CSV\"" +
                "}";

        WebServiceResponse webServiceResponse = genWebServiceClient.sendPostRequest(TestHelper.REQUEST_EXPORT, request);

        verifyResponse(webServiceResponse, "arg1,arg2|V11,V21|V11,V22|V12,V21|V12,V22||");
    }

    @Test
    public void generateWithInvalidTemplate() {

        IWebServiceClient genWebServiceClient =
                TestHelper.createWebServiceClient(GenWebServiceClient.getTestCasesEndPoint());

        final String template = "XXX";

        String request = "{" +
                "\"model\":\"TestUuid11\"," +
                "\"method\":\"test.Class1.testMethod(java.lang.String,java.lang.String)\"," +
                "\"userData\":\"{'dataSource':'genCartesian'}\"," +
                "\"template\":\"" + template + "\"" +
                "}";

        WebServiceResponse webServiceResponse = genWebServiceClient.sendPostRequest(TestHelper.REQUEST_EXPORT, request);

        verifyErrorResponse(webServiceResponse);
    }

    @Test
    public void generateWithTrivialCustomTemplate() {

        IWebServiceClient genWebServiceClient =
                TestHelper.createWebServiceClient(GenWebServiceClient.getTestCasesEndPoint());

        final String template = "[Header]\\nHEADER\\n[TestCase]\\nTESTCASE\\n[Footer]\\nFOOTER";

        String request = "{" +
                "\"model\":\"TestUuid11\"," +
                "\"method\":\"test.Class1.testMethod(java.lang.String,java.lang.String)\"," +
                "\"userData\":\"{'dataSource':'genCartesian'}\"," +
                "\"template\":\"" + template + "\"" +
                "}";

        WebServiceResponse webServiceResponse = genWebServiceClient.sendPostRequest(TestHelper.REQUEST_EXPORT, request);

        verifyResponse(webServiceResponse, "HEADER|TESTCASE|TESTCASE|TESTCASE|TESTCASE|FOOTER|");
   }

    private void verifyErrorResponse(WebServiceResponse webServiceResponse) {

        if (!webServiceResponse.isResponseStatusOk()) {
            fail();
        }

        BufferedReader bufferedReader = webServiceResponse.getResponseBufferedReader();

        String line = null;

        try {
            line = bufferedReader.readLine();
        } catch (Exception e) {
            fail();
        }

        if (!line.startsWith("ERROR: ")) {
            fail();
        }
    }

    private void verifyJsonTemplateResponse(WebServiceResponse webServiceResponse) {

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

        int resultTestCaseIndex = jsonObjectRow.getInt("index");
        assertEquals(testCaseIndex, resultTestCaseIndex);

        String arg1 = jsonObjectRow.getString("arg1");
        final String v = "V";

        if (!arg1.startsWith(v)) {
            fail();
        }

        String arg2 = jsonObjectRow.getString("arg2");
        if (!arg2.startsWith(v)) {
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

    private void verifyResponse(
            WebServiceResponse webServiceResponse,
            String expectedResponse) {

        if (!webServiceResponse.isResponseStatusOk()) {
            fail();
        }

        BufferedReader bufferedReader = webServiceResponse.getResponseBufferedReader();

        String result = "";

        try {
            for (String line; (line = bufferedReader.readLine()) != null; ) {
                result = result + (line + "|");
            }
        } catch (IOException e) {
            fail();
        }

        assertEquals(expectedResponse, result);
    }

}
