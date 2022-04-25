package com.nsi.enumeration;

public enum AgentRoleEnum {
	AGENT("AG"),
	MANAGER_1("M1"),
	MANAGER_2("M2"),
	MANAGER_3("M3"),
	MANAGER_4("M4"),
	MANAGER_5("M5"),
	DIRECTOR_1("D1"),
	DIRECTOR_2("D2"),
	DIRECTOR_3("D3"),
	DIRECTOR_4("D4"),
	DIRECTOR_5("D5"),
	DIRECTOR_6("D6");
	
	private AgentRoleEnum(String value) {
		this.value = value;
	}
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
