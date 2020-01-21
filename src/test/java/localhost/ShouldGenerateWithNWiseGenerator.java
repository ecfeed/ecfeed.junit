package localhost;

import com.ecfeed.junit.annotation.*;
import com.ecfeed.junit.utils.TestDataSupplier;
import localhost.utils.ExecutionConditionLocalHostAvailable;
import localhost.utils.TestHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EcFeed
@EcFeedService(TestHelper.GEN_SERVICE_URL_ON_LOCALHOST)
@EcFeedKeyStore("src/test/resources/security")
@EcFeedModel(TestDataSupplier.testModelUuid)
@ExtendWith(ExecutionConditionLocalHostAvailable.class)
public class ShouldGenerateWithNWiseGenerator {

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
    @EcFeedInput("'dataSource':'genNWise','properties':{'coverage':'100','n':'2'}, 'method':'test.Class1.randomized'")
    public void test(int arg1, String arg2) {
        fActualCallCounter++;
    }

}
