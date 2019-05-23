package backend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class StripeTest {

    public static final String ENV_EC_STRIPE_TOKEN = "EC_STRIPE_TOKEN";

    private String createCustomersUrl(String specificUrlPart) {

        return "https://api.stripe.com/v1/customers" + specificUrlPart;
    }

    @Test
    public void shouldRequestUsersDataByMail() {
        RestServiceTestRunner.runRestServiceTest(new RequestUsersDataByMailTest());
    }

    class RequestUsersDataByMailTest implements IRestServiceTest {

        @Override
        public void runServiceTest() {

            final String email = "k.skorupski@testify.no";
            final String url = createCustomersUrl("?email=" + email);

            final String token = RestServiceHelper.getTokenFromEnvironment(ENV_EC_STRIPE_TOKEN);

            HttpResponse<JsonNode> response =
                    RestServiceHelper.sendRequestWithJsonResponse(url, token);

            //            final JSONObject bodyJsonObject = response.getBody().getObject();
            //            JSONArray responseDataArray = bodyJsonObject.getJSONArray("data");
            //            Object hasMore = bodyJsonObject.get("has_more");
            //            Object urlFromResponse = bodyJsonObject.get("url");
        }
    }

}
