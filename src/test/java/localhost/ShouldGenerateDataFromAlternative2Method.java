package localhost;

import com.ecfeed.junit.annotation.*;
import localhost.utils.ServerAvailableCondition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EcFeed
@EcFeedService("https://localhost:8090/testCaseService")
@EcFeedKeyStore("src/test/resources/security")
@EcFeedModel("TestUuid1")
@ExtendWith(ServerAvailableCondition.class)
public class ShouldGenerateDataFromAlternative2Method {

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
    @EcFeedInput("'dataSource':'genCartesian','method':'test.Class1.alternative2'")
    public void test(String arg1, int arg2) {
        fActualCallCounter++;
    }
}
