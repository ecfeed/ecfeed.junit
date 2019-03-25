package com.ecfeed.junit.runner.web;

import com.ecfeed.core.genservice.protocol.GenServiceProtocolHelper;
import com.ecfeed.core.genservice.protocol.GenServiceProtocolState;
import com.ecfeed.core.genservice.protocol.schema.*;
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

// TODO - move to genservice package

public class RemoteTCProvider implements ITCProvider {

    IWebServiceClient fWebServiceClient;
    WebServiceResponse fWebServiceResponse;
    MethodNode fMethodNode;
    GenServiceProtocolState fGenServiceProtocolState;


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

        fGenServiceProtocolState = GenServiceProtocolState.AFTER_INITIALIZE;
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

    // TODO - ADD totalProgress and progress

    @Override
    public TestCaseNode getNextTestCase() throws Exception {

        while(true) {

            String line = readLine(fWebServiceResponse.getResponseBufferedReader());

            System.out.println(line);

            if (line == null) {
                return null;
            }

            IMainSchema mainSchema = MainSchemaParser.parse(line);

            fGenServiceProtocolState = processProtocolState(mainSchema, fGenServiceProtocolState);

            if (fGenServiceProtocolState == GenServiceProtocolState.AFTER_END_DATA) {
                return null;
            }

            if (mainSchema instanceof ResultTestCaseSchema) {
                return createTestCase((ResultTestCaseSchema)mainSchema);
            }
        }
    }

    private static GenServiceProtocolState processProtocolState(
            IMainSchema mainSchema,
            GenServiceProtocolState currentGenServiceProtocolState) {

        if (mainSchema instanceof ResultInfoSchema) {
            return currentGenServiceProtocolState;
        }

        if (currentGenServiceProtocolState == GenServiceProtocolState.AFTER_INITIALIZE) {
            return processStateAfterInitialize(mainSchema);
        }

        if (currentGenServiceProtocolState == GenServiceProtocolState.AFTER_BEG_DATA) {
            return processStateAfterBegData(mainSchema);
        }

        if (currentGenServiceProtocolState == GenServiceProtocolState.AFTER_BEG_CHUNK) {
            return processStateAfterBegChunk(mainSchema);
        }

        if (currentGenServiceProtocolState == GenServiceProtocolState.AFTER_END_CHUNK) {
            return processStateAfterEndChunk(mainSchema);
        }

        ExceptionHelper.reportRuntimeException("Invalid protocol state.");
        return null;
    }

    private static GenServiceProtocolState processStateAfterInitialize(IMainSchema mainSchema) {

        if (!(mainSchema instanceof ResultStatusSchema)) {
            ExceptionHelper.reportRuntimeException("Status line expected.");
        }

        ResultStatusSchema resultStatusSchema = (ResultStatusSchema)mainSchema;

        String status = resultStatusSchema.getStatus();

        if (!GenServiceProtocolHelper.isTagBegData(status)) {
            ExceptionHelper.reportRuntimeException("Expected status: " + GenServiceProtocolHelper.TAG_BEG_DATA);
        }

        return GenServiceProtocolState.AFTER_BEG_DATA;
    }

    private static GenServiceProtocolState processStateAfterBegData(
            IMainSchema mainSchema) {

        if (!(mainSchema instanceof ResultStatusSchema)) {
            ExceptionHelper.reportRuntimeException("Status line expected.");
        }

        ResultStatusSchema resultStatusSchema = (ResultStatusSchema)mainSchema;

        String status = resultStatusSchema.getStatus();

        if (!GenServiceProtocolHelper.isTagBegChunk(status)) {
            ExceptionHelper.reportRuntimeException("Expected status: " + GenServiceProtocolHelper.TAG_BEG_CHUNK);
        }

        return GenServiceProtocolState.AFTER_BEG_CHUNK;
    }

    private static GenServiceProtocolState processStateAfterBegChunk(
            IMainSchema mainSchema) {

        if (mainSchema instanceof ResultTestCaseSchema) {
            return GenServiceProtocolState.AFTER_BEG_CHUNK;
        }

        if (mainSchema instanceof ResultStatusSchema) {

            ResultStatusSchema resultStatusSchema = (ResultStatusSchema)mainSchema;

            String status = resultStatusSchema.getStatus();

            if (!GenServiceProtocolHelper.isTagEndChunk(status)) {
                ExceptionHelper.reportRuntimeException("Expected status: " + GenServiceProtocolHelper.TAG_END_CHUNK);
            }

            return GenServiceProtocolState.AFTER_END_CHUNK;
        }

        ExceptionHelper.reportRuntimeException("Invalid command line.");
        return null;
    }

    private static GenServiceProtocolState processStateAfterEndChunk(IMainSchema mainSchema) {

        if (!(mainSchema instanceof ResultStatusSchema)) {
            ExceptionHelper.reportRuntimeException("Status line expected.");
        }

        ResultStatusSchema resultStatusSchema = (ResultStatusSchema)mainSchema;

        String status = resultStatusSchema.getStatus();

        if (!GenServiceProtocolHelper.isTagEndData(status)) {
            ExceptionHelper.reportRuntimeException("Expected status: " + GenServiceProtocolHelper.TAG_END_DATA);
        }

        return GenServiceProtocolState.AFTER_END_DATA;
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
