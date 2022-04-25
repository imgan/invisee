package com.nsi.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import com.nsi.repositories.core.KycRepository;
import com.nsi.repositories.core.UserRepository;
import com.nsi.services.UtilService;
import com.nsi.services.ViseepayService;
import com.nsi.util.ConstantUtil;
import com.nsi.util.DateTimeUtil;
import com.nsi.util.ValidateUtil;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/micropayment")
public class MicroPaymentController extends BaseController {

    @Autowired
    KycRepository kycRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ViseepayService viseepayService;
    @Autowired
    UtilService utilService;

    @RequestMapping(value = "/balance", method = RequestMethod.POST)
    public ResponseEntity<Map> balance(HttpServletRequest request, @RequestBody Map map) {
        Map resultMap;
        try {
            loggerHttp(request, ConstantUtil.RESPONSE, map);
            resultMap = ValidateUtil.validateAPI("micropayment/balance.json", map);
            if (resultMap == null) {
                Map tokenMap = utilService.checkToken((String) map.get("token"), getIpAddress(request));
                if (Integer.parseInt(tokenMap.get("code").toString()) == 100) {
                    resultMap = tokenMap;
                } else {
                    User user = (User) tokenMap.get("user");
                    Kyc kyc = kycRepository.findByAccount(user);
                    resultMap = viseepayService.checkBalance(kyc);
                }
            }
        } catch (IOException e) {
        	logger.error("[FATAL]" ,e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "micropayment", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseEntity<Map> list(HttpServletRequest request, @RequestBody Map map) {
        Map resultMap;
        try {
            loggerHttp(request, ConstantUtil.RESPONSE, map);
            resultMap = ValidateUtil.validateAPI("micropayment/list.json", map);
            if (resultMap == null) {
                Map tokenMap = utilService.checkToken((String) map.get("token"), getIpAddress(request));
                if (Integer.parseInt(tokenMap.get("code").toString()) == 100) {
                    resultMap = tokenMap;
                } else {
                    Date to = DateTimeUtil.maxDateWeb(new Date());
                    if (map.get("to") != null) {
                        to = DateTimeUtil.maxDateWeb(DateTimeUtil.convertStringToDateCustomized(map.get("to").toString(), "yyyy-MM-dd"));
                    }
                    Date from = DateTimeUtil.minDateWeb(to);
                    if (map.get("from") != null) {
                        from = DateTimeUtil.minDateWeb(DateTimeUtil.convertStringToDateCustomized(map.get("from").toString(), "yyyy-MM-dd"));
                    }
                    
                    System.out.println("from : " + from);
                    System.out.println("to   : " + to);

                    if (from.after(to)) {
                        resultMap = errorResponse(14, "micropayment list", "from after to");
                    } else {
                        User user = (User) tokenMap.get("user");
                        Kyc kyc = kycRepository.findByAccount(user);
                        resultMap = viseepayService.historys(kyc, from, to);
                    }
                }
            }
        } catch (IOException e) {
        	logger.error("[FATAL]" ,e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "micropayment", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

}
