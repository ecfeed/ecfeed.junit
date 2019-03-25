package com.ecfeed.core.genservice.protocol.schema;

import com.ecfeed.core.genservice.protocol.GenServiceProtocolHelper;
import com.ecfeed.core.utils.ExceptionHelper;

import java.io.IOException;

public class MainSchemaParser {

    public static IMainSchema parse(String json) {

        IMainSchema result;

        result = parseStatus(json);
        if (result != null) {
            return result;
        }

        result = parseTestCase(json);
        if (result != null) {
            return result;
        }

        result = parseInfo(json);
        if (result != null) {
            return result;
        }

        ExceptionHelper.reportRuntimeException("Can not parse json.");
        return null;
    }

    private static IMainSchema parseStatus(String json) {

        try {
            return GenServiceProtocolHelper.parseStatus(json);
        } catch (IOException e) {
            return null;
        }
    }

    private static IMainSchema parseTestCase(String json) {

        try {
            return GenServiceProtocolHelper.parseTestCase(json);
        } catch (IOException e) {
            return null;
        }
    }

    private static IMainSchema parseInfo(String json) {

        try {
            return GenServiceProtocolHelper.parseInfo(json);
        } catch (IOException e) {
            return null;
        }
    }
}
