package com.ecfeed.junit.performance.runner;

import com.ecfeed.core.junit5.annotation.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

@Disabled
@EcFeed
@DisplayName("External - JUnit5")
@EcFeedTarget("https://prod-gen.ecfeed.com/testCaseService")
@EcFeedKeyStore("src/test/resources/security")
public class ExternalJUnit5Test {

    @EcFeedTest
    @DisplayName("Static")
    @EcFeedModel("7477-5282-6642-0375-3480")
    @EcFeedInput("'dataSource':'static','testSuites':['default'],'method':'test.Class1.randomized(int arg1, String arg2)'")
    void generatorStaticTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

    @EcFeedTest
    @DisplayName("N-Wise")
    @EcFeedModel(TestDataSupplier.testModelUuid)
    @EcFeedInput("'dataSource':'genNWise','coverage':'100','n':'2','method':'test.Class1.randomized(int arg1, String arg2)'")
    void generatorNWiseTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

    @EcFeedTest
    @DisplayName("Cartesian")
    @EcFeedModel(TestDataSupplier.testModelUuid)
    @EcFeedInput("'dataSource':'genCartesian','method':'test.Class1.randomized(int arg1, String arg2)'")
    void generatorCartesianTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

    @EcFeedTest
    @DisplayName("Random")
    @EcFeedModel(TestDataSupplier.testModelUuid)
    @EcFeedInput("'dataSource':'genRandom', 'length':'10', 'duplicates':'true','method':'test.Class1.randomized(int arg1, String arg2)'")
    void generatorRandomTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

    @EcFeedTest
    @DisplayName("Adaptive random")
    @EcFeedModel(TestDataSupplier.testModelUuid)
    @EcFeedInput("'dataSource':'genAdaptiveRandom', 'depth':'1', 'length':'10', 'candidates':'1', 'duplicates':'true','method':'test.Class1.randomized(int arg1, String arg2)'")
    void generatorRandomAdaptiveTest(int arg1, String arg2) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "]");
    }

}
