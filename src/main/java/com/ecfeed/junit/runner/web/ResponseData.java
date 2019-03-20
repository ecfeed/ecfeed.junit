package com.ecfeed.junit.runner.web;

import java.io.BufferedReader;

public class ResponseData {

    private int fResponseStatus;
    private BufferedReader fResponseBufferedReader;

    public ResponseData(int responseStatus, BufferedReader responseBufferedReader) {
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
