package com.nsi.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsi.domain.core.Countries;
import com.nsi.domain.core.CronEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.GlobalParameter;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.LookupLine;
import com.nsi.domain.core.McwEmailScheduller;
import com.nsi.domain.core.SettlementAccounts;
import com.nsi.domain.core.UtTransactionType;
import com.nsi.domain.core.UtTransactions;
import com.nsi.repositories.core.CountriesRepository;
import com.nsi.repositories.core.CronEmailRepository;
import com.nsi.repositories.core.FundPackageProductsRepository;
import com.nsi.repositories.core.GlobalParameterRepository;
import com.nsi.repositories.core.InvestmentAccountsRepository;
import com.nsi.repositories.core.LookupHeaderRepository;
import com.nsi.repositories.core.LookupLineRepository;
import com.nsi.repositories.core.McwEmailSchedullerRepository;
import com.nsi.repositories.core.SettlementAccountsRepository;
import com.nsi.repositories.core.UtProductsSettlementRepository;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.services.ChannelService;
import com.nsi.services.EmailService;
import com.nsi.util.DateTimeUtil;
import com.nsi.util.StringUtil;
import com.nsi.util.WkhtmltoPdfUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailServiceImpl implements EmailService {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    GlobalParameterRepository globalParameterRepository;
    @Autowired
    UtTransactionsRepository utTransactionsRepository;
    @Autowired
    FundPackageProductsRepository fundPackageProductsRepository;
    @Autowired
    UtProductsSettlementRepository utProductsSettlementRepository;
    @Autowired
    ChannelService channelService;
    @Autowired
    InvestmentAccountsRepository investmentAccountsRepository;
    @Autowired
    SettlementAccountsRepository settlementAccountsRepository;
    @Autowired
    CronEmailRepository cronEmailRepository;
    @Autowired
    LookupLineRepository lookupLineRepository;
    @Autowired
    LookupHeaderRepository lookupHeaderRepository;
    @Autowired
    CountriesRepository countriesRepository;
    @Autowired
    McwEmailSchedullerRepository mcwEmailSchedullerRepository;

    private String sendRestTemplate(JSONObject data, List<String> tos, String template) {
        try {
            GlobalParameter gpLink = globalParameterRepository.findByName("EMAIL_API_URL");
            GlobalParameter gpChannel = globalParameterRepository.findByName("EMAIL_API_CHANNEL");
            GlobalParameter gpAgent = globalParameterRepository.findByName("EMAIL_API_AGENT");
            GlobalParameter gpBcc = globalParameterRepository.findByName("EMAIL_API_BCC");

            JSONObject sender = new JSONObject();
            sender.put("to", tos);

            if (gpBcc != null && gpBcc.getValue() != null && !gpBcc.getValue().isEmpty()) {
                sender.put("bcc", Arrays.asList(gpBcc.getValue().split(",")));
            }

            JSONObject body = new JSONObject();
            body.put("channel", gpChannel.getValue());
            body.put("agent", gpAgent.getValue());
            body.put("template", template);
            body.put("data", data);
            body.put("sender", sender);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

            RestTemplate rest = new RestTemplate();

            return rest.postForObject(gpLink.getValue(), entity, String.class);
        } catch (JSONException | RestClientException e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public boolean sendOrderTransaction(InvestmentAccounts investmentAccounts, String orderNo, FundPackages fundPackage, UtTransactionType trxType) {
        try {
            logger.info("set kyc from investment accounts");
            Kyc kyc = investmentAccounts.getKycs();

            logger.info("kyc : " + kyc.getId());
            if (kyc.getAccount().getAgent().getEmailCustom() == null) {
                logger.info("kalau misal email custom di table agent null");
                return sendNeedPayment(kyc, orderNo);
            }

            logger.info("hey im setting the variables");
            String api = "trx_order";
            Map map = new HashMap();
            map.put("portalCif", kyc.getPortalcif());
            map.put("type", trxType.getTrxName());
            map.put("orderNo", orderNo);

            logger.info("mapping the object : " + map);
            String json = new ObjectMapper().writeValueAsString(map);

            List<McwEmailScheduller> mcws = mcwEmailSchedullerRepository.findAllByStatusAndValueAndApiEmail(0, json, api);
            if (mcws == null || mcws.isEmpty()) {
                logger.info("if mcws null or empty");
                McwEmailScheduller mcw = new McwEmailScheduller();
                mcw.setApiEmail(api);
                mcw.setCreatedBy(kyc.getEmail());
                mcw.setDateCreated(new Date());
                mcw.setStatus(0);
                mcw.setValue(json);

                mcw = mcwEmailSchedullerRepository.save(mcw);
                return (mcw != null && mcw.getId() != null);
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.info(e);
            return false;
        }
    }

//    @Override
//    public boolean sendOrderTransaction(InvestmentAccounts investmentAccounts, String orderNo, FundPackages fundPackage, UtTransactionType trxType) {
//        try {
//            Kyc kyc = investmentAccounts.getKycs();
//            FundPackages fundPackages = investmentAccounts.getFundPackages();
//            Double total = 0.0;
//
//            List<JSONObject> compositions = new ArrayList<>();
//
//            List<UtTransactions> utTransactionses = utTransactionsRepository.findAllByOrderNoAndKycId(orderNo, kyc);
//            for (UtTransactions utTransaction : utTransactionses) {
//                Double netAmount = utTransaction.getNetAmount();
//                Double feeAmount = utTransaction.getFeeAmount();
//                Double totAmount = netAmount + feeAmount;
//
//                System.out.println("LEWAT");
//                FundPackageProducts fpp = fundPackageProductsRepository.findByFundPackagesAndUtProducts(fundPackage, utTransaction.getProductId());
//                System.out.println("fpp : " + fpp);
//                UtProducts up = utTransaction.getProductId();
//                List<UtProductsSettlement> upses = utProductsSettlementRepository.findAllByUtProduct(up);
//
//                List<JSONObject> settlementAccounts = new ArrayList<>();
//                for (UtProductsSettlement upse : upses) {
//                    JSONObject settlementAccount = new JSONObject();
//                    settlementAccount.put("bankName", upse.getAccountName());
//                    settlementAccount.put("accountId", upse.getAccountNumber());
//                    settlementAccounts.add(settlementAccount);
//                }
//
//                JSONObject utTrx = new JSONObject();
//                utTrx.put("productName", up.getProductName());
//                utTrx.put("composition", StringUtil.numberFormat2Decimal(fpp.getCompositition() * 100, 2));
//                utTrx.put("settlementAccounts", settlementAccounts);
//                utTrx.put("netAmount", StringUtil.numberFormat2Decimal(netAmount, 2));
//                utTrx.put("feeAmount", StringUtil.numberFormat2Decimal(feeAmount, 2));
//                utTrx.put("totAmount", StringUtil.numberFormat2Decimal(totAmount, 2));
//
//                compositions.add(utTrx);
//                total += totAmount;
//            }
//
//            JSONObject kycs = new JSONObject();
//            kycs.put("firstName", kyc.getFirstName());
//            kycs.put("middleName", kyc.getMiddleName());
//            kycs.put("lastName", kyc.getLastName());
//
//            JSONObject order = new JSONObject();
//            order.put("orderNo", orderNo);
//            order.put("productName", fundPackages.getFundPackageName());
//            order.put("investmentAccount", investmentAccounts.getInvestmentAccountNo());
//            order.put("trxType", trxType.getTrxName());
//            order.put("composition", compositions);
//            order.put("total", StringUtil.numberFormat2Decimal(total, 2));
//
//            JSONObject data = new JSONObject();
//            data.put("kycs", kycs);
//            data.put("order", order);
//            data.put("cutOffDate", DateTimeUtil.convertDateToStringCustomized(new Date(), "dd MMM yyyy"));
//            data.put("cutOffTime", DateTimeUtil.convertDateToStringCustomized(fundPackages.getSettlementCutOff(), "HH:mm:ss"));
//
//            List<String> tos = new ArrayList<>();
//            tos.add(kyc.getEmail());
//
//            String response = sendRestTemplate(data, tos, "ORDER");
//
//            logger.info("response : " + response);
//            System.out.println("response : " + response);
//            return (response != null);
//        } catch (JSONException | RestClientException e) {
//            logger.error(e);
//            return false;
//        }
//    }
    @Override
    public boolean sendUserStatusActive(Kyc kyc) {
        try {
            JSONObject kycs = new JSONObject();
            kycs.put("firstName", kyc.getFirstName());
            kycs.put("middleName", kyc.getMiddleName());
            kycs.put("lastName", kyc.getLastName());

            JSONObject data = new JSONObject();
            data.put("kycs", kycs);

            List<String> tos = new ArrayList<>();
            tos.add(kyc.getEmail());

            String response = sendRestTemplate(data, tos, "USER_ACTIVE");

            logger.info("response : " + response);
            System.out.println("response : " + response);
            return (response != null);
        } catch (JSONException | RestClientException e) {
            logger.error(e);
            return false;
        }
    }

    @Override
    public boolean sendNeedPayment(Kyc kyc, String orderNo) {
        JSONObject registerJSON = new JSONObject();
        registerJSON.put("customerCIF", kyc.getPortalcif());
        registerJSON.put("signature", channelService.generateHashSHA256(kyc.getAccount().getCustomerKey()));
        registerJSON.put("orderNo", orderNo);

        return sendToGroovy("/email/sendCheckOutTransferConvAgent", registerJSON.toString());
    }

    @Override
    public boolean sendSettlementTransaction(Kyc kyc, Map map) {
        if (!kyc.getAccount().getAgent().getEmailCustom()) {
            List<JSONObject> listData = new ArrayList<>();
            JSONObject registerJSON = new JSONObject();
            registerJSON.put("customerCIF", kyc.getPortalcif());
            registerJSON.put("signature", channelService.generateHashSHA256(kyc.getAccount().getCustomerKey()));
            registerJSON.put("data", map);
            listData.add(registerJSON);

            return sendToGroovy("/email/sendOrderPaidAgent", listData);
        } else {
            try {
                String api = "trx_already_paid";
                Map mapx = new HashMap();
                mapx.put("portalCif", kyc.getPortalcif());
                mapx.put("orderNo", map.get("order_number"));
                mapx.put("type", "SUBSCRIPTION");

                String json = new ObjectMapper().writeValueAsString(mapx);

                List<McwEmailScheduller> mcws = mcwEmailSchedullerRepository.findAllByStatusAndValueAndApiEmail(0, json, api);
                if (mcws == null || mcws.isEmpty()) {
                    McwEmailScheduller mcw = new McwEmailScheduller();
                    mcw.setApiEmail(api);
                    mcw.setCreatedBy(kyc.getEmail());
                    mcw.setDateCreated(new Date());
                    mcw.setStatus(0);
                    mcw.setValue(json);

                    mcw = mcwEmailSchedullerRepository.save(mcw);
                    return (mcw != null && mcw.getId() != null);
                } else {
                    return false;
                }
            } catch (JsonProcessingException e) {
                logger.info(e);
                return false;
            }
        }
    }

    @Override
    public boolean sendOpenRekening(Kyc kyc) {
        Boolean checkEmailCustom = null;
        try {
           checkEmailCustom = kyc.getAccount().getAgent().getEmailCustom();
        } catch (Exception ignored) {
            checkEmailCustom = false;
        }
        if (!checkEmailCustom) {
            List<CronEmail> crs = cronEmailRepository.findAllByCifAndTypeAndStatus(kyc.getPortalcif(), "OPEN_ACCOUNT", 0);
            if (crs == null || crs.isEmpty()) {
                List<JSONObject> listData = new ArrayList<>();
                JSONObject registerJSON = new JSONObject();
                registerJSON.put("customerCIF", kyc.getPortalcif());
                registerJSON.put("signature", channelService.generateHashSHA256(kyc.getAccount().getCustomerKey()));
                listData.add(registerJSON);

                CronEmail cronEmail = new CronEmail();
                cronEmail.setCif(kyc.getPortalcif());
                cronEmail.setType("OPEN_ACCOUNT");
                cronEmail.setUrl("/email/sendActivatedAccountEmailAgent");
                cronEmail.setValue(listData.toString());
                cronEmail.setCreatedDate(DateTimeUtil.getCostumMinuteDate(new Date(), 2));
                cronEmail.setStatus(0);

                cronEmailRepository.save(cronEmail);
            }
        }
        try {
            String api = "acc_open";
            Map map = new HashMap();
            map.put("portalCif", kyc.getPortalcif());
            String json = new ObjectMapper().writeValueAsString(map);

            List<McwEmailScheduller> mcws = mcwEmailSchedullerRepository.findAllByStatusAndValueAndApiEmail(0, json, api);
            if (mcws == null || mcws.isEmpty()) {
                McwEmailScheduller mcw = new McwEmailScheduller();
                mcw.setApiEmail(api);
                mcw.setCreatedBy(kyc.getEmail());
                mcw.setDateCreated(new Date());
                mcw.setStatus(0);
                mcw.setValue(json);

                mcw = mcwEmailSchedullerRepository.save(mcw);
                return (mcw != null && mcw.getId() != null);
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.info(e);
            return false;
        }
    }

    public void fileOpenRekening(Kyc kyc) {
        WkhtmltoPdfUtil wkhtmltoPdfUtil = new WkhtmltoPdfUtil();

        LookupLine sourceOfIncome = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("SOURCE_OF_INCOME"), kyc.getSourceOfIncome(), true);
        LookupLine totalIncome = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("ANNUAL_INCOME"), kyc.getTotalIncomePa(), true);
        LookupLine totalAset = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("TOTAL_ASSET"), kyc.getTotalAsset(), true);
        LookupLine investmentPurpose = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("INVESTMENT_PURPOSE"), kyc.getInvestmentPurpose(), true);
        LookupLine investmentExperience = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("INVESTMENT_EXPERIENCE"), kyc.getInvestmentExperience(), true);
        LookupLine gender = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("GENDER"), kyc.getGender(), true);
        Countries nationality = countriesRepository.findByAlpha3Code(kyc.getNationality());
        LookupLine marital = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("MARITAL_STATUS"), kyc.getMaritalStatus(), true);
        LookupLine education = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("EDUCATION_BACKGROUND"), kyc.getEducationBackground(), true);
        LookupLine religion = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("RELIGION"), kyc.getReligion(), true);

//            if (isExistingData(kycs.get("statement_type"))) {
//                statementType = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("STATEMENT_TYPE"), kycs.get("statement_type").toString(), true);
//                if (statementType == null) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.statement_type", null);
//                }
//            }
//
//            if (isExistingData(kycs.get("occupation"))) {
//                occupation = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("OCCUPATION"), kycs.get("occupation").toString(), true);
//                if (occupation == null) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.occupation", null);
//                }
//            }
//
//            if (isExistingData(kycs.get("business_nature"))) {
//                businessNature = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("NATURE_OF_BUSINESS"), kycs.get("business_nature").toString(), true);
//                if (businessNature == null) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.business_nature", null);
//                }
//            }
//
//            if (isExistingData(kycs.get("id_expiration"))) {
//                expirationDate = DateTimeUtil.convertStringToDateCustomized(kycs.get("id_expiration").toString(), DateTimeUtil.API_MCW);
//            }
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("kyc", kyc);
        mapData.put("settlementAccounts", settlementAccountsRepository.findByKycs(kyc));

        byte[] bytes = wkhtmltoPdfUtil.getPdf("kyc.html", mapData, "123456");
        try {
            FileUtils.writeByteArrayToFile(File.createTempFile("tampan", ".pdf"), bytes);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(EmailServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean sendToGroovy(String url, List<JSONObject> listData) {
        logger.info("listData : " + listData);
        return sendToGroovy(url, listData.toString());
    }

    @Override
    public boolean sendToGroovy(String url, String listData) {
        try {
            GlobalParameter gp = globalParameterRepository.findByName("GROOVY_API_URL");

            logger.info("send to : " + gp.getValue() + url);
            logger.info("listData : " + listData);

            RestTemplate rest = new RestTemplate();
            String response = rest.postForObject(gp.getValue() + url, listData, String.class);

            logger.info("response core invisee= " + response);
            return true;
        } catch (RestClientException e) {
            logger.error("send email failed : " + e);
            return false;
        }
    }

    @Override
    public boolean sendRedeem(Kyc kyc, String orderNo) {
        if (!kyc.getAccount().getAgent().getEmailCustom()) {

            try {
                UtTransactions utTransactions = null;

                List<UtTransactions> utTransactionses = utTransactionsRepository.findAllByOrderNoAndKycIdOrderByIdDesc(orderNo, kyc);
                if (utTransactionses != null && !utTransactionses.isEmpty()) {
                    utTransactions = utTransactionses.get(0);
                }

                if (utTransactions == null) {
                    return false;
                }

                SettlementAccounts settlementAccount = settlementAccountsRepository.findByKycs(kyc);

                JSONObject order = new JSONObject();
                order.put("orderNo", orderNo);
                order.put("investment", utTransactions.getInvestementAccount().getInvestmentAccountNo());
                order.put("packageName", utTransactions.getFundPackageRef().getFundPackageName());
                order.put("marketValue", StringUtil.numberFormat2Decimal(utTransactions.getOrderAmount(), 2));
                order.put("fee", StringUtil.numberFormat2Decimal(utTransactions.getFeeAmount(), 2));
                order.put("redeemAmount", StringUtil.numberFormat2Decimal(utTransactions.getNetAmount(), 2));
                order.put("accountNumber", settlementAccount.getSettlementAccountNo());
                order.put("accountName", settlementAccount.getSettlementAccountName());
                order.put("bankName", settlementAccount.getBankId().getBankName());
                order.put("paidDate", DateTimeUtil.convertDateToStringCustomized(utTransactions.getPriceDate(), "yyyy-MM-dd"));

                JSONObject kycs = new JSONObject();
                kycs.put("firstName", kyc.getFirstName());
                kycs.put("middleName", kyc.getMiddleName());
                kycs.put("lastName", kyc.getLastName());

                JSONObject data = new JSONObject();
                data.put("kycs", kycs);
                data.put("order", order);

                List<String> tos = new ArrayList<>();
                tos.add(kyc.getEmail());

                String response = sendRestTemplate(data, tos, "REDEEM");

                logger.info("response : " + response);
                return (response != null);
            } catch (JSONException | RestClientException e) {
                logger.error(e);
                return false;
            }
        }

        try {
            UtTransactions utTransactions = null;

            List<UtTransactions> utTransactionses = utTransactionsRepository.findAllByOrderNoAndKycIdOrderByIdDesc(orderNo, kyc);
            if (utTransactionses != null && !utTransactionses.isEmpty()) {
                utTransactions = utTransactionses.get(0);
            }

            String api = "trx_redeem";
            Map mapx = new HashMap();
            mapx.put("portalCif", kyc.getPortalcif());
            mapx.put("orderNo", orderNo);
            mapx.put("type", utTransactions.getTransactionType().getTrxName());

            String json = new ObjectMapper().writeValueAsString(mapx);

            List<McwEmailScheduller> mcws = mcwEmailSchedullerRepository.findAllByStatusAndValueAndApiEmail(0, json, api);
            if (mcws == null || mcws.isEmpty()) {
                McwEmailScheduller mcw = new McwEmailScheduller();
                mcw.setApiEmail(api);
                mcw.setCreatedBy(kyc.getEmail());
                mcw.setDateCreated(new Date());
                mcw.setStatus(0);
                mcw.setValue(json);

                mcw = mcwEmailSchedullerRepository.save(mcw);
                return (mcw != null && mcw.getId() != null);
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.info(e);
            return false;
        }
    }
}
