package com.ecfeed.junit.performance.runner;

import com.ecfeed.core.junit5.runner.web.ServiceRestTemplate;
import com.ecfeed.core.utils.TestCasesRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class PerformanceServiceTest extends ServiceRestTemplate {

    private boolean fTerminate = false;

    private int fLocalCount = 0;
    private int fCount = 0;

    private List<Integer> fReturnValue;

    public PerformanceServiceTest(TestCasesRequest request, List<Integer> returnValue) {
        super(request, "https://localhost:8091/testCaseService");

        fReturnValue = returnValue;
    }

    @Override
    protected void adjustParameters(String... customSettings) {
        fClientType = "localTestRunner";
        fKeyStorePath = "src/test/resources/security";
        fTrustStorePath = "src/test/resources/security";
    }

    @Override
    protected void consumeReceivedMessage(String message) {

        if (fLocalCount == 100000) {
            System.out.println("checkpoint = [" + fCount + "]");
            fLocalCount = 0;
        }

        fLocalCount++;
        fCount++;
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

        fReturnValue.add(fCount);
        fTerminate = true;
    }

}
