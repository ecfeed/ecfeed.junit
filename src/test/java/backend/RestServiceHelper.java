package backend;

import com.ecfeed.core.utils.ExceptionHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class RestServiceHelper {

    public static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    public static final String HEADER_NAME_CACHE_CONTROL = "cache-control";
    public static final String NO_CACHE = "no-cache";
    public static final int OK_STATUS = 200;

    public static HttpResponse<JsonNode> sendOptionsRequest(String url) {

        HttpResponse<JsonNode> response = null;

        try {
            response = sendOptionsRequestIntr(url);
        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Getting http response failed.", e);
        }

        verifyResponseStatusOk(response);

        return response;
    }

    public static HttpResponse<JsonNode> sendGetRequest(String url, String token) {

        String authorization = createAuthorizationValue(token);
        HttpResponse<JsonNode> response = null;

        try {
            Unirest.setTimeouts(0, 0);

            response = Unirest
                    .get(url)
                    .header(HEADER_NAME_AUTHORIZATION, authorization)
                    .header(HEADER_NAME_CACHE_CONTROL, NO_CACHE)
                    .asJson();

        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Getting http response failed.", e);
        }

        verifyResponseStatusOk(response);

        return response;
    }

    private static HttpResponse<JsonNode> sendOptionsRequestIntr(String url) throws Exception {

        Unirest.setTimeouts(0, 0);

        return Unirest
                .options(url)
//                .header(HEADER_NAME_AUTHORIZATION, authorization)
                .header(HEADER_NAME_CACHE_CONTROL, NO_CACHE)
                .asJson();
    }

    public static void delete(String url, String authorization) {

        try {
            Unirest.setTimeouts(0, 0);

            Unirest.delete(url)
                    .header(HEADER_NAME_AUTHORIZATION, authorization)
                    .header(HEADER_NAME_CACHE_CONTROL, NO_CACHE)
                    .asJson();

        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException(e);
        }
    }

    public static String createAuthorizationValue(String token) {

        if (token == null) {
            ExceptionHelper.reportRuntimeException("Authorization token is not set.");
        }

        return "Bearer " + token;
    }


    private static void verifyResponseStatusOk(HttpResponse<JsonNode> httpResponse) {

        int status = httpResponse.getStatus();

        if (status == 200) {
            return;
        }

        String responseBody = httpResponse.getBody().toString();

        String lastCause = getLastCause(responseBody);

        String message =
                "Http request returned error code: " + status +
                        ". Cause from server: " + lastCause;

        throw new RuntimeException(message);
    }

    private static String getLastCause(String responseBody) {

        final String causedBy = "Caused by: ";

        int lastReasonIndex = responseBody.lastIndexOf(causedBy);

        if (lastReasonIndex != -1) {
            responseBody = responseBody.substring(lastReasonIndex + causedBy.length());
        }
        return responseBody;
    }

    static String getTokenFromEnvironment(String envVariable) {

        String stripeToken = System.getenv(envVariable);

        if (stripeToken == null) {
            throw new RuntimeException("Enviroment variable: " + envVariable + " is not set.");
        }

        return stripeToken;
    }


}
