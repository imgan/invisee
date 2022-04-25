package com.nsi.services.impl;

import com.nsi.domain.core.*;
import com.nsi.repositories.core.SbnBankRepository;
import com.nsi.repositories.core.*;
import com.nsi.services.SbnService;
import com.nsi.util.ConstantUtil;
import com.nsi.util.DateTimeUtil;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SbnServiceImpl extends BaseService implements SbnService {
    @Autowired
    SbnPackagesRepository sbnPackagesRepository;
    @Autowired
    SbnSidRepository sbnSidRepository;
    @Autowired
    GlobalParameterRepository globalParameterRepository;
    @Autowired
    SbnSubregistryRepository sbnSubregistryRepository;
    @Autowired
    SbnAccountDetailRepository sbnAccountDetailRepository;
    @Autowired
    SbnTransactionsRepository sbnTransactionsRepository;
    @Autowired
    SbnTransactionsHistoryRepository sbnTransactionsHistoryRepository;
    @Autowired
    KycRepository kycRepository;
    @Autowired
    SubmidisRepository submidisRepository;
    @Autowired
    SbnTransactionsPopRepository sbnTransactionsPopRepository;
    @Autowired
    HolidayRepository holidayRepository;
    @Autowired
    SbnBankRepository sbnBankRepository;

    byte[] hmac_sha256(String secretKey, String data) throws NoSuchAlgorithmException {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] digest = mac.doFinal(data.getBytes());
            return digest;
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key exception while converting to HMac SHA256");
        } catch (NoSuchAlgorithmException e) {
            throw e;
        }
    }

    String getAutherizationWithUrl(String apiUrl, String requestbody, String reqhttpmethod) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        try{
            logger.info("requestbody :"+requestbody);
            String apiId = globalParameterRepository.findByCategory("SBN_API_ID").getValue();
            String apiKey = globalParameterRepository.findByCategory("SBN_API_KEY").getValue();

            String encodedUrl;
            encodedUrl = java.net.URLEncoder.encode(apiUrl, "UTF-8").trim().toLowerCase();
            final Date currentTime = new Date();
            final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            String dateStr = sdf.format(currentTime);
            Date currentDate = sdf.parse(dateStr);
            long unixTime = currentDate.getTime() / 1000L;
            String nonce = UUID.randomUUID().toString();
            nonce = nonce.replaceAll("-", "");
            String canonanicalized = "nUVowAnSA6sQ4z6plToCZA==";
            if (!requestbody.isEmpty()) {
                requestbody = requestbody.replaceAll("'", "\"");
                requestbody = requestbody.replaceAll("\r", "");
                requestbody = requestbody.replaceAll("\n", "");
                requestbody = requestbody.replaceAll("\t", "");
                requestbody = requestbody.replaceAll(" ", "");

                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(requestbody.getBytes());
                byte[] enc = md5.digest();
                canonanicalized = new String(Base64.encodeBase64(enc));
            }
            logger.info("apiId :"+apiId);
            logger.info("reqhttpmethod :"+reqhttpmethod);
            logger.info("encodedUrl :"+encodedUrl);
            logger.info("unixTime :"+String.valueOf(unixTime));
            logger.info("nonce :"+nonce);
            logger.info("canonanicalized :"+canonanicalized);
            String signatureRawData = apiId + reqhttpmethod + encodedUrl + String.valueOf(unixTime) + nonce + canonanicalized;
            byte[] hash = hmac_sha256(apiKey, signatureRawData);
            String signatureBase64 = new String(Base64.encodeBase64(hash));

            String s = apiId + ":" + signatureBase64 + ":" + nonce + ":" + String.valueOf(unixTime);
            logger.info("s :"+s);
            String authString = new String(Base64.encodeBase64(s.getBytes()));
            return authString;
        }catch(Exception e){
            throw e;
        }
    }

    Map findByWithUrl(String urlWithParam) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String requestbody = "";
        String url = globalParameterRepository.findByCategory("SBN_API_URL_WITHOUT_VERSION").getValue() + urlWithParam;
        String auth = getAutherizationWithUrl(url, requestbody, "GET");
        logger.info("auth :"+auth);
        String authString = "amx " + auth;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", authString);

        HttpEntity entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        logger.info("response :"+response);
        return (Map) response.getBody();
    }

    Map createPemesanan(String sid, String idSeri, String idRekDana, String idRekSB, String nominal) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        JSONObject requestbody = new JSONObject();
        requestbody.put("Sid", sid);
        requestbody.put("IdSeri", idSeri);
        requestbody.put("IdRekDana", idRekDana);
        requestbody.put("IdRekSB", idRekSB);
        requestbody.put("Nominal", nominal);

        String url = globalParameterRepository.findByCategory("SBN_API_URL_WITHOUT_VERSION").getValue() + "v1/pemesanan";
        String auth = getAutherizationWithUrl(url, requestbody.toJSONString(), "POST");

        HttpHeaders headers = new HttpHeaders();
        String authString = "amx " + auth;

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", authString);

        HttpEntity entity = new HttpEntity(requestbody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
        logger.info("response :"+response);
        return (Map) response.getBody();
    }

    Map createRedeem(String kodePemesanan, String sid, String nominal) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        JSONObject requestbody = new JSONObject();
        requestbody.put("KodePemesanan", kodePemesanan);
        requestbody.put("Sid", sid);
        requestbody.put("Nominal", nominal);

        String url = globalParameterRepository.findByCategory("SBN_API_URL_WITHOUT_VERSION").getValue() + "v1/redemption";
        String auth = getAutherizationWithUrl(url, requestbody.toJSONString(), "POST");

        HttpHeaders headers = new HttpHeaders();

        String authString = "amx " + auth;

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", authString);

        HttpEntity entity = new HttpEntity(requestbody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
        logger.info("response :"+response);
        return (Map) response.getBody();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map addPemesanan(User user, Long idSeri, BigInteger nominal) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Map resultMap = new LinkedHashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Kyc kyc = kycRepository.findByAccount(user);
        SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);
        if(sbnSid == null){
            return errorResponse(14, "Invalid or unregister e-mail , please check your format or Sign Up now", null);
        }

        SbnPackages sbnPackages = sbnPackagesRepository.findByIdSeri(idSeri);
        if(sbnPackages == null){
            return errorResponse(14, "id_seri", null);
        }

        if(sbnSid.getSid().substring(0, 3).equals("IDF")){
            if(!sbnPackages.getPackageCode().contains("DIASPO")){
                return errorResponse(14, "Seri ini hanya tersedia untuk investor Diaspora", null);
            }
        }else{
            if(sbnPackages.getPackageCode().contains("DIASPO") && kyc.getNoKmiln() == null){
                return errorResponse(14, "Seri ini hanya tersedia untuk investor Diaspora", null);
            }
        }

        if (user.getIsSbnCustomer() && user.getIsSbnCustomerProcess()) {
            Boolean isAllowTrx = false;
            Map investor = findByWithUrl("v1/investor/" + sbnSid.getSid());
            String subregId = globalParameterRepository.findByCategoryAndName("SBN", "SBN_SUBREGISTRY_ID").getValue();
            SbnSubregistry sbnSubregistry = sbnSubregistryRepository.findBySubregId(subregId);
            for (Map rekSB : (List<Map>) investor.get("RekeningSB")) {
                if(sbnSubregistry.getSubregName().equalsIgnoreCase((String) rekSB.get("NamaSubregistry"))){
                    isAllowTrx = true;
                }
            }

            if(!isAllowTrx){
                resultMap.put(ConstantUtil.CODE, 12);
                resultMap.put(ConstantUtil.INFO.toLowerCase(), "Rekening Surat Berharga Anda Sedang Dalam Proses Pembaruan.");
                return resultMap;
            }

            SbnAccountDetail sbnAccountDetail;

            String oriIdSeri = globalParameterRepository.findByCategory("ORI_ID_SERI").getValue();
            if(idSeri.toString().equals(oriIdSeri)){
                SbnSubregistry sbnSub = sbnSubregistryRepository.getOne(Long.valueOf(1));
                sbnAccountDetail = sbnAccountDetailRepository.findBySbnSidAndSbnSubregistry(sbnSid, sbnSub);
            }else{
                sbnAccountDetail = sbnAccountDetailRepository.findBySbnSid(sbnSid);
            }

            if (sbnAccountDetail == null) {
                return errorResponse(88, "Rekening Surat Berharga Anda Sedang Dalam Proses Pembaruan", null);
            }

            Map response = createPemesanan(sbnSid.getSid(), idSeri.toString(), sbnAccountDetail.getIdRekeningDanaSbn().toString(),
                    sbnAccountDetail.getIdSubregistry().toString(), nominal.toString());

            SbnTransactions sbnTrx = new SbnTransactions();
            sbnTrx.setIdSid(sbnSid.getId());
            sbnTrx.setSbnSid(sbnSid);
            sbnTrx.setIdRekeningDana(Long.valueOf(response.get("IdRekDana").toString()));
            sbnTrx.setIdSeri(Long.valueOf(response.get("IdSeri").toString()));
            sbnTrx.setIdRekeningSb(Long.valueOf(response.get("IdRekSb").toString()));
            sbnTrx.setTrxAmount(new BigDecimal(response.get("Nominal").toString()).longValue());
            sbnTrx.setBatasWaktuBayar(sdf.parse((String) response.get("BatasWaktuBayar")));
            sbnTrx.setIdStatus(Long.valueOf((String) response.get("IdStatus")));
            sbnTrx.setStatusDesc(response.get("Status").toString());
            sbnTrx.setKodeBilling(response.get("KodeBilling").toString());
            sbnTrx.setSisaKepemilikan(new BigDecimal(response.get("SisaKepemilikan").toString()).longValue());
            sbnTrx.setKodePemesanan((String) response.get("KodePemesanan"));
            sbnTrx.setCreatedBy(response.get("CreatedBy").toString());
            sbnTrx.setCreatedDate(sdf.parse((String) response.get("TglPemesanan")));
            sbnTransactionsRepository.saveAndFlush(sbnTrx);

            SbnTransactionsHistory sbnTransactionsHistory = new SbnTransactionsHistory();
            sbnTransactionsHistory.setCreatedDate(sdf.parse((String) response.get("TglPemesanan")));
            sbnTransactionsHistory.setCreatedBy((String) response.get("CreatedBy"));
            sbnTransactionsHistory.setTransactionsCode((String) response.get("KodePemesanan"));
            sbnTransactionsHistory.setStatusDesc((String) response.get("Status"));
            sbnTransactionsHistory.setTransactionsAmount(Double.valueOf(response.get("Nominal").toString()));
            sbnTransactionsHistory.setRemainingAmount(Double.valueOf(response.get("SisaKepemilikan").toString()));
            sbnTransactionsHistoryRepository.saveAndFlush(sbnTransactionsHistory);

            resultMap.put(ConstantUtil.CODE, 0);
            resultMap.put(ConstantUtil.INFO.toLowerCase(), "Order successfully submitted");
            resultMap.put(ConstantUtil.DATA.toLowerCase(), response);
            return resultMap;
        } else if (user.getIsSbnCustomer() && !user.getIsSbnCustomerProcess()) {
            resultMap.put(ConstantUtil.CODE, 12);
            resultMap.put(ConstantUtil.INFO.toLowerCase(), "Akun anda sedang dalam proses verifikasi oleh tim officer kami.");
            return resultMap;
        } else {
            resultMap.put(ConstantUtil.CODE, 14);
            resultMap.put(ConstantUtil.INFO.toLowerCase(), "Anda belum terdaftar sebagai nasabah SBN.");
            return resultMap;
        }
    }

    public Map validateCustomerSbn(User user) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Map result = new LinkedHashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        if (user.getIsSbnCustomer() && user.getIsSbnCustomerProcess()) {
            Boolean isAllowTrx = false;
            Kyc kyc = kycRepository.findByAccount(user);
            SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);

            if(sbnSid == null){
                return errorResponse(14, "Invalid or unregister e-mail , please check your format or Sign Up now", null);
            }

            Map data = new LinkedHashMap();
            Map investor = findByWithUrl("v1/investor/" + sbnSid.getSid());
            List rekDana = (List)investor.get("RekeningDana");

            data.put("sid", sbnSid.getSid());
            data.put("sidName", sbnSid.getSidName());
            data.put("email", investor.get("Email"));
            data.put("bankName", ((Map) rekDana.get(0)).get("NamaBank"));
            data.put("accountNumber", ((Map) rekDana.get(0)).get("NoRek"));
            data.put("noKmiln", kyc.getNoKmiln());
            data.put("tglTerbit", kyc.getIssueDateKmiln() == null ? null : sdf.format(kyc.getIssueDateKmiln()));

            String subregId = globalParameterRepository.findByCategoryAndName("SBN", "SBN_SUBREGISTRY_ID").getValue();
            SbnSubregistry sbnSubregistry = sbnSubregistryRepository.findBySubregId(subregId);

            List<Map> rekeningSB = new ArrayList<>();
            for (Map rekSB : (List<Map>) investor.get("RekeningSB")) {
                if(sbnSubregistry.getSubregName().equalsIgnoreCase((String) rekSB.get("NamaSubregistry"))){
                    isAllowTrx = true;
                }
                Map rsb = new LinkedHashMap();
                rsb.put("subregistryName", rekSB.get("NamaSubregistry"));
                rsb.put("subregistryNumber", rekSB.get("NoRek"));
                rekeningSB.add(rsb);
            }
            data.put("rekeningSB", rekeningSB);

            if(isAllowTrx){
                result.put(ConstantUtil.CODE, 1);
                result.put(ConstantUtil.INFO.toLowerCase(), "Status akun ini diizinkan untuk melakukan transaksi Product SBN.");
                result.put(ConstantUtil.DATA.toLowerCase(), data);
            }else{
                result.put(ConstantUtil.CODE, 12);
                result.put(ConstantUtil.INFO.toLowerCase(), "Rekening Surat Berharga Anda Sedang Dalam Proses Pembaruan.");
            }

        } else if (user.getIsSbnCustomer() && !user.getIsSbnCustomerProcess()) {
            result.put(ConstantUtil.CODE, 12);
            result.put(ConstantUtil.INFO.toLowerCase(), "Akun anda sedang dalam proses verifikasi oleh tim officer kami.");
        } else {
            result.put(ConstantUtil.CODE, 14);
            result.put(ConstantUtil.INFO.toLowerCase(), "Anda belum terdaftar sebagai nasabah SBN.");
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map redeem(Kyc kyc, String kodePemesanan, BigInteger nominal) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Map result = new LinkedHashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);
        if(sbnSid == null){
            return errorResponse(14, "Invalid or unregister e-mail , please check your format or Sign Up now", null);
        }

        SbnTransactions sbnTrx = sbnTransactionsRepository.findByKodePemesananAndSbnSid(kodePemesanan, sbnSid);
        if(sbnTrx == null){
            return errorResponse(14, "kode pemesanan not found", null);
        }

        Map response = createRedeem(kodePemesanan, sbnSid.getSid(), nominal.toString());

        sbnTrx.setSisaKepemilikan(new BigDecimal(response.get("SisaKepemilikan").toString()).longValue());
        sbnTrx.setRedeemableAmount(Double.valueOf(response.get("Redeemable").toString()));
        sbnTrx.setStatusDesc((String) response.get("Status"));
        sbnTransactionsRepository.saveAndFlush(sbnTrx);

        SbnTransactionsHistory sbnTransactionsHistory = new SbnTransactionsHistory();
        sbnTransactionsHistory.setCreatedDate(sdf.parse((String) response.get("TglRedeem")));
        sbnTransactionsHistory.setCreatedBy(kyc.getAccount().getUsername());
        sbnTransactionsHistory.setTransactionsCode(sbnTrx.getKodePemesanan());
        sbnTransactionsHistory.setStatusDesc(sbnTrx.getStatusDesc());
        sbnTransactionsHistory.setTransactionsAmount(Double.valueOf(response.get("Nominal").toString()));
        sbnTransactionsHistory.setRemainingAmount(Double.valueOf(sbnTrx.getSisaKepemilikan()));
        sbnTransactionsHistory.setRedeemCode((String) response.get("KodeRedeem"));
        sbnTransactionsHistory.setRedeemableAmount(sbnTrx.getRedeemableAmount());
        sbnTransactionsHistory.setSettlementDate(sdf.parse((String) response.get("TglSetelmen")));
        sbnTransactionsHistoryRepository.saveAndFlush(sbnTransactionsHistory);

        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), "Order successfully submitted");
        result.put(ConstantUtil.DATA.toLowerCase(), response);
        return result;
    }

    public Map getKuotaBySidAndSeri(String sid, Long idSeri, Kyc kyc) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Map result = new LinkedHashMap();
        SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);
        if(sbnSid == null){
            return errorResponse(14, "Invalid or unregister e-mail , please check your format or Sign Up now", null);
        }

        if(!sbnSid.getSid().equals(sid)){
            return errorResponse(14, "sid", null);
        }

        Map data = findByWithUrl("v1/Kuota/" + idSeri.toString() + "/" + sid);
        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), "Successfully loaded");
        result.put(ConstantUtil.DATA.toLowerCase(), data);
        return result;
    }

    Date getTradeDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();

        List<Holiday> holidayList = holidayRepository.findAllByHolidayDateWithCustomQuery(sdf.format(now));
        List<String> holidays = new ArrayList<String>();
        for (Holiday holiday : holidayList) {
            holidays.add(sdf.format(DateTimeUtil.clearTime(holiday.getHolidayDate())));
        }
        // get the current date without the hours, minutes, seconds and millis
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        Integer dow = cal.get(Calendar.DAY_OF_WEEK);
        while (cal.get(Calendar.DAY_OF_WEEK) == 15 || dow == Calendar.SATURDAY || dow == Calendar.SUNDAY ||
                holidays.contains(sdf.format(cal.getTime()))) {
            cal.add(Calendar.DATE, 1);
            dow = cal.get(Calendar.DAY_OF_WEEK);
        }
        return DateTimeUtil.clearTime(cal.getTime());
    }

    Date getLastCouponDate(Date tradeDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(tradeDate);
        int actualDate = cal.get(Calendar.DAY_OF_MONTH);
        int prevMonth = cal.get(Calendar.MONTH) - 1;
        if(actualDate <= 15){
            cal.set(Calendar.MONTH, prevMonth);
        }
        cal.set(Calendar.DAY_OF_MONTH, 15);
        return DateTimeUtil.clearTime(cal.getTime());
    }

    Date getNextCouponDate(Date lastCouponDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastCouponDate);
        int nextMonth = cal.get(Calendar.MONTH) + 1;
        cal.set(Calendar.MONTH, nextMonth);
        return DateTimeUtil.clearTime(cal.getTime());
    }

    Date addBussinessDate(Date date, int addedDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        List<Holiday> holidayList = holidayRepository.findAllByHolidayDateWithCustomQuery(sdf.format(date));
        List<String> holidays = new ArrayList<String>();
        for (Holiday holiday : holidayList) {
            holidays.add(sdf.format(DateTimeUtil.clearTime(holiday.getHolidayDate())));
        }

        for (int i = 1; i <= addedDate; i++){
            calendar.add(Calendar.DATE, 1);
            while(calendar.get(Calendar.DAY_OF_WEEK)  == Calendar.SATURDAY
                    || calendar.get(Calendar.DAY_OF_WEEK)  == Calendar.SUNDAY
                    || holidays.contains(sdf.format(calendar.getTime()))){
                calendar.add(Calendar.DATE, 1);
            }
        }
        return DateTimeUtil.clearTime(calendar.getTime());
    }

    public Map calculateRedemption(String kodePemesanan, Kyc kyc){
        SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);
        if(sbnSid == null){
            return errorResponse(14, "Invalid or unregister e-mail , please check your format or Sign Up now", null);
        }

        SbnTransactions sbnTransactions = sbnTransactionsRepository.findByKodePemesananAndSbnSid(kodePemesanan, sbnSid);
        if(sbnTransactions == null){
            return errorResponse(14, "kode pemesanan not found", null);
        }

        if(sbnTransactions.getSisaKepemilikan() < 1){
            return errorResponse(14, "Investasi ini sudah dijual", null);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map result = new LinkedHashMap();
        Date tradeDate = getTradeDate();
        Date lastCouponDate = getLastCouponDate(tradeDate);
        Date nextCouponDate = getNextCouponDate(lastCouponDate);
        double tax = 0.15;
        Long trxAmount = sbnTransactions.getTrxAmount();
        SbnPackages sbnPackages = sbnPackagesRepository.findByIdSeri(sbnTransactions.getIdSeri());
        Submidis submidisLast = submidisRepository.findBySbnPackagesWithCustomQuery(sbnPackages.getId());
        String maturityDate = sdf.format(sbnPackages.getDueDate());
        String acquisitionDate = sdf.format(sbnPackages.getSettlementDate());
        Date settlementDate = addBussinessDate(tradeDate, 2);
        Integer acquisitionPrice = 100;
        Double salePrice = submidisLast.getSubmidisPrice();
        Double proceed = salePrice /100 * trxAmount;
        Long difDateMs = Math.abs( lastCouponDate.getTime() - settlementDate.getTime());
        Double difDate = Double.valueOf(TimeUnit.DAYS.convert(difDateMs, TimeUnit.MILLISECONDS));

        Long diffCouponDateMs = Math.abs( lastCouponDate.getTime() - nextCouponDate.getTime());
        Double diffCouponDate = Double.valueOf(TimeUnit.DAYS.convert(diffCouponDateMs, TimeUnit.MILLISECONDS));

        Double accruedInterest = trxAmount * (sbnPackages.getCouponRate()/ 100 / 12) * (difDate / diffCouponDate);
        Double netProceed = proceed + accruedInterest;
        Double couponTax = 0.0;
        Double capitalGainTax = 0.0;
        if(salePrice >= 100){
            couponTax = tax * accruedInterest;
            capitalGainTax = ((salePrice - acquisitionPrice)/100) * trxAmount  * tax;
        }
        Double totalTax = couponTax + capitalGainTax;
        Double settlementAmount = netProceed - (totalTax + 55000.0d);

        Map data = new LinkedHashMap();
        data.put("lastCouponDate", sdf.format(lastCouponDate));
        data.put("nextCouponDate", sdf.format(nextCouponDate));
        data.put("packageCode", sbnPackages.getPackageCode());
        data.put("acquisitionPrice", new BigDecimal(acquisitionPrice));
        data.put("sellPrice", new BigDecimal(salePrice));
        data.put("proceed", new BigDecimal(proceed).toPlainString());
        data.put("accruedInterest", new BigDecimal(accruedInterest));
        data.put("netProceed", new BigDecimal(netProceed));
        data.put("couponTax", new BigDecimal(couponTax));
        data.put("capitalGainTax", new BigDecimal(capitalGainTax));
        data.put("totalTax", new BigDecimal(totalTax));
        data.put("settlementAmount", new BigDecimal(settlementAmount));
        data.put("movingCostEffect", 55000.0d);
        data.put("maturityDate", maturityDate);
        data.put("acquisitionDate", acquisitionDate);
        data.put("settlementDate", sdf.format(settlementDate));
        data.put("amount", trxAmount);
        data.put("couponRate", sbnPackages.getCouponRate());
        data.put("tradeDate", sdf.format(tradeDate));

        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
        result.put(ConstantUtil.DATA.toLowerCase(), data);
        return result;
    }

    public Map createFullRedeem(String kodePemesanan, Kyc kyc) throws ParseException {
        Map dataCalculation = calculateRedemption(kodePemesanan, kyc);
        if((int) dataCalculation.get("code") != 0){
            return dataCalculation;
        }

        SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);
        SbnTransactions sbnTrx = sbnTransactionsRepository.findByKodePemesananAndSbnSid(kodePemesanan, sbnSid);
        sbnTrx.setSisaKepemilikan((long) 0);
        sbnTrx.setRedeemableAmount(0d);
        sbnTrx.setUpdatedDate(new Date());
        sbnTrx.setUpdatedBy(kyc.getAccount().getUsername());
        sbnTransactionsRepository.saveAndFlush(sbnTrx);

        Map calculation = (Map) dataCalculation.get("data");
        SimpleDateFormat redeemFormat = new SimpleDateFormat("yyyMMddHHmmss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        SbnTransactionsHistory sbnTransactionsHistory = new SbnTransactionsHistory();
        sbnTransactionsHistory.setCreatedDate(new Date());
        sbnTransactionsHistory.setCreatedBy(kyc.getAccount().getUsername());
        sbnTransactionsHistory.setTransactionsCode(sbnTrx.getKodePemesanan());
        sbnTransactionsHistory.setStatusDesc("Confirm");
        sbnTransactionsHistory.setTransactionsAmount(((BigDecimal)calculation.get("settlementAmount")).doubleValue());
        sbnTransactionsHistory.setRemainingAmount(0d);
        sbnTransactionsHistory.setRedeemCode("ORI" + redeemFormat.format(new Date()).substring(1));
        sbnTransactionsHistory.setRedeemableAmount(0d);
        sbnTransactionsHistory.setSettlementDate(sdf.parse((String) calculation.get("settlementDate")));
        sbnTransactionsHistoryRepository.saveAndFlush(sbnTransactionsHistory);

        Map dataResult = new LinkedHashMap();
        dataResult.put("kodePemesanan", sbnTrx.getKodePemesanan());
        dataResult.put("createdDate", sbnTransactionsHistory.getCreatedDate());
        dataResult.put("packageCode", calculation.get("packageCode"));
        dataResult.put("settlementAmount", sbnTransactionsHistory.getTransactionsAmount());

        Map result = new LinkedHashMap();
        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
        result.put(ConstantUtil.DATA.toLowerCase(), dataResult);
        return result;
    }

    public Map transactionList(Kyc kyc){
        SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);
        if(sbnSid == null){
            return errorResponse(14, "Invalid or unregister e-mail , please check your format or Sign Up now", null);
        }
        
        List<SbnTransactions> sbnTransactionsList = sbnTransactionsRepository.findAllBySbnSidOrderByCreatedDateDesc(sbnSid);
        if (sbnTransactionsList.size() == 0) {
            return errorResponse(50, "Data transaction not found", null);
        }

        List<Map> dataPesanan = new ArrayList<>();
        for (SbnTransactions sbnTransactions : sbnTransactionsList) {
            List<SbnTransactionsHistory> histories = sbnTransactionsHistoryRepository.findAllByTransactionsCodeOrderByCreatedDateDesc(sbnTransactions.getKodePemesanan());
            SbnPackages sbnPackages = sbnPackagesRepository.findByIdSeri(sbnTransactions.getIdSeri());

            for (SbnTransactionsHistory history : histories) {
                Map transactions = new LinkedHashMap();
                transactions.put("packageName", sbnPackages.getPackageName());
                transactions.put("transactionsDate", history.getCreatedDate());
                transactions.put("transactionsCode", sbnTransactions.getKodePemesanan());
                transactions.put("billingCode", sbnTransactions.getKodeBilling());
                transactions.put("transactionsStatus", history.getStatusDesc());
                transactions.put("transactionsType", history.getRedeemCode() != null ? "Redemption" : "Subscription");
                transactions.put("redeemCode", history.getRedeemCode());
                transactions.put("amount", history.getTransactionsAmount());
                dataPesanan.add(transactions);

                if (history.getStatusDesc().equals("Completed Order")) {
                    break;
                }
            }
        }

        if (dataPesanan.size() == 0) {
            return errorResponse(50, "Data transaction not found", null);
        }

        Collections.sort(dataPesanan, new Comparator<Map>() {
            @Override
            public int compare(Map a, Map b) {
                return ((Date) b.get("transactionsDate")).compareTo((Date) a.get("transactionsDate"));
            }
        });

        Map result = new LinkedHashMap();
        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), "Sukses mengambil data pesanan");
        result.put(ConstantUtil.DATA.toLowerCase(), dataPesanan);
        return result;
    }

    public Map transactionDetail(Kyc kyc, String trxCode){
        SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);
        if(sbnSid == null){
            return errorResponse(14, "Invalid or unregister e-mail , please check your format or Sign Up now", null);
        }

        SbnTransactions sbnTransactions = sbnTransactionsRepository.findByKodePemesananAndSbnSid(trxCode, sbnSid);
        if(sbnTransactions == null){
            return errorResponse(50, "Data transaction not found", null);
        }

        SbnTransactionsHistory sbnTransactionsHistory = sbnTransactionsHistoryRepository.findByTransactionsCodeWithCustomQuery(trxCode);
        if(sbnTransactionsHistory == null){
            return errorResponse(50, "Data transaction not found", null);
        }

        SbnPackages sbnPackages = sbnPackagesRepository.findByIdSeri(sbnTransactions.getIdSeri());
        Map transactions = new LinkedHashMap();
        transactions.put("packageName", sbnPackages.getPackageName());
        transactions.put("transactionsDate", DateTimeUtil.convertDateToStringCustomized(sbnTransactionsHistory.getCreatedDate(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
        transactions.put("transactionsCode", sbnTransactionsHistory.getTransactionsCode());
        transactions.put("billingCode", sbnTransactions.getKodeBilling());
        transactions.put("transactionsStatus", sbnTransactions.getStatusDesc());
        transactions.put("transactionsType", sbnTransactionsHistory.getRedeemCode() != null ? "Redemption" : "Subscription");
        transactions.put("amount", sbnTransactionsHistory.getTransactionsAmount());
        if (sbnTransactionsHistory.getRedeemCode() != null) {
            transactions.put("redeemCode", sbnTransactionsHistory.getRedeemCode());
            transactions.put("settlementDate", DateTimeUtil.convertDateToStringCustomized(sbnTransactionsHistory.getSettlementDate(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
        } else {
            transactions.put("transactionsTimeLimit", sbnTransactions.getBatasWaktuBayar());
            transactions.put("pop", sbnTransactions.getPop());
        }

        String popName = null;
        try {
            if (sbnTransactions.getPop() != null) {
                SbnTransactionsPop sbnTransactionsPop = sbnTransactionsPopRepository.findByFileKey(sbnTransactions.getPop());
                popName = sbnTransactionsPop.getFileName();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        transactions.put("popName", popName);

        Map result = new LinkedHashMap();
        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), "Sukses mengambil data pesanan");
        result.put(ConstantUtil.DATA.toLowerCase(), transactions);
        return result;
    }

    public Map productList(){
        List<Map> data = new ArrayList<>();
        List<SbnPackages> sbnPackagesList = sbnPackagesRepository.findAllByDeletedAndActivatedOrderByCreatedDateDesc(false, true);
        for (SbnPackages sbn : sbnPackagesList) {
            Map sbnPackages = new LinkedHashMap();
            sbnPackages.put("id", sbn.getId());
            sbnPackages.put("packageCode", sbn.getPackageCode());
            sbnPackages.put("packageName", sbn.getPackageName());
            sbnPackages.put("packageDesc", sbn.getPackageDesc());
            sbnPackages.put("picture", sbn.getPicture());
            sbnPackages.put("effectiveDate", DateTimeUtil.convertDateToStringCustomized(sbn.getEffectiveDate(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
            sbnPackages.put("currency", sbn.getCurrency());
            sbnPackages.put("periodStart", DateTimeUtil.convertDateToStringCustomized(sbn.getPeriodStart(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
            sbnPackages.put("periodEnd", DateTimeUtil.convertDateToStringCustomized(sbn.getPeriodEnd(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
            sbnPackages.put("minimumTransaction", sbn.getMinimumTransaction());
            sbnPackages.put("subsFee", sbn.getSubsFee());
            sbnPackages.put("redeemFee", sbn.getRedeemFee());
            sbnPackages.put("couponRate", sbn.getCouponRate());
            sbnPackages.put("activated", sbn.getActivated());
            data.add(sbnPackages);
        }

        Map result = new LinkedHashMap();
        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
        result.put(ConstantUtil.DATA.toLowerCase(), data);
        return result;
    }

    public Map productDetail(Long productId){
        SbnPackages sbnPackages = sbnPackagesRepository.findByIdAndDeleted(productId, false);
        if(sbnPackages == null){
            return errorResponse(50, "product not found", null);
        }

        Map data = new LinkedHashMap();
        data.put("id", sbnPackages.getId());
        data.put("couponRate", sbnPackages.getCouponRate());
        data.put("couponDesc", sbnPackages.getCouponDesc());
        data.put("createdBy", sbnPackages.getCreatedBy());
        data.put("createdDate", sbnPackages.getCreatedDate());
        data.put("updatedBy", sbnPackages.getUpdatedBy());
        data.put("updatedDate", sbnPackages.getUpdatedDate());
        data.put("currency", sbnPackages.getCurrency());
        data.put("effectiveDate", DateTimeUtil.convertDateToStringCustomized(sbnPackages.getEffectiveDate(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
        data.put("deleted", sbnPackages.getDeleted());
        data.put("idSeri", sbnPackages.getIdSeri());
        data.put("packageName", sbnPackages.getPackageName());
        data.put("packageCode", sbnPackages.getPackageCode());
        data.put("packageDesc", sbnPackages.getPackageDesc());
        data.put("periodStart", DateTimeUtil.convertDateToStringCustomized(sbnPackages.getPeriodStart(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
        data.put("periodEnd", DateTimeUtil.convertDateToStringCustomized(sbnPackages.getPeriodEnd(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
        data.put("picture", sbnPackages.getPicture());
        data.put("subsFee", sbnPackages.getSubsFee());
        data.put("redeemFee", sbnPackages.getRedeemFee());
        data.put("activated", sbnPackages.getActivated());
        data.put("minimumTransaction", sbnPackages.getMinimumTransaction());
        data.put("maximumTransaction", sbnPackages.getMaximumTransaction());
        data.put("earlyStartRedemption", DateTimeUtil.convertDateToStringCustomized(sbnPackages.getEarlyStartRedemption(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
        data.put("earlyEndRedemption", DateTimeUtil.convertDateToStringCustomized(sbnPackages.getEarlyEndRedemption(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
        data.put("earlyRedeemablePercentage", sbnPackages.getEarlyRedeemablePercentage());
        data.put("quotaDate", DateTimeUtil.convertDateToStringCustomized(sbnPackages.getQuotaDate(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
        data.put("settlementDate", DateTimeUtil.convertDateToStringCustomized(sbnPackages.getSettlementDate(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
        data.put("dueDate", DateTimeUtil.convertDateToStringCustomized(sbnPackages.getDueDate(), DateTimeUtil.DATE_TIME_WITH_TIMEZONE));
        data.put("dueDateText", sbnPackages.getDueDateText());
        data.put("content", sbnPackages.getContent());

        Map result = new LinkedHashMap();
        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), "Sukses, data berhasil ditemukan");
        result.put(ConstantUtil.DATA.toLowerCase(), data);
        return result;
    }

    public Map investmentList(Kyc kyc){
        SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);
        if(sbnSid == null){
            return errorResponse(14, "Invalid or unregister e-mail , please check your format or Sign Up now", null);
        }

        List<SbnTransactions> sbnTransactionsList = sbnTransactionsRepository.findAllBySbnSidAndIdStatusOrderByCreatedDateDesc(sbnSid, (long) 4);
        if(sbnTransactionsList == null){
            return errorResponse(50, "Data portofolio tidak ada", null);
        }

        List<Map> lisData = new ArrayList<>();
        for (SbnTransactions sbnTransactions : sbnTransactionsList) {
            SbnPackages sbnPackages = sbnPackagesRepository.findByIdSeri(sbnTransactions.getIdSeri());
            Map transactions = new LinkedHashMap();
            transactions.put("transactionsId", sbnTransactions.getId());
            transactions.put("picture", sbnPackages.getPicture());
            transactions.put("amount", sbnTransactions.getTrxAmount());
            transactions.put("remaining", sbnTransactions.getSisaKepemilikan());
            transactions.put("packageName", sbnPackages.getPackageName());
            transactions.put("packageCode", sbnPackages.getPackageCode());
            lisData.add(transactions);
        }

        Map result = new LinkedHashMap();
        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
        result.put(ConstantUtil.DATA.toLowerCase(), lisData);
        return result;
    }

    public Map investmentSummary(Kyc kyc){
        SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);
        if(sbnSid == null){
            return errorResponse(14, "Invalid or unregister e-mail , please check your format or Sign Up now", null);
        }

        BigInteger totalInvestment = sbnTransactionsRepository.getTotalInvestment(sbnSid.getId());
        Map dataSummary = new LinkedHashMap();
        dataSummary.put("invest_amount", totalInvestment);

        Map data = new LinkedHashMap();
        data.put("summary", dataSummary);

        Map result = new LinkedHashMap();
        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), "Portofolio status loaded");
        result.put(ConstantUtil.DATA.toLowerCase(), data);
        return result;
    }

    public Map investmentDetail(Kyc kyc, Long trxId){
        SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);
        if(sbnSid == null){
            return errorResponse(14, "Invalid or unregister e-mail , please check your format or Sign Up now", null);
        }

        SbnTransactions sbnTrx = sbnTransactionsRepository.findByIdAndSbnSid(trxId, sbnSid);
        if(sbnTrx == null){
            return errorResponse(14, "transaction not found", null);
        }

        SbnPackages sbnPackages = sbnPackagesRepository.findByIdSeri(sbnTrx.getIdSeri());

        Map data = new LinkedHashMap();
        data.put("picture", sbnPackages.getPicture());
        data.put("package_name", sbnPackages.getPackageName());
        data.put("package_code", sbnPackages.getPackageCode());
        data.put("sid", sbnSid.getSid());
        data.put("amount", sbnTrx.getTrxAmount());
        data.put("remaining", sbnTrx.getSisaKepemilikan());
        
        if(sbnTrx.getTrxAmount() == 1000000){
            data.put("redeemableAmount", 0);
        }else{
            data.put("redeemableAmount", sbnTrx.getRedeemableAmount());
        }

        data.put("transactionCode", sbnTrx.getKodePemesanan());
        data.put("idSeri", sbnTrx.getIdSeri());

        List<Submidis> listSubmidis = submidisRepository.findAllBySbnPackages_Id(sbnPackages.getId());
        List listDataSub = new ArrayList();
        for(Submidis sub: listSubmidis){
            Map mapData = new LinkedHashMap();
            mapData.put("submidisName", sub.getSubmidisName());
            mapData.put("submidisPict", sub.getSubmidisPicture());
            mapData.put("submidisPrice", sub.getSubmidisPrice());
            listDataSub.add(mapData);
        }

        data.put("submidis", listDataSub);

        Map result = new LinkedHashMap();
        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), "Portofolio status loaded");
        result.put(ConstantUtil.DATA.toLowerCase(), data);
        return result;
    }

    public Map getBankList(){
        List<Object[]> listData = sbnBankRepository.getListBankWithCustomQuery();
        List listResult = new ArrayList();
        for(Object[] data: listData){
            Map mapData = new LinkedHashMap<>();
            mapData.put("bankId", sbnBankRepository.findByBankNameWithCustomQuery(data[0].toString()).getId());
            mapData.put("bankName", data[0]);
            mapData.put("imageKey", data[1]== null ? "" : data[1]);
            listResult.add(mapData);
        }

        Map result = new LinkedHashMap();
        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
        result.put(ConstantUtil.DATA.toLowerCase(), listResult);
        return result;
    }

    public Map getBankWithPayment(Long id){
        SbnBank sbnBank = sbnBankRepository.getOne(id);
        if(sbnBank == null){
            return errorResponse(14, "Invalid bank id", null);
        }

        List<Object[]> listData = sbnBankRepository.findAllByBankNameWithCustomQuery(sbnBank.getBankName());
        List listResult = new ArrayList();
        for(Object[] data: listData){
            Map dataMap = new LinkedHashMap();
            dataMap.put("bank_name", data[0]);
            dataMap.put("method", data[1]);
            dataMap.put("petunjuk", data[2]);
            listResult.add(dataMap);
        }

        Map result = new LinkedHashMap();
        result.put(ConstantUtil.CODE, 0);
        result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
        result.put(ConstantUtil.DATA.toLowerCase(), listResult);
        return result;
    }
}