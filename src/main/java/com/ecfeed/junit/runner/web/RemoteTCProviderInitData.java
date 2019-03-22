package com.ecfeed.junit.runner.web;

import com.ecfeed.core.provider.ITCProviderInitData;

public class RemoteTCProviderInitData implements ITCProviderInitData {

    public RemoteTCProviderInitData(String requestTypeArg, String requestTextArg) {
        requestType = requestTypeArg;
        requestText = requestTextArg;
    }

    public String requestType;
    public String requestText;
}
