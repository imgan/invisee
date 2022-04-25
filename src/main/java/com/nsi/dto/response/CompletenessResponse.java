package com.nsi.dto.response;

import java.util.List;

public class CompletenessResponse {
  private Double completeness;
  private Integer calculatedData;
  private Integer totalCalculatedData;
  private List<String> incompleteData;

  public Double getCompleteness() {
    return completeness;
  }

  public void setCompleteness(Double completeness) {
    this.completeness = completeness;
  }

  public Integer getCalculatedData() {
    return calculatedData;
  }

  public void setCalculatedData(Integer calculatedData) {
    this.calculatedData = calculatedData;
  }

  public Integer getTotalCalculatedData() {
    return totalCalculatedData;
  }

  public void setTotalCalculatedData(Integer totalCalculatedData) {
    this.totalCalculatedData = totalCalculatedData;
  }

  public List<String> getIncompleteData() {
    return incompleteData;
  }

  public void setIncompleteData(List<String> incompleteData) {
    this.incompleteData = incompleteData;
  }
}
