package com.nsi.dto.response;

import java.util.Date;

public class PasswordChangeResponse {
  private String code;
  private String info;
  private Date serverTime = new Date();

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public Date getServerTime() {
    return serverTime;
  }

  public void setServerTime(Date serverTime) {
    this.serverTime = serverTime;
  }
}
