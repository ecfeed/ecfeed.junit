package com.ecfeed.junit.runner.web;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebServiceClient {

    static final private String TAG_CLIENT_VERSION = "clientVersion";
    static final private String TAG_CLIENT_TYPE = "clientType";
    static final private String TAG_REQUEST_TYPE = "requestType";

    public static ResponseData getResponse(

            String requestText,
            WebTarget webTarget,
            String clientType,
            String clientVersion,
            String requestType) {

        Response response = webTarget
                .queryParam(TAG_CLIENT_VERSION, clientVersion)
                .queryParam(TAG_CLIENT_TYPE, clientType)
                .queryParam(TAG_REQUEST_TYPE, requestType)
                .request()
                .post(Entity.entity(requestText, MediaType.APPLICATION_JSON));

        int responseStatus = response.getStatus();
        BufferedReader responseBufferedReader =
                new BufferedReader(new InputStreamReader(response.readEntity(InputStream.class)));

        return new ResponseData(responseStatus, responseBufferedReader);
    }

    public static ResponseData getResponse2(String requestText, String requestType, WebTarget webTarget) {
        Response response =
                webTarget
                        .queryParam(TAG_REQUEST_TYPE, requestType)
                        .request()
                        .post(Entity.entity(requestText, MediaType.APPLICATION_JSON));

        int responseStatus = response.getStatus();
        BufferedReader responseBufferedReader =
                new BufferedReader(new InputStreamReader(response.readEntity(InputStream.class)));

        return new ResponseData(responseStatus, responseBufferedReader);
    }

}
