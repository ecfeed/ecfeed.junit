package com.ecfeed.junit.runner.web;

public interface IWebServiceClient {

    ResponseData getServerResponse(String requestType, String requestText);

}
