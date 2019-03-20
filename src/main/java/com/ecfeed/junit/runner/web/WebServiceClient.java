package com.ecfeed.junit.runner.web;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebServiceClient implements IWebServiceClient {

    static final private String TAG_CLIENT_VERSION = "clientVersion";
    static final private String TAG_CLIENT_TYPE = "clientType";
    static final private String TAG_REQUEST_TYPE = "requestType";

    private WebTarget fWebTarget;
    private String fClientType;
    private String fClientVersion;

    public WebServiceClient(WebTarget webTarget, String clientType, String clientVersion) {

        fWebTarget = webTarget;
        fClientType = clientType;
        fClientVersion = clientVersion;
    }

    @Override
    public ResponseData getServerResponse(String requestType, String requestText) {

        Response response = fWebTarget
                .queryParam(TAG_CLIENT_TYPE, fClientType)
                .queryParam(TAG_CLIENT_VERSION, fClientVersion)
                .queryParam(TAG_REQUEST_TYPE, requestType)
                .request()
                .post(Entity.entity(requestText, MediaType.APPLICATION_JSON));

        int responseStatus = response.getStatus();

        BufferedReader responseBufferedReader =
                new BufferedReader(new InputStreamReader(response.readEntity(InputStream.class)));

        return new ResponseData(responseStatus, responseBufferedReader);
    }

}
