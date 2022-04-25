package com.nsi.dto.response;

import java.util.Date;
import java.util.List;

public class PackagePerformanceResponse {
  private List<Double> percentage;
  private List<Double> amount;
  private List<Date> date;

  public List<Double> getPercentage() {
    return percentage;
  }

  public void setPercentage(List<Double> percentage) {
    this.percentage = percentage;
  }

  public List<Double> getAmount() {
    return amount;
  }

  public void setAmount(List<Double> amount) {
    this.amount = amount;
  }

  public List<Date> getDate() {
    return date;
  }

  public void setDate(List<Date> date) {
    this.date = date;
  }
}
