package com.nsi.dto.response;

import com.nsi.domain.core.User;

public class TokenValidationResponse {
  private Boolean isAllowed;
  private String message;
  private User user;

  public Boolean getAllowed() {
    return isAllowed;
  }

  public void setAllowed(Boolean allowed) {
    isAllowed = allowed;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
