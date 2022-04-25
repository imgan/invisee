package com.nsi.services.impl;

import com.nsi.domain.core.FundEscrowAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.SubcriptionJobScheduller;
import com.nsi.domain.core.User;
import com.nsi.domain.core.UtTransactions;
import com.nsi.repositories.core.FundEscrowAccountRepository;
import com.nsi.repositories.core.GlobalParameterRepository;
import com.nsi.repositories.core.SubcriptionJobSchedullerRepository;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.util.DateTimeUtil;
import com.nsi.util.StringUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.nsi.services.ViseepayService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ViseepayServiceImpl extends BaseService implements ViseepayService {

    @Autowired
    private GlobalParameterRepository globalParameterRepository;

    @Autowired
    SubcriptionJobSchedullerRepository subcriptionJobSchedullerRepository;

    @Autowired
    UtTransactionsRepository utTransactionsRepository;

    @Autowired
    FundEscrowAccountRepository fundEscrowAccountRepository;

    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public boolean topUp(Kyc kyc, String trxId, Double amount) {
        boolean status = false;

        try {
            String url = globalParameterRepository.findByCategory("REDIRECT_URL_TO_PAYMENT").getValue();
            String disbursement = globalParameterRepository.findByCategory("DISBURSEMENT_SIGNATURE_MICROPAYMENT").getValue();
            String issuer = globalParameterRepository.findByName("ISSUER_CODE").getValue();
            String account = issuer + kyc.getPortalcif().substring(1);
            String signature = DigestUtils.sha1Hex(disbursement + "##" + StringUtil.doubleToString(amount) + "##" + trxId);

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url + "/cores/topup/nonscp")
                    .queryParam("account", account)
                    .queryParam("amount", StringUtil.doubleToString(amount))
                    .queryParam("issuer", issuer)
                    .queryParam("trxId", trxId)
                    .queryParam("signature", signature);

            logger.info("send request : " + builder);

            RestTemplate rest = new RestTemplate();
            String resp = rest.postForObject(builder.toString(), null, String.class);

            logger.info("get response : " + resp);

            JSONObject wall = new JSONObject(resp);

            if (!wall.getBoolean("failure")) {
                status = true;
            }
        } catch (RestClientException e) {
            logger.error(e);
        }
        return status;
    }

    @Override
    public Map checkBalance(Kyc kyc) {
        User user = kyc.getAccount();
        String url = globalParameterRepository.findByCategory("REDIRECT_URL_TO_PAYMENT").getValue() + "/pds/getbalance/investmentportal";

        JSONObject request = new JSONObject();
        request.put("account", kyc.getPortalcif().replace("C", "08"));
        request.put("pocket", user.getCardNumber());
        request.put("securityKey", user.getPasswordPayment());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Application-Token", "");

        logger.info("send request : " + url + ", body : " + request.toString());

        RestTemplate rest = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        ResponseEntity<String> response = rest.exchange(url, HttpMethod.POST, entity, String.class);

        logger.info("send response : " + response);

        Map resultMap = new HashMap();
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject wall = new JSONObject(response.getBody());
            if (wall.getBoolean("failure")) {
                return errorResponse(90, "inquiry viseepay", "Terjadi kesalahan pada Viseepay");
            }

            resultMap.put("account_id", user.getCardNumber());
            resultMap.put("balance", wall.get("balance"));
            return errorResponse(0, "inquiry viseepay", resultMap);
        } else {
            return errorResponse(90, "inquiry viseepay", "Terjadi kesalahan pada Viseepay");
        }
    }

    @Override
    public Map historys(Kyc kyc, Date from, Date to) {
        User user = kyc.getAccount();
        String url = globalParameterRepository.findByCategory("REDIRECT_URL_TO_PAYMENT").getValue() + "/pds/gethistory/investmentportal";

        from = DateTimeUtil.minDateWeb(from);
        to = DateTimeUtil.maxDateWeb(to);

        System.out.println("dateFrom : " + from);
        System.out.println("dateTo   : " + to);

        String dateFrom = DateTimeUtil.convertDateToStringCustomized(from, "MMM dd, yyyy hh:mm:ss a");
        String dateTo = DateTimeUtil.convertDateToStringCustomized(to, "MMM dd, yyyy hh:mm:ss a");

        System.out.println("dateFrom : " + dateFrom);
        System.out.println("dateTo   : " + dateTo);

        JSONObject request = new JSONObject();
        request.put("account", kyc.getPortalcif().replace("C", "08"));
        request.put("channel", "1");
        request.put("pocket", user.getCardNumber());
        request.put("securityKey", user.getPasswordPayment());
        request.put("dateFrom", dateFrom);
        request.put("dateTo", dateTo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Application-Token", "");

        logger.info("send request : " + url + ", body : " + request.toString());

        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> response = rest.exchange(url, HttpMethod.POST, entity, String.class);

        logger.info("send response : " + response);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject wall = new JSONObject(response.getBody());
            JSONArray objs = wall.getJSONArray("trxList");
            int size = objs.length();

            Map listTrx = new HashMap();

            for (int i = 0; i < size; i++) {
                JSONObject obj = (JSONObject) objs.get(i);

                String status = "";
                String transactionType = "";
                if (obj.getString("status").equalsIgnoreCase("APP")) {
                    status = "SUCCESS";
                } else if (obj.getString("status").equalsIgnoreCase("REJ")) {
                    status = "CANCELLED";
                } else if (obj.getString("status").equalsIgnoreCase("PND")) {
                    String orderNumber = obj.getString("transactionNumber");
                    SubcriptionJobScheduller job = subcriptionJobSchedullerRepository.findByOrderNo(orderNumber);
                    if (job.getStatus().equalsIgnoreCase("4") || job.getStatus().equalsIgnoreCase("3")) {
                        status = "CANCELLED";
                    }
                }

                if (obj.getString("transactionType").equalsIgnoreCase("ECTRX")) {
                    transactionType = "ORDER_PAYMENT";
                } else if (obj.getString("transactionType").equalsIgnoreCase("SCPPAY")) {
                    transactionType = "TOPUP_WALLET";
                } else if (obj.getString("transactionType").equalsIgnoreCase("MULTI_TRF")) {
                    transactionType = "STL_PAYMENT";
                }

                Map map = new HashMap();
                map.put("accountedAmount", obj.getDouble("accountedAmount"));
                map.put("transactionTimeStamp", DateTimeUtil.convertStringToStringFormaterCustomized(obj.getString("transactionTimeStamp"), "MMM dd, yyyy HH:mm:ss aa", "dd/MM/yyyy"));
                map.put("enteredAmount", obj.getDouble("enteredAmount"));
                map.put("status", status);
                map.put("transactionTypeCode", obj.getString("transactionTypeCode"));
                map.put("refNumber", obj.get("refNumber").toString());
                map.put("destAccount", obj.getString("destAccount"));
                if (!obj.isNull("destName")) {
                    map.put("destName", obj.getString("destName"));
                }
                map.put("transactionType", transactionType);
                map.put("time", DateTimeUtil.convertStringToStringFormaterCustomized(obj.getString("transactionTimeStamp"), "MMM dd, yyyy HH:mm:ss aa", "HH:mm"));
                if (!obj.isNull("sourceName")) {
                    map.put("sourceName", obj.getString("sourceName"));
                }
                map.put("feeAmount", obj.getDouble("feeAmount"));
                map.put("description", obj.getString("description"));
                map.put("transactionNumber", obj.getString("transactionNumber"));
                map.put("balanceAfterTrx", obj.getDouble("balanceAfterTrx"));

                listTrx.put(i + 1, map);
            }

            return errorResponse(0, "historis viseepay", listTrx);
        } else {
            return errorResponse(90, "historis viseepay", "Terjadi kesalahan pada Viseepay");
        }
    }

    @Override
    public User createAccount(Kyc kyc, String passwordPayment) {
        String email = globalParameterRepository.findByName("EMAIL_CONFIG_USERNAME_DEFAULT").getValue();
        String coCode = globalParameterRepository.findByName("ISSUER_CODE").getValue();
        String url = globalParameterRepository.findByName("REDIRECT_URL_TO_PAYMENT").getValue() + "/pds/register/advancedrestclient";
        User user = kyc.getAccount();

        JSONArray arr = new JSONArray();
        JSONObject customer = new JSONObject();
        String passDecode = user.getPasswordTemp();
        if(passDecode == null) passDecode = user.getUpdatedBy();
        
        String name = kyc.getFirstName();
        if (kyc.getMiddleName() != null && kyc.getMiddleName().trim().isEmpty()) {
            name += " " + kyc.getMiddleName();
        }
        if (kyc.getLastName() != null && kyc.getLastName().trim().isEmpty()) {
            name += " " + kyc.getLastName();
        }
        String gender = "ML";

//        if(kyc.getBirthDate() == null) kyc.setBirthDate(new Date());
        if(kyc.getGender() != null) gender = kyc.getGender();
        
        String passPayment = DigestUtils.sha1Hex(kyc.getPortalcif().replace("C", "08") + passDecode);
        
        customer.put("name", name);
        customer.put("phoneNumber", kyc.getPortalcif().replace("C", "08"));
        customer.put("email", email);
        customer.put("password", passDecode);
        customer.put("cardNumber", arr);
        customer.put("securityKey", passPayment);
        customer.put("gmcId", "");
        customer.put("birthday", "1950-01-01");
        customer.put("gender", gender.equalsIgnoreCase("ML") ? "M" : "F");
        customer.put("issuer", coCode);
        customer.put("channel", "1");
        customer.put("accountType", "1");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Application-Token", "2");
        HttpEntity<String> entity = new HttpEntity<>(customer.toString(), headers);

        logger.info("post request : " + entity);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        logger.info("get response : " + response);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject wall = new JSONObject(response.getBody());
            if (!wall.getBoolean("failure")) {
                JSONArray strs = wall.getJSONArray("cardNumber");
                user.setCardNumber(strs.getString(0));
                user.setPasswordTemp(passDecode);
                user.setPasswordPayment(passPayment);
                return user;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean trx(Kyc kyc, String orderNumber) {
        String url = globalParameterRepository.findByName("REDIRECT_URL_TO_PAYMENT").getValue();
        String urlInquiry = url + "/pds/inquirymultisplit/advanced";
        
        List<UtTransactions> trxs = utTransactionsRepository.findAllByOrderNoAndKycId(orderNumber, kyc);
        User user = kyc.getAccount();

        RestTemplate rest = new RestTemplate();
        List destList = new ArrayList();
        for (UtTransactions trx : trxs) {
            FundEscrowAccount fea = fundEscrowAccountRepository.findByFundPackages(trx.getFundPackageRef());

            Map cartMap = new HashMap();
            cartMap.put("destinationPocket", fea.getEscrowNumber());
            cartMap.put("amount", new BigDecimal(trx.getOrderAmount()));
            destList.add(cartMap);
        }

        JSONObject request = new JSONObject();
        request.put("account", kyc.getPortalcif().replace("C", "08"));
        request.put("pocket", user.getCardNumber());
        request.put("securityKey", user.getPasswordPayment());
        request.put("trxId", orderNumber);
        request.put("description", "order payment");
        request.put("destinationList", destList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Application-Token", "");
        
        logger.info("inquiry request : " + url + ", body : " + request.toString());
        
        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        ResponseEntity<String> response = rest.exchange(urlInquiry, HttpMethod.POST, entity, String.class);
        
        logger.info("inquiry response : " + response);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject wall = new JSONObject(response.getBody());
            if(wall.get("failure").equals(false)){
                return transferMerchantPay(user.getPasswordPayment(), orderNumber);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    private boolean transferMerchantPay(String passwordPayment, String orderNo){
        String issuer = globalParameterRepository.findByName("MICROPAYMENT_CO_CODE").getValue();
        String url = globalParameterRepository.findByName("REDIRECT_URL_TO_PAYMENT").getValue();
        String urlTransfer = url + "/pds/transfermultisplit/investmentportal";
        
        JSONObject request = new JSONObject();
        request.put("issuer", issuer);
        request.put("trxId", orderNo);
        request.put("securityKey", passwordPayment);
        
        RestTemplate rest = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Application-Token", "");
        
        logger.info("inquiry request : " + url + ", body : " + request.toString());
        
        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        ResponseEntity<String> response = rest.exchange(urlTransfer, HttpMethod.POST, entity, String.class);
    
        logger.info("inquiry response : " + response);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject wall = new JSONObject(response.getBody());
            if(wall.get("failure").equals(false)){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    } 
}
