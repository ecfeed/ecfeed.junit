package backend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class UnirestHelper {

    public static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    public static final String HEADER_NAME_CACHE_CONTROL = "cache-control";
    public static final String NO_CACHE = "no-cache";

    public static HttpResponse<JsonNode> sendRequestWithJsonResponse(String url, String token) throws UnirestException {

        String authorization = createAuthorizationValue(token);

        HttpResponse<JsonNode> response = Unirest.get(url)
                .header(HEADER_NAME_AUTHORIZATION, authorization)
                .header(HEADER_NAME_CACHE_CONTROL, NO_CACHE)
                .asJson();

        verifyResponseStatusOk(response.getStatus());

        return response;
    }

    private static String createAuthorizationValue(String token) {

        if (token == null) {
            throw new RuntimeException("Authorization token is not set.");
        }

        return "Bearer " + token;
    }


    private static void verifyResponseStatusOk(int status) {

        if (status != 200) {
            throw new RuntimeException("Stripe request returned code: " + status);
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
