package com.ecfeed.junit.runner;

import com.ecfeed.junit.annotation.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

@Disabled
@EcFeed
@DisplayName("External - JUnit5")
@EcFeedService("https://localhost:8090/testCaseService")
@EcFeedKeyStore("src/test/resources/security")
public class ExternalJUnit5Test {

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
    @EcFeedInput("'dataSource':'genNWise','coverage':'100','N':'2','method':'test.Class1.randomized(int arg1, String arg2)'")
    void generatorNWiseTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

    @EcFeedTest
    @DisplayName("Cartesian")
    @EcFeedModel("TestUuid1")
    @EcFeedInput("'dataSource':'genCartesian','method':'test.Class1.randomized(int arg1, String arg2)'")
    void generatorCartesianTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

    @EcFeedTest
    @DisplayName("Random")
    @EcFeedModel("TestUuid1")
    @EcFeedInput("'dataSource':'genRandom', 'length':'10', 'duplicates':'true','method':'test.Class1.randomized(int arg1, String arg2)'")
    void generatorRandomTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

    @EcFeedTest
    @DisplayName("Adaptive random")
    @EcFeedModel("TestUuid1")
    @EcFeedInput("'dataSource':'genAdaptiveRandom', 'depth':'1', 'length':'10', 'candidates':'1', 'duplicates':'true','method':'test.Class1.randomized(int arg1, String arg2)'")
    void generatorRandomAdaptiveTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

}
