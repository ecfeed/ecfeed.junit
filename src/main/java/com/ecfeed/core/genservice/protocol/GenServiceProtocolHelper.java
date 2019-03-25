package com.ecfeed.core.genservice.protocol;

import com.ecfeed.core.genservice.protocol.schema.ResultInfoSchema;
import com.ecfeed.core.genservice.protocol.schema.ResultStatusSchema;
import com.ecfeed.core.genservice.protocol.schema.ResultTestCaseSchema;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class GenServiceProtocolHelper {

    public static String TAG_BEG_DATA = "BEG_DATA";
    public static String TAG_BEG_CHUNK = "BEG_CHUNK";
    public static String TAG_END_DATA = "END_DATA";
    public static String TAG_END_CHUNK = "END_CHUNK";
    public static String TAG_ACKNOWLEDGED = "ACKNOWLEDGED";

    public static ResultStatusSchema parseStatus(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.reader().forType(ResultStatusSchema.class).readValue(json);
    }

    public static ResultTestCaseSchema parseTestCase(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.reader().forType(ResultTestCaseSchema.class).readValue(json);
    }

    public static ResultInfoSchema parseInfo(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.reader().forType(ResultInfoSchema.class).readValue(json);
    }

}
