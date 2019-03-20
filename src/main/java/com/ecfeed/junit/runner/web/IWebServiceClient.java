package com.ecfeed.junit.runner.web;

public interface IWebServiceClient {

    WebServiceResponse postRequest(String requestType, String request);
    void close();

}
