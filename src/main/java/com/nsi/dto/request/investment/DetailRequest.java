package com.nsi.dto.request.investment;

import java.io.Serializable;

public class DetailRequest implements Serializable {

  private String token;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
