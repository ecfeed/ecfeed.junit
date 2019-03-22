package com.ecfeed.core.genservice.protocol.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultTestSchema {

	private String id;
	private ChoiceSchema[] testCase;

	public ResultTestSchema() {};
	
	public ChoiceSchema[] getTestCase() {
		return testCase;
	}

	public void setTestCase(ChoiceSchema[] testCase) {
		this.testCase = testCase;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		result.append("Selected choices | Test id: " + id);

		for (ChoiceSchema argument : testCase) {
			result.append(System.lineSeparator());
			result.append(argument.toString());
		}
		
		return result.toString(); 
	}
	
}
