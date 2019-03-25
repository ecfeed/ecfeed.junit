package com.ecfeed.junit.performance;

import com.ecfeed.core.utils.TestCasesRequest;
import com.ecfeed.web.service.Application;
import external.runner.PerformanceServiceTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@DisplayName("Performance")
public class PerformanceTest {

    private static TestCasesRequest getTestCaseRequest() {
        TestCasesRequest request = new TestCasesRequest();

        request.setModelName("TestUuid1");
        request.setMethod("test.Class1.testMethod(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7)");
        request.setUserData("{'dataSource':'genCartesian'}");
        return request;
    }

    @Test
    @DisplayName("Performance test")
    public void performanceTest() {
        TestCasesRequest request = getTestCaseRequest();

        List<Integer> numberOfTests = new ArrayList<>();

        new PerformanceServiceTest(request, numberOfTests).run();

        System.out.println(numberOfTests);

        assertThat(numberOfTests.get(0))
                .as("The number of tests should be greater than 2,000,000.")
                .isGreaterThan(2000000);
    }

}
