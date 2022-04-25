package com.nsi.services.impl;


import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import com.nsi.domain.core.UtTransactions;
import com.nsi.repositories.core.GlobalParameterRepository;
import com.nsi.repositories.core.KycRepository;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.services.FinpayService;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FinpayServiceImpl extends BaseService implements FinpayService {
  @Autowired
  KycRepository kycRepository;
  @Autowired
  GlobalParameterRepository globalParameterRepository;
  @Autowired
  UtTransactionsRepository utTransactionsRepository;

  @Override
  public Map topUpFinpay(User user, String trxNo) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    Kyc kyc = kycRepository.findByAccount(user);
    UtTransactions trx = utTransactionsRepository.findByOrderNoAndKycId(trxNo, kyc);

    String merchantId = globalParameterRepository.findByCategoryAndName("BLANJAINVEST_FINPAY", "MERCHANT_ID").getValue();
    String merchantKey = globalParameterRepository.findByCategoryAndName("BLANJAINVEST_FINPAY","MERCHANT_KEY").getValue();
    String sofId = globalParameterRepository.findByCategoryAndName("BLANJAINVEST_FINPAY","SOF_ID").getValue();
    String sofType = globalParameterRepository.findByCategoryAndName("BLANJAINVEST_FINPAY", "SOF_TYPE").getValue();
    String apiToFinpay = globalParameterRepository.findByCategoryAndName("BLANJAINVEST_FINPAY", "API").getValue();
    String returnUrl = globalParameterRepository.findByCategoryAndName("BLANJAINVEST_FINPAY", "RETURN_URL").getValue();
    String timeOut  = globalParameterRepository.findByCategoryAndName("BLANJAINVEST_FINPAY", "TIME_OUT").getValue();
    String fee  = globalParameterRepository.findByCategoryAndName("BLANJAINVEST_FINPAY", "FEE").getValue();

    String msisdn = kyc.getMobileNumber();
    if (msisdn.contains("-")){
      msisdn = msisdn.replace("-","");
    }

    msisdn = msisdn.replaceAll("^(62)", "0");

    Map <String,String> req = new LinkedHashMap<>();
    req.put("add_info1", kyc.getFirstName() + " " + kyc.getMiddleName() + " " + kyc.getLastName());
    req.put("amount", String.valueOf(trx.getOrderAmount().intValue() + Integer.parseInt(fee)));
    req.put("cust_email", kyc.getEmail());
    req.put("cust_id", kyc.getId().toString());
    req.put("cust_msisdn", msisdn);
    req.put("cust_name", kyc.getFirstName() + " " + kyc.getMiddleName() + " " + kyc.getLastName());
//    req.put("failed_url", returnUrl + "/payment/validate?atTrxId="+ trx.getAtTrxNo() +"&paymentStatus="+ false +"");
    req.put("invoice", trxNo);
//    req.put("items", "A");
    req.put("merchant_id", merchantId);
    req.put("return_url", returnUrl + "/payment/validate?atTrxId="+ trx.getAtTrxNo() +"");
    req.put("sof_id", sofId);
    req.put("sof_type", sofType);
//    req.put("success_url", returnUrl + "/payment/validate?atTrxId="+ trx.getAtTrxNo() +"&paymentStatus="+ true +"");
    req.put("timeout", timeOut);
    req.put("trans_date", sdf.format(trx.getTrxDate()));

    logger.info("Request Finpay billing code -> " + req);

    String[] mer_signature_component = req.values().toArray(new String[0]);
    String mer_signature_string = "";
    for (int i = 0; i < req.size(); i++) {
      mer_signature_string = mer_signature_string + (mer_signature_component[i].toUpperCase() + "%").toUpperCase();
    }

    mer_signature_string += merchantKey;
    String mer_signature_hash = DigestUtils.sha256Hex(mer_signature_string);
    req.put("mer_signature", mer_signature_hash.trim());

    logger.info("String signature : " + mer_signature_string);
    logger.info("Hashed signature : " + mer_signature_hash);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    RestTemplate rest = new RestTemplate();
    HttpEntity<String> entity = new HttpEntity(req, headers);

    ResponseEntity<String> response = rest.exchange(apiToFinpay, HttpMethod.POST, entity, String.class);
    logger.info("Response -> " + response.getBody());
    JSONObject object = new JSONObject(response.getBody());

    Map result = new LinkedHashMap();
    if (object.getString("status_code").equals("00")){
      
      trx.setSettlementRefNo(object.getString("payment_code"));
      utTransactionsRepository.save(trx);

      result.put("code", object.getString("status_code"));
      result.put("status", object.getString("status_desc"));
      result.put("paymentCode", object.getString("payment_code"));
      result.put("paymentExpiredTime", timeOut);
      result.put("orderAmount", trx.getOrderAmount());
      result.put("trxFee", Integer.parseInt(fee));
    }else {
      result.put("code", object.getString("status_code"));
      result.put("status", object.getString("status_desc"));
      result.put("paymentCode", "");
      result.put("paymentExpiredTime", "");
      result.put("orderAmount", "");
      result.put("trxFee", "");
    }

    return result;
  }
}