package com.ecfeed.junit.runner.web;

import java.io.BufferedReader;

public class WebServiceResponse {

    private int fResponseStatus;
    private BufferedReader fResponseBufferedReader;

    public WebServiceResponse(int responseStatus, BufferedReader responseBufferedReader) {
        fResponseStatus = responseStatus;
        fResponseBufferedReader = responseBufferedReader;
    }

    public int getResponseStatus() {
        return fResponseStatus;
    }

    public BufferedReader getResponseBufferedReader() {
        return fResponseBufferedReader;
    }
}
