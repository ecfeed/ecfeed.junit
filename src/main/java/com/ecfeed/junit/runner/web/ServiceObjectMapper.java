package com.ecfeed.junit.runner.web;

import com.ecfeed.core.utils.ExceptionHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// TODO - move to core ?
public class ServiceObjectMapper {

    public String mapRequestToString(Object request) {

        ObjectMapper fMapper = new ObjectMapper();

        try {
            return fMapper.writer().writeValueAsString(request);

        } catch (JsonProcessingException e) {
            ExceptionHelper.reportRuntimeException("Cannot convert request to string.", e);
            return null;
        }
    }

}
