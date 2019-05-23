package backend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.mashape.unirest.http.Unirest;

import static org.junit.jupiter.api.Assertions.fail;

public class Test1 {

    public static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    public static final String HEADER_NAME_CACHE_CONTROL = "cache-control";
    public static final String NO_CACHE = "no-cache";

    @Test
    public void test1() {

        final String email = "abc@com";
        final String url = createCustomersUrl("?email=" + email);

        try {
            HttpResponse<JsonNode> response = sendCustomerRequest(url);

            System.out.println("response.getBody: ");
            System.out.println(response.getBody().toString());

            final JSONObject bodyJsonObject = response.getBody().getObject();

            JSONArray responseDataArray = bodyJsonObject.getJSONArray("data");
            System.out.println("Response data array: " + responseDataArray.toString());

            Object hasMore = bodyJsonObject.get("has_more");
            System.out.println("Has more: " + hasMore.toString());

            Object urlFromResponse = bodyJsonObject.get("url");
            System.out.println("Url from response: " + urlFromResponse.toString());


        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    private HttpResponse<JsonNode> sendCustomerRequest(String url) throws UnirestException {

        HttpResponse<JsonNode> response = Unirest.get(url)
                .header(HEADER_NAME_AUTHORIZATION, createAuthorizationValue())
                .header(HEADER_NAME_CACHE_CONTROL, NO_CACHE)
                .asJson();

        verifyResponseStatusOk(response.getStatus());

        return response;
    }

    private void verifyResponseStatusOk(int status) {

        if (status != 200) {
            throw new RuntimeException("Stripe request returned code: " + status);
        }
    }

    private String createCustomersUrl(String specificUrlPart) {

        final String url = "https://api.stripe.com/v1/customers" + specificUrlPart;

        return url;
    }

    private String createAuthorizationValue() {

        String stripeToken = System.getenv("EC_STRIPE_TOKEN");

        if (stripeToken == null) {
            throw new RuntimeException("Enviroment varialbe containing stripe token is not set.");
        }

        return "Bearer " + stripeToken;
    }

}
