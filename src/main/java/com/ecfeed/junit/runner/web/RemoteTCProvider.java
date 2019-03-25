package com.ecfeed.junit.runner.web;

import com.ecfeed.core.genservice.protocol.schema.ChoiceSchema;
import com.ecfeed.core.genservice.protocol.schema.IMainSchema;
import com.ecfeed.core.genservice.protocol.schema.MainSchemaParser;
import com.ecfeed.core.genservice.protocol.schema.ResultTestCaseSchema;
import com.ecfeed.core.model.*;
import com.ecfeed.core.provider.ITCProvider;
import com.ecfeed.core.provider.ITCProviderInitData;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IEcfProgressMonitor;
import com.ecfeed.core.utils.SystemLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoteTCProvider implements ITCProvider {

    IWebServiceClient fWebServiceClient;
    WebServiceResponse fWebServiceResponse;
    MethodNode fMethodNode;

    public RemoteTCProvider(IWebServiceClient webServiceClient) {
        fWebServiceClient = webServiceClient;
    }

    @Override
    public void initialize(ITCProviderInitData initData, IEcfProgressMonitor progressMonitor) throws Exception {

        RemoteTCProviderInitData remoteTCProviderInitData = (RemoteTCProviderInitData)initData;
        String requestType = remoteTCProviderInitData.requestType;
        String requestText = remoteTCProviderInitData.requestText;
        fMethodNode = remoteTCProviderInitData.methodNode;

        fWebServiceResponse = fWebServiceClient.postRequest(requestType, requestText);

        if (!fWebServiceResponse.isResponseStatusOk()) {
            ExceptionHelper.reportRuntimeException(
                    "Request failed. Response status: " + fWebServiceResponse.getResponseStatus());
        }
    }

    private String readLine(BufferedReader responseBufferedReader) {

        try {
            return responseBufferedReader.readLine();
        } catch (IOException e) {
            ExceptionHelper.reportRuntimeException("Cannot read line from response.", e);
        }
        return null;
    }

    @Override
    public void close() {

        try {
            fWebServiceResponse.getResponseBufferedReader().close();
        } catch (Exception e) {
            SystemLogger.logCatch("Cannot close response buffer.");
        }

        fWebServiceClient.close();
    }

    @Override
    public MethodNode getMethodNode() {

        return fMethodNode;
    }

    @Override
    public TestCaseNode getNextTestCase() throws Exception {

        while(true) {

            String line = readLine(fWebServiceResponse.getResponseBufferedReader());

            if (line == null) {
                return null;
            }

            IMainSchema mainSchema = MainSchemaParser.parse(line);

            if (mainSchema instanceof ResultTestCaseSchema) {
                return createTestCase((ResultTestCaseSchema)mainSchema);
            }
        }
    }

    private TestCaseNode createTestCase(ResultTestCaseSchema testCaseSchema) {

        int parametersCount = fMethodNode.getParametersCount();

        ChoiceSchema[] choiceSchemas = testCaseSchema.getTestCase();
        List<ChoiceNode> choiceNodes = new ArrayList();

        for (int paramIndex = 0; paramIndex < parametersCount; paramIndex++) {
            MethodParameterNode methodParameterNode = getMethodNode().getMethodParameter(paramIndex);

            String choiceName = choiceSchemas[paramIndex].getName();
            ChoiceNode choiceNode = methodParameterNode.findChoice(choiceName);

            if (choiceNode == null) {
                ExceptionHelper.reportRuntimeException("Cannot find choice node for name: " + choiceName + ".");
            }

            choiceNodes.add(choiceNode);
        }

        return new TestCaseNode(choiceNodes);
    }

    @Override
    public boolean canCalculateProgress() {
        return false;
    }

    @Override
    public int getTotalProgress() {
        return 0;
    }

    @Override
    public int getActualProgress() {
        return 0;
    } // TODO - move to another package

}
