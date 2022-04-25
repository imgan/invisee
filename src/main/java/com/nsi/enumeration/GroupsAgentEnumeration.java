package com.nsi.enumeration;

public enum GroupsAgentEnumeration {

	AGENT_DEFAULT("AGENT_DEFAULT"),
	AGENT("AGENT");
    
    private String status;

    private GroupsAgentEnumeration(String status){
      this.status=status;
    }

    public String getStatus() {
        return status;
    }
}
