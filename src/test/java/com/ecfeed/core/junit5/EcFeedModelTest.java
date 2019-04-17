package com.ecfeed.core.junit5;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import com.ecfeed.junit.annotation.EcFeed;
import com.ecfeed.junit.annotation.EcFeedInput;
import com.ecfeed.junit.annotation.EcFeedModel;
import com.ecfeed.junit.annotation.EcFeedTest;
import org.openjdk.jmh.annotations.Benchmark;

@EcFeed("auto")
public class EcFeedModelTest {

	public static enum EnumData {D1, D2, D3, D4, D5}

	@EcFeedTest
	@EcFeedModel("src/test/resources/big.ect")
	@DisplayName("Custom test")
	@EcFeedInput(""
			+ "'dataSource':'genNWise',"
			+ "'method':'com.ecfeed.core.junit5.EcFeedModelTest.ecFeedModelTest',"
			+ "'coverage':'100',"
			+ "'N':'2'"
	)
	void ecFeedModelTest(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, int arg9) {
	}
	
	@EcFeedTest
	@EcFeedModel("src/test/resources/test.ect")
	@EcFeedInput(""
			+ "'dataSource':'static',"
			+ "'testSuites':['t1'],"
			+ "'method':'com.ecfeed.core.junit5.EcFeedModelTest.ecFeedModelTest'"
	)
	void ecFeedModelSuiteTest(
			int op0, byte op1, short op2, long op3, float op4, double op5, String op6, char op7, boolean op8) {
	}
	
	@Nested
	@DisplayName("Local")
	class LocalTest {

		@EcFeedTest
		@DisplayName("N-Wise")
		@EcFeedInput("'dataSource':'genNWise', 'coverage':'100', 'N':'3'")
		void serviceNWiseTest(EnumData op0, String op1, byte op2, char op3, short op4, int op5, long op6, float op7, double op8, boolean op9) {
			System.out.println("op0 = [" + op0 + "], op1 = [" + op1 + "], op2 = [" + op2 + "], op3 = [" + op3 + "], op4 = [" + op4 + "], op5 = [" + op5 + "], op6 = [" + op6 + "], op7 = [" + op7 + "], op8 = [" + op8 + "], op9 = [" + op9 + "]");
		}
		
		@EcFeedTest
		@DisplayName("Cartesian")
		@EcFeedInput("'dataSource':'genCartesian'")
		void serviceCartesianTest(EnumData op0, String op1, byte op2) {}
		
		@EcFeedTest
		@DisplayName("Random")
		@EcFeedInput("'dataSource':'genRandom', 'length':'10', 'duplicates':'false'")
		void serviceRandomTest(EnumData op0, String op1, byte op2, char op3, short op4, int op5, long op6, float op7, double op8, boolean op9) {}
		
		@EcFeedTest
		@DisplayName("Random adaptive")
		@EcFeedInput("'dataSource':'genAdaptiveRandom', 'depth':'10', 'length':'10', 'candidates':'10', 'duplicates':'false'")
		void serviceRandomAdaptiveTest(EnumData op0, String op1, byte op2, char op3, short op4, int op5, long op6, float op7, double op8, boolean op9) {}
	}

	
}
