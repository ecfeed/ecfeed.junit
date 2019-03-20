package com.ecfeed.junit.runner.web;

public interface IWebServiceClient {

    ResponseData getServerResponse(String clientType, String clientVersion, String requestType, String requestText);

}
