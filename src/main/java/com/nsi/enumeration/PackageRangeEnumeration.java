package com.nsi.enumeration;

public enum PackageRangeEnumeration {

	PERFORMANCE("PERFORMANCE"),
    SUMMARY("SUMMARY");
    
    private String status;

    private PackageRangeEnumeration(String status){
      this.status=status;
    }

    public String getStatus() {
        return status;
    }
}
