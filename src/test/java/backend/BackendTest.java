package backend;

import org.junit.jupiter.api.Test;

public class BackendTest {

    @Test
    public void test1() {

        String auth0Token = System.getenv("EC_AUTH0_TOKEN");
        System.out.println("Auth0 token: <<<" + auth0Token + ">>>");
    }
}
