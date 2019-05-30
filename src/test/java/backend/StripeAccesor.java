package backend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;

public class StripeAccesor {

    public static final String ENV_EC_STRIPE_TOKEN = "EC_STRIPE_TOKEN";

    static JSONArray getUsers(String email) {

        final String url = createCustomersUrl("?email=" + email);

        final String token = RestServiceHelper.getTokenFromEnvironment(ENV_EC_STRIPE_TOKEN);

        HttpResponse<JsonNode> response =
                RestServiceHelper.sendGetRequest(url, token);

        final JSONObject bodyObject = response.getBody().getObject();

        JSONArray dataArray = bodyObject.getJSONArray("data");

        return dataArray;

        //            JSONArray responseDataArray = bodyJsonObject.getJSONArray("data");
        //            Object hasMore = bodyJsonObject.get("has_more");
        //            Object urlFromResponse = bodyJsonObject.get("url");
    }

    private static String createCustomersUrl(String specificUrlPart) {

        return "https://api.stripe.com/v1/customers" + specificUrlPart;
    }
}
