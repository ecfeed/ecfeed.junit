package localhost;

import com.ecfeed.core.model.*;
import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ParserException;
import com.ecfeed.core.utils.IEcfProgressMonitor;
import com.ecfeed.core.utils.TestModel;
import com.ecfeed.junit.runner.web.GenWebServiceClient;
import com.ecfeed.junit.runner.web.RemoteTCProvider;
import com.ecfeed.junit.runner.web.IWebServiceClient;
import com.ecfeed.junit.runner.web.RemoteTCProviderInitData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class RemoteTCProviderTest {

    TestProgressMonitor fEcfProgressMonitor;

    @Test
    @DisplayName("testGenerator")
    public void testGenerator() {

        try {
            runGeneratorTest();
        } catch (Exception e) {
            fail();
        }
    }

    private void runGeneratorTest() throws Exception {

        MethodNode methodNode = getMethodNode();

        String requestText = "{\"method\":\"test.Class1.testMethod(java.lang.String,java.lang.String)\",\"model\":\"TestUuid1\",\"userData\":\"{'dataSource':'genCartesian'}\"}";

        RemoteTCProvider remoteTCProvider = createTCProvider(methodNode, requestText);

        try {
            getAndCheckGeneratedTestCases(remoteTCProvider);
        } finally {
            remoteTCProvider.close();
        }
    }

    @Test
    @DisplayName("testStatic")
    public void testStatic() {

        try {
            runStaticTest();
        } catch (Exception e) {
            fail();
        }
    }

    private void runStaticTest() throws Exception {

        MethodNode methodNode = getMethodNode();

        String requestText = "{\"method\":\"test.Class1.testMethod(java.lang.String,java.lang.String)\",\"model\":\"TestUuid1\",\"userData\":\"{'dataSource':'static','testSuites':['second']}\"}";

        RemoteTCProvider remoteTCProvider = createTCProvider(methodNode, requestText);

        try {
            getAndCheckStaticTestCases(remoteTCProvider);
        } finally {
            remoteTCProvider.close();
        }
    }

    private MethodNode getMethodNode() throws ParserException {

        RootNode rootNode = new ModelParser().parseModel(TestModel.getModelXml(), null);

        ClassNode classNode = rootNode.getClass("test.Class1");
        MethodNode methodNode = classNode.getMethods().get(1);

        return methodNode;
    }

    private RemoteTCProvider createTCProvider(MethodNode methodNode, String requestText) throws Exception {

        IWebServiceClient webServiceClient = createWebServiceClient();
        RemoteTCProvider remoteTCProvider = new RemoteTCProvider(webServiceClient);

        RemoteTCProviderInitData remoteTCProviderInitData =
                new RemoteTCProviderInitData(methodNode, "requestData", requestText);

        fEcfProgressMonitor = new TestProgressMonitor();

        remoteTCProvider.initialize(remoteTCProviderInitData, fEcfProgressMonitor);

        return remoteTCProvider;
    }

    private void getAndCheckGeneratedTestCases(RemoteTCProvider remoteTCProvider) throws Exception {

        int counter = 0;

        while (true) {

            TestCaseNode testCaseNode = remoteTCProvider.getNextTestCase();

            if (testCaseNode == null) {
                break;
            }

            counter++;
            checkTestCase(testCaseNode);
        }

        assertEquals(4, counter);
    }

    private void getAndCheckStaticTestCases(RemoteTCProvider remoteTCProvider) throws Exception {

        int counter = 0;

        while (true) {

            TestCaseNode testCaseNode = remoteTCProvider.getNextTestCase();

            if (testCaseNode == null) {
                break;
            }

            counter++;
            checkTestCase(testCaseNode);
        }

        assertEquals(4, counter);
        assertEquals(4, fEcfProgressMonitor.getTotalProgress());
        assertEquals(true, fEcfProgressMonitor.wasEndTask());
    }

    private void checkTestCase(TestCaseNode testCaseNode) {

        List<ChoiceNode> choiceNodes = testCaseNode.getTestData();

        ChoiceNode choiceNode1 = choiceNodes.get(0);
        checkTheFirstChoice(choiceNode1);

        ChoiceNode choiceNode2 = choiceNodes.get(1);
        checkTheSecondChoice(choiceNode2);
    }

    private void checkTheFirstChoice(ChoiceNode theFirstChoice) {

        String name = theFirstChoice.getQualifiedName();

        if (name.equals("choice11")) {
            return;
        }

        if (name.equals("choice12")) {
            return;
        }

        fail("Invalid choice name: " + name);
    }

    private void checkTheSecondChoice(ChoiceNode theFirstChoice) {

        String name = theFirstChoice.getQualifiedName();

        if (name.equals("choice21")) {
            return;
        }

        if (name.equals("choice22")) {
            return;
        }

        fail("Invalid choice name: " + name);
    }

    private IWebServiceClient createWebServiceClient() {

        return new GenWebServiceClient(
                "https://localhost:8090/testCaseService", // TODO
                "TLSv1.2", // TODO
                "src/test/resources/security", // TODO
                "localTestRunner",
                "1.0");
    }

    private static class TestProgressMonitor implements IEcfProgressMonitor { // TODO rename and MOVE TO CORE ?

        int fTotalProgress = IEcfProgressMonitor.PROGRESS_UNKNOWN;
        int fCurrentProgress;
        boolean fWasEndTask = false;

        @Override
        public void setTaskBegin(String name, int totalProgress) {
            fTotalProgress = totalProgress;
            fCurrentProgress = 0;
        }

        @Override
        public void setTaskEnd() {
            fWasEndTask = true;
        }

        @Override
        public void setCurrentProgress(int currentProgress) {
            fCurrentProgress = currentProgress;
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public boolean canCalculateProgress() {

            if (fTotalProgress == IEcfProgressMonitor.PROGRESS_UNKNOWN) {
                return false;
            }

            return true;
        }

        public int getTotalProgress() {
            return fTotalProgress;
        }

        public boolean wasEndTask() {
            return fWasEndTask;
        }
    }
}
