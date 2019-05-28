package backend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class BackendTest {

    // REMARK: WITHOUT AUTH0 TESTS !!!

    @Test
    public void shouldCheckIfUserExists() {

        RestServiceTestRunner.launchTest(new CheckIfUserExistsTest());
    }

    class CheckIfUserExistsTest implements IRestServiceTest {

        @Override
        public void runTest() {

            // TODO - user should be logged first (test is incomplete)

            String mailToFind = "non-existent-user-45672168652@none.com";
            String url = "http://localhost:8085/api/users/existence/" + mailToFind;

            HttpResponse<JsonNode> response = sendGetManagersRequest(url);

            JsonNode jsonNode = response.getBody();
            JSONObject jsonObject = jsonNode.getObject();
            String message = jsonObject.getString("message");
            assertEquals("N", message);
        }

//        private void deleteUser() {
//
//            String url = "http://localhost:8085/api/users";
//
//            String token = getUsersToken();
//            String authorization = RestServiceHelper.createAuthorizationValue(token);
//            RestServiceHelper.delete(url, authorization);
//        }
//
//        private boolean userExistInAuth0(String mailToFind) {
//
//            String url = "http://localhost:8085/api/users/auth0";
//
//            // TODO - get only selected users
//            HttpResponse<JsonNode> response = getManagerResponse(url);
//
//            final JSONObject bodyObject = response.getBody().getObject();
//            JSONArray usersJsonArray = bodyObject.getJSONArray("users");
//
//            for (int index = 1; index < usersJsonArray.length(); index++) {
//                JSONObject userObject = (JSONObject) usersJsonArray.get(index);
//
//                String email = userObject.getString("email");
//
//                if (email.equals(mailToFind)) {
//                    return true;
//                }
//            }
//
//            return false;
//        }
//
//        private boolean userExistInDb(String mailToFind) {
//
//            String url = "http://localhost:8085/api/users/db";
//
//            // TODO - get only selected users
//            HttpResponse<JsonNode> response = getManagerResponse(url);
//
//            final JSONObject bodyObject = response.getBody().getObject();
//
//            System.out.println("Body object:");
//            System.out.println(bodyObject);
//
////            JSONArray usersJsonArray = bodyObject.getJSONArray("users");
////
////            for (int index = 1; index < usersJsonArray.length(); index++) {
////                JSONObject userObject = (JSONObject) usersJsonArray.get(index);
////
////                String email = userObject.getString("email");
////
////                if (email.equals(mailToFind)) {
////                    return true;
////                }
////            }
//
//            return false;
//        }
//
//        private String getUsersMail(JSONObject userObject) {
//
//            return (String) userObject.get("email");
//        }
    }

    @Test
    public void shouldGetUsersStripeHistory() {

        RestServiceTestRunner.launchTest(new GetUsersStripeHistoryTest());
    }

    class GetUsersStripeHistoryTest implements IRestServiceTest {

        @Override
        public void runTest() {

            String url = "http://localhost:8085/api/stripe/history";

            HttpResponse<JsonNode> response = getUserResponse(url);

            assertEquals(RestServiceHelper.OK_STATUS, response.getStatus());
        }
    }

    @Test
    public void shouldNotGetUsersStripeHistoryWhenInvalidToken() {

        RestServiceTestRunner.launchTest(new GetUsersStripeHistoryTest2());
    }

    class GetUsersStripeHistoryTest2 implements IRestServiceTest {

        @Override
        public void runTest() {

            String token = "rubbish";
            String url = "http://localhost:8085/api/stripe/history";

            try {
                RestServiceHelper.sendRequestWithJsonResponse(url, token);
            } catch (Exception e) {
                return;
            }
        }
    }

    private HttpResponse<JsonNode> getUserResponse(String url) {

        String token = getUsersToken();

        return RestServiceHelper.sendRequestWithJsonResponse(url, token);
    }

    private HttpResponse<JsonNode> sendGetManagersRequest(String url) {

        String token = getManagersToken();

//        System.out.println("Manager token: ");
//        System.out.println(token);

        return RestServiceHelper.sendRequestWithJsonResponse(url, token);
    }

    private HttpResponse<JsonNode> sendManagersDeleteRequest(String url) {

        String token = getManagersToken();

//        System.out.println("Manager token: ");
//        System.out.println(token);

        return RestServiceHelper.sendRequestWithJsonResponse(url, token);
    }

    public String getManagersToken() {

        //        String token = RestServiceHelper.getTokenFromEnvironment("EC_AUTH0_MANAGER_TOKEN");
        String token = "PASTE HERE";

        return token;
    }

    private String getUsersToken() {

        //        String token = RestServiceHelper.getTokenFromEnvironment("EC_AUTH0_USER_TOKEN");
        String token = "PASTE HERE";

        return token;
    }

}
