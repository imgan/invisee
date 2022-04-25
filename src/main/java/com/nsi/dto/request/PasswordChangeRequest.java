package com.nsi.dto.request;

public class PasswordChangeRequest {
  private String token;
  private String password;
  private String passwordConfirm;
  private String passwordOld;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordConfirm() {
    return passwordConfirm;
  }

  public void setPasswordConfirm(String passwordConfirm) {
    this.passwordConfirm = passwordConfirm;
  }

  public String getPasswordOld() {
    return passwordOld;
  }

  public void setPasswordOld(String passwordOld) {
    this.passwordOld = passwordOld;
  }
}
