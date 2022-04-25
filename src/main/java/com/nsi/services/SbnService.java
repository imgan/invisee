package com.nsi.services;

import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Map;

public interface SbnService {
    public Map addPemesanan(User user, Long idSeri, BigInteger nominal) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException;
    public Map validateCustomerSbn(User user) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException;
    public Map redeem(Kyc kyc, String kodePemesanan, BigInteger nominal) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException;
    public Map getKuotaBySidAndSeri(String sid, Long idSeri, Kyc kyc) throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException;
    public Map calculateRedemption(String kodePemesanan, Kyc kyc);
    public Map createFullRedeem(String kodePemesanan, Kyc kyc) throws ParseException;
    public Map transactionList(Kyc kyc);
    public Map transactionDetail(Kyc kyc, String transactionCode);
    public Map productList();
    public Map productDetail(Long productId);
    public Map investmentList(Kyc kyc);
    public Map investmentSummary(Kyc kyc);
    public Map investmentDetail(Kyc kyc, Long trxId);
    public Map getBankList() throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException;
    public Map getBankWithPayment(Long id);
}
