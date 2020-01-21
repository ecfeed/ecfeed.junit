package com.ecfeed.junit.performance;

import com.ecfeed.junit.annotation.*;
import com.ecfeed.junit.utils.TestDataSupplier;
import localhost.utils.ExecutionConditionLocalHostAvailable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

@EcFeed(TestDataSupplier.testModelUuid)
@DisplayName("Performance - JUnit5")
@Disabled("This test is not compatible with IDE")
@EcFeedService("https://localhost:8090/testCaseService")
@EcFeedKeyStore("src/test/resources/security")
@ExtendWith(ExecutionConditionLocalHostAvailable.class)
public class PerformanceJUnit5Test {

    private static int fLocalCount = 0;
    private static int fCount = 0;

    @EcFeedTest
    @DisplayName("Performance test - JUnit5")
    @EcFeedInput("'dataSource':'genCartesian','method':'test.Class1.testMethod(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7)'")
    public void performanceTest(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {

        if (fLocalCount == 10000) {
            System.out.println("checkpoint = [" + fCount + "]");
            fLocalCount = 0;
        }

        fLocalCount++;
        fCount++;
    }

}
