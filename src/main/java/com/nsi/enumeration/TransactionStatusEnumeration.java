package com.nsi.enumeration;

public enum TransactionStatusEnumeration {

	ORDERED("ORD","Ordered"),
	CANCELED("CAN","Canceled"),
    SETTLED("STL","Settled"),
    ALLOCATED("ALL", "Allocated");
	
    
	private String key;
    private String status;

    private TransactionStatusEnumeration(String key, String status){
    	this.key = key;
    	this.status=status;
    }

    public String getStatus() {
        return status;
    }

	public String getKey() {
		return key;
	}
    
}
