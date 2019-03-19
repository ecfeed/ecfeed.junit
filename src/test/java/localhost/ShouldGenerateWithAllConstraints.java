package localhost;

import com.ecfeed.junit.annotation.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EcFeed
@EcFeedService("https://localhost:8090/testCaseService")
@EcFeedKeyStore("src/test/resources/security")
@EcFeedModel("TestUuid1")
public class ShouldGenerateWithAllConstraints {

    private static int fActualCallCounter;

    @BeforeAll
    public static void executeBeforeTest() {
        fActualCallCounter = 0;
    }

    @AfterAll
    public static void executeAfterTest() {
        assertEquals(1, fActualCallCounter);
    }

    @EcFeedTest
    @EcFeedInput("'dataSource':'genCartesian','constraints':'ALL', 'method':'test.Class1.testMethod'")
    public void test(int arg1, int arg2) {
        fActualCallCounter++;
    }

}
