package internal.runner;

import com.ecfeed.core.junit5.annotation.*;
import com.ecfeed.web.service.Application;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@EcFeed
@DisplayName("User Input - JUnit5")
@EcFeedTarget("https://localhost:8091/testCaseService")
@EcFeedKeyStore("src/test/resources/security")
public class UserInputJUnit5Test {

    private static ConfigurableApplicationContext fConfigurableApplicationContext = null;

    @BeforeAll
    public static void beforeAll() {
        fConfigurableApplicationContext = SpringApplication.run(Application.class);
    }

    @AfterAll
    public static void afterAll() {
        fConfigurableApplicationContext.close();
    }

    @EcFeedTest
    @DisplayName("Static")
    @EcFeedModel("TestUuid1")
    @EcFeedInput("'dataSource':'static','testSuites':['default'],'method':'test.Class1.randomized'")
    void generatorStaticTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

    @EcFeedTest
    @DisplayName("N-Wise")
    @EcFeedModel("TestUuid1")
    @EcFeedInput("'dataSource':'genNWise','coverage':'100','n':'n','method':'test.Class1.randomized'")
    void generatorNWiseTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

    @EcFeedTest
    @DisplayName("Cartesian")
    @EcFeedModel("TestUuid1")
    @EcFeedInput("'dataSource':'genCartesian','method':'test.Class1.randomized'")
    void generatorCartesianTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

    @EcFeedTest
    @DisplayName("Random")
    @EcFeedModel("TestUuid1")
    @EcFeedInput("'dataSource':'genRandom', 'length':'10', 'duplicates':'true','method':'test.Class1.randomized'")
    void generatorRandomTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

    @EcFeedTest
    @DisplayName("Adaptive random")
    @EcFeedModel("TestUuid1")
    @EcFeedInput("'dataSource':'genAdaptiveRandom', 'depth':'1', 'length':'10', 'candidates':'1', 'duplicates':'true','method':'test.Class1.randomized'")
    void generatorRandomAdaptiveTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

}
