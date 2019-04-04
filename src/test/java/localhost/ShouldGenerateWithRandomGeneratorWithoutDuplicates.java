package localhost;

import com.ecfeed.junit.annotation.*;
import localhost.utils.ExecutionConditionLocalHostAvailable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EcFeed
@EcFeedService("https://localhost:8090")
@EcFeedKeyStore("src/test/resources/security")
@EcFeedModel("TestUuid1")
@ExtendWith(ExecutionConditionLocalHostAvailable.class)
public class ShouldGenerateWithRandomGeneratorWithoutDuplicates {

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
    @EcFeedInput("'dataSource':'genRandom', 'length':'10', 'duplicates':'false', 'method':'test.Class1.testMethod'")
    public void test(String arg1, String arg2) {
        fActualCallCounter++;
    }
}
