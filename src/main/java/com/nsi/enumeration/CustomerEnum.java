package com.nsi.enumeration;

public enum CustomerEnum {

	_AGENT("AGENT"),
    _CUSTOMER("CUSTOMER");
    
    private String name;
    
    private CustomerEnum(String name){
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
