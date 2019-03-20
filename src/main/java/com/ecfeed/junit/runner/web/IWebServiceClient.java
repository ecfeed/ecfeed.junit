package com.ecfeed.junit.runner.web;

public interface IWebServiceClient {

    WebServiceResponseData getServerResponse(String requestType, String requestText);
    void close();

}
