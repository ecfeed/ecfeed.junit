package com.ecfeed.junit.runner.web;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.provider.ITCProvider;
import com.ecfeed.core.provider.ITCProviderInitData;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IEcfProgressMonitor;
import com.ecfeed.core.utils.SystemLogger;

import java.io.BufferedReader;
import java.io.IOException;

public class RemoteTCProvider implements ITCProvider {

    IWebServiceClient fWebServiceClient;

    WebServiceResponse fWebServiceResponse;

    public RemoteTCProvider(IWebServiceClient webServiceClient) {
        fWebServiceClient = webServiceClient;
    }

    @Override
    public void initialize(ITCProviderInitData initData, IEcfProgressMonitor progressMonitor) throws Exception {

        RemoteTCProviderInitData remoteTCProviderInitData = (RemoteTCProviderInitData)initData;
        String requestType = remoteTCProviderInitData.requestType;
        String requestText = remoteTCProviderInitData.requestText;

        fWebServiceResponse = fWebServiceClient.postRequest(requestType, requestText);

        if (!fWebServiceResponse.isResponseStatusOk()) {
            ExceptionHelper.reportRuntimeException(
                    "Request failed. Response status: " + fWebServiceResponse.getResponseStatus());
        }

        String line;
        while ((line = readLine(fWebServiceResponse.getResponseBufferedReader())) != null) {
            System.out.println(line);
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
        return null;
    }

    @Override
    public TestCaseNode getNextTestCase() throws Exception {
        return null;
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
