package com.ecfeed.junit.performance;

import com.ecfeed.junit.annotation.EcFeedInput;
import com.ecfeed.junit.annotation.EcFeedModel;
import com.ecfeed.junit.annotation.EcFeedService;
import com.ecfeed.junit.annotation.EcFeedTest;
import org.junit.platform.engine.discovery.ClassNameFilter;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

@EcFeedService("https://development-gen.ecfeed.com")
//@EcFeedModel("/home/krzysztof/Downloads/Dnummer.ect")
//@EcFeedModel("5644-7697-7869-7887-2905")
@EcFeedModel("0017-1368-6313-5486-8112")

public class Hager {

     static SummaryGeneratingListener listener;
    static LauncherDiscoveryRequest request;
    static Launcher launcher;

    @EcFeedTest
    @EcFeedInput("'dataSource':'genCartesian','method':'com.example.test.TestClass1.testMethod1'")
    void test(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, int arg9, int arg10) {
        System.out.println("arg1 = [" + arg1 + "], arg2 = [" + arg2 + "], arg3 = [" + arg3 + "], arg4 = [" + arg4 + "], arg5 = [" + arg5 + "], arg6 = [" + arg6 + "], arg7 = [" + arg7 + "], arg8 = [" + arg8 + "], arg9 = [" + arg9 + "], arg10 = [" + arg10 + "]");
    }

    @EcFeedTest
    @EcFeedInput("'dataSource':'genNWise', 'coverage':'100', 'n':'2','method':'no.nav.drek.core.rekvisisjon.service.validation.RekvirerDnummer.testMethod'")
	public void testMethod(String stedsNavn, String CO_ADRESSENAVN, int x) {
		// TODO Auto-generated method stub
		System.out.println("testMethod(" + stedsNavn + ", " + CO_ADRESSENAVN + ", " + x +")");
	}


    public static void main(String[] args) {
         listener = new SummaryGeneratingListener();

       request = LauncherDiscoveryRequestBuilder.request()
            .selectors(DiscoverySelectors.selectPackage("com.ecfeed.junit.performance"))
            .filters(ClassNameFilter.includeClassNamePatterns(".*Hager"))
            .build();

        launcher = LauncherFactory.create();
//        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

//    TestExecutionSummary summary = runner.listener.getSummary();
//    summary.printTo(new PrintWriter(System.out));
    }
}
