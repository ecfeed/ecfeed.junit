package localhost.utils;

import com.ecfeed.core.genservice.provider.RemoteTCProvider;
import com.ecfeed.core.genservice.provider.RemoteTCProviderInitData;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.SimpleProgressMonitor;
import com.ecfeed.core.webservice.client.GenWebServiceClient;
import com.ecfeed.core.webservice.client.GenWebServiceClientType;
import com.ecfeed.core.webservice.client.IWebServiceClient;
import com.ecfeed.core.webservice.client.WebServiceResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestHelper {

    public static final String GEN_SERVICE_URL_ON_LOCALHOST = "https://localhost:8090";
    public static final String REQUEST_DATA = "requestData";
    public static final String REQUEST_EXPORT = "requestExport";
    private static final String TEST_KEY_STORE_PATH = "src/test/resources/security";

    public static IWebServiceClient createWebServiceClient(String endpoint) {

        Optional<String> keyStorePath = Optional.of(TEST_KEY_STORE_PATH);

        return new GenWebServiceClient(
                TestHelper.GEN_SERVICE_URL_ON_LOCALHOST,
                endpoint,
                GenWebServiceClientType.LOCAL_TEST_RUNNER.toString(),
                keyStorePath);
    }

    public static RemoteTCProvider createTCProvider(
            MethodNode methodNode,
            String requestType,
            String requestText,
            SimpleProgressMonitor simpleProgressMonitor) throws Exception {

        IWebServiceClient webServiceClient =
                TestHelper.createWebServiceClient(GenWebServiceClient.getTestCasesEndPoint());

        RemoteTCProvider remoteTCProvider = new RemoteTCProvider(webServiceClient);

        RemoteTCProviderInitData remoteTCProviderInitData =
                new RemoteTCProviderInitData(methodNode, requestType, requestText);

        remoteTCProvider.initialize(remoteTCProviderInitData, simpleProgressMonitor);

        return remoteTCProvider;
    }

    public static void verifyResponse(
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

