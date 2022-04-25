package com.nsi.services;

import com.nsi.dto.request.PackagePerformanceRequest;
import com.nsi.dto.response.BaseResponse;
import com.nsi.dto.response.PackagePerformanceResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nsi.domain.core.FundPackageProducts;
import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.Score;

public interface PackageService {

  public Map getFundPackageList(Integer offset, Integer limit, Score score);

  public Map getFundPackageListV2(Integer offset, Integer limit, Score score, String agentCode);
  
  public Map getFundPackageListV3(Integer offset, Integer limit, Score score, String agentCode);

  public Map getPackageDetails(String code);

  public Map getPackageFundAllocation(String code);

  public Map getPackageTransactionFee(String code);

  public Map getPackageSubscriptionFee(String code);

  public Map getPackageRedemptionFee(String code);

  public Map getPackagePaymentTypes(Map map);

  public String getColourByFundTypeName(String value);

  public Map getPackagePerformanceOneYear(FundPackages fundPackages);
  
  public Map getDocumentDownload(String code);

  public Boolean getTempAndLastDate(List<FundPackageProducts> products);

  public Boolean checkHoliday(Date checkDate);

  BaseResponse<PackagePerformanceResponse> performance(PackagePerformanceRequest packagePerformanceRequest);

}
