package backend;

import com.ecfeed.core.utils.ExceptionHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BackendTestHelper {

    public enum TestUsers {
        MANAGER_ONLY,
        USER_ONLY,
        MANAGER_AND_USER,
    }

    public static void launchTest(IRestServiceTest restServiceTest, TestUsers testUsers) {

        try {
            String managersToken = prepareManager(testUsers);
            String usersToken = prepareUser(testUsers);
            String tokens[] = {managersToken, usersToken};

            restServiceTest.runTest(tokens);
        } catch (Exception e) {
            String message = ExceptionHelper.createErrorMessage(e, true, false);
            throw new RuntimeException(message);
        }
    }

    private static String prepareManager(TestUsers testUsers) {

        if (testUsers == TestUsers.MANAGER_ONLY || testUsers == TestUsers.MANAGER_AND_USER) {
            return prepareTestManager();
        }

        return null;
    }

    private static String prepareUser(TestUsers testUsers) {

        if (testUsers == TestUsers.USER_ONLY || testUsers == TestUsers.MANAGER_AND_USER) {
            return prepareTestUser(1);
        }

        return null;
    }

    public static String getManagersToken(String[] tokens) {

        return tokens[0];
    }

    public static String getUsersToken(int userNo, String[] tokens) {

        return tokens[userNo];
    }

    public static String prepareTestUser(int userNo) {

        String email = BackendTestHelper.createTestUsersEmail(1);

        String password = BackendTestHelper.createTestUsersPassword();

        String url =
                "http://localhost:8085/api/users/test/prepareUser" +
                        "?email=" + email +
                        "&password=" + password;

        return sendOptionsRequestReturningData(url);
    }

    public static String prepareTestManager() {

        String email = BackendTestHelper.createTestManagersEmail();

        String password = BackendTestHelper.createTestManagersPassword();

        String url =
                "http://localhost:8085/api/users/test/prepareManager" +
                        "?email=" + email +
                        "&password=" + password;

        return sendOptionsRequestReturningData(url);
    }

    public static String sendOptionsRequestReturningData(String url) {

        HttpResponse<JsonNode> response = RestServiceHelper.sendOptionsRequest(url);

        JSONObject jsonObject = getBodyAsJsonObject(response);

        return jsonObject.getString("data");
    }

    public static JSONObject getBodyAsJsonObject(HttpResponse<JsonNode> response) {

        JsonNode jsonNode = response.getBody();
        JSONObject jsonObject = jsonNode.getObject();

        return jsonObject;
    }

    public static void sendGetRequestWithOkResponse(String token, String url) {

        HttpResponse<JsonNode> response = RestServiceHelper.sendGetRequest(url, token);
        assertResponseIsOk(response);
    }

    private static void assertResponseIsOk(HttpResponse<JsonNode> response) {

        JSONObject jsonObject = getBodyAsJsonObject(response);
        String result = jsonObject.getString("message");

        assertEquals("OK", result);
    }

    public static String createTestManagersEmail() {

        String postfix = System.getenv("EC_BACKEND_TEST_USER_POSTFIX");

        return "test_manager_" + postfix + "@testify.no";
    }

    public static String createTestUsersEmail(int userNo) {

        String postfix = System.getenv("EC_BACKEND_TEST_USER_POSTFIX");
        return "test_user_" + postfix + "_" + userNo + "@testify.no";
    }

    public static String createTestManagersPassword() {

        return "$Manager85642!";
    }

    public static String createTestUsersPassword() {

        return "$User76582!";
    }

    public static String createBackendUrl(String urlPostfix) {

        return "http://localhost:8085/api" + urlPostfix;
    }

//    public static String getUsersToken(String email, String password) {
//
//        String url ="http://localhost:8085/api/users/test/getUsersToken" +
//                "?email=" + email +
//                "&password=" + password;
//
//        return sendOptionsRequestReturningData(url);
//    }

//    public static String getAuth0TokenFromUsersCredentials(
//            String userMail, String userPassword) {
//
//        String token;
//
//        try {
//            return getTokenIntr(userMail, userPassword);
//
//        } catch (Exception e) {
//            ExceptionHelper.reportRuntimeException("Failed to get token from Auth0", e);
//            return null;
//        }
//    }

//    private static String getTokenIntr(String userMail, String userPassword) throws Exception {
//
//        HttpResponse<JsonNode> response = getResponseFromAuth0(userMail, userPassword);
//
//        JsonNode jsonNode = response.getBody();
//        //JSONObject jsonObject = jsonNode.getObject().getJSONObject();
//
//        System.out.println(jsonNode);
//
//        return "TODO";
//    }

    // TODO
//    private static HttpResponse<JsonNode> getResponseFromAuth0(
//            String userMail, String userPassword) throws Exception {
//
//        final String body = BackendTestHelper.createBody(userMail, userPassword);
//
//        final String url = "https://YOUR_DOMAIN/oauth/token"; // TODO
//
//        final String contentType = "application/x-www-form-urlencoded";
//
//        HttpResponse<JsonNode> response = Unirest.post(url)
//                .header("content-type", contentType)
//                .body(body)
//                .asJson();
//
//        return response;
//    }

//    private static String createBody(String userMail, String userPassword) {
//
//        String formattedUserMail = formatMailForSending(userMail);
//
//        final String audience = "YOUR_API_IDENTIFIER"; // TODO
//        final String scope = "read%3Asample"; // TODO
//        final String accountWithClientId = "account.clientId";
//        final String clientSecret = "YOUR_CLIENT_SECRET"; // TODO
//
//        return "grant_type=password" +
//                "&username=" + formattedUserMail +
//                "&password=" + userPassword +
//                "&audience=" + audience +
//                "&scope=" + scope +
//                "&client_id=%24%7B" + accountWithClientId + "%7D" +
//                "&client_secret=" + clientSecret;
//    }

//    private final static String formatMailForSending(String mail) {
//
//        final String AT_SYMBOL = "%40"; // @ // TODO - move
//
//        return mail.replace("@", AT_SYMBOL);
//    }

}
