package com.nsi.services;

import java.util.List;
import java.util.Map;

import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import org.springframework.web.multipart.MultipartFile;

public interface TransactionService {
  Map subscribeOrder(List<Map> orders, Kyc kyc, String statusPayment);
  Map subscribeOrderByTransfer(List<Map> orders, Kyc kyc);
  Map subscribeOrderByFinpay(List<Map> orders, Kyc kyc);
  Map subscribeOrderByTCash(List<Map> orders, Kyc kyc);
  Map subscribeOrderByWallet(List<Map> orders, Kyc kyc);
  Map topupOrder(List<Map> maps, Kyc kyc, String statusPayment);
  Map topupOrderByTransfer(List<Map> maps, Kyc kyc);
  Map topupOrderByFinpay(List<Map> maps, Kyc kyc);
  Map topupOrderByTCash(List<Map> maps, Kyc kyc);
  Map topupOrderByWallet(List<Map> maps, Kyc kyc);
  Map uploadDocument(User user, MultipartFile uploadfile, String orderNo) throws Exception;
  Map getTransactionLists(Kyc kyc, Map map);
  Map redeemOrder(List<Map> list, Kyc kyc);
  Map transactionList(Map map, User user);
  Map getRangeOfPartialByInvestment(String invNo, Kyc kyc);
  Map checkRedemptionTransaction(String investmentNumber);
  Double getRedemptionFee(InvestmentAccounts investment);
  Map executeOrderByTCash(List<String> orders, boolean success);
  Map checkRisk(String packageCode, Kyc kyc);
  Map subscribeOrTopupOrder(List<Map> maps, Kyc kyc, String statusPayment);
}
