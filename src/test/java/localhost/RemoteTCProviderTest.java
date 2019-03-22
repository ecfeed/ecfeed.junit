package localhost;

import com.ecfeed.core.utils.IEcfProgressMonitor;
import com.ecfeed.junit.runner.web.GenWebServiceClient;
import com.ecfeed.junit.runner.web.RemoteTCProvider;
import com.ecfeed.junit.runner.web.IWebServiceClient;
import com.ecfeed.junit.runner.web.RemoteTCProviderInitData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class RemoteTCProviderTest {

    @Test
    public void testTCP() {
        System.out.println("TEST");

        try {
            runTestTCP();
        } catch (Exception e) {
            fail();
        }
    }

    private void runTestTCP() throws Exception {

        IWebServiceClient webServiceClient = createWebServiceClient();

        RemoteTCProvider remoteTCProvider = new RemoteTCProvider(webServiceClient);

        String requestText = "{\"method\":\"public void localhost.ShouldGenerateWithCartesianGenerator.test(java.lang.String,java.lang.String)\",\"model\":\"TestUuid1\",\"userData\":\"{'dataSource':'genCartesian', 'method':'test.Class1.testMethod'}\"}";

        RemoteTCProviderInitData remoteTCProviderInitData =
                new RemoteTCProviderInitData("requestData", requestText);

        EcfProgressMonitor ecfProgressMonitor = new EcfProgressMonitor();
        remoteTCProvider.initialize(remoteTCProviderInitData, ecfProgressMonitor);
    }

    private IWebServiceClient createWebServiceClient() {

        return new GenWebServiceClient(
                "https://localhost:8090/testCaseService", // TODO
                "TLSv1.2", // TODO
                "src/test/resources/security", // TODO
                "localTestRunner",
                "1.0");

    }

    private static class EcfProgressMonitor implements IEcfProgressMonitor {

        @Override
        public void setTaskBegin(String name, int totalProgress) {

        }

        @Override
        public void setTaskEnd() {

        }

        @Override
        public void setCurrentProgress(int work) {

        }

        @Override
        public boolean isCanceled() {
            return false;
        }
    }
}
