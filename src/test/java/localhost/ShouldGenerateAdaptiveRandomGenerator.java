package localhost;

import com.ecfeed.junit.annotation.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EcFeed
@EcFeedService("https://localhost:8090/testCaseService")
@EcFeedKeyStore("src/test/resources/security")
@EcFeedModel("TestUuid1")
public class ShouldGenerateAdaptiveRandomGenerator {

    private static int fActualCallCounter;

    @BeforeAll
    public static void executeBeforeTest() {
        fActualCallCounter = 0;
    }

    @AfterAll
    public static void executeAfterTest() {
        assertEquals(4, fActualCallCounter);
    }

    @EcFeedTest
    @EcFeedInput("'dataSource':'genAdaptiveRandom', 'depth':'10', 'length':'10', 'candidates':'10', 'duplicates':'false', 'method':'test.Class1.testMethod'")
    public void test(String arg1, String arg2) {
        fActualCallCounter++;
    }
}
