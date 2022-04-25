package com.nsi.controllers;

import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import com.nsi.interceptor.NeedLogin;
import com.nsi.repositories.core.KycRepository;
import com.nsi.services.AgentService;
import com.nsi.services.SbnService;
import com.nsi.services.UtilService;
import com.nsi.util.ConstantUtil;
import com.nsi.util.ValidateUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/sbn")
public class SbnController extends BaseController{
    @Autowired
    SbnService sbnService;
    @Autowired
    UtilService utilService;
    @Autowired
    AgentService agentService;
    @Autowired
    KycRepository kycRepository;

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER)
    @PostMapping(value = "/addPemesanan")
    public ResponseEntity<Map> addPemesanan(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
        try {
            resultMap = ValidateUtil.validateAPI("sbn/addPemesanan.json", map);
            if (resultMap == null) {
                resultMap = sbnService.addPemesanan(user, Long.valueOf(map.get("id_seri").toString()), BigInteger.valueOf(Long.parseLong(map.get("nominal").toString())));
            }
        }catch (HttpStatusCodeException e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            try{
                JSONParser parser = new JSONParser();
                resultMap = (JSONObject) parser.parse(e.getResponseBodyAsString());
            }catch(ParseException ex){
                resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "addPemesanan", null);
            }
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "addPemesanan", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, true));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER)
    @PostMapping("/penjualan")
    public ResponseEntity<Map> redeem(HttpServletRequest request) {
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
        try{
            resultMap = ValidateUtil.validateAPI("sbn/redeem.json", map);
            if(resultMap == null){
                Kyc kyc = kycRepository.findByAccount(user);
                resultMap = sbnService.redeem(kyc, (String) map.get("kode_pemesanan"), BigInteger.valueOf(Long.parseLong(map.get("nominal").toString())));
            }
        }catch (HttpStatusCodeException e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            try{
                JSONParser parser = new JSONParser();
                resultMap = (JSONObject) parser.parse(e.getResponseBodyAsString());
            }catch(ParseException ex){
                resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "penjualan", null);
            }
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "penjualan", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, true));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER)
    @PostMapping("/getKuotaBySidAndSeri")
    public ResponseEntity<Map> getKuotaBySidAndSeri(HttpServletRequest request) {
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
        try{
            resultMap = ValidateUtil.validateAPI("sbn/getKuotaByDisAndSeri.json", map);
            if(resultMap == null){
                Kyc kyc = kycRepository.findByAccount(user);
                resultMap = sbnService.getKuotaBySidAndSeri((String) map.get("sid"), Long.valueOf(map.get("id_seri").toString()), kyc);
            }
        }catch (HttpStatusCodeException e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            try{
                JSONParser parser = new JSONParser();
                resultMap = (JSONObject) parser.parse(e.getResponseBodyAsString());
            }catch(ParseException ex){
                resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "getKuotaBySidAndSeri", null);
            }
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "getKuotaBySidAndSeri", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER)
    @PostMapping("/calculationRedeem")
    public ResponseEntity<Map> calculationRedeem(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
        try{
            resultMap = ValidateUtil.validateAPI("sbn/calculationRedeem.json", map);
            if(resultMap == null){
                Kyc kyc = kycRepository.findByAccount(user);
                resultMap = sbnService.calculateRedemption((String) map.get("kode_pemesanan"), kyc);
            }
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "calculationRedeem", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER)
    @PostMapping("/createFullRedeem")
    public ResponseEntity<Map> createFullRedeem(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
        try{
            resultMap = ValidateUtil.validateAPI("sbn/createFullRedeem.json", map);
            if(resultMap == null){
                Kyc kyc = kycRepository.findByAccount(user);
                resultMap = sbnService.createFullRedeem((String) map.get("kode_pemesanan"), kyc);
            }
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "createFullRedeem", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER)
    @PostMapping("/transaction/list")
    public ResponseEntity<Map> transactionList(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
        try{
            Kyc kyc = kycRepository.findByAccount(user);
            resultMap = sbnService.transactionList(kyc);
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "transaction list", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER)
    @PostMapping("/transaction/detail")
    public ResponseEntity<Map> transactionDetail(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
        try{
            resultMap = ValidateUtil.validateAPI("sbn/transactionDetail.json", map);
            if(resultMap == null){
                Kyc kyc = kycRepository.findByAccount(user);
                resultMap = sbnService.transactionDetail(kyc, (String) map.get("transactions_code"));
            }
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "transaction detail", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER)
    @PostMapping("/transaction/check_order")
    public ResponseEntity<Map> transactionCheckOrder(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
        try{
            resultMap = ValidateUtil.validateAPI("sbn/checkOrder.json", map);
            if(resultMap == null){
                Kyc kyc = kycRepository.findByAccount(user);
                resultMap = sbnService.transactionDetail(kyc, (String) map.get("transactions_code"));
            }
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "transaction check order", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.AGENT)
    @PostMapping("/product/list")
    public ResponseEntity<Map> productList(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        try{
            resultMap = sbnService.productList();
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "product list", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.AGENT)
    @PostMapping("/product/detail")
    public ResponseEntity<Map> productDetail(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        try{
            resultMap = ValidateUtil.validateAPI("sbn/productDetail.json", map);
            if(resultMap == null){
                resultMap = sbnService.productDetail(Long.valueOf(map.get("id").toString()));
            }
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "product detail", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER)
    @PostMapping("/investment/list")
    public ResponseEntity<Map> investmentList(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
        try{
            Kyc kyc = kycRepository.findByAccount(user);
            resultMap = sbnService.investmentList(kyc);
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "investment list", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER)
    @PostMapping("/investment/summary")
    public ResponseEntity<Map> investmentSummary(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
        try{
            Kyc kyc = kycRepository.findByAccount(user);
            resultMap = sbnService.investmentSummary(kyc);
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "investment summary", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER)
    @PostMapping("/investment/detail")
    public ResponseEntity<Map> investmentDetail(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
        try{
            resultMap = ValidateUtil.validateAPI("sbn/productDetail.json", map);
            if(resultMap == null){
                Kyc kyc = kycRepository.findByAccount(user);
                resultMap = sbnService.investmentDetail(kyc, Long.valueOf(map.get("id").toString()));
            }
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "investment detail", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER_WITHOUT_SIGNATURE)
    @PostMapping("/getBankList")
    public ResponseEntity<Map> getBankList(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        try{
            resultMap = sbnService.getBankList();
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "get bank list sbn", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }

    @NeedLogin(NeedLogin.UserLogin.CUSTOMER_WITHOUT_SIGNATURE)
    @PostMapping("/getBankWithPayment")
    public ResponseEntity<Map> getBankWithPayment(HttpServletRequest request){
        Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        try{
            resultMap = ValidateUtil.validateAPI("sbn/getBankWithPayment.json", map);
            if(resultMap == null){
                resultMap = sbnService.getBankWithPayment(Long.valueOf(map.get("id").toString()));
            }
        }catch (Exception e){
            logger.error("[FATAL] :" + e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "get bank with payment sbn", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, false));
    }
}