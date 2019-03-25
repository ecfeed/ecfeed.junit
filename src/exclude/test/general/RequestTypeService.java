package general;

import com.ecfeed.core.junit5.runner.web.ServiceRestTemplate;

import java.util.List;

public class RequestTypeService extends ServiceRestTemplate {

    private List<String> fResponse;
    private boolean fTerminate;

    public RequestTypeService(Object request, String requestType, List<String> response) {
        super(request, "https://localhost:8091/testCaseService");

        fResponse = response;

        fClientInitialReqeust = requestType;
    }

    @Override
    protected void adjustParameters(String... customSettings) {
        fClientType = "localTestRunner";
        fKeyStorePath = "src/test/resources/security";
        fTrustStorePath = "src/test/resources/security";
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
