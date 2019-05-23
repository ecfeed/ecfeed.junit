package backend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class StripeTest {

    public static final String ENV_EC_STRIPE_TOKEN = "EC_STRIPE_TOKEN";

    @Test
    public void shouldRequestUsersDataByMail() {

        final String email = "k.skorupski@testify.no";
        final String url = createCustomersUrl("?email=" + email);

        try {
            final String token = UnirestHelper.getTokenFromEnvironment(ENV_EC_STRIPE_TOKEN);

            HttpResponse<JsonNode> response =
                    UnirestHelper.sendRequestWithJsonResponse(url, token);

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

    private String createCustomersUrl(String specificUrlPart) {

        return "https://api.stripe.com/v1/customers" + specificUrlPart;
    }

}
