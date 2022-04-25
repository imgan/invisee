/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.services.impl;

import com.nsi.domain.core.ContentOTP;
import com.nsi.domain.core.Kyc;
import com.nsi.repositories.core.ContentOTPRepository;
import com.nsi.repositories.core.GlobalParameterRepository;
import com.nsi.services.OtpService;
import com.nsi.util.DateTimeUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author hatta.palino
 */
@Service
public class OtpServiceImpl implements OtpService {

  private Logger logger = Logger.getLogger(this.getClass());

  @Autowired
  private GlobalParameterRepository globalParameterRepository;

  @Autowired
  private ContentOTPRepository contentOTPRepository;

  @Override
  public String send(String channelId, String typeotp, String type, Kyc kyc, String agentCode) {
    String url = globalParameterRepository.findByName("REDIRECT_URL_TO_OTP").getValue() + "/send";
    String urlPortal = globalParameterRepository.findByCategoryAndName("URL_PORTAL", "PROD").getValue();
    String otpApiCannel = "" ;
    String channel = channelId.toUpperCase();

    if (channel != null) {
      String dest = kyc.getEmail();
      if (channel.equals("SMS")) {
        dest = kyc.getMobileNumber().replaceAll("-", "");
      }
    if(agentCode.equals("BLANJA")){
        otpApiCannel = globalParameterRepository.findByName("OTP_API_CHANNEL_BLANJA").getValue();
      } else {
        otpApiCannel = globalParameterRepository.findByName("OTP_API_CHANNEL").getValue();
      }
      String name;
      name = kyc.getFirstName() + " " + kyc.getLastName();
      String linkImagesEmail = urlPortal + "/images";

      JSONObject request = new JSONObject();
      request.put("channel", otpApiCannel);
      request.put("recipient", dest);
      request.put("trx_id", dest.concat("_").concat(otpApiCannel).concat("_").concat(channel).concat("_").concat(DateTimeUtil.convertDateToStringCustomized(new Date(), "yyMMdd_HHmm")));
      request.put("name", name);
      request.put("type", channel);
      request.put("template", type);
      Map map = new HashMap();
      ContentOTP contentOtp = contentOTPRepository.findByTypeotp(typeotp);
      map.put("header", contentOtp.getSubject());
      map.put("content", contentOtp.getContent().replace("#CUSTOMER_NAME#", name)
          .replace("#IMAGELINK#", linkImagesEmail));
      request.put("custom", map);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.add("X-Application-Token", "");

      logger.info("inquiry request : " + url + ", body : " + request.toString());

      HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);

      RestTemplate rest = new RestTemplate();
      ResponseEntity<String> response = rest.exchange(url, HttpMethod.POST, entity, String.class);

      logger.info("inquiry response : " + response);

      if (response.getStatusCode() == HttpStatus.OK) {
        JSONObject wall = new JSONObject(response.getBody());
        if (wall.get("code").equals(0)) {
          JSONObject data = wall.getJSONObject("data");
          return data.getString("stan");
        }
      }
    }
    return null;
  }

  @Override
  public boolean validate(String channelId, Kyc kyc, String keyOtp, String valueOtp) {
    String url = globalParameterRepository.findByName("OTP_API_URL").getValue() + "/valid";
    String ch = globalParameterRepository.findByName("OTP_API_CHANNEL").getValue();

    String channel = null;
    String dest = null;

    if (channelId.equalsIgnoreCase("EMAIL")) {
      channel = "EMAIL";
      dest = kyc.getEmail();
    } else if (channelId.equalsIgnoreCase("SMS")) {
      channel = "SMS";
      dest = kyc.getMobileNumber().replaceAll("-", "");
    }

    if (channel != null) {

      JSONObject request = new JSONObject();
      request.put("channel", ch);
      request.put("stan", keyOtp);
      request.put("token", valueOtp);
      request.put("trx_id", dest.concat("_").concat(ch).concat("_").concat(channel).concat("_").concat(DateTimeUtil.convertDateToStringCustomized(new Date(), "yyMMdd_HHmm")));
      request.put("type", channel);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.add("X-Application-Token", "");

      logger.info("inquiry request : " + url + ", body : " + request.toString());

      HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);

      RestTemplate rest = new RestTemplate();
      ResponseEntity<String> response = rest.exchange(url, HttpMethod.POST, entity, String.class);

      logger.info("inquiry response : " + response);

      if (response.getStatusCode() == HttpStatus.OK) {
        JSONObject wall = new JSONObject(response.getBody());
        return (wall.get("code").equals(0));
      }
    }
    return false;
  }

}
