package backend;

import org.junit.jupiter.api.Test;


public class BackendTest {

    // REMARK: WITHOUT AUTH0 TESTS !!!

    @Test
    public void shouldPrepareManager() {

        BackendTestHelper.launchTest(new PrepareManagerTest());
    }

    class PrepareManagerTest implements IRestServiceTest {

        @Override
        public void runTest() {

            String backendManagersToken = BackendTestHelper.prepareTestManager();

            String url = BackendTestHelper.createBackendUrl("/users/test/managerAccess");

            BackendTestHelper.sendGetRequestWithOkResponse(backendManagersToken, url);
        }

    }

    @Test
    public void shouldPrepareUserTest() {

        BackendTestHelper.launchTest(new PrepareUserTest());
    }

    class PrepareUserTest implements IRestServiceTest {

        @Override
        public void runTest() {

            String token = BackendTestHelper.prepareTestUser(1);

            String url = BackendTestHelper.createBackendUrl("/users/test/regularUserAccess");

            BackendTestHelper.sendGetRequestWithOkResponse(token, url);
        }
    }

    //    @Test
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
//            String mailToFind = "non-existent-user-45672168652@none.com";
//            String url = "http://localhost:8085/api/users/existence/" + mailToFind;
//
////            String managersToken = BackendTestHelper.getManagersTestToken();
//            String managersToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qbEVOVVpCTkRReE1rRTBRME0yT1RRMk4wVTFRVE5EUmpjMU5EWXlSVEJCTlRVMFFqZzVOZyJ9.eyJodHRwOi8vY29tLmVjZmVlZC90b2tlblV1aWQiOiI2MWE3YjU5OS01Yzc2LTRiMTktYjYxMy03YjEwM2JlOWI3YmEiLCJpc3MiOiJodHRwczovL2VjZmVlZC1kZXZlbG9wbWVudC5ldS5hdXRoMC5jb20vIiwic3ViIjoiZ29vZ2xlLW9hdXRoMnwxMDIwNDI0NDMzNjI2MzA4MzA1ODciLCJhdWQiOlsiaHR0cDovL2VjZmVlZC5jb20vYXBpIiwiaHR0cHM6Ly9lY2ZlZWQtZGV2ZWxvcG1lbnQuZXUuYXV0aDAuY29tL3VzZXJpbmZvIl0sImlhdCI6MTU1OTEwODM3MywiZXhwIjoxNTU5MTE1NTczLCJhenAiOiJWc0s4Q25zb2pMOFNCNzYzVWNWdEJLZHFtcG5pRzlSSSIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwgbWFuYWdlciIsInBlcm1pc3Npb25zIjpbIm1hbmFnZXIiXX0.A6J2jdTLqKFJnpLCTIvZeJTi-HqEgbnYLkboWQrBGDV02iGZRq6ufxbqH6He56PyCXSx8wm7UfvY1epvupKiaNCYAbcwMUcuyniEAB9aD2vuHXYjkbIjjd9GzimQY9QwZOFoOXA5zArd47HE9S8syNHlRcg5thgoVbv9KFHCnQGkrv-mALhkc-p2-voEnKg5lcRuigOm3ZbLo63qBrSiAMGEtP0c8RQwO3RuiGZsg-uUV-ZeWl5NF3K1hefESKx5m2_r0EByY0G06mSqlgZPx9L_-pRymCKW4gS8wdRFBgNYpoezhouJs9JE-NbO-EEyTBBgtXlcADRPnlIqd6cOTA";
//
//            HttpResponse<JsonNode> response =
//                    RestServiceHelper.sendGetRequest(url, managersToken);
//
//            JsonNode jsonNode = response.getBody();
//            JSONObject jsonObject = jsonNode.getObject();
//            String message = jsonObject.getString("data");
//            assertEquals("N", message);
//        }

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
