package backend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BackendTest {

    @Test
    public void shouldGetUsersStripeHistory() {

        RestServiceTestRunner.runRestServiceTest(new GetUsersStripeHistoryTest());
    }

    class GetUsersStripeHistoryTest implements IRestServiceTest {

        @Override
        public void runServiceTest() {

            String token = RestServiceHelper.getTokenFromEnvironment("EC_AUTH0_TOKEN");

            String url = "http://localhost:8085/api/stripe/history";

            HttpResponse<JsonNode> response =
                    RestServiceHelper.sendRequestWithJsonResponse(url, token);

            assertEquals(RestServiceHelper.OK_STATUS, response.getStatus());
        }
    }

    @Test
    public void shouldNotGetUsersStripeHistoryWhenInvalidToken() {

        RestServiceTestRunner.runRestServiceTest(new GetUsersStripeHistoryTest2());
    }

    class GetUsersStripeHistoryTest2 implements IRestServiceTest {

        @Override
        public void runServiceTest() {

            String token = "rubbish";
            String url = "http://localhost:8085/api/stripe/history";

            try {
                HttpResponse<JsonNode> response =
                        RestServiceHelper.sendRequestWithJsonResponse(url, token);
            } catch (Exception e) {
                return;
            }
        }
    }

}
