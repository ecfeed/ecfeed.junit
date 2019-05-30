package backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
                    url, BackendTestHelper.getManagersToken(tokens));
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
                    url, BackendTestHelper.getUsersToken(1, tokens));
        }
    }

    @Test
    public void shouldCheckIfUserExists() {

        BackendTestHelper.launchTest(
                new CheckIfUserExistsTest(), BackendTestHelper.TestUsers.MANAGER_ONLY);
    }

    class CheckIfUserExistsTest implements IRestServiceTest {

        @Override
        public void runTest(String[] tokens) {

            String mailToFind = "non-existent-user-45672168652@none.com";
            String url = "http://localhost:8085/api/users/existence/" + mailToFind;

            String data =
                    BackendTestHelper.sendGetRequestReturningData(
                            url, BackendTestHelper.getManagersToken(tokens));

            assertEquals("N", data);
        }

//        private void deleteUser() {
//
//            String url = "http://localhost:8085/api/users";
//
//            String token = getUsersToken();
//            String authorization = RestServiceHelper.createAuthorizationValue(token);
//            RestServiceHelper.delete(url, authorization);
//        }

    }

}
