package com.example.test;

import com.ecfeed.junit.annotation.*;

@EcFeedModel("auto")
public class LoanDecisionDynamicTest {

	@EcFeedTest
	public void generateCustomerData(String familyName, String firstName, Gender gender, int age, String documentSerialNumber, DocumentType documentType) {
		System.out.println("generateCustomerData(" + familyName + ", " + firstName + ", " + gender + ", " + age + ", " + documentSerialNumber + ", " + documentType + ")");
	}

	@EcFeedTest
	public void processLoanApplication(int age, String firstName, String familyName, Gender gender, EmploymentStatus employmentStatus, long lastYearIncome, LoanDecision loanDecision) {
		System.out.println("processLoanApplication(" + age + ", " + firstName + ", " + familyName + ", " + gender + ", " + employmentStatus + ", " + lastYearIncome + ", " + loanDecision + ")");
	}
}