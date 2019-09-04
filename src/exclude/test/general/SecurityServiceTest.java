package general;

import com.ecfeed.core.junit5.runner.web.ServiceRestTemplate;

import java.util.List;

public class SecurityServiceTest extends ServiceRestTemplate {

    private List<String> fResponse;
    private boolean fTerminate;

    public SecurityServiceTest(Object request, List<String> response, String... settings) {
        super(request, "https://localhost:8091/testCaseService", settings);

        fResponse = response;
    }

    @Override
    protected void adjustParameters(String... settings) {
        fClientType = GenWebServiceClientType.LOCAL_TEST_RUNNER.toString();

        fKeyStorePath = settings[0];
        fTrustStorePath = settings[0];

        fCommunicationProtocol = settings[1];
    }

    @Override
    protected void consumeReceivedMessage(String message) {
        fResponse.add(message);
        System.out.println("message = [" + message + "]");
    }

    @Override
    protected boolean cancelExecution() {
        return fTerminate;
    }

    @Override
    protected Object sendUpdatedRequest() {
        return null;
    }

    @Override
    protected void handleException(Exception e) {
        RuntimeException exception = new RuntimeException("Exception");
        exception.addSuppressed(e);

        throw exception;
    }

    protected void waitForStreamEnd() {
        fTerminate = true;
    }

}