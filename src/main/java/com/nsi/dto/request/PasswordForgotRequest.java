package com.nsi.dto.request;

import java.io.Serializable;

public class PasswordForgotRequest implements Serializable {

  private String username;
  private String agentCode;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAgentCode() {
    return agentCode;
  }

  public void setAgentCode(String agentCode) {
    this.agentCode = agentCode;
  }
}
