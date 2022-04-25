package com.nsi.enumeration;

public enum TrxStatusEnum {

	IN_ACTIVE("IN_ACTIVE"),
    ACTIVE("ACTIVE");
    
    private String status;

    private TrxStatusEnum(String status){
      this.status=status;
    }

    public String getStatus() {
        return status;
    }
}
