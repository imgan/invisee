package com.nsi.controllers.v2;

import com.nsi.controllers.BaseController;
import com.nsi.domain.core.*;
import com.nsi.repositories.core.*;
import com.nsi.services.*;
import com.nsi.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController("transactionV2")
@RequestMapping("/transaction/v2")
public class TransactionController extends BaseController {
    @Autowired
    TransactionService transactionService;
    @Autowired
    KycRepository kycRepository;
    @Autowired
    AgentService agentService;
    @Autowired
    UtilService utilService;
    @Autowired
    OtpService otpService;
    
    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public ResponseEntity<Map> subscribePackage(@RequestBody Map map, HttpServletRequest request) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap = new HashMap<>();
        Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), getIpAddress(request));
        if (!tokenMap.get("code").equals(1)) {
            resultMap = tokenMap;
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        User user = (User) tokenMap.get("user");
        if (user.getUserStatus() == null || !user.getUserStatus().equals("VER")) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid Access: User Not Verified");
            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        if (user.getAgent() == null || user.getCustomerKey() == null) {
            resultMap.put("code", 10);
            resultMap.put("info", "incomplete data");
            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Boolean checkSignature = agentService.checkSignatureCustomer(user, String.valueOf(map.get("signature")));
        if (!checkSignature) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid access");
            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }
        String statusPayment = "STL";

        if (map.get("status_payment") != null) {
            if (!map.get("status_payment").toString().equalsIgnoreCase("ORD") && !map.get("status_payment").toString().equalsIgnoreCase("STL")) {
                resultMap = errorResponse(11, "status_payment", null);
                loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
                return new ResponseEntity<>(resultMap, HttpStatus.OK);
            } else {
                statusPayment = map.get("status_payment").toString().toUpperCase();
            }
        }

        Kyc kyc = kycRepository.findByAccount(user);
        if (user.getAgent().getNeedTokenTrx()) {
            Map result = checkToken(map, kyc);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        resultMap = transactionService.subscribeOrTopupOrder((List<Map>) map.get("order"), kyc, statusPayment);
        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @SuppressWarnings("ALL")
    private Map checkToken(Map map, Kyc kyc) {
        if (map.get("type_otp") == null) {
            return errorResponse(10, "type_otp", null);
        }
        if (map.get("otp") == null) {
            return errorResponse(10, "otp", null);
        }
        if (map.get("stan") == null) {
            return errorResponse(10, "stan", null);
        }

        String channelOtp = map.get("type_otp").toString();
        String valueOtp = map.get("otp").toString();
        String stan = map.get("stan").toString();

        boolean val = otpService.validate(channelOtp, kyc, stan, valueOtp);
        if (!val) {
            return errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "token problem", "wrong token");
        } else {
            return null;
        }
    }
}
