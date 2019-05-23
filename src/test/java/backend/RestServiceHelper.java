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

    public static HttpResponse<JsonNode> sendRequestWithJsonResponse(String url, String token) {

        String authorization = createAuthorizationValue(token);
        HttpResponse<JsonNode> response = null;

        try {
            response = getJsonNodeHttpResponse(url, authorization);
        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Getting http response failed.", e);
        }

        verifyResponseStatusOk(response.getStatus());

        return response;
    }

    private static HttpResponse<JsonNode> getJsonNodeHttpResponse(
            String url, String authorization) throws Exception {

        return Unirest.get(url)
                    .header(HEADER_NAME_AUTHORIZATION, authorization)
                    .header(HEADER_NAME_CACHE_CONTROL, NO_CACHE)
                    .asJson();
    }

    private static String createAuthorizationValue(String token) {

        if (token == null) {
            ExceptionHelper.reportRuntimeException("Authorization token is not set.");
        }

        return "Bearer " + token;
    }


    private static void verifyResponseStatusOk(int status) {

        if (status != 200) {
            throw new RuntimeException("Request returned code: " + status);
        }
    }

    static String getTokenFromEnvironment(String envVariable) {

        String stripeToken = System.getenv(envVariable);

        if (stripeToken == null) {
            throw new RuntimeException("Enviroment variable: " + envVariable + " is not set.");
        }

        return stripeToken;
    }
}
