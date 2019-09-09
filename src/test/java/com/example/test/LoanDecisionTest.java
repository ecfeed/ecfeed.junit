package com.example.test;

import com.ecfeed.junit.annotation.EcFeedInput;
import com.ecfeed.junit.annotation.EcFeedKeyStore;
import com.ecfeed.junit.annotation.EcFeedModel;
import com.ecfeed.junit.annotation.EcFeedTest;

@EcFeedModel("src/test/resources/tutorial.ect")
@EcFeedInput("'dataSource':'genNWise', 'constraints':'ALL', 'coverage':'100', 'n':'2'")
@EcFeedKeyStore("src/test/resources/security")
public class LoanDecisionTest {

	@EcFeedTest
	public void generateCustomerData(String familyName, String firstName, Gender gender, int age, String documentSerialNumber, DocumentType documentType) {
		System.out.println("generateCustomerData(" + familyName + ", " + firstName + ", " + gender + ", " + age + ", " + documentSerialNumber + ", " + documentType + ")");
	}

	// TODO Not enough information to check the constraint
	
	@EcFeedTest
	public void processLoanApplication(int age, String firstName, String familyName, Gender gender, EmploymentStatus employmentStatus, long lastYearIncome, LoanDecision loanDecision) {
		System.out.println("processLoanApplication(" + age + ", " + firstName + ", " + familyName + ", " + gender + ", " + employmentStatus + ", " + lastYearIncome + ", " + loanDecision + ")");
	}
}