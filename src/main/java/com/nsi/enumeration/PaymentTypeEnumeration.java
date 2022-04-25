package com.nsi.enumeration;

public enum PaymentTypeEnumeration {

	TRANSFER_VA_PAYMENTTYPE("TRAN","TRANSFER VA"),
	TRANSFER_PAYMENTTYPE("CTRAN","TRANSFER"),
	WALLET_PAYMENTTYPE("WALL","WALLET");
	
	private String key;
    private String status;

    private PaymentTypeEnumeration(String key, String status){
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
