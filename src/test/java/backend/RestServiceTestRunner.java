package backend;

import com.ecfeed.core.utils.ExceptionHelper;

import static org.junit.jupiter.api.Assertions.fail;

public class RestServiceTestRunner {

    public static void launchTest(IRestServiceTest backendTest) {

        try {
            backendTest.runTest();
        } catch (Exception e) {
            String message = ExceptionHelper.createErrorMessage(e, true, false);
            throw new RuntimeException(message);
        }
    }
}
