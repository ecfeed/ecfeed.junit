package backend;

import com.ecfeed.core.utils.ExceptionHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class BackendTestHelper {

    public static String getAuth0TokenFromUsersCredentials(
            String userMail, String userPassword) {

        String token;

        try {
            return getTokenIntr(userMail, userPassword);

        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Failed to get token from Auth0", e);
            return null;
        }
    }

    private static String getTokenIntr(String userMail, String userPassword) throws Exception {

        HttpResponse<JsonNode> response = getResponseFromAuth0(userMail, userPassword);

        JsonNode jsonNode = response.getBody();
        //JSONObject jsonObject = jsonNode.getObject().getJSONObject();

        System.out.println(jsonNode);

        return "TODO";
    }

    // TODO
    private static HttpResponse<JsonNode> getResponseFromAuth0(
            String userMail, String userPassword) throws UnirestException {

        final String body = BackendTestHelper.createBody(userMail, userPassword);

        final String url = "https://YOUR_DOMAIN/oauth/token"; // TODO

        HttpResponse<JsonNode> response = Unirest.post(url)
                .header("content-type", "application/x-www-form-urlencoded")
                .body(body)
                .asJson();

        return response;
    }

    private static String createBody(String userMail, String userPassword) {

        String formattedUserMail = formatMailForSending(userMail);

        final String audience = "YOUR_API_IDENTIFIER"; // TODO
        final String scope = "read%3Asample"; // TODO
        final String accountWithClientId = "account.clientId";
        final String clientSecret = "YOUR_CLIENT_SECRET"; // TODO

        return "grant_type=password" +
                "&username=" + formattedUserMail +
                "&password=" + userPassword +
                "&audience=" + audience +
                "&scope=" + scope +
                "&client_id=%24%7B" + accountWithClientId + "%7D" +
                "&client_secret=" + clientSecret;
    }

    private final static String formatMailForSending(String mail) {

        final String AT_SYMBOL = "%40"; // @ // TODO - move

        return mail.replace("@", AT_SYMBOL);
    }

}
