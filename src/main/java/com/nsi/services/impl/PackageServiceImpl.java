package com.nsi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsi.domain.core.*;
import com.nsi.dto.request.PackagePerformanceRequest;
import com.nsi.dto.response.BaseResponse;
import com.nsi.dto.response.PackagePerformanceResponse;
import com.nsi.repositories.core.*;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.nsi.services.GlobalService;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.nsi.enumeration.PackageRangeEnumeration;
import com.nsi.services.ChannelService;
import com.nsi.services.PackageService;
import com.nsi.services.UtilService;
import com.nsi.util.DateTimeUtil;
import java.util.LinkedHashMap;
import org.springframework.web.client.RestTemplate;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class PackageServiceImpl extends BaseService implements PackageService {
  @Autowired
  FundPackagesRepository fundPackagesRepository;
  @Autowired
  FundPackageProductsRepository fundPackageProductsRepository;
  @Autowired
  LookupLineRepository lookupLineRepository;
  @Autowired
  LookupHeaderRepository lookupHeaderRepository;
  @Autowired
  UtTransactionTypeRepository utTransactionTypeRepository;
  @Autowired
  FundPackageFeeSetupRepository fundPackageFeeSetupRepository;
  @Autowired
  FundEscrowAccountRepository fundEscrowAccountRepository;
  @Autowired
  PackagePaymentRepository packagePaymentRepository;
  @Autowired
  UtProductFundPricesRepository utProductFundPricesRepository;
  @Autowired
  LookupLineRepositories lookupLineRepositories;
  @Autowired
  HolidayRepository holidayRepository;
  @Autowired
  AgentRepository agentRepository;
  @Autowired
  ChannelService channelService;
  @Autowired
  UtilService utilService;
  @Autowired
  AllowedPackagesAgentRepository allowedPackagesAgentRepository;
  @Autowired
  GlobalParameterRepository globalParameterRepository;
  @PersistenceContext
  EntityManager entityManager;
  @Autowired
  GlobalService globalService;
  @Autowired
  UtProductsSettlementRepository utProductsSettlementRepository;
  @Autowired
  AttachFileRepository attachFileRepository;
  Date tempDate = null;
  Date lastdate = null;

  private Double calculateGain(Double firstvalue, Double bidPrice) {
    Double gain = (bidPrice - firstvalue) / firstvalue;
    return gain;
  }

  private Double calculateCrossGain(Double gain, Double compositition) {
    Double crossGain = gain * compositition;
    return crossGain;
  }

  @Override
  public Map getFundPackageList(Integer offset, Integer limit, Score score) {
    List productLists = new ArrayList();
    List<Long> pkgReksadana = new ArrayList<>();
    List<Map> pkgReksadanaMap = new ArrayList<>();
    //		if(offset<0 || limit<0){
    //			funds = fundPackagesRepository.findAll();
    //		}else{
    //			Pageable pageable = new PageRequest(offset,limit);
    //			funds = fundPackagesRepository.findAllByOrderByFundPackageName(pageable);
    //		}

    LookupHeader header = lookupHeaderRepository.findByCategory("PRODUCT_TYPE");
    List<LookupLine> lines = lookupLineRepository.findAllByCategoryOrderBySequenceLookupAsc(header);
    for (LookupLine line : lines) {
      List result = new ArrayList();

      List<FundPackages> packages = fundPackagesRepository
          .findByCurrentDateAndPublishStatusWithQuery();
      for (FundPackages fp : packages) {
        Map data = new HashMap();
        List<FundPackageProducts> fundPackageProducts = fundPackageProductsRepository
            .findAllByFundPackages(fp);

        if (fundPackageProducts.size() == 1) {
          if (Long.valueOf(fundPackageProducts.get(0).getUtProducts().getProductType())
              .equals(line.getId())) {
            List<UtProductFundPrices> fundPrices = utProductFundPricesRepository
                .findTop1ByUtProductsOrderByPriceDateDesc(
                    fundPackageProducts.get(0).getUtProducts());

            Date now = null;

            for (UtProductFundPrices utpf : fundPrices) {
              now = utpf.getPriceDate();

              data.put("last_nav", utpf.getBidPrice());
              SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
              data.put("last_nav_date", sdf.format(utpf.getPriceDate()));
            }

            data.put("total_fund", fundPackageProducts.size());
            data.put("id", fp.getFundPackageId());
            data.put("name", fp.getFundPackageName());
            data.put("code", fp.getPackageCode());
            data.put("image", fp.getPackageImage());
            try {
              // Double perfOneYears = packagePerformanceService.getPackagePerformanceOneYear4Product(Long.valueOf(fund[0].toString()))
              // data.put("perf_oneyear",perfOneYears.data[0].value)

              Date bef = DateTimeUtil.minDateWeb(DateTimeUtil.getCustomYears(now, -1));
              Double priceBef = 0.0;
              Double priceNow = (Double) data.get("last_nav");
              Double calculate = 0.0;

              for (UtProductFundPrices utpf : fundPrices) {
                System.out.println("product_id : " + utpf.getUtProducts().getId());

                UtProductFundPrices oldPrice = utProductFundPricesRepository
                    .findByUtProductsAndPriceDate(utpf.getUtProducts(), lastdate);
                bef = utProductFundPricesRepository
                    .findByUtProductWithMaxPriceDateQuery(utpf.getUtProducts(), bef);
                priceBef = utProductFundPricesRepository
                    .findByUtProductWithBidPriceQuery(utpf.getUtProducts(), bef);

                System.out.println("bef : " + bef);
                System.out.println("priceBef : " + priceBef);

//                                bef = oldPrice.getPriceDate();
//                                priceBef = oldPrice.getBidPrice();
//                                
//                                bef = utProductFundPricesRepository.findByUtProductWithMaxPriceDateQuery(utpf.getUtProducts(), bef);
//                                priceBef = utProductFundPricesRepository.findByUtProductWithBidPriceQuery(utpf.getUtProducts(), bef);
              }

              calculate += calculateGain(priceBef, priceNow);

              System.out
                  .println("====================================================================");
              System.out.println("package ID : " + fp.getFundPackageId());
              System.out.println("package Name : " + fp.getFundPackageName());
              System.out.println("priceNow : " + priceNow);
              System.out.println("bef : " + bef);
              System.out.println("priceBef : " + priceBef);
              System.out.println("calculate : " + calculate);
              System.out
                  .println("====================================================================");

              data.put("perf_oneyear", calculate);

//                            Map pcgMap = this.getPackagePerformanceOneYear(fp);
//                            Map dataMap = (Map) pcgMap.get("data");
//                            data.put("perf_oneyear", dataMap.get("price"));
            } catch (Exception e) {
              // TODO: handle exception
              logger.info("perf one year is null");
              data.put("perf_oneyear", BigDecimal.ZERO);
            }
            data.put("recommended", this.getRecomendedProduct(score, fp));
            result.add(data);
          }
        } else {
          if (!pkgReksadana.contains(fp.getFundPackageId())) {
            pkgReksadana.add(fp.getFundPackageId());
          }
        }
      }

      Map dataProducts = new LinkedHashMap();
      dataProducts.put("type_code", line.getCode());
      dataProducts.put("package_type", line.getDescription());

      if (offset != -1 && limit != -1) {
        try {
          dataProducts.put("package_list", result.subList(offset, limit));
        } catch (Exception e) {
          dataProducts.put("package_list", result.subList(offset, result.size()));
        }
      } else {
        dataProducts.put("package_list", result);
      }
      productLists.add(dataProducts);
    }

    try {
      for (Long i : pkgReksadana) {
        Map data = new HashMap();
        FundPackages fundPkg = fundPackagesRepository.getOne(i);
        List<FundPackageProducts> products = fundPackageProductsRepository
            .findAllByFundPackages(fundPkg);
        data.put("total_fund", products.size());
        data.put("id", fundPkg.getFundPackageId());
        data.put("name", fundPkg.getFundPackageName());
        data.put("code", fundPkg.getPackageCode());
        data.put("image", fundPkg.getPackageImage());
        data.put("perf_oneyear", null);
        data.put("last_nav", null);
        data.put("last_nav_date", null);
        pkgReksadanaMap.add(data);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    LookupHeader lookupHeaderPkg = lookupHeaderRepository.findByCategory("PKG_REKSADANA");
    LookupLine linePkg = lookupLineRepositories
        .findByCategoryAndPublishStatus(lookupHeaderPkg, true);
    if (linePkg != null) {
      Map dataPro = new HashMap();
      dataPro.put("type_code", linePkg.getId());
      dataPro.put("package_type", linePkg.getDescription());
      dataPro.put("package_list", pkgReksadanaMap);
      productLists.add(dataPro);
    }

    Map resultMap = new HashMap<>();
    resultMap.put("code", 0);
    resultMap.put("info", "Package list successfully loaded");
    resultMap.put("data", productLists);
    return resultMap;
  }

  @Override
  public Map getFundPackageListV2(Integer offset, Integer limit, Score score, String agentCode) {
    String query = "SELECT\n" +
            "fp.fund_package_id AS fp_id,\n" +
            "fp.fund_package_name AS fp_name,\n" +
            "fp.package_image AS fp_image,\n" +
            "up.product_id AS product_id,\n" +
            "line.description AS product_type,\n" +
            "line.code AS product_code,\n" +
            "CAST(upfp_after.price_date AS DATE) AS price_date_after,\n" +
            "upfp_after.bid_price AS bid_price_after,\n" +
            "CAST(upfp_before.price_date AS DATE) AS price_date_before,\n" +
            "upfp_before.bid_price AS bid_price_before,\n" +
            "fc.total AS total_fund,\n" +
            "fp.package_code as \"packageCode\",\n" +
            "fp.market_value as market_value \n" +
            "FROM allowed_packages_agent apg\n" +
            "JOIN agent ON apg.agent = agent.\"id\"\n" +
            "JOIN fund_packages fp ON apg.fund_package = fp.fund_package_id\n" +
            "JOIN fund_package_products fpp ON fpp.fund_packages_id = fp.fund_package_id\n" +
            "JOIN ut_products up ON fpp.ut_products_id = up.product_id\n" +
            "JOIN lookup_line line ON up.product_type = CAST ( line.lookup_id AS VARCHAR )\n" +
            "JOIN lookup_header head ON line.category_id = head.category_id\n" +
            "JOIN investment_managers im ON up.investment_managers_id = im.inv_manager_id\n" +
            "JOIN (\n" +
            "SELECT\n" +
            "faa.products_id,\n" +
            "foo.price_date AS price_date,\n" +
            "faa.bid_price AS bid_price \n" +
            "FROM\n" +
            "ut_product_fund_prices faa\n" +
            "JOIN ( SELECT products_id AS products_id, MAX ( price_date ) AS price_date FROM ut_product_fund_prices GROUP BY products_id ) AS foo ON faa.products_id = foo.products_id \n" +
            "AND faa.price_date = foo.price_date \n" +
            ") AS upfp_after ON upfp_after.products_id = up.product_id\n" +
            "LEFT JOIN (\n" +
            "SELECT\n" +
            "fee.products_id,\n" +
            "fuu.price_date AS price_date,\n" +
            "fee.bid_price AS bid_price \n" +
            "FROM\n" +
            "ut_product_fund_prices fee\n" +
            "JOIN (\n" +
            "SELECT\n" +
            "faa.products_id,\n" +
            "MAX ( faa.price_date ) AS price_date \n" +
            "FROM\n" +
            "ut_product_fund_prices faa\n" +
            "JOIN ( SELECT products_id AS products_id, MAX ( price_date ) AS price_date FROM ut_product_fund_prices GROUP BY products_id ) AS foo ON faa.products_id = foo.products_id \n" +
            "WHERE\n" +
            "faa.price_date BETWEEN CAST(foo.price_date AS DATE) - (CAST(EXTRACT(DOY FROM TO_DATE(CONCAT('31-12-', to_char(foo.price_date, 'yyyy')), 'dd-MM-yyyy')) AS INTEGER) + 30) \n" +
            "AND CAST(foo.price_date AS DATE) - CAST(EXTRACT(DOY FROM TO_DATE(CONCAT('31-12-', to_char(foo.price_date, 'yyyy')), 'dd-MM-yyyy')) AS INTEGER) \n" +
            "GROUP BY\n" +
            "faa.products_id \n" +
            "ORDER BY\n" +
            "faa.products_id \n" +
            ") AS fuu ON fee.products_id = fuu.products_id \n" +
            "AND fee.price_date = fuu.price_date \n" +
            ") AS upfp_before ON upfp_before.products_id = up.product_id\n" +
            "JOIN (SELECT\n" +
            "fund_packages_id,\n" +
            "COUNT ( fund_packages_id ) AS total \n" +
            "FROM\n" +
            "fund_package_products \n" +
            "GROUP BY\n" +
            "fund_packages_id) as fc ON fp.fund_package_id = fc.fund_packages_id\n" +
            "WHERE\n" +
            "agent.code = :agentCode \n" +
            "AND fp.effective_date <= now() \n" +
            "AND fp.publish_status = TRUE \n" +
            "AND head.category = 'PRODUCT_TYPE' \n" +
            "GROUP BY\n" +
            "fp.fund_package_id,\n" +
            "up.product_id,\n" +
            "line.description,\n" +
            "line.code,\n" +
            "line.sequence_lookup,\n" +
            "upfp_after.price_date,\n" +
            "upfp_after.bid_price,\n" +
            "upfp_before.price_date,\n" +
            "upfp_before.bid_price,\n" +
            "fc.total,\n" +
            "fp.package_code,\n" +
            "im.display_name\n" +
            "ORDER BY\n" +
            "line.sequence_lookup asc";

    List<Object[]> listData = entityManager.createNativeQuery(query).setParameter("agentCode", agentCode).getResultList();
    List<Map> result = new ArrayList();
    Map packagesType = new LinkedHashMap();
    List<Map> packagesList = new ArrayList<>();
    for(Object[] data :listData){
      if (packagesType.isEmpty() || (!packagesType.get("package_type").equals(data[4]) && !packagesType.get("type_code").equals(data[5]))) {
        if (packagesList.size() > 0) {
          packagesType.put("package_list", packagesList);
          result.add(packagesType);
        }
        packagesType = new LinkedHashMap();
        packagesType.put("package_type", data[4]);
        packagesType.put("type_code", data[5]);
        packagesList = new ArrayList();
      }

      Double lastNav = Double.parseDouble(data[7].toString());
      Double perfOneyear = null;
      if(data[12] != null){
        perfOneyear = Double.parseDouble(data[12].toString());
      }else{
        if(data[9] != null){
          Double lastYearNav = Double.parseDouble(data[9].toString());
          perfOneyear = (lastNav - lastYearNav) / lastYearNav;
        }
      }

      Map packages = new LinkedHashMap();
      packages.put("id", data[0]);
      packages.put("code", data[11]);
      packages.put("name", data[1]);
      packages.put("image", data[2]);
      packages.put("last_nav_date", new SimpleDateFormat("yyyy-MM-dd").format(data[6]));
      packages.put("last_nav", lastNav);
      packages.put("total_fund", data[10]);
      packages.put("perf_oneyear", perfOneyear);
      packages.put("recommended", this.getRecomendedProduct(score, fundPackagesRepository.getOne(((BigInteger) data[0]).longValue())));
      packagesList.add(packages);
    }

    packagesType.put("package_list", packagesList);
    result.add(packagesType);

    Map resultMap = new HashMap<>();
    resultMap.put("code", 0);
    resultMap.put("info", "Package list successfully loaded");
    resultMap.put("data", result);
    return resultMap;
  }

  //TODO: Get Recommended product based to risk_profile kyc
  Boolean getRecomendedProduct(Score score, FundPackages fund) {
    try {
      List<Object[]> fp = new ArrayList();
      if (score == null) {
        fp = fundPackagesRepository.findByFundWithQuery();
      } else {
        List<Object[]> listFpWithScore = fundPackagesRepository.listFpWithScore(score);
        fp.addAll(listFpWithScore);

        for (Object[] obj : listFpWithScore) {
          if (fund.getFundPackageId().equals(obj[0])) {
            return true;
          }
        }
      }
      return false;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return false;
    }
  }

  @Override
  public Map getPackageDetails(String code) {
    Map resultMap = new HashMap<>();
    Map data = new HashMap<>();
    Map map = new HashMap<>();
    List packages = new ArrayList<>();

    FundPackages fund = fundPackagesRepository.findByPackageCode(code);

    if (fund != null) {
      map.put("code", fund.getPackageCode());
      List<FundPackageProducts> fp = fundPackageProductsRepository.findAllByFundPackages(fund);
      LookupLine lookupLine = lookupLineRepository.findById(Long.valueOf(fp.get(0).getUtProducts().getProductType()));
      map.put("type_code", lookupLine.getCode());
      map.put("package_type", lookupLine.getValue());
      map.put("name", fund.getFundPackageName());
      map.put("description", fund.getPackageDesc());
      map.put("image", fund.getPackageImage());
      SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
      map.put("effective_date", sdfDate.format(fund.getEffectiveDate()));
      map.put("currency", fund.getCurrency());
      SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");
      map.put("transaction_cut_off", sdf.format(fund.getTransactionCutOff()));
      map.put("settlement_cut_off", sdf.format(fund.getSettlementCutOff()));
      map.put("settlement_period", fund.getSettlementPeriod());
      map.put("minimal_subscribe", fund.getMinSubscriptionAmount());
      map.put("minimal_topup", fund.getMinTopupAmount());
      map.put("risk_level", fund.getRisk_Profile().getScoreCode());
      FundEscrowAccount account = fundEscrowAccountRepository.findByFundPackages(fund);
      map.put("bank_logo", account.getBank().getImageKey());
      map.put("bank_code", account.getBank().getBankCode());
      map.put("bank_name", account.getBank().getBankName());

      UtProductsSettlement utProductsSettlement = utProductsSettlementRepository.findByUtProduct(fp.get(0).getUtProducts());
      map.put("bank_account_name", utProductsSettlement.getAccountName());
      map.put("bank_account", utProductsSettlement.getAccountNumber());
      List<UtProductFundPrices> fundPrices = utProductFundPricesRepository.findTop1ByUtProductsOrderByPriceDateDesc(fp.get(0).getUtProducts());
      Date now = null;
      for (UtProductFundPrices utpf : fundPrices) {
        now = utpf.getPriceDate();
        map.put("last_nav", utpf.getBidPrice());
        SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd");
        map.put("last_nav_date", sdfFormat.format(now));
      }

      Date bef = DateTimeUtil.minDateWeb(DateTimeUtil.getCustomYears(now, -1));
      bef = globalService.getPrevWorkingDay(bef);
      Double priceBef = null;
      Double priceNow = (Double) map.get("last_nav");

      for (UtProductFundPrices utpf : fundPrices) {
        priceBef = utProductFundPricesRepository.findByUtProductWithBidPriceQuery(utpf.getUtProducts(), bef);
      }

      Double calculate = null;
      if(fund.getMarketValue() != null){
        calculate = fund.getMarketValue();
      }else{
        if(priceBef != null){
          calculate = (priceNow - priceBef) / priceBef;
        }
      }

      map.put("perf_oneyear", calculate);
      map.put("mi_name", fp.get(0).getUtProducts().getInvestmentManagers().getFullName());

      resultMap.put("code", 0);
      resultMap.put("info", "Package DetailResponse successfully loaded");
    } else {
      resultMap.put("code", 1);
      resultMap.put("info", "Package DetailResponse allocation is empty");
    }
    data.put("package", map);
    resultMap.put("data", data);

    return resultMap;
  }

  @Override
  public String getColourByFundTypeName(String fundTypeName) {
    try {
      LookupHeader header = lookupHeaderRepository.findByCategory("PRODUCT_TYPE");
      List<LookupLine> productType = lookupLineRepository
          .findAllByCategoryOrderBySequenceLookupAsc(header);

      LookupHeader headerColours = lookupHeaderRepository.findByCategory("FUND_TYPE_COLOUR");
      List<LookupLine> productTypeColour = lookupLineRepository
          .findAllByCodeAndCategoryOrderBySequenceLookupAsc("COLOURS", headerColours);

      String result = null;
      for (int i = 0; i < productType.size(); i++) {
        if (fundTypeName != null) {
          if (productType.get(i).getValue().equals(fundTypeName)) {
            result = productTypeColour.get(i).getValue();
            break;
          }
        }
      }

      return result;
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  public Map getPackagePerformanceOneYear(FundPackages fundPackages) {
    Map resultMap = new HashMap<>();
    tempDate = null;
    lastdate = null;
    List<FundPackageProducts> products = fundPackageProductsRepository
        .findAllByFundPackages(fundPackages);
    Boolean valueDate = getTempAndLastDate(products);
    if (!valueDate) {
      resultMap.put("code", 0);
      resultMap.put("info", "No data");
      return resultMap;
    }

    List<Map> maps = new ArrayList<>();
    List<String> showDays = new ArrayList<String>();
    showDays = getRangeOneYear(tempDate, lastdate, PackageRangeEnumeration.SUMMARY);
    if (showDays.isEmpty()) {
      //SUMMARY
      for (FundPackageProducts fpp : products) {
        UtProductFundPrices oldPrice = utProductFundPricesRepository
            .findByUtProductsAndPriceDate(fpp.getUtProducts(), lastdate);
        UtProductFundPrices newPrice = utProductFundPricesRepository
            .findByUtProductsAndPriceDate(fpp.getUtProducts(), tempDate);
        Map fppMap = new HashMap<>();
        fppMap.put("price_date", oldPrice.getPriceDate());
        fppMap.put("price", calculateGain(oldPrice.getBidPrice(), newPrice.getBidPrice()));
        fppMap.put("fp_product", fpp.getId());
        fppMap.put("composition", fpp.getCompositition());
        maps.add(fppMap);
      }

      Map xmap = new HashMap<>();
      for (Map map : maps) {
        if (xmap.containsKey(map.get("price_date"))) {
          xmap.put("price_date", map.get("price_date"));
          xmap.put("price",
              this.calculateCrossGain((Double) map.get("price"), (Double) map.get("composition"))
                  + (Double) xmap.get(map.get("price_date")));
          xmap.put(map.get("price_date"),
              this.calculateCrossGain((Double) map.get("price"), (Double) map.get("composition"))
                  + (Double) xmap.get(map.get("price_date")));
        }

        if (!xmap.containsKey(map.get("price_date"))) {
          xmap.put("price_date", map.get("price_date"));
          xmap.put("price",
              this.calculateCrossGain((Double) map.get("price"), (Double) map.get("composition")));
          xmap.put(map.get("price_date"),
              this.calculateCrossGain((Double) map.get("price"), (Double) map.get("composition")));
        }
      }

      resultMap.put("code", 0);
      resultMap.put("info", "Package List successfully loaded");
      resultMap.put("data", xmap);

    }
    return resultMap;
  }

  @Override
  public Map getPackageFundAllocation(String code) {
    Map resultMap = new HashMap<>();
    List fundAllocations = new ArrayList<>();
    Map data = new HashMap<>();

    FundPackages fund = fundPackagesRepository.findByPackageCode(code);
    List<FundPackageProducts> fpProducts = fundPackageProductsRepository
        .findAllByFundPackages(fund);
    if (!fpProducts.isEmpty()) {
      for (FundPackageProducts fp : fpProducts) {
        Map map = new HashMap<>();
        map.put("product_name", fp.getUtProducts().getProductName());
        map.put("allocation", fp.getCompositition() * 100);
        LookupLine lookupLine = lookupLineRepository
            .getOne(Long.parseLong(fp.getUtProducts().getProductType()));
        map.put("product_type", lookupLine.getValue());
        map.put("type_color", this.getColourByFundTypeName(lookupLine.getValue()));
        map.put("prospectus", fp.getUtProducts().getProspectusKey());
        map.put("fund_fact_sheet", fp.getUtProducts().getFundFactSheetKey());
        fundAllocations.add(map);
      }
      resultMap.put("code", 0);
      resultMap.put("info", "Package fund allocation successfully loaded");
    } else {
      resultMap.put("code", 1);
      resultMap.put("info", "Package fund allocation is empty");
    }
    data.put("fund_allocation", fundAllocations);
    resultMap.put("data", data);
    return resultMap;
  }

  @Override
  public Map getPackageTransactionFee(String code) {
    Map resultMap = new HashMap<>();
    Map data = new HashMap<>();

    FundPackages fund = fundPackagesRepository.findByPackageCode(code);
    UtTransactionType subs = utTransactionTypeRepository.findByTrxCode("SUBCR");
    List<FundPackageFeeSetup> feeSubsSetups = fundPackageFeeSetupRepository
        .findAllByFundPackagesAndTransactionTypeOrderByIdAsc(fund, subs);

    List subsFee = new ArrayList<>();
    if (!feeSubsSetups.isEmpty()) {
      for (FundPackageFeeSetup fee : feeSubsSetups) {
        Map map = new HashMap<>();
        map.put("fee", fee.getFeeAmount() * 100);
        map.put("amount_start", fee.getAmountMin());
        map.put("amount_end", fee.getAmountMax());
        subsFee.add(map);
      }
    }
    data.put("subscription_fee", subsFee);

    UtTransactionType rede = utTransactionTypeRepository.findByTrxCode("REDMP");
    List<FundPackageFeeSetup> feeRedeSetups = fundPackageFeeSetupRepository
        .findAllByFundPackagesAndTransactionTypeOrderByIdAsc(fund, rede);
    List redeFee = new ArrayList<>();
    if (!feeRedeSetups.isEmpty()) {
      for (FundPackageFeeSetup fee : feeRedeSetups) {
        Map map = new HashMap<>();
        map.put("fee", fee.getFeeAmount() * 100);
        map.put("period_start", fee.getAmountMin());
        map.put("period_end", fee.getAmountMax());
        redeFee.add(map);
      }
    }
    data.put("redemption_fee", redeFee);

    resultMap.put("code", 0);
    resultMap.put("info", "Package transaction fee successfully loaded");
    resultMap.put("data", data);
    return resultMap;
  }

  @Override
  public Map getPackageSubscriptionFee(String code) {
    Map resultMap = new HashMap<>();
    Map data = new HashMap<>();

    FundPackages fund = fundPackagesRepository.findByPackageCode(code);
    UtTransactionType subs = utTransactionTypeRepository.findByTrxCode("SUBCR");
    List<FundPackageFeeSetup> feeSubsSetups = fundPackageFeeSetupRepository
        .findAllByFundPackagesAndTransactionTypeOrderByIdAsc(fund, subs);

    List subsFee = new ArrayList<>();
    if (!feeSubsSetups.isEmpty()) {
      for (FundPackageFeeSetup fee : feeSubsSetups) {
        Map map = new HashMap<>();
        map.put("fee", fee.getFeeAmount() * 100);
        map.put("amount_start", fee.getAmountMin());
        map.put("amount_end", fee.getAmountMax());
        subsFee.add(map);
      }
    }
    data.put("subscription_fee", subsFee);
    resultMap.put("code", 0);
    resultMap.put("info", "Package Subscription fee successfully loaded");
    resultMap.put("data", data);
    return resultMap;
  }

  @Override
  public Map getPackageRedemptionFee(String code) {
    // TODO Auto-generated method stub
    Map resultMap = new HashMap<>();
    Map data = new HashMap<>();

    FundPackages fund = fundPackagesRepository.findByPackageCode(code);
    UtTransactionType rede = utTransactionTypeRepository.findByTrxCode("REDMP");
    List<FundPackageFeeSetup> feeRedeSetups = fundPackageFeeSetupRepository
        .findAllByFundPackagesAndTransactionTypeOrderByIdAsc(fund, rede);
    List redeFee = new ArrayList<>();
    if (!feeRedeSetups.isEmpty()) {
      for (FundPackageFeeSetup fee : feeRedeSetups) {
        Map map = new HashMap<>();
        map.put("fee", fee.getFeeAmount() * 100);
        map.put("period_start", fee.getAmountMin());
        map.put("period_end", fee.getAmountMax());
        redeFee.add(map);
      }
    }
    data.put("redemption_fee", redeFee);

    resultMap.put("code", 0);
    resultMap.put("info", "Package transaction fee successfully loaded");
    resultMap.put("data", data);
    return resultMap;
  }

  //TODO: Get Maximum & Minimum Price Date per FundPackages
  public Boolean getTempAndLastDate(List<FundPackageProducts> products) {
    for (FundPackageProducts fpp : products) {
      UtProducts up = fpp.getUtProducts();
      //TODO: Get max priceDate by UtProducts
      Date startDatePackagePerformance = null;
      try {
        startDatePackagePerformance = utProductFundPricesRepository
            .findByUtProductWithMaxPriceDateQuery(up);
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (startDatePackagePerformance == null) {
        break;
      }

      if (tempDate == null) {
        tempDate = startDatePackagePerformance;
      } else if (tempDate != startDatePackagePerformance || !(tempDate)
          .equals(startDatePackagePerformance)) {
        if (tempDate.compareTo(startDatePackagePerformance) > 0) {
          tempDate = startDatePackagePerformance;
        }
      }

      //TODO: Get min priceDate by UtProducts
      Date lastDatePackagePerformance = null;
      try {
        lastDatePackagePerformance = utProductFundPricesRepository
            .findByUtProductWithMinPriceDateQuery(up);
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (lastDatePackagePerformance == null) {
        break;
      }

      if (lastdate == null) {
        lastdate = lastDatePackagePerformance;
      } else if (lastdate != lastDatePackagePerformance || !(lastdate)
          .equals(lastDatePackagePerformance)) {
        if (lastdate.compareTo(lastDatePackagePerformance) > 0) {
          lastdate = lastDatePackagePerformance;
        }
      }
    }

    if (tempDate == null || lastdate == null) {
      return false;
    } else {
      return true;
    }
  }

  private String formatterDate(Date vardate) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String format = formatter.format(vardate);
    return format;
  }

  private Date formatterDateToDate(String vardate) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    Date format = null;
    try {
      format = formatter.parse(vardate);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return format;
  }

  private List<String> getRangeOneYear(Date tempDate, Date lastDate,
      PackageRangeEnumeration status) {
    List<String> annually = new ArrayList<String>();
    Date thisToday = tempDate;
    Calendar thisTodayCalendar = Calendar.getInstance();

    Calendar lastDateCalendar = Calendar.getInstance();
    lastDateCalendar.setTime(lastDate);

    //ONE YEAR - End Date
    Calendar endDateCalender = Calendar.getInstance();
    endDateCalender.setTime(thisToday);
    endDateCalender.add(Calendar.YEAR, -1);

    if (endDateCalender.compareTo(lastDateCalendar) >= 0) { //If one year ago more than last date
      thisTodayCalendar.setTime(thisToday);
      long diff = thisTodayCalendar.getTime().getTime() - endDateCalender.getTime().getTime();
      long duration = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
      if (PackageRangeEnumeration.PERFORMANCE.equals(status)) {
        for (long i = 0; i <= duration; i++) {
          thisTodayCalendar.setTime(thisToday);
          thisTodayCalendar
              .set(Calendar.DATE, thisTodayCalendar.get(Calendar.DATE) - Math.toIntExact(i));
          if (!checkHoliday(thisTodayCalendar.getTime())) {
            String format = formatterDate(thisTodayCalendar.getTime());
            annually.add(format);
          }
        }
      } else if (PackageRangeEnumeration.SUMMARY.equals(status)) {
        for (long i = 1; i <= duration; i++) {
          if (!checkHoliday(endDateCalender.getTime())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            String endDateStr = sdf.format(endDateCalender.getTime());
            try {
              lastdate = sdf.parse(endDateStr);
            } catch (ParseException e) {
              e.printStackTrace();
            }
            break;
          }
          endDateCalender.add(Calendar.DATE, endDateCalender.get(Calendar.DATE) + 1);
        }
      }

    } else {
      if (PackageRangeEnumeration.PERFORMANCE.equals(status)) {
        thisTodayCalendar.setTime(thisToday);
        while (thisTodayCalendar.compareTo(lastDateCalendar) >= 0) {
          if (!checkHoliday(thisTodayCalendar.getTime())) {
            String format = formatterDate(thisTodayCalendar.getTime());
            annually.add(format);
          }
          thisTodayCalendar.add(Calendar.DATE, thisTodayCalendar.get(Calendar.DATE) - 1);
        }
      } else if (PackageRangeEnumeration.SUMMARY.equals(status)) {
        while (lastDateCalendar.before(thisTodayCalendar)) {
          if (!checkHoliday(lastDateCalendar.getTime())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            String endDateStr = sdf.format(lastDateCalendar.getTime());
            try {
              lastdate = sdf.parse(endDateStr);
            } catch (ParseException e) {
              e.printStackTrace();
            }
            break;
          }
          endDateCalender.add(Calendar.DATE, endDateCalender.get(Calendar.DATE) + 1);
        }

      }
    }
    System.out.println("annually : " + annually);
    return annually;
  }

  //TODO: Check Holiday
  public Boolean checkHoliday(Date checkDate) {
    List<Date> holidays = holidayRepository.getHolidayDate();
    List<String> holidayStr = new ArrayList<String>();
    for (Date day : holidays) {
      holidayStr.add(this.formatterDate(day));
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(checkDate);

    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    switch (dayOfWeek) {
      case Calendar.SATURDAY:
        return true;
      case Calendar.SUNDAY:
        return true;
      default:
        while (holidayStr.contains(this.formatterDate(cal.getTime()))) {
          return true;
        }
        break;
    }

    return false;
  }

  @Override
  public BaseResponse<PackagePerformanceResponse> performance(
      PackagePerformanceRequest packagePerformanceRequest) {
    BaseResponse<PackagePerformanceResponse> result = new BaseResponse<>();
    try {
      String url = globalParameterRepository.findByName("GROOVY_API_URL").getValue()
          + "/performance/getPackagePerformance";

      String range =
          packagePerformanceRequest.getRange().toString() + packagePerformanceRequest.getRangeType()
              .toLowerCase();
      JSONObject request = new JSONObject();
      request.put("packageId", packagePerformanceRequest.getPackageId().toString());
      request.put("range", range);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.add("X-Application-Token", "");
      RestTemplate rest = new RestTemplate();
      HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
      ResponseEntity<HashMap> response;

      try {
        logger.info("send request : " + url + ", body : " + request.toString());
        response = rest.exchange(url, HttpMethod.POST, entity, HashMap.class);
        logger.info("send response : " + response);
      } catch (Exception ignored) {
        result.setCode("CONNECTION_FAILED");
        result.setInfo("Core system down");
        result.setServerTime(new Date());
        return result;
      }


      Map responseData = new ObjectMapper().convertValue(response.getBody(), Map.class);
      Map data = (Map) responseData.get("data");
      List<Map> performance_data = (List<Map>) data.get("performance_data");
      List<String> performance_date = (List<String>) data.get("performance_date");
      List<Double> performance_data_percentage = (List<Double>) performance_data.get(0).get("value");
      List<Double> performance_data_amount = (List<Double>) performance_data.get(1).get("value");

      List<Date> dateList = new ArrayList<>();
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

      List<Double> percentageList = new ArrayList<>(performance_data_percentage);
      List<Double> amountList = new ArrayList<>(performance_data_amount);
      for (String s : performance_date) dateList.add(sdf.parse(s));

      PackagePerformanceResponse packagePerformanceResponse = new PackagePerformanceResponse();
      packagePerformanceResponse.setPercentage(percentageList);
      packagePerformanceResponse.setAmount(amountList);
      packagePerformanceResponse.setDate(dateList);

      result.setCode("SUCCEED");
      result.setInfo("Package performance successfully loaded");
      result.setData(packagePerformanceResponse);
    } catch (Exception ignored) {
      result.setCode("INTERNAL_SERVER_ERROR");
      result.setInfo("Internal server error");
    }
    result.setServerTime(new Date());
    return result;
  }

  @Override
  public Map getPackagePaymentTypes(Map map) {
    Map resultMap = new HashMap();
    Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
    Map result = channelService.generateAgentSignature(String.valueOf(map.get("agent")),
        String.valueOf(map.get("signature")));

    logger.info("agent : " + agent);
    logger.info("result : " + result);

    if (result.get("code").equals(0)) {
      if (utilService.checkAccessPermission(agent)) {
        FundPackages fundPackages = fundPackagesRepository
            .findByPackageCode(map.get("code").toString());
        logger.info("fundPackages " + fundPackages.toString());
        if (fundPackages == null) {
          return errorResponse(50, "package payment", "package code not found");
        }

        List<PackagePayment> packagePayments = packagePaymentRepository
            .findAllByFundPackages(fundPackages);
        List<Map> dataMapList = new ArrayList<>();
        int sequence = 1;
        logger.info("Count packagePayments : " + packagePayments.size());
        for (PackagePayment line : packagePayments) {

          Map dataMap = new HashMap<>();
          dataMap.put("sequence", sequence);
          dataMap.put("code", line.getPaymentMethod().getCode());
          dataMap.put("value", line.getPaymentMethod().getName());

          if (line.getPaymentMethod().getRowStatus()) {
            logger.info("line " + line.getPaymentMethod().getRowStatus());
            dataMapList.add(dataMap);
            sequence++;
          }
        }

        return errorResponse(0, "package payment", dataMapList);
      } else {
        return errorResponse(12, "package payment", "invalid access, you dont have permission");
      }
    } else {
      resultMap.put("code", result.get("code"));
      resultMap.put("info", result.get("info"));
    }
    return resultMap;
  }
  

	@Override
	public Map getDocumentDownload(String code) {
		  Map resultMap = new HashMap<>();
		  Map maps = new HashMap<>();
		  String encodedString = null;
			
				try {
					AttachFile doc = attachFileRepository.findByKey(code);
					if (doc == null) {
						resultMap = errorResponse(50, "File Not Found", null);
					}
				
					File file = new File(doc.getLokasiFile());
					FileInputStream fis = new FileInputStream(file);
					byte[] fileContent = new byte[(int) file.length()];
					fis = new FileInputStream(file);
					fis.read(fileContent);
					encodedString = Base64.encodeBase64String(fileContent);
					fis.close();
					maps.put("content", encodedString);
				    maps.put("fileName", doc.getNamaFile());
				    resultMap.put("code", 0);
				    resultMap.put("info", "successfully loaded");
				    resultMap.put("data", maps);
				   } catch (Exception e) {
					   logger.error("[FATAL] " ,e);
					   resultMap = errorResponse(50, "File Not Found", null);
			       }     
			   
		    return resultMap;
	}
	
	@Override
	  public Map getFundPackageListV3(Integer offset, Integer limit, Score score, String agentCode) {
	    String query = "SELECT q.*,(SELECT \n"
	    		+ "SUM(upfp.bid_price)/(SELECT      \n"
	    		+ "count(ut_products_id)     \n"
	    		+ "FROM fund_package_products\n"
	    		+ "WHERE      \n"
	    		+ "fund_packages_id = q.fp_id )\n"
	    		+ "FROM ut_product_fund_prices upfp\n"
	    		+ "WHERE upfp.products_id in ( SELECT      \n"
	    		+ "ut_products_id as product_id      \n"
	    		+ "FROM fund_package_products       \n"
	    		+ " WHERE      \n"
	    		+ "fund_packages_id = q.fp_id)\n"
	    		+ "AND upfp.price_date = (SELECT max(upfpp.price_date)\n"
	    		+ "FROM ut_product_fund_prices upfpp\n"
	    		+ "WHERE upfpp.products_id = upfp.products_id)) as avg_last_nav ,  \n"
	    		+ "(SELECT\n"
	    		+ "SUM(pp.one_year)/(SELECT      \n"
	    		+ "count(ut_products_id)     \n"
	    		+ "FROM fund_package_products\n"
	    		+ " WHERE      \n"
	    		+ "fund_packages_id = q.fp_id )\n"
	    		+ "FROM performance_product pp\n"
	    		+ "WHERE pp.product_id in ( SELECT      \n"
	    		+ "ut_products_id as product_id      \n"
	    		+ "FROM fund_package_products       \n"
	    		+ "WHERE      \n"
	    		+ "fund_packages_id = q.fp_id)\n"
	    		+ "AND pp.price_date = (SELECT max(ppp.price_date)\n"
	    		+ "FROM performance_product ppp\n"
	    		+ "WHERE ppp.product_id = pp.product_id)) as avg_one_year_performance  \n"
	    		+ "from (SELECT\n" +
	            "fp.fund_package_id AS fp_id,\n" +
	            "fp.fund_package_name AS fp_name,\n" +
	            "fp.package_image AS fp_image,\n" +
	            "up.product_id AS product_id,\n" +
	            "line.description AS product_type,\n" +
	            "line.code AS product_code,\n" +
	            "CAST(upfp_after.price_date AS DATE) AS price_date_after,\n" +
	            "upfp_after.bid_price AS bid_price_after,\n" +
	            "CAST(upfp_before.price_date AS DATE) AS price_date_before,\n" +
	            "upfp_before.bid_price AS bid_price_before,\n" +
	            "fc.total AS total_fund,\n" +
	            "fp.package_code as \"packageCode\",\n" +
	            "fp.market_value as market_value, \n" +
	            "fp.risk_profile as risk_profile, \n" +
	            "fp.transaction_cut_off as transaction_cut_off, \n" +
	            "fp.settlement_cut_off as settlement_cut_off, \n" +
	            "fp.currency as currency, \n" +
	            "fp.min_subscription_amount as min_subscription_amount, \n" +
	            "fp.min_topup_amount as min_topup_amount, \n" +
	            "up.fund_fact_sheet_key as fund_face_sheet_key, \n" +
	            "up.prospectus_key as propectus_key, \n" +
	            "upfp_after.at_price_group_id, \n" +
	            "MAX(pp.price_date) as price_date \n"+
	            "FROM allowed_packages_agent apg\n" +
	            "JOIN agent ON apg.agent = agent.\"id\"\n" +
	            "JOIN fund_packages fp ON apg.fund_package = fp.fund_package_id\n" +
	            "JOIN fund_package_products fpp ON fpp.fund_packages_id = fp.fund_package_id\n" +
	            "JOIN ut_products up ON fpp.ut_products_id = up.product_id\n" +
	            "LEFT JOIN (SELECT one_year AS one_year, product_id AS product_id, MAX ( price_date ) AS price_date FROM performance_product GROUP BY product_id, one_year order by price_date desc limit 1 ) AS pp ON up.product_id = pp.product_id \n" +
	            "JOIN lookup_line line ON up.product_type = CAST ( line.lookup_id AS VARCHAR )\n" +
	            "JOIN lookup_header head ON line.category_id = head.category_id\n" +
	            "JOIN investment_managers im ON up.investment_managers_id = im.inv_manager_id\n" +
	            "JOIN (\n" +
	            "SELECT\n" +
	            "faa.at_price_group_id AS at_price_group_id,\n" +
	            "faa.products_id,\n" +
	            "foo.price_date AS price_date,\n" +
	            "faa.bid_price AS bid_price \n" +
	            "FROM\n" +
	            "ut_product_fund_prices faa\n" +
	            "JOIN ( SELECT products_id AS products_id, MAX ( price_date ) AS price_date FROM ut_product_fund_prices GROUP BY products_id ) AS foo ON faa.products_id = foo.products_id \n" +
	            "AND faa.price_date = foo.price_date \n" +
	            ") AS upfp_after ON upfp_after.products_id = up.product_id\n" +
	            "LEFT JOIN (\n" +
	            "SELECT\n" +
	            "fee.products_id,\n" +
	            "fuu.price_date AS price_date,\n" +
	            "fee.bid_price AS bid_price \n" +
	            "FROM\n" +
	            "ut_product_fund_prices fee\n" +
	            "JOIN (\n" +
	            "SELECT\n" + 
	            "faa.products_id,\n" +
	            "MAX ( faa.price_date ) AS price_date \n" +
	            "FROM\n" +
	            "ut_product_fund_prices faa\n" +
	            "JOIN ( SELECT products_id AS products_id, MAX ( price_date ) AS price_date FROM ut_product_fund_prices GROUP BY products_id ) AS foo ON faa.products_id = foo.products_id \n" +
	            "WHERE\n" +
	            "faa.price_date BETWEEN CAST(foo.price_date AS DATE) - (CAST(EXTRACT(DOY FROM TO_DATE(CONCAT('31-12-', to_char(foo.price_date, 'yyyy')), 'dd-MM-yyyy')) AS INTEGER) + 30) \n" +
	            "AND CAST(foo.price_date AS DATE) - CAST(EXTRACT(DOY FROM TO_DATE(CONCAT('31-12-', to_char(foo.price_date, 'yyyy')), 'dd-MM-yyyy')) AS INTEGER) \n" +
	            "GROUP BY\n" +
	            "faa.products_id \n" +
	            "ORDER BY\n" +
	            "faa.products_id \n" +
	            ") AS fuu ON fee.products_id = fuu.products_id \n" +
	            "AND fee.price_date = fuu.price_date \n" +
	            ") AS upfp_before ON upfp_before.products_id = up.product_id\n" +
	            "JOIN (SELECT\n" +
	            "fund_packages_id,\n" +
	            "COUNT ( fund_packages_id ) AS total \n" +
	            "FROM\n" +
	            "fund_package_products \n" +
	            "GROUP BY\n" +
	            "fund_packages_id) as fc ON fp.fund_package_id = fc.fund_packages_id\n" +
	            "WHERE\n" +
	            "agent.code = :agentCode \n" +
	            "AND fp.effective_date <= now() \n" +
	            "AND fp.publish_status = TRUE \n" +
	            "AND head.category = 'PRODUCT_TYPE' \n" +
	            "GROUP BY\n" +
	            "fp.fund_package_id,\n" +
	            "up.product_id,\n" +
	            "line.description,\n" +
	            "line.code,\n" +
	            "line.sequence_lookup,\n" +
	            "upfp_after.price_date,\n" +
	            "upfp_after.bid_price,\n" +
	            "upfp_after.at_price_group_id,\n" +
	            "upfp_before.price_date,\n" +
	            "upfp_before.bid_price,\n" + 
	            "fc.total,\n" +
	            "fp.package_code,\n" +
	            "im.display_name,"+
	            "pp.price_date\n" +
	            "ORDER BY\n" +
	            "line.sequence_lookup asc) as q  ";
	    List<Object[]> listData = entityManager.createNativeQuery(query).setParameter("agentCode", agentCode).getResultList();
	    List<Map> result = new ArrayList();
	    Map packagesType = new LinkedHashMap();
	    List<Map> packagesList = new ArrayList<>();
	    
	    for(Object[] data :listData){
	    	if (packagesType.isEmpty() || (!packagesType.get("package_type").equals(data[4]) && !packagesType.get("package_code").equals(data[11]))) {
	    		Double avg_nav = null;
			if (packagesList.size() > 0) {
	            packagesType.put("package_detail", packagesList);
	            result.add(packagesType);
			}
	        packagesType = new LinkedHashMap();
	        packagesType.put("package_name", data[1]);
	        packagesType.put("package_code", data[11]);
	        packagesType.put("package_type", data[4]);
	        packagesType.put("package_risk_profile", data[13]);        
	        packagesType.put("last_nav_date", new SimpleDateFormat("yyyy-MM-dd").format(data[6]));
	        packagesType.put("avg_last_nav",data[23]);
	        packagesType.put("total_minimal_subscribe", data[17]);
	        packagesType.put("total_minimal_topup", data[18]);
	        packagesType.put("currency", data[16]);
	        packagesType.put("transaction_cut_off", data[14]);
	        packagesType.put("settlement_cut_off", data[15]);
	        packagesType.put("avg_performance_one_year", data[24]);
	        packagesList = new ArrayList();
	      }
	      Map packages = new LinkedHashMap();
	      packages.put("fund_fact_sheet", data[19]);
	      packages.put("prospectus", data[20]);
	      packagesList.add(packages);
	    }

	    packagesType.put("package_detail", packagesList);
	    result.add(packagesType);

	    Map resultMap = new HashMap<>();
	    resultMap.put("code", 0);
	    resultMap.put("info", "Package list successfully loaded");
	    resultMap.put("data", result);
	    return resultMap;
	  }
}
