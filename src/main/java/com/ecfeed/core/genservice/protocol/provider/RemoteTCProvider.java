package com.ecfeed.core.genservice.protocol.provider;

import com.ecfeed.core.genservice.protocol.util.GenServiceProtocolHelper;
import com.ecfeed.core.genservice.protocol.util.GenServiceProtocolState;
import com.ecfeed.core.genservice.protocol.schema.*;
import com.ecfeed.core.model.*;
import com.ecfeed.core.provider.ITCProvider;
import com.ecfeed.core.provider.ITCProviderInitData;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IEcfProgressMonitor;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.core.webservice.client.WebServiceResponse;
import com.ecfeed.core.webservice.client.IWebServiceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO - move to genservice package

public class RemoteTCProvider implements ITCProvider {

    private static final int PROGRESS_UNKNOWN = -1;
    private IWebServiceClient fWebServiceClient;
    private WebServiceResponse fWebServiceResponse;
    private MethodNode fMethodNode;
    private GenServiceProtocolState fGenServiceProtocolState;
    private IEcfProgressMonitor fEcfProgressMonitor;
    private String fBufferedLine;
    private int fTotalProgress;


    public RemoteTCProvider(IWebServiceClient webServiceClient) {
        fWebServiceClient = webServiceClient;
        fGenServiceProtocolState = GenServiceProtocolState.BEFORE_BEG_DATA;
    }

    @Override
    public void initialize(ITCProviderInitData initData, IEcfProgressMonitor progressMonitor) throws Exception {

        RemoteTCProviderInitData remoteTCProviderInitData = (RemoteTCProviderInitData)initData;
        String requestType = remoteTCProviderInitData.requestType;
        String requestText = remoteTCProviderInitData.requestText;

        fEcfProgressMonitor = progressMonitor;

        fMethodNode = remoteTCProviderInitData.methodNode;

        fWebServiceResponse = fWebServiceClient.postRequest(requestType, requestText);

        if (!fWebServiceResponse.isResponseStatusOk()) {
            ExceptionHelper.reportRuntimeException(
                    "Request failed. Response status: " + fWebServiceResponse.getResponseStatus());
        }

        fTotalProgress = PROGRESS_UNKNOWN;

        processInitialTags();

        if (fEcfProgressMonitor.isCanceled()) {
            return;
        }

        fEcfProgressMonitor.setTaskBegin("Remote test cases provider", fTotalProgress);
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

            if (fEcfProgressMonitor.isCanceled()) {
                return null;
            }

            String line = readLine(fWebServiceResponse.getResponseBufferedReader());

            System.out.println(line);

            if (line == null) {
                ExceptionHelper.reportRuntimeException("Truncated data from remote testcases provider."); // TODO
            }

            IMainSchema mainSchema = MainSchemaParser.parse(line);

            if (processProgress(mainSchema)) {
                continue;
            }

            fGenServiceProtocolState = processProtocolState(mainSchema, fGenServiceProtocolState);

            if (fGenServiceProtocolState == GenServiceProtocolState.AFTER_END_DATA) {
                fEcfProgressMonitor.setTaskEnd();
                return null;
            }

            if (mainSchema instanceof ResultTestCaseSchema) {
                return createTestCase((ResultTestCaseSchema)mainSchema);
            }
        }
    }

    public void processInitialTags() throws Exception {

        fBufferedLine = null;

        while(true) {

            if (fEcfProgressMonitor.isCanceled()) {
                return ;
            }

            String line = readLine(fWebServiceResponse.getResponseBufferedReader());

            System.out.println(line);

            if (line == null) {
                ExceptionHelper.reportRuntimeException("Truncated data from remote testcases provider."); // TODO
            }

            IMainSchema mainSchema = MainSchemaParser.parse(line);

            if (processProgress(mainSchema)) {
                continue;
            }

            fGenServiceProtocolState = processProtocolState(mainSchema, fGenServiceProtocolState);

            if (mainSchema instanceof ResultTestCaseSchema) {
                fBufferedLine = line;
                return;
            }
        }
    }

    private String readLine(BufferedReader responseBufferedReader) {

        if (fBufferedLine != null) {
            String tmpLine = new String(fBufferedLine);
            fBufferedLine = null;
            return tmpLine;
        }

        try {
            return responseBufferedReader.readLine();
        } catch (IOException e) {
            ExceptionHelper.reportRuntimeException("Cannot read line from response.", e);
        }

        return null;
    }

    private boolean processProgress(IMainSchema mainSchema) {

        if (mainSchema instanceof ResultTotalProgressSchema) {

            ResultTotalProgressSchema resultTotalProgressSchema = (ResultTotalProgressSchema)mainSchema;
            fTotalProgress = resultTotalProgressSchema.getTotalProgress();
            return true;
        }

        if (mainSchema instanceof ResultProgressSchema) {

            ResultProgressSchema resultProgressSchema = (ResultProgressSchema)mainSchema;
            fEcfProgressMonitor.setCurrentProgress(resultProgressSchema.getProgress());
            return true;
        }

        return false;
    }

    private static GenServiceProtocolState processProtocolState(
            IMainSchema mainSchema,
            GenServiceProtocolState currentGenServiceProtocolState) {

        if (mainSchema instanceof ResultInfoSchema) {
            return currentGenServiceProtocolState;
        }

        if (currentGenServiceProtocolState == GenServiceProtocolState.BEFORE_BEG_DATA) {
            return processStateBeforeBegData(mainSchema);
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

    private static GenServiceProtocolState processStateBeforeBegData(IMainSchema mainSchema) {

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

    private void verifyProtocolStateForProgress() {

        if (fGenServiceProtocolState == GenServiceProtocolState.BEFORE_BEG_DATA
                || fGenServiceProtocolState == GenServiceProtocolState.AFTER_BEG_DATA) {
            ExceptionHelper.reportRuntimeException("Can not calculate progress.");
        }
    }

}
