package com.nsi.dto.response;

import java.util.List;

public class CompletenessDetailResponse {
  private Boolean isCompleted;
  private Double calculatedData;
  private Double totalCalculatedData;
  private List<String> incompleteData;

  public Boolean getCompleted() {
    return isCompleted;
  }

  public void setCompleted(Boolean completed) {
    isCompleted = completed;
  }

  public Double getCalculatedData() {
    return calculatedData;
  }

  public void setCalculatedData(Double calculatedData) {
    this.calculatedData = calculatedData;
  }

  public Double getTotalCalculatedData() {
    return totalCalculatedData;
  }

  public void setTotalCalculatedData(Double totalCalculatedData) {
    this.totalCalculatedData = totalCalculatedData;
  }

  public List<String> getIncompleteData() {
    return incompleteData;
  }

  public void setIncompleteData(List<String> incompleteData) {
    this.incompleteData = incompleteData;
  }
}
