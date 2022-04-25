package com.nsi.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.nsi.domain.core.GlobalParameter;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.TransactionDocument;
import com.nsi.domain.core.User;
import com.nsi.domain.core.UtProductFundPrices;
import com.nsi.domain.core.UtTransactions;
import com.nsi.enumeration.TransactionStatusEnumeration;
import com.nsi.repositories.core.CustomerDocumentRepository;
import com.nsi.repositories.core.DocumentTypeRepository;
import com.nsi.repositories.core.GlobalParameterRepository;
import com.nsi.repositories.core.InvestmentAccountsRepository;
import com.nsi.repositories.core.KycRepository;
import com.nsi.repositories.core.TransactionDocumentRepository;
import com.nsi.repositories.core.UserRepository;
import com.nsi.repositories.core.UtProductFundPricesRepository;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.services.AgentService;
import com.nsi.services.ChannelService;
import com.nsi.services.EmailService;
import com.nsi.services.GlobalService;
import com.nsi.services.OtpService;
import com.nsi.services.TransactionService;
import com.nsi.services.UtilService;
import com.nsi.util.ConstantUtil;
import com.nsi.util.DateTimeUtil;
import com.nsi.util.ValidateUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import org.json.JSONException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/transaction")
public class TransactionController extends BaseController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    GlobalService globalService;
    @Autowired
    UtTransactionsRepository utTransactionsRepository;
    @Autowired
    TransactionService transactionService;
    @Autowired
    KycRepository kycRepository;
    @Autowired
    AgentService agentService;
    @Autowired
    UtilService utilService;
    @Autowired
    ChannelService channelService;
    @Autowired
    GlobalParameterRepository globalParameterRepository;
    @Autowired
    DocumentTypeRepository documentTypeRepository;
    @Autowired
    CustomerDocumentRepository customerDocumentRepository;
    @Autowired
    TransactionDocumentRepository transactionDocumentRepository;
    @Autowired
    InvestmentAccountsRepository investmentAccountsRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    OtpService otpService;
    
    @Autowired
    private UtProductFundPricesRepository utProductFundPricesRepository;

    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/check_order", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> checkOrder(HttpServletRequest request, @RequestBody Map map) {//,@RequestParam("token") String token
        String version = request.getHeader("version");
        User user = null;
        Map<String, Object> resultMap = new HashMap<>();
        if (map.get("order_number") == null) {
            if (map.get("channel_order_id") == null) {
                resultMap.put("code", 10);
                resultMap.put("info", "missing parameter request order_number and channel_order_id");

                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                return new ResponseEntity<>(resultMap, HttpStatus.OK);
            }
        }
        if (!map.get("token").equals("")) {
            //TODO: Cek token
            Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), request.getHeader("X-FORWARDED-FOR") == null
                    ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR"));
            if (!tokenMap.get("code").equals(1)) {
                resultMap = tokenMap;

                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                return new ResponseEntity<>(resultMap, HttpStatus.OK);
            } else {
                user = (User) tokenMap.get("user");

                if (user != null) {
                    Kyc kyc = kycRepository.findByAccount(user);
                    List<Map<String, Object>> maps = new ArrayList<>();
                    List<UtTransactions> transactions;
                    String optionalResponseMessage;
                    try {
                        transactions = utTransactionsRepository.findAllByOrderNoAndKycId(String.valueOf(map.get("order_number")), kyc);
                        optionalResponseMessage = "order number '" + map.get("order_number") + "'.";
                    } catch (Exception ignored) {
                        transactions = utTransactionsRepository.findAllByChannelOrderIdAndKycId(user.getAgent().getCode() + String.valueOf(map.get("channel_order_id")), kyc);
                        optionalResponseMessage = "channel order id '" + map.get("channel_order_id") + "'.";
                    }
                    for (UtTransactions trx : transactions) {
                        Map<String, Object> trxMap = new HashMap<>();
                        trxMap.put("updated_on", trx.getUpdatedDate());
                        trxMap.put("type", trx.getTransactionType().getTrxName());
                        trxMap.put("unit", trx.getOrderUnit());
                        Date clearDate = DateTimeUtil.clearTime(trx.getPriceDate());
                        UtProductFundPrices utProductFundPrices = 
                        		utProductFundPricesRepository.findByUtProductsAndPriceDate(trx.getProductId(), clearDate);
                        if(utProductFundPrices != null) {
                        	trxMap.put("nav", utProductFundPrices.getBidPrice());
                        	trxMap.put("nav_date", utProductFundPrices.getPriceDate());                        	
                        }
                        if (trx.getTrxStatus().equals(TransactionStatusEnumeration.ORDERED.getKey())) {
                            trxMap.put("status", TransactionStatusEnumeration.ORDERED.getStatus());
                        } else if (trx.getTrxStatus().equals(TransactionStatusEnumeration.ALLOCATED.getKey())) {
                            trxMap.put("status", TransactionStatusEnumeration.ALLOCATED.getStatus());
                        } else if (trx.getTrxStatus().equals(TransactionStatusEnumeration.CANCELED.getKey())) {
                            trxMap.put("status", TransactionStatusEnumeration.CANCELED.getStatus());
                        } else if (trx.getTrxStatus().equals(TransactionStatusEnumeration.SETTLED.getKey())) {
                            trxMap.put("status", TransactionStatusEnumeration.SETTLED.getStatus());
                        }

                        TransactionDocument transactionDocument = transactionDocumentRepository.findByOrderNo(String.valueOf(map.get("order_number")));
                        if(transactionDocument != null){
                            trxMap.put("payment_image", transactionDocument.getCustomerDocument().getFileKey());
                        }


                        maps.add(trxMap);
                    }
                    if (maps.size() > 0) {
                        resultMap.put("code", 0);
                        resultMap.put("info", "Order successfully loaded");
                        resultMap.put("data", maps);
                    } else {
                        resultMap.put("code", 1);
                        resultMap.put("info", "Couldn't find transaction with " + optionalResponseMessage);
                    }
                }
            }
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /*@RequestMapping(value = "/list", method = RequestMethod.POST)
	public ResponseEntity<Map> transactionList(@RequestBody Map map, @RequestParam("token") String token) {
		Map resultMap = new HashMap<>();
		// Map checkToken = globalService.checkToken(token, request);
		// if(checkToken.get("code").equals(100)){
		// return new ResponseEntity<Map>(checkToken, HttpStatus.OK);
		// }
		//
		// User user = (User) checkToken.get("data");

		Kyc kyc = kycRepository.getOne(308L);

		Map transactionHistories = transactionService.getTransactionLists(kyc, map);
		return new ResponseEntity<Map>(transactionHistories, HttpStatus.OK);
	}*/
    @RequestMapping(value = "/subscribe_wallet", method = RequestMethod.POST)
    public ResponseEntity<Map> subscribePackageWallet(@RequestBody Map map, HttpServletRequest request) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), getIpAddress(request));
        if (!tokenMap.get("code").equals(1)) {
            resultMap = tokenMap;

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        User user = (User) tokenMap.get("user");

        // TODO: Cek status customer, VER/Bukan
        if (user.getUserStatus() == null || !user.getUserStatus().equals("VER")) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid Access: User Not Verified");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        // TODO: Cek signature
        if (user.getAgent() == null || user.getCustomerKey() == null) {
            resultMap.put("code", 10);
            resultMap.put("info", "incomplete data");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Boolean checkSignature = agentService.checkSignatureCustomer(user, String.valueOf(map.get("signature")));
        if (!checkSignature) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid access");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Kyc kyc = kycRepository.findByAccount(user);

        if (user.getAgent().getNeedTokenTrx()) {
            Map result = checkToken(map, kyc);
            if (result != null) {

                if("2".equals(version)){
                    result = changeCodeIntToString(result);
                }

                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        resultMap = transactionService.subscribeOrderByWallet((List<Map>) map.get("order"), kyc);

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/subscribe_tcash", method = RequestMethod.POST)
    public ResponseEntity<Map> subscribePackageByTcash(@RequestBody Map map, HttpServletRequest request) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), getIpAddress(request));
        if (!tokenMap.get("code").equals(1)) {
            resultMap = tokenMap;

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        User user = (User) tokenMap.get("user");

        // TODO: Cek status customer, VER/Bukan
        if (user.getUserStatus() == null || !user.getUserStatus().equals("VER")) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid Access: User Not Verified");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        // TODO: Cek signature
        if (user.getAgent() == null || user.getCustomerKey() == null) {
            resultMap.put("code", 10);
            resultMap.put("info", "incomplete data");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Boolean checkSignature = agentService.checkSignatureCustomer(user, String.valueOf(map.get("signature")));
        if (!checkSignature) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid access");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        //Cek permission
//        if (!utilService.checkAccessPermission(user.getAgent())) {
//            resultMap.put("code", 12);
//            resultMap.put("info", "Invalid access, you dont have permission");
//            return new ResponseEntity<>(resultMap, HttpStatus.OK);
//        }
        //TO DO : initial statusPayment
        Kyc kyc = kycRepository.findByAccount(user);

        if (user.getAgent().getNeedTokenTrx()) {
            Map result = checkToken(map, kyc);
            if (result != null) {

                if("2".equals(version)){
                    result = changeCodeIntToString(result);
                }

                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        resultMap = transactionService.subscribeOrderByTCash((List<Map>) map.get("order"), kyc);

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/subscribe_transfer", method = RequestMethod.POST)
    public ResponseEntity<Map> subscribePackageByTransfer(@RequestBody Map map, HttpServletRequest request) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), getIpAddress(request));
        if (!tokenMap.get("code").equals(1)) {
            resultMap = tokenMap;

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        User user = (User) tokenMap.get("user");

        // TODO: Cek status customer, VER/Bukan
        if (user.getUserStatus() == null || !user.getUserStatus().equals("VER")) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid Access: User Not Verified");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        // TODO: Cek signature
        if (user.getAgent() == null || user.getCustomerKey() == null) {
            resultMap.put("code", 10);
            resultMap.put("info", "incomplete data");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Boolean checkSignature = agentService.checkSignatureCustomer(user, String.valueOf(map.get("signature")));
        if (!checkSignature) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid access");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        //Cek permission
//        if (!utilService.checkAccessPermission(user.getAgent())) {
//            resultMap.put("code", 12);
//            resultMap.put("info", "Invalid access, you dont have permission");
//            return new ResponseEntity<>(resultMap, HttpStatus.OK);
//        }
        //TO DO : initial statusPayment
        Kyc kyc = kycRepository.findByAccount(user);

        if (user.getAgent().getNeedTokenTrx()) {
            Map result = checkToken(map, kyc);
            if (result != null) {
                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        List<Map> lMaps = (List<Map>) map.get("order");
        for (Map order: lMaps){
            if (order.get("payment").toString().equalsIgnoreCase("finpay")){
                resultMap = transactionService.subscribeOrderByFinpay(lMaps, kyc);
            }

            if (order.get("payment").toString().equalsIgnoreCase("transfer")){
                resultMap = transactionService.subscribeOrderByTransfer(lMaps, kyc);
            }
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public ResponseEntity<Map> subscribePackage(@RequestBody Map map, HttpServletRequest request) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), getIpAddress(request));
        if (!tokenMap.get("code").equals(1)) {
            resultMap = tokenMap;

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        User user = (User) tokenMap.get("user");

        // TODO: Cek status customer, VER/Bukan
        if (user.getUserStatus() == null || !user.getUserStatus().equals("VER")) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid Access: User Not Verified");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        // TODO: Cek signature
        if (user.getAgent() == null || user.getCustomerKey() == null) {
            resultMap.put("code", 10);
            resultMap.put("info", "incomplete data");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Boolean checkSignature = agentService.checkSignatureCustomer(user, String.valueOf(map.get("signature")));
        if (!checkSignature) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid access");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        //Cek permission
//        if (!utilService.checkAccessPermission(user.getAgent())) {
//            resultMap.put("code", 12);
//            resultMap.put("info", "Invalid access, you dont have permission");
//            return new ResponseEntity<>(resultMap, HttpStatus.OK);
//        }
        //TO DO : initial statusPayment
        String statusPayment = "STL";

        if (map.get("status_payment") != null) {
            if (!map.get("status_payment").toString().equalsIgnoreCase("ORD") && !map.get("status_payment").toString().equalsIgnoreCase("STL")) {
                resultMap.put("code", 10);
                resultMap.put("info", "incomplete data : status_payment");

                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

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
                if("2".equals(version)){
                    result = changeCodeIntToString(result);
                }

                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        resultMap = transactionService.subscribeOrder((List<Map>) map.get("order"), kyc, statusPayment);

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/topup_wallet", method = RequestMethod.POST)
    public ResponseEntity<Map> TopupPackageWallet(@RequestBody Map map, HttpServletRequest request) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();

        Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), getIpAddress(request));
        if (!tokenMap.get("code").equals(1)) {
            resultMap = tokenMap;

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        User user = (User) tokenMap.get("user");
        // TODO: Cek status customer, VER/Bukan
        if (user.getUserStatus() != null && !user.getUserStatus().equalsIgnoreCase("VER")) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid Access: User Not Verified");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        // TODO: Cek signature
        if (user.getAgent() == null || user.getCustomerKey() == null) {
            resultMap.put("code", 10);
            resultMap.put("info", "incomplete data");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Boolean checkSignature = agentService.checkSignatureCustomer(user, String.valueOf(map.get("signature")));
        if (!checkSignature) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid access");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Kyc kyc = kycRepository.findByAccount(user);

        if (user.getAgent().getNeedTokenTrx()) {
            Map result = checkToken(map, kyc);
            if (result != null) {
                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        resultMap = transactionService.topupOrderByWallet((List<Map>) map.get("order"), kyc);
        if (resultMap != null && resultMap.get("code").equals(0)) {
            try {
                GlobalParameter gp = globalParameterRepository.findByName("GROOVY_API_URL");
                logger.info("gp : " + gp);
                if (gp != null) {
                    List<JSONObject> listData = new ArrayList<>();
                    JSONObject registerJSON = new JSONObject();
                    registerJSON.put("customerCIF", kyc.getPortalcif());
                    registerJSON.put("signature", channelService.generateHashSHA256(user.getCustomerKey()));
                    registerJSON.put("data", resultMap.get("data"));
                    listData.add(registerJSON);

                    RestTemplate rest = new RestTemplate();
                    String response = rest.postForObject(gp.getValue() + "/email/sendOrderPaidAgent", listData.toString(), String.class);
                    logger.info("response core invisee= " + response);
                }
            } catch (JSONException | RestClientException e) {
            	logger.error("[FATAL] send email failed : " ,e.getMessage(), e);
            }
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/topup_transfer", method = RequestMethod.POST)
    public ResponseEntity<Map> TopupPackageByTransfer(@RequestBody Map map, HttpServletRequest request) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();

        Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), getIpAddress(request));
        if (!tokenMap.get("code").equals(1)) {
            resultMap = tokenMap;

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        User user = (User) tokenMap.get("user");

        // TODO: Cek status customer, VER/Bukan
        if (user.getUserStatus() != null && !user.getUserStatus().equalsIgnoreCase("VER")) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid Access: User Not Verified");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        // TODO: Cek signature
        if (user.getAgent() == null || user.getCustomerKey() == null) {
            resultMap.put("code", 10);
            resultMap.put("info", "incomplete data");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Boolean checkSignature = agentService.checkSignatureCustomer(user, String.valueOf(map.get("signature")));
        if (!checkSignature) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid access");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Kyc kyc = kycRepository.findByAccount(user);

        if (user.getAgent().getNeedTokenTrx()) {
            Map result = checkToken(map, kyc);
            if (result != null) {
                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        List<Map> lMaps = (List<Map>) map.get("order");
        for (Map order: lMaps){
            if (order.get("payment").toString().equalsIgnoreCase("finpay")){
                resultMap = transactionService.topupOrderByFinpay(lMaps, kyc);
            }

            if (order.get("payment").toString().equalsIgnoreCase("transfer")){
                resultMap = transactionService.topupOrderByTransfer(lMaps, kyc);
            }
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/topup_tcash", method = RequestMethod.POST)
    public ResponseEntity<Map> TopupPackageByTcash(@RequestBody Map map, HttpServletRequest request) {
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();

        Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), getIpAddress(request));
        if (!tokenMap.get("code").equals(1)) {
            resultMap = tokenMap;

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        User user = (User) tokenMap.get("user");
        // TODO: Cek status customer, VER/Bukan
        if (user.getUserStatus() != null && !user.getUserStatus().equalsIgnoreCase("VER")) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid Access: User Not Verified");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        // TODO: Cek signature
        if (user.getAgent() == null || user.getCustomerKey() == null) {
            resultMap.put("code", 10);
            resultMap.put("info", "incomplete data");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Boolean checkSignature = agentService.checkSignatureCustomer(user, String.valueOf(map.get("signature")));
        if (!checkSignature) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid access");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Kyc kyc = kycRepository.findByAccount(user);

        if (user.getAgent().getNeedTokenTrx()) {
            Map result = checkToken(map, kyc);
            if (result != null) {
                if("2".equals(version)){
                    result = changeCodeIntToString(result);
                }
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        resultMap = transactionService.topupOrderByTCash((List<Map>) map.get("order"), kyc);

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/topup", method = RequestMethod.POST)
    public ResponseEntity<Map> TopupPackage(@RequestBody Map map, HttpServletRequest request) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();

        Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR"));
        if (!tokenMap.get("code").equals(1)) {
            resultMap = tokenMap;

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        User user = (User) tokenMap.get("user");

        // TODO: Cek status customer, VER/Bukan
        if (user.getUserStatus() != null && !user.getUserStatus().equalsIgnoreCase("VER")) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid Access: User Not Verified");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        // TODO: Cek signature
        if (user.getAgent() == null || user.getCustomerKey() == null) {
            resultMap.put("code", 10);
            resultMap.put("info", "incomplete data");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Boolean checkSignature = agentService.checkSignatureCustomer(user, String.valueOf(map.get("signature")));
        if (!checkSignature) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid access");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        //TO DO : initial statusPayment
        String statusPayment = "STL";

        if (map.get("status_payment") != null) {
            if (!map.get("status_payment").toString().equalsIgnoreCase("ORD") && !map.get("status_payment").toString().equalsIgnoreCase("STL")) {
                resultMap.put("code", 10);
                resultMap.put("info", "incomplete data : status_payment");

                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                return new ResponseEntity<>(resultMap, HttpStatus.OK);
            } else {
                statusPayment = map.get("status_payment").toString().toUpperCase();
            }
        }

        Kyc kyc = kycRepository.findByAccount(user);

        if (user.getAgent().getNeedTokenTrx()) {
            Map result = checkToken(map, kyc);
            if (result != null) {
                if("2".equals(version)){
                    result = changeCodeIntToString(result);
                }

                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        resultMap = transactionService.topupOrder((List<Map>) map.get("order"), kyc, statusPayment);
        if (resultMap != null && resultMap.get("code").equals(0)) {
            if(kyc.getAccount().getAgent().getEmailCustom()){
                //do not sent email
            }else{
                try {
                    GlobalParameter gp = globalParameterRepository.findByName("GROOVY_API_URL");
                    logger.info("gp : " + gp);
                    if (gp != null) {
                        List<JSONObject> listData = new ArrayList<>();
                        JSONObject registerJSON = new JSONObject();
                        registerJSON.put("customerCIF", kyc.getPortalcif());
                        registerJSON.put("signature", channelService.generateHashSHA256(user.getCustomerKey()));
                        registerJSON.put("data", resultMap.get("data"));
                        listData.add(registerJSON);

                        RestTemplate rest = new RestTemplate();
                        String response = rest.postForObject(gp.getValue() + "/email/sendOrderPaidAgent", listData.toString(), String.class);
                        logger.info("response core invisee= " + response);
                    }
                } catch (JSONException | RestClientException e) {
                	logger.error("[FATAL] send email failed : " ,e.getMessage(), e);
                }
            }
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/redeem", method = RequestMethod.POST)
    public ResponseEntity<Map> redeem(@RequestBody Map map, HttpServletRequest request) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        String version = request.getHeader("version");
        Map resultMap;
        try {
            resultMap = ValidateUtil.validateAPI("transaction/redeem.json", map);
            if (resultMap == null) {
                boolean noErr = true;
                User user = null;
                Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), getIpAddress(request));
                if (!tokenMap.get("code").equals(1)) {
                    resultMap = tokenMap;
                    noErr = false;
                } else {
                    user = (User) tokenMap.get("user");
                }

                if (user != null && !user.getUserStatus().equalsIgnoreCase("VER")) {
                    resultMap = errorResponse(12, "Redeem", "User Not Verified or Pending");
                    noErr = false;
                }

                if (noErr && !agentService.checkSignatureCustomer(user, String.valueOf(map.get("signature")))) {
                    resultMap = errorResponse(12, "Redeem", "signature not valid");
                    noErr = false;
                }

                if (noErr && (user.getAgent() == null || !utilService.checkAccessPermission(user.getAgent()))) {
                    resultMap = errorResponse(12, "Redeem", "Agent can't access this API");
                    noErr = false;
                }

                if (noErr) {
                    Kyc kyc = kycRepository.findByAccount(user);

                    if (kyc.getAccount().getAgent().getNeedTokenTrx() || "2".equals(version)) {
                        
                        Map result = checkToken(map, kyc);
                        if (result != null) {
                            if("2".equals(version)){
                                result = changeCodeIntToString(result);
                            }

                            return new ResponseEntity<>(result, HttpStatus.OK);
                        }
                    }

                    resultMap = transactionService.redeemOrder((List<Map>) map.get("order"), kyc);
                }
            }
        } catch (IOException e) {
        	logger.error("[FATAL]" ,e);
            resultMap = errorResponse(99, "redeem", null);
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/rangeOfPartial", method = RequestMethod.POST)
    public ResponseEntity<Map> rangeOfPartial(@RequestBody Map map, HttpServletRequest request) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        User user = null;
        Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), request.getHeader("X-FORWARDED-FOR") == null
                ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR"));
        if (!tokenMap.get("code").equals(1)) {
            resultMap = tokenMap;

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        } else {
            user = (User) tokenMap.get("user");
        }

        // TODO: Cek status customer, VER/Bukan
        if (!user.getUserStatus().equalsIgnoreCase("VER")) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid Access");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        Kyc kyc = kycRepository.findByAccount(user);
        //		Kyc kyc = kycRepository.findByEmail("invisee.cus15@mailinator.com");

        resultMap = transactionService.getRangeOfPartialByInvestment(String.valueOf(map.get("invNo")), kyc);
        //		resultMap = transactionService.checkRedemptionTransaction(String.valueOf(map.get("invNo")));

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/document_update", method = RequestMethod.POST)
    public ResponseEntity<Map> documentUpdate(HttpServletRequest request, @RequestParam("uploadfile") MultipartFile uploadfile, @RequestParam("token") String token, @RequestParam("order_no") String orderNo) {
        String version = request.getHeader("version");
        Map resultMap;
        try {
            logger.info("document_update");
            if (!isExistingDataAndStringValue(token)) {
                resultMap = errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "token", null);
            } else if (!isExistingDataAndStringValue(orderNo)) {
                resultMap = errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "order_no", null);
            } else if (!isExistingData(uploadfile)) {
                resultMap = errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "uploadfile", null);
            } else {
                Map checkToken = utilService.checkToken(token, getIpAddress(request));
                if (!checkToken.get("code").equals(1)) {
                    resultMap = checkToken;
                } else {
                    User user = (User) checkToken.get("user");
                    
                    resultMap = transactionService.uploadDocument(user, uploadfile, orderNo);
                    
                    if (resultMap.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
                        Map mapData = (Map) resultMap.get(ConstantUtil.DATA);
                        TransactionDocument doc = (TransactionDocument) mapData.get(ConstantUtil.DOCUMENT);

                        Map dataMap = new LinkedHashMap<>();
                        dataMap.put("code", 0);
                        dataMap.put("order_no", doc.getOrderNo());
                        dataMap.put("info", "successfully loaded : upload document transaction");

                        resultMap = dataMap;
                    } else {
                        resultMap = (Map) resultMap.get(ConstantUtil.DATA);
                    }
                }
            }
        } catch (Exception e) {
        	logger.error("[FATAL]" ,e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "upload document transaction", null);
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseEntity<Map> list(HttpServletRequest request, @RequestBody Map map) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        String version = request.getHeader("version");
        Map resultMap;
        try {
            Map checkToken = utilService.checkToken((String) map.get("token"),
                    request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr()
                    : request.getHeader("X-FORWARDED-FOR"));
            if (Integer.parseInt(checkToken.get("code").toString()) != 1) {
                resultMap = checkToken;
            } else {
                resultMap = transactionService.transactionList(map, (User) checkToken.get("user"));
            }
        } catch (NumberFormatException e) {
        	logger.error("[FATAL]" ,e);
            resultMap = new HashMap();
            resultMap.put("code", 99);
            resultMap.put("info", "General error");

        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

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
    
    @RequestMapping(value = "/tcash_success", method = RequestMethod.GET)
    public ResponseEntity<Map> executeTcashSuccess(HttpServletRequest request) {
        String orders = request.getParameter("orders");
        String version = request.getHeader("version");
        String[] orderx = orders.split(",");
        Map resultMap = executeTcashTrx(orderx, true);

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/tcash_failed", method = RequestMethod.GET)
    public ResponseEntity<Map> executeTcashFailed(HttpServletRequest request) {
        String orders = request.getParameter("orders");
        String version = request.getHeader("version");
        String[] orderx = orders.split(",");
        Map resultMap = executeTcashTrx(orderx, false);

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    private Map executeTcashTrx(String[] orders, boolean success) {
        try {
            return transactionService.executeOrderByTCash(Arrays.asList(orders), success);
        } catch (Exception e) {
        	logger.error("[FATAL]" ,e);
            return errorResponse(99, "tcash transaction", orders);
        }
    }

    @PostMapping(value = "/check_risk")
    public ResponseEntity<Map> checkRiskProfile(@RequestBody Map map, HttpServletRequest request) {
        loggerHttp(request, ConstantUtil.REQUEST, map);

        Map resultMap;
        try {
            resultMap = ValidateUtil.validateAPI("transaction/check_risk.json", map);
            if (resultMap == null) {
                resultMap = utilService.checkToken(String.valueOf(map.get("token")), getIpAddress(request));
                if((int)resultMap.get("code") == 1){
                    Kyc kyc = kycRepository.findByAccount((User) resultMap.get("user"));
                    resultMap = transactionService.checkRisk((String) map.get("code"), kyc);
                }
            }
        } catch (IOException e) {
            logger.error("[FATAL]" ,e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "check_risk", null);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /*
    @RequestMapping(value = "/document_update", method = RequestMethod.POST)
    public ResponseEntity<Map> documentUpdate(HttpServletRequest request, @RequestParam("uploadfile") MultipartFile uploadfile, @RequestParam("token") String token, @RequestParam("order_no") String orderNo) {
        try {
            if(token == null) {
                Map docuMap = new HashMap<>();
                docuMap.put("code", 10);
                docuMap.put("info", "incomplete data : token");
                return new ResponseEntity<>(docuMap, HttpStatus.OK);
            } 
            
            if(orderNo == null) {
                Map docuMap = new HashMap<>();
                docuMap.put("code", 10);
                docuMap.put("info", "incomplete data : order_no");
                return new ResponseEntity<>(docuMap, HttpStatus.OK);
            } 
            
            if(uploadfile == null) {
                Map docuMap = new HashMap<>();
                docuMap.put("code", 10);
                docuMap.put("info", "incomplete data : uploadfile");
                return new ResponseEntity<>(docuMap, HttpStatus.OK);
            } 
            
            Map checkToken = utilService.checkToken(token, request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR"));
            if (!checkToken.get("code").equals(1)) {
                return new ResponseEntity<>(checkToken, HttpStatus.OK);
            }

            Map resultMap = checkToken;
            User user = (User) checkToken.get("user");
            Kyc kyc = kycRepository.findByAccount(user);
            
            System.out.println("kyc : " + kyc.getId());
            System.out.println("orderNo : " + orderNo);

            List<UtTransactions> list = utTransactionsRepository.findAllByOrderNoAndKycId(orderNo, kyc);
            
            System.out.println("kyc : " + kyc.getId());
            System.out.println("orderNo : " + orderNo);
            System.out.println("list : " + list);
            
            if(list == null || list.isEmpty()) {
                Map docuMap = new HashMap<>();
                docuMap.put("code", 11);
                docuMap.put("order_no", orderNo);
                docuMap.put("info", "order not found");
                return new ResponseEntity<>(docuMap, HttpStatus.OK);
            } 
                
            UtTransactions ut = list.get(0);
            if(!ut.getTrxStatus().equalsIgnoreCase("ORD")) {
                Map docuMap = new HashMap<>();
                docuMap.put("code", 12);
                docuMap.put("order_no", orderNo);
                docuMap.put("info", "not order status");
                return new ResponseEntity<>(docuMap, HttpStatus.OK);
            }
            
            String filename = uploadfile.getOriginalFilename();
            GlobalParameter globalParameter = globalParameterRepository.findByName("CUSTOMER_FILE_PATH");
            String directory = globalParameter.getValue();
            File dir = new File(directory);
            if(!dir.exists()) dir.mkdirs();
            
            String filepath = Paths.get(directory, System.currentTimeMillis() + "_" + filename).toString();
            Long fileSize = uploadfile.getSize();
            String contentType = uploadfile.getContentType();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            if (fileSize > 0) {
                TransactionDocument td = transactionDocumentRepository.findByOrderNo(orderNo);
                if (td != null) {
                    CustomerDocument cd = td.getCustomerDocument();
                    String fileLocation = cd.getFileLocation();
                    File file = new File(fileLocation);
                    if (file.exists()) {
                        file.delete();
                    }
                    uploadfile.transferTo(new File(cd.getFileLocation()));

                    Map docuMap = new HashMap<>();
                    docuMap.put("code", 0);
                    docuMap.put("order_no", orderNo);
                    docuMap.put("info", "upload document transaction success");
                    return new ResponseEntity<>(docuMap, HttpStatus.OK);

                } else {
                    CustomerDocument doc = new CustomerDocument();
                    doc.setUser(user);
                    doc.setFileLocation(filepath);
                    doc.setFileName(filename);
                    doc.setFileType(contentType);
                    doc.setFileSize(fileSize);
                    doc.setFileKey(UUID.randomUUID().toString());
                    doc.setDocumentType("CusTrans01");
                    doc.setSourceType(CustomerEnum._CUSTOMER.getName());
                    doc.setRowStatus(false);
                    doc.setCreatedBy(user.getUsername());
                    doc.setCreatedOn(new Date());
                    doc.setEndedOn(sdf.parse("9999-12-31"));
                    doc.setVersion(0);

                    uploadfile.transferTo(new File(doc.getFileLocation()));

                    doc = customerDocumentRepository.save(doc);

                    td = new TransactionDocument();
                    td.setCustomerDocument(doc);
                    td.setOrderNo(orderNo);

                    transactionDocumentRepository.save(td);

                    Map docuMap = new HashMap<>();
                    docuMap.put("code", 0);
                    docuMap.put("order_no", orderNo);
                    docuMap.put("info", "upload document transaction success");
                    return new ResponseEntity<>(docuMap, HttpStatus.OK);
                }
            }

            Map docuMap = new HashMap<>();
            docuMap.put("code", 10);
            docuMap.put("info", "uploadfile problem");
            return new ResponseEntity<>(docuMap, HttpStatus.OK);

        } catch (IOException | IllegalStateException | ParseException e) {
            e.printStackTrace();
            Map resultMap = new HashMap();
            resultMap.put("code", 99);
            resultMap.put("info", "General error");
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }
    }
     */
}
