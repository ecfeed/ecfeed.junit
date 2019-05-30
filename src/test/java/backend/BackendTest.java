package backend;

import org.junit.jupiter.api.Test;


public class BackendTest {

    // REMARK: WITHOUT AUTH0 TESTS !!!

    @Test
    public void shouldPrepareManager() {

        BackendTestHelper.launchTest(
                new PrepareManagerTest(), BackendTestHelper.TestUsers.MANAGER_ONLY);
    }

    class PrepareManagerTest implements IRestServiceTest {

        @Override
        public void runTest(String[] tokens) {

            String url = BackendTestHelper.createBackendUrl("/users/test/managerAccess");

            BackendTestHelper.sendGetRequestWithOkResponse(
                    BackendTestHelper.getManagersToken(tokens), url);
        }

    }

    @Test
    public void shouldPrepareUserTest() {

        BackendTestHelper.launchTest(
                new PrepareUserTest(), BackendTestHelper.TestUsers.USER_ONLY);
    }

    class PrepareUserTest implements IRestServiceTest {

        @Override
        public void runTest(String[] tokens) {

            String url = BackendTestHelper.createBackendUrl("/users/test/regularUserAccess");

            BackendTestHelper.sendGetRequestWithOkResponse(
                    BackendTestHelper.getUsersToken(1, tokens), url);
        }
    }

//        @Test
//    public void shouldCheckIfUserExists() {
//
//        BackendTestHelper.launchTest(new CheckIfUserExistsTest());
//    }
//
//    class CheckIfUserExistsTest implements IRestServiceTest {
//
//        @Override
//        public void runTest() {
//
//            String backendManagersToken = BackendTestHelper.prepareTestManager();
//
//            String mailToFind = "non-existent-user-45672168652@none.com";
//            String url = "http://localhost:8085/api/users/existence/" + mailToFind;
//
//            HttpResponse<JsonNode> response =
//                    RestServiceHelper.sendGetRequest(url, managersToken);
//
//            JsonNode jsonNode = response.getBody();
//            JSONObject jsonObject = jsonNode.getObject();
//            String message = jsonObject.getString("data");
//            assertEquals("N", message);
//        }
//
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
//    }

//    @Test
//    public void shouldGetUsersStripeHistory() {
//
//        BackendTestHelper.launchTest(new GetUsersStripeHistoryTest());
//    }
//
//    class GetUsersStripeHistoryTest implements IRestServiceTest {
//
//        @Override
//        public void runTest() {
//
//            String url = "http://localhost:8085/api/stripe/history";
//
//            HttpResponse<JsonNode> response = getUserResponse(url);
//
//            assertEquals(RestServiceHelper.OK_STATUS, response.getStatus());
//        }
//    }

//    @Test
//    public void shouldNotGetUsersStripeHistoryWhenInvalidToken() {
//
//        BackendTestHelper.launchTest(new GetUsersStripeHistoryTest2());
//    }
//
//    class GetUsersStripeHistoryTest2 implements IRestServiceTest {
//
//        @Override
//        public void runTest() {
//
//            String token = "rubbish";
//            String url = "http://localhost:8085/api/stripe/history";
//
//            try {
//                RestServiceHelper.sendRequestWithJsonResponse(url, token);
//            } catch (Exception e) {
//                return;
//            }
//        }
//    }

//    private HttpResponse<JsonNode> sendManagersDeleteRequest(String url) {
//
//        return RestServiceHelper.sendRequestWithJsonResponse(url, token);
//    }

}
