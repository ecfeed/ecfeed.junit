package backend;

import com.ecfeed.core.utils.ExceptionHelper;

import static org.junit.jupiter.api.Assertions.fail;

public class RestServiceTestRunner {

    public static void runRestServiceTest(IRestServiceTest backendTest) {

        try {
            backendTest.runServiceTest();
        } catch (Exception e) {
            String message = ExceptionHelper.createErrorMessage(e, true, false);
            throw new RuntimeException(message);
        }
    }
}
