package com.nsi.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.nsi.domain.core.Kyc;
import com.nsi.services.AgentService;
import com.nsi.services.OtpService;
import com.nsi.util.ConstantUtil;
import com.nsi.util.ValidateUtil;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/otp")
public class OtpController extends BaseController {

  @Autowired
  OtpService otpService;
  @Autowired
  AgentService agentService;

  @RequestMapping(value = "/send")
  public ResponseEntity<Map> send(HttpServletRequest request, @RequestBody Map map) {
    loggerHttp(request, ConstantUtil.REQUEST, map);
    String version = request.getHeader("version");
    Map resultMap;
    try {
      resultMap = ValidateUtil.validateAPI("otp/send.json", map);
      if (resultMap == null) {
        if (!agentService
            .checkSignatureAgent(map.get("agent").toString(), map.get("signature").toString())) {
          resultMap = errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "Channel invalid", null);
        } else {
//------special requirement for blanja
          String firstName;
          String lastName;
          String typeotp;
          String agentCode = map.get("agent").toString();
          if (agentCode.equals("BLANJA")) {
            typeotp = "BLANJA".concat("_").concat(map.get("type_otp").toString());
            firstName = "Sahabat";
            lastName = "Investasi";
          } else {
           firstName = map.get("first_name").toString();
           lastName = map.get("last_name").toString();
           typeotp = map.get("type_otp").toString();
          }
          String email = map.get("email").toString();
          String phone = map.get("phone_number").toString();
          String channelOtp = map.get("channel_otp").toString();
          Kyc kyc = new Kyc();
          kyc.setFirstName(firstName);
          kyc.setLastName(lastName);
          kyc.setEmail(email);
          kyc.setMobileNumber(phone);

          String val = otpService.send(channelOtp, typeotp, "CUSTOM", kyc ,agentCode);
          if (val != null) {
            Map mapx = new HashMap();
            mapx.put("stan", val);
            mapx.put("type_otp", channelOtp);
            resultMap = errorResponse(0, "otp_send", mapx);
          } else {
            resultMap = errorResponse(99, "otp_send", null);
          }
        }
      }
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "otp_send", null);
    }

    if("2".equals(version)){
      resultMap = changeCodeIntToString(resultMap);
    }

    loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
    return new ResponseEntity<>(resultMap, HttpStatus.OK);
  }
}
