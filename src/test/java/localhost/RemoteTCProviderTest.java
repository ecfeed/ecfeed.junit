package localhost;


import com.ecfeed.core.model.*;
import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ParserException;
import com.ecfeed.core.utils.SimpleProgressMonitor;
import com.ecfeed.core.utils.TestModel;
import com.ecfeed.core.webservice.client.GenWebServiceClient;
import com.ecfeed.core.genservice.provider.RemoteTCProvider;
import com.ecfeed.core.webservice.client.GenWebServiceClientType;
import com.ecfeed.core.webservice.client.IWebServiceClient;
import com.ecfeed.core.genservice.provider.RemoteTCProviderInitData;
import com.ecfeed.junit.utils.TestDataSupplier;
import localhost.utils.ExecutionConditionLocalHostAvailable;
import localhost.utils.TestHelper;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// TODO - add tests with WebServiceClientStub

public class RemoteTCProviderTest {

    SimpleProgressMonitor fSimpleProgressMonitor;

    @Test
    @DisplayName("testGenerator")
    @ExtendWith(ExecutionConditionLocalHostAvailable.class)
    public void testGenerator() {
        try {
            runGeneratorTest();
        } catch (Exception e) {
            fail();
        }
    }

    private void runGeneratorTest() throws Exception {

        MethodNode methodNode = getMethodNode();

        String requestText = "{\"method\":\"test.Class1.testMethod(java.lang.String,java.lang.String)\",\"model\":\"" +
                TestDataSupplier.testModelUuid +
                "\",\"userData\":\"{'dataSource':'genCartesian'}\"}";

        fSimpleProgressMonitor = new SimpleProgressMonitor();

        RemoteTCProvider remoteTCProvider = TestHelper.createTCProvider(methodNode, TestHelper.REQUEST_DATA, requestText, fSimpleProgressMonitor);

        try {
            getAndCheckGeneratedTestCases(remoteTCProvider);
        } finally {
            remoteTCProvider.close();
        }
    }

    @Test
    @DisplayName("testStatic")
    @ExtendWith(ExecutionConditionLocalHostAvailable.class)
    public void testStatic() {

        try {
            runStaticTest();
        } catch (Exception e) {
            fail();
        }
    }

    private void runStaticTest() throws Exception {

        MethodNode methodNode = getMethodNode();

        String requestText = "{\"method\":\"test.Class1.testMethod(java.lang.String,java.lang.String)\",\"model\":\"" +
                TestDataSupplier.testModelUuid +
                "\",\"userData\":\"{'dataSource':'static','testSuites':['second']}\"}";

        fSimpleProgressMonitor = new SimpleProgressMonitor();

        RemoteTCProvider remoteTCProvider = TestHelper.createTCProvider(methodNode, TestHelper.REQUEST_DATA, requestText, fSimpleProgressMonitor);

        try {
            getAndCheckStaticTestCases(remoteTCProvider);
        } finally {
            remoteTCProvider.close();
        }
    }

    private MethodNode getMethodNode() throws ParserException {

        RootNode rootNode = new ModelParser().parseModel(TestModel.getModelXml(), null, new ArrayList<String>());

        ClassNode classNode = rootNode.getClass("test.Class1");
        MethodNode methodNode = classNode.getMethods().get(1);

        return methodNode;
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
        assertEquals(4, fSimpleProgressMonitor.getTotalProgress());
        assertEquals(false, fSimpleProgressMonitor.isTaskRunning());
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

}
