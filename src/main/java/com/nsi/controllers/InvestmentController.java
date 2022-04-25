package com.nsi.controllers;

import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import com.nsi.dto.request.investment.DetailRequest;
import com.nsi.repositories.core.FundPackageProductsRepository;
import com.nsi.repositories.core.InvestmentAccountsRepository;
import com.nsi.repositories.core.KycRepository;
import com.nsi.repositories.core.UserRepository;
import com.nsi.services.GlobalService;
import com.nsi.services.InvestmentService;
import com.nsi.services.PieChartService;
import com.nsi.services.UtilService;
import com.nsi.util.ConstantUtil;
import com.nsi.util.ValidateUtil;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/investment")
public class InvestmentController extends BaseController {

  @Autowired
  GlobalService globalService;

  @Autowired
  UserRepository userService;

  @Autowired
  InvestmentService investmentService;
  @Autowired
  UtilService utilService;
  @Autowired
  KycRepository kycRepository;
  @Autowired
  InvestmentAccountsRepository investmentAccountsRepository;
  @Autowired
  FundPackageProductsRepository fundPackageProductsRepository;

  @Autowired
  PieChartService pieChartService;

  @RequestMapping(value = "/redeem", method = RequestMethod.POST)
  public ResponseEntity<Map> redeemPackage(@RequestBody Map map, @RequestParam("token") String token, HttpServletRequest request) {
    String version = request.getHeader("version");
    User user = userService.getOne(new Long(349));
    Boolean checkPin = globalService.checkpinValid(map, user);
    Map resultMap = new HashMap<>();
    if (checkPin) {
      Map transMap = investmentService.redeemTransaction(map, user);
    }

    if("2".equals(version)){
      resultMap = changeCodeIntToString(resultMap);
    }

    return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
  }

  @RequestMapping(value = "/list", method = RequestMethod.POST)
  public ResponseEntity<Map> investmentList(HttpServletRequest request, @RequestBody Map map)
      throws ParseException {
    String version = request.getHeader("version");
    Integer offset = -1, limit = -1;
    Map resultMap = new HashMap<>();
    if (map.get("offset") != null) {
      offset = Integer.parseInt(String.valueOf(map.get("offset")));
    } else {
      offset = 0;
    }

    if (map.get("limit") != null) {
      limit = Integer.parseInt(String.valueOf(map.get("limit")));
    } else {
      limit = 100;
    }

    User user = null;
    Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")),
        request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr()
            : request.getHeader("X-FORWARDED-FOR"));
    if (!tokenMap.get("code").equals(1)) {
      resultMap = tokenMap;

      if("2".equals(version)){
        resultMap = changeCodeIntToString(resultMap);
      }

      return new ResponseEntity<>(resultMap, HttpStatus.OK);
    } else {
      user = (User) tokenMap.get("user");
    }
    Kyc kyc = kycRepository.findByAccount(user);

    resultMap = investmentService.investmentList(offset, limit, map, kyc);

    if("2".equals(version)){
      resultMap = changeCodeIntToString(resultMap);
    }

    return new ResponseEntity<>(resultMap, HttpStatus.OK);
  }

  @PostMapping("/{invNo}")
  public ResponseEntity<Map> detail(@PathVariable("invNo") String invNo, @RequestBody DetailRequest request, HttpServletRequest httpServletRequest) {
    String version = httpServletRequest.getHeader("version");
    Map resultMap;
    ResponseEntity<Map> result;
    HttpStatus httpStatus = HttpStatus.OK;
    if("2".equals(version)){
      result = investmentService.detailNewVersion(invNo, request, httpServletRequest);
      resultMap = result.getBody();
    }else{
      result = investmentService.detail(invNo, request, httpServletRequest);
      resultMap = result.getBody();
    }

    if((int)resultMap.get("code") == 401){
      httpStatus = HttpStatus.UNAUTHORIZED;
    }else if((int)resultMap.get("code") == 403){
      httpStatus = HttpStatus.FORBIDDEN;
    }

    if("3".equals(version)){
      resultMap = changeCodeIntToString(resultMap);
    }

    return new ResponseEntity<>(resultMap, httpStatus);
  }

  @RequestMapping(value = "/summary", method = RequestMethod.POST)
  public ResponseEntity<Map> summary(HttpServletRequest request, @RequestBody Map map) {
    String version = request.getHeader("version");
    try {
      Map checkToken = utilService.checkToken((String) map.get("token"), request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR"));
      if (Integer.parseInt(checkToken.get("code").toString()) == 100) {

        if("2".equals(version)){
          checkToken = changeCodeIntToString(checkToken);
        }

        return new ResponseEntity<>(checkToken, HttpStatus.OK);
      }

      User user = (User) checkToken.get("user");
      Kyc kyc = kycRepository.findByAccount(user);
      String name = "";
      if ("ACT".equals(kyc.getAccount().getUserStatusSebelumnya())) {
        name = "Akun sedang diverifikasi";
      } else {
        name = kyc.getFirstName().concat(" ").concat(kyc.getMiddleName()).concat("").concat(kyc.getLastName());
      }

      Double investmentAccount = 0.0;
      Double amountMarketValue = 0.0;

      Map resultMap = investmentService.investmentList(0, 100, map, kyc);
      if (resultMap.get("code").equals(0)) {
        Map listMax = (Map) resultMap.get("data");
        if (listMax != null && !listMax.isEmpty()) {
          List<Map> listMap = (List<Map>) listMax.get("investment");
          if (listMap != null && !listMap.isEmpty()) {
            for (Map map1 : listMap) {
              investmentAccount += Double.valueOf(map1.get("invest_amount").toString());
              List<Map> mapx = (List<Map>) map1.get("investmentComposition");
              for (Map map2 : mapx) {
                amountMarketValue += Double.valueOf(map2.get("market_value").toString());
              }
            }
          }
        }
      }

      Map dataScore = new LinkedHashMap();
      dataScore.put("code", kyc.getRiskProfile().getScoreCode());
      dataScore.put("value", kyc.getRiskProfile().getScoreName());

      Map invest = new LinkedHashMap();
      invest.put("invest_amount", investmentAccount);
      invest.put("market_value", amountMarketValue);
      invest.put("profit_loss", amountMarketValue - investmentAccount);

      Map summary = new LinkedHashMap<>();
      summary.put("customer_risk_profile", dataScore);
      summary.put("summary", invest);
      summary.put("status_user", kyc.getAccount().getUserStatus());
      summary.put("name", name);

      resultMap = new LinkedHashMap<>();
      resultMap.put("code", "0");
      resultMap.put("data", summary);
      resultMap.put("info", "Portofolio status loaded");

      return new ResponseEntity<>(resultMap, HttpStatus.OK);
    } catch (Exception e) {
    	logger.error("[FATAL]", e);
    	Map resultMap = new HashMap();
    	if(version != null && version.equals("2")){
          resultMap.put("code", "99");
        }else{
          resultMap.put("code", 99);
        }
    	resultMap.put("info", "General error");
    	return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/piechart", method = RequestMethod.POST)
  public ResponseEntity<Map> piechart(HttpServletRequest request, @RequestBody Map map) {
    String version = request.getHeader("version");
    Map resultMap;
    try {
      resultMap = ValidateUtil.validateAPI("customer/pie.json", map);
      if (resultMap != null) {

        if("2".equals(version)){
          resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
      }

      Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR"));
      if (!tokenMap.get("code").equals(1)) {
        resultMap = tokenMap;

        if("2".equals(version)){
          resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
      }

      User user = (User) tokenMap.get("user");
      resultMap = pieChartService.valuepieChartMap(user);

      if("2".equals(version)){
        resultMap = changeCodeIntToString(resultMap);
      }

      loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
      return new ResponseEntity<Map>(resultMap, HttpStatus.OK);

    } catch (IOException e) {
    	logger.error("[FATAL]" ,e);
    	return new ResponseEntity<Map>(errorResponse(99, "piechart", null), HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/balance", method = RequestMethod.POST)
  public ResponseEntity<Map> listBalance(HttpServletRequest request, @RequestBody Map map) {
    loggerHttp(request, ConstantUtil.REQUEST, map);
    String version = request.getHeader("version");
    Map resultMap;
    try {
      resultMap = ValidateUtil.validateAPI("investment/list_balance.json", map);
      if (resultMap != null) {

        if("2".equals(version)){
          resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity(resultMap, HttpStatus.OK);
      }

      Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")),
              request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR"));
      if ((int) tokenMap.get("code") != 1) {
        resultMap = tokenMap;

        if("2".equals(version)){
          resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity(resultMap, HttpStatus.UNAUTHORIZED);
      }

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      sdf.setLenient(false);
      Date navDate;
      try{
        navDate = sdf.parse((String) map.get("balance_date"));
      }catch(ParseException e){
        resultMap = errorResponse(11, "balance_date", null);

        if("2".equals(version)){
          resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity(resultMap, HttpStatus.OK);
      }

      User user = (User) tokenMap.get("user");
      resultMap = investmentService.listBalance((String) map.get("inv_account_no"), navDate, user);
      loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
      HttpStatus httpStatus = HttpStatus.OK;
      if((int) resultMap.get("code") == 404){
        httpStatus = HttpStatus.NOT_FOUND;
      }
      return new ResponseEntity(resultMap, httpStatus);

    } catch (IOException e) {
      logger.error("[FATAL] :" + e.getMessage(), e);
      loggerHttp(request, ConstantUtil.RESPONSE, errorResponse(99, "list_balance", null));
      return new ResponseEntity<Map>(errorResponse(99, "list_balance", null), HttpStatus.OK);
    }
  }
    
  @PostMapping("/{invNo}/performance")
  public ResponseEntity<Map> performance(@PathVariable("invNo") String invNo, @RequestBody DetailRequest request, HttpServletRequest httpServletRequest) {
    String version = httpServletRequest.getHeader("version");
    Map resultMap;
    ResponseEntity<Map> result;
    HttpStatus httpStatus = HttpStatus.OK;

    result = investmentService.performance(invNo, request, httpServletRequest);
    resultMap = result.getBody();

    if((int)resultMap.get("code") == 401){
      httpStatus = HttpStatus.UNAUTHORIZED;
    }else if((int)resultMap.get("code") == 50){
      httpStatus = HttpStatus.NOT_FOUND;
    }

    if("2".equals(version)){
      resultMap = changeCodeIntToString(resultMap);
    }

    return new ResponseEntity<Map>(resultMap, httpStatus);
  }
}