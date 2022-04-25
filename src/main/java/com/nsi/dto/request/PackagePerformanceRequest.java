package com.nsi.dto.request;

public class PackagePerformanceRequest {
  private Long packageId;
  private String rangeType;
  private Long range;

  public Long getPackageId() {
    return packageId;
  }

  public void setPackageId(Long packageId) {
    this.packageId = packageId;
  }

  public String getRangeType() {
    return rangeType;
  }

  public void setRangeType(String rangeType) {
    this.rangeType = rangeType;
  }

  public Long getRange() {
    return range;
  }

  public void setRange(Long range) {
    this.range = range;
  }
}
