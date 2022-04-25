package com.nsi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsi.domain.core.*;
import com.nsi.dto.RedemptionDto;
import com.nsi.enumeration.CustomerEnum;
import com.nsi.enumeration.PaymentTypeEnumeration;
import com.nsi.repositories.core.*;
import com.nsi.services.*;
import com.nsi.util.ConstantUtil;
import com.nsi.util.DateTimeUtil;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@SuppressWarnings("ALL")
@Service
public class TransactionServiceImpl extends BaseService implements TransactionService {
  @PersistenceContext(unitName = "core")
  private EntityManager entityManager;
  @Autowired
  private FeeService feeService;
  @Autowired
  private EmailService emailService;
  @Autowired
  private PackagePaymentRepository packagePaymentRepository;
  @Autowired
  private SubcriptionJobSchedullerRepository subcriptionJobSchedullerRepository;
  @Autowired
  private UtTransactionsCartRepository utTransactionsCartRepository;
  @Autowired
  private InvestmentAccountsRepository investmentAccountsRepository;
  @Autowired
  private FundPackagesRepository fundPackagesRepository;
  @Autowired
  private GlobalService globalService;
  @Autowired
  private KycRepository kycRepository;
  @Autowired
  private UtTransactionTypeRepository utTransactionTypeRepository;
  @Autowired
  private FundPackageFeeSetupRepository fundPackageFeeSetupRepository;
  @Autowired
  private PaymentMethodRepository paymentMethodRepository;
  @Autowired
  private FundEscrowAccountRepository fundEscrowAccountRepository;
  @Autowired
  private FundPackageProductsRepository fundPackageProductsRepository;
  @Autowired
  private UtTransactionsRepository utTransactionsRepository;
  @Autowired
  private SubcriptionJobSchedullerService subscriptionJobSchedullerService;
  @Autowired
  private InvestmentAccountsService investmentAccountsService;
  @Autowired
  private UtTransactionsGroupRepository utTransactionsGroupRepository;
  @Autowired
  private CustomerBalanceRepository customerBalanceRepository;
  @Autowired
  private UtProductFundPricesRepository utProductFundPricesRepository;
  @Autowired
  private InvestmentPromotionRepository investmentPromotionRepository;
  @Autowired
  private InvestmentService investmentService;
  @Autowired
  private SettlementAccountsRepository settlementAccountsRepository;
  @Autowired
  private GlobalParameterRepository globalParameterRepository;
  @Autowired
  TransactionDocumentRepository transactionDocumentRepository;
  @Autowired
  CustomerDocumentRepository customerDocumentRepository;
  @Autowired
  ViseepayService viseepayService;
  @Autowired
  UtProductsSettlementRepository utProductsSettlementRepository;
  @Autowired
  EmailJobSchedullerRepository emailJobSchedullerRepository;
  @Autowired
  FinpayServiceImpl finpayService;
  @Autowired
  AllowedPackagesAgentRepository allowedPackagesAgentRepository;
  @Autowired
  AttachFileService attachFileService;

  private Map checkIncompleteDataMandatory(List<Map> maps, String payTrans) {
    Map resultMap = new HashMap<>();

    for (Map map : maps) {
      if (!isExistingDataAndStringValue(map.get("channel_order"))) {
        return errorResponse(10, "channel_order", null);
      }
      if (!isExistingDataAndDoubleValue(map.get("fee_amount"))) {
        return errorResponse(10, "fee_amount", null);
      }
      if (!isExistingDataAndDoubleValue(map.get("total_amount"))) {
        return errorResponse(10, "total_amount", null);
      }
      if (!payTrans.equalsIgnoreCase("REDMP") && !isExistingDataAndDateValue(map.get("price_date"))) {
        return errorResponse(10, "price_date", null);
      }

      if (payTrans.equalsIgnoreCase("SUBSCR")) {
        if (!isExistingDataAndStringValue(map.get("package_code"))) {
          return errorResponse(10, "package_code", null);
        }
        if (!isExistingDataAndDoubleValue(map.get("net_amount"))) {
          return errorResponse(10, "net_amount", null);
        }
      } else if (payTrans.equalsIgnoreCase("TOPUP")) {
        if (!isExistingDataAndStringValue(map.get("investment"))) {
          return errorResponse(10, "investment", null);
        }
        if (!isExistingDataAndDoubleValue(map.get("net_amount"))) {
          return errorResponse(10, "net_amount", null);
        }
      } else if (payTrans.equalsIgnoreCase("REDMP")) {
        if (!isExistingDataAndStringValue(map.get("investment"))) {
          return errorResponse(10, "investment", null);
        }

        if (!isExistingDataAndStringValue(map.get("amount_type"))) {
          return errorResponse(10, "amount_type", null);
        }

        if (!isExistingDataAndDoubleValue(map.get("amount_value"))) {
          return errorResponse(10, "amount_value", null);
        }
      }
    }
    return resultMap;
  }

  private Map checkIncompleteDataSubsTopUp(List<Map> maps, Agent agent) {
    Map resultMap = new HashMap<>();

    for (Map map : maps) {

      if (!isExistingDataAndStringValue(map.get("channel_order"))) {
        return errorResponse(10, "channel_order", null);
      }
      if (!isExistingDataAndDoubleValue(map.get("fee_amount"))) {
        return errorResponse(10, "fee_amount", null);
      }
      if (!isExistingDataAndDoubleValue(map.get("total_amount"))) {
        return errorResponse(10, "total_amount", null);
      }
      if (!isExistingDataAndDoubleValue(map.get("net_amount"))) {
        return errorResponse(10, "net_amount", null);
      }
      if (!isExistingDataAndStringValue(map.get("package_code"))) {
        return errorResponse(10, "package_code", null);
      }
      FundPackages fp = fundPackagesRepository.findByPackageCode((String) map.get("package_code"));
      if(fp == null){
        return errorResponse(50, "package_code :"+map.get("package_code"), null);
      }
      if(!fp.getPublishStatus()){
        return errorResponse(50, "package_code :"+map.get("package_code"), null);
      }
      AllowedPackagesAgent allowedPackagesAgent = allowedPackagesAgentRepository.findByAgent_CodeAndPackages(agent.getCode(), fp);
      if(allowedPackagesAgent == null){
        return errorResponse(50, "package_code :"+map.get("package_code"), null);
      }
      if(Double.valueOf(map.get("net_amount").toString()) < fp.getMinSubscriptionAmount()){
        return errorResponse(14, "net_amount is less than min subscription amount or min topup amount", null);
      }
    }
    return resultMap;
  }

  private UtTransactionsCart insertUTCart(FundPackages fundPackage, PaymentMethod paymentMethod, FundEscrowAccount account, Kyc kyc, UtTransactionType trxType, String orderNo,
      InvestmentAccounts investmentAccounts, Double netAmountTrx, Double feeAmount) {
    return insertUTCart(fundPackage, paymentMethod, account, kyc, trxType, orderNo, investmentAccounts, netAmountTrx, feeAmount, Boolean.FALSE);
  }

  private UtTransactionsCart insertUTCart(FundPackages fundPackage, PaymentMethod paymentMethod,
      FundEscrowAccount account, Kyc kyc, UtTransactionType trxType, String orderNo,
      InvestmentAccounts investmentAccounts, Double netAmountTrx, Double feeAmount,
      Boolean isActiveCart) {
    if (trxType.getTrxCode().equalsIgnoreCase("SUBCR")) {
      investmentAccounts = investmentAccountsService.saveInvestmentAccount(fundPackage, kyc);
    }
    UtTransactionsCart cart = new UtTransactionsCart();
    cart.setInvestmentAccount(investmentAccounts);
    cart.setFundPackages(fundPackage);
    cart.setCreatedBy(kyc.getAccount().getUsername());
    cart.setCreatedDate(new Date());
    cart.setKyc(kyc);
    cart.setNetAmount(netAmountTrx);
    cart.setFeeAmount(feeAmount);
    cart.setOrderAmount(cart.getNetAmount() + cart.getFeeAmount());
    cart.setPaymentType(paymentMethod.getCode());
    cart.setTrxDate(new Date());
    cart.setTransactionType(trxType);

    String trxStatus = "IN_ACTIVE";
    if (isActiveCart) {
      trxStatus = "ACTIVE";
    }

    cart.setTrxStatus(trxStatus);
    cart.setOrderNo(orderNo);
    if (cart.getPaymentType().equalsIgnoreCase("TCASH")) {
      cart.setSettlementRefNo("T" + DateTimeUtil.convertDateToStringCustomized(new Date(), DateTimeUtil.YYYYMMDDHHMMSSSSS));
    } else {
      cart.setSettlementRefNo(account.getVaCode() + " " + account.getEscrowNumber());
    }
    return utTransactionsCartRepository.save(cart);
  }

  private List<UtTransactions> saveUtTransactions(String channelOrderId, UtTransactionsCart cart,
      UtTransactionType trxType, FundEscrowAccount escrowAccount, Kyc kyc, Date priceDate,
      String statusPayment, String note) {
    List<UtTransactions> utTransactions = new ArrayList<>();
    List<FundPackageProducts> packageProducts = fundPackageProductsRepository
        .findAllByFundPackages(cart.getFundPackages());

    for (FundPackageProducts packageProduct : packageProducts) {

      UtTransactions utTransaction = new UtTransactions();

      Calendar newPriceDate = Calendar.getInstance();
      newPriceDate.setTime(priceDate);
      newPriceDate.set(Calendar.HOUR_OF_DAY, 13);
      newPriceDate.set(Calendar.MINUTE, 0);
      newPriceDate.set(Calendar.SECOND, 0);

      //utTransaction.setSettlementStatus("STL");
      //utTransaction.setTrxStatus("STL");
      utTransaction.setOrderNo(cart.getOrderNo());
      utTransaction.setCreatedBy(kyc.getAccount().getUsername());
      utTransaction.setCreatedDate(new Date());
      utTransaction.setFeeAmount(cart.getFeeAmount() * packageProduct.getCompositition());
      utTransaction.setChannelOrderId(kyc.getAccount().getAgent().getCode() + channelOrderId);
      utTransaction.setNetAmount(cart.getNetAmount() * packageProduct.getCompositition());
      utTransaction.setOrderAmount(utTransaction.getFeeAmount() + utTransaction.getNetAmount());
      utTransaction.setFundPackageRef(cart.getFundPackages());
      utTransaction.setPriceDate(newPriceDate.getTime()); // developer sebelumnya bapuk
      utTransaction.setTrxNotes(note);
      utTransaction.setTransactionType(trxType);
      utTransaction.setKycId(kyc);
      utTransaction.setProductId(packageProduct.getUtProducts());
      utTransaction.setSettlementAmount(utTransaction.getOrderAmount());
      utTransaction.setTrxDate(cart.getTrxDate());
      utTransaction.setTrxNo(globalService.generateTrxNo(cart.getTransactionType(), 1));
      utTransaction.setSettlementStatus(statusPayment);
      utTransaction.setTrxStatus(statusPayment);
      utTransaction.setAtTrxNo(UUID.randomUUID().toString());
      utTransaction.setTrxType(cart.getTransactionType().getId().intValue());
      utTransaction.setSettlementNoRef(escrowAccount);
      utTransaction.setTransactionDate(cart.getTrxDate());
      utTransaction.setInvestementAccount(cart.getInvestmentAccount());
      utTransaction.setTaxAmount(new Double(0));
      utTransaction = utTransactionsRepository.save(utTransaction);
      utTransactions.add(utTransaction);
    }
    return utTransactions;
  }

  private Date getPriceDate(FundPackages fundPackage, Date priceDate) {
    Calendar currentDate = Calendar.getInstance();

    Calendar fundPackageDate = Calendar.getInstance();
    fundPackageDate.setTime(fundPackage.getTransactionCutOff());
    fundPackageDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
    fundPackageDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
    fundPackageDate.set(Calendar.DATE, currentDate.get(Calendar.DATE));

    logger.info("package         : " + fundPackage.getFundPackageId());
    logger.info("currentDate     : " + currentDate.getTime());
    logger.info("fundPackageDate : " + fundPackageDate.getTime());

    if (currentDate.before(fundPackageDate)) {
      priceDate = currentDate.getTime();
    } else if (currentDate.after(fundPackageDate)) {
      priceDate = DateTimeUtil.getCustomDate(currentDate.getTime(), 1);
    }

    return globalService.getWorkingDate(priceDate);
  }

  @Transactional
  Map order(Kyc kyc, PaymentMethod paymentMethod, FundPackages fundPackage,
      InvestmentAccounts investAcc, UtTransactionType trxType, Double netAmount, Double feeAmount,
      Double totalAmount, Date datePrice, String statusPayment, String channelOrderId) {

    Double amount = netAmount + feeAmount;
    String channelName = kyc.getAccount().getAgent().getChannel().getName();

    String orderNo = null;
    try {
      orderNo = globalService.generateOrderNo(amount, channelName);
    } catch (InterruptedException e) {
      logger.error(e.getMessage(), e);
    }
    System.out.println("LEWAT : " + orderNo);

    FundEscrowAccount fundEscrow = fundEscrowAccountRepository.findByFundPackages(fundPackage);
    System.out.println("LEWAT 2: " + fundEscrow);

    Date fixDatePrice = getPriceDate(fundPackage, datePrice);
    String orderNote = "";

    System.out.println(fixDatePrice);

    Map mapNote = new HashMap<>();
    mapNote.put("net_amount", netAmount);
    mapNote.put("fee_amount", feeAmount);
    mapNote.put("total_amount", totalAmount);
    mapNote.put("price_date", DateTimeUtil.convertDateToStringCustomized(datePrice, "yyyy-MM-dd"));

    if (!fixDatePrice.equals(datePrice)) {
      orderNote = orderNo + " price date has been changed to continue transaction. ";
    }

    Double fixFeeAmount = feeService.checkFeeAmount(netAmount, feeAmount, fundPackage, trxType);

    if (!fixFeeAmount.equals(feeAmount)) {
      orderNote += orderNo + " fee amount has been changed to continue transaction. ";
    }

    //INI SPESIAL CHASE
    fixFeeAmount = feeAmount;

    UtTransactionsCart cart = insertUTCart(fundPackage, paymentMethod, fundEscrow, kyc, trxType, orderNo, investAcc, netAmount, fixFeeAmount);
    System.out.println("save cart");
    saveUtTransactions(channelOrderId, cart, trxType, fundEscrow, kyc, fixDatePrice, statusPayment, mapNote.toString());
    System.out.println("saveUtTransactions");

    String status = "X";
    if (kyc.getAccount().getUserStatus().equalsIgnoreCase("VER")) {
      status = "0";
    }
    if (paymentMethod.getCode().equalsIgnoreCase("TCASH")) {
      status = "T";
    }

    subscriptionJobSchedullerService.saveJob(orderNo, status, null, null, null, paymentMethod.getCode());
    System.out.println("saveJobs");

//        Map EscrowMap = new HashMap<>();
//        EscrowMap.put("bank_code", fundEscrow.getBank().getBankCode());
//        EscrowMap.put("bank_name", fundEscrow.getBank().getBankName());
//        EscrowMap.put("bank_account", fundEscrow.getEscrowNumber());

    FundPackageProducts fundPackageProducts = fundPackageProductsRepository.findByFundPackages(fundPackage);
    UtProductsSettlement utProductsSettlement = utProductsSettlementRepository.findByUtProduct(fundPackageProducts.getUtProducts());
    Map EscrowMap = new HashMap<>();
    EscrowMap.put("bank_code", fundEscrow.getBank().getBankCode());
    EscrowMap.put("bank_name", utProductsSettlement.getAccountName());
    EscrowMap.put("bank_account", utProductsSettlement.getAccountNumber());

    Map trxMap = new HashMap<>();
    trxMap.put("code_trans", statusPayment);

    if (statusPayment.equalsIgnoreCase("ORD")) {
      trxMap.put("name_trans", "MENUNGGU PEMBAYARAN");
    } else if (statusPayment.equalsIgnoreCase("STL")) {
      trxMap.put("name_trans", "SUDAH DIBAYAR");
    } else if (statusPayment.equalsIgnoreCase("ALL")) {
      trxMap.put("name_trans", "SELESEI");
    } else if (statusPayment.equalsIgnoreCase("CAN")) {
      trxMap.put("name_trans", "TRANSAKSI DIBATALKAN");
    }

    Map xMap = new HashMap<>();
    xMap.put("order_number", orderNo);
    xMap.put("investment_account", cart.getInvestmentAccount().getInvestmentAccountNo());
    xMap.put("channel_order", channelOrderId);
    xMap.put("package_code", cart.getFundPackages().getPackageCode());
    xMap.put("net_amount", cart.getNetAmount());
    xMap.put("fee_amount", cart.getFeeAmount());
    xMap.put("total_amount", cart.getOrderAmount());
    xMap.put("price_date", DateTimeUtil.convertDateToStringCustomized(fixDatePrice, "yyyy-MM-dd"));
    xMap.put("order_note", orderNote);
    if (statusPayment.equalsIgnoreCase("ORD")) {
      xMap.put("settlement_cut_off", DateTimeUtil.convertDateToStringCustomized(fixDatePrice, "yyyy-MM-dd") + " " + fundPackage.getSettlementCutOff());
    }
    xMap.put("payment_method", paymentMethod.getName());
    if (paymentMethod.getCode().equalsIgnoreCase("finpay")){
      Map finpay = finpayService.topUpFinpay(kyc.getAccount(), cart.getOrderNo());
      xMap.put("finpay", finpay);
    }else {
      xMap.put("bank_transfer", EscrowMap);
    }
    xMap.put("type_trans", trxType.getTrxName());
    xMap.put("status_trans", trxMap);

    System.out.println("xmap : " + xMap);
    if (!paymentMethod.getCode().equalsIgnoreCase("TCASH")) {
      logger.info("paymentMethod is other than TCASH");
      if (kyc.getAccount().getAgent().getEmailCustom()) {
        //do not send email
      } else {
        if (statusPayment.equalsIgnoreCase("ORD")) {
          logger.info("if statusPayment is 'ORD'");
          emailService.sendOrderTransaction(cart.getInvestmentAccount(), orderNo, fundPackage, trxType);
        } else {
          logger.info("if statusPayment is not 'ORD'");
          emailService.sendSettlementTransaction(kyc, xMap);
        }
      }

    }
    logger.info("ini data yang bakal dibalikin : " + xMap);

    return xMap;
  }

  Map orderWithoutTransactional(Kyc kyc, PaymentMethod paymentMethod, FundPackages fundPackage,
            InvestmentAccounts investAcc, UtTransactionType trxType, Double netAmount, Double feeAmount,
            Double totalAmount, Date datePrice, String statusPayment, String channelOrderId) {

    Double amount = netAmount + feeAmount;
    String channelName = kyc.getAccount().getAgent().getChannel().getName();

    String orderNo = null;
    try {
      orderNo = globalService.generateOrderNo(amount, channelName);
    } catch (InterruptedException e) {
      logger.error(e.getMessage(), e);
    }

    FundEscrowAccount fundEscrow = fundEscrowAccountRepository.findByFundPackages(fundPackage);

    Date fixDatePrice = getPriceDate(fundPackage, datePrice);
    String orderNote = "";

    Map mapNote = new HashMap<>();
    mapNote.put("net_amount", netAmount);
    mapNote.put("fee_amount", feeAmount);
    mapNote.put("total_amount", totalAmount);
    mapNote.put("price_date", DateTimeUtil.convertDateToStringCustomized(datePrice, "yyyy-MM-dd"));

    if (!fixDatePrice.equals(datePrice)) {
      orderNote = orderNo + " price date has been changed to continue transaction. ";
    }

    Double fixFeeAmount = feeService.checkFeeAmount(netAmount, feeAmount, fundPackage, trxType);

    if (!fixFeeAmount.equals(feeAmount)) {
      orderNote += orderNo + " fee amount has been changed to continue transaction. ";
    }

    //INI SPESIAL CHASE
    fixFeeAmount = feeAmount;

    UtTransactionsCart cart = insertUTCart(fundPackage, paymentMethod, fundEscrow, kyc, trxType, orderNo, investAcc, netAmount, fixFeeAmount);
    saveUtTransactions(channelOrderId, cart, trxType, fundEscrow, kyc, fixDatePrice, statusPayment, mapNote.toString());

    String status = "X";
    if (kyc.getAccount().getUserStatus().equalsIgnoreCase("VER")) {
      status = "0";
    }
    if (paymentMethod.getCode().equalsIgnoreCase("TCASH")) {
      status = "T";
    }

    subscriptionJobSchedullerService.saveJob(orderNo, status, null, null, null, paymentMethod.getCode());

    List<FundPackageProducts> listFpp = fundPackageProductsRepository.findAllByFundPackages(fundPackage);
    List listEscrowAccount = new ArrayList();
    for(FundPackageProducts fpp : listFpp){
      UtProductsSettlement utProductsSettlement = utProductsSettlementRepository.findByUtProduct(fpp.getUtProducts());
      Map escrowMap = new HashMap<>();
      escrowMap.put("bank_code", fundEscrow.getBank().getBankCode());
      escrowMap.put("bank_name", utProductsSettlement.getAccountName());
      escrowMap.put("bank_account", utProductsSettlement.getAccountNumber());
      listEscrowAccount.add(escrowMap);
    }

    Map trxMap = new HashMap<>();
    trxMap.put("code_trans", statusPayment);

    if (statusPayment.equalsIgnoreCase("ORD")) {
      trxMap.put("name_trans", "MENUNGGU PEMBAYARAN");
    } else if (statusPayment.equalsIgnoreCase("STL")) {
      trxMap.put("name_trans", "SUDAH DIBAYAR");
    } else if (statusPayment.equalsIgnoreCase("ALL")) {
      trxMap.put("name_trans", "SELESEI");
    } else if (statusPayment.equalsIgnoreCase("CAN")) {
      trxMap.put("name_trans", "TRANSAKSI DIBATALKAN");
    }

    Map xMap = new HashMap<>();
    xMap.put("order_number", orderNo);
    xMap.put("investment_account", cart.getInvestmentAccount().getInvestmentAccountNo());
    xMap.put("channel_order", channelOrderId);
    xMap.put("package_code", cart.getFundPackages().getPackageCode());
    xMap.put("net_amount", cart.getNetAmount());
    xMap.put("fee_amount", cart.getFeeAmount());
    xMap.put("total_amount", cart.getOrderAmount());
    xMap.put("price_date", DateTimeUtil.convertDateToStringCustomized(fixDatePrice, "yyyy-MM-dd"));
    xMap.put("order_note", orderNote);
    if (statusPayment.equalsIgnoreCase("ORD")) {
      xMap.put("settlement_cut_off", DateTimeUtil.convertDateToStringCustomized(fixDatePrice, "yyyy-MM-dd") + " " + fundPackage.getSettlementCutOff());
    }
    xMap.put("payment_method", paymentMethod.getName());
    if (paymentMethod.getCode().equalsIgnoreCase("finpay")){
      Map finpay = finpayService.topUpFinpay(kyc.getAccount(), cart.getOrderNo());
      xMap.put("finpay", finpay);
    }else {
      xMap.put("bank_transfer", listEscrowAccount);
    }
    xMap.put("type_trans", trxType.getTrxName());
    xMap.put("status_trans", trxMap);

    System.out.println("xmap : " + xMap);
    if (!paymentMethod.getCode().equalsIgnoreCase("TCASH")) {
      logger.info("paymentMethod is other than TCASH");
      if (kyc.getAccount().getAgent().getEmailCustom()) {
        //do not send email
      } else {
        if (statusPayment.equalsIgnoreCase("ORD")) {
          logger.info("if statusPayment is 'ORD'");
          emailService.sendOrderTransaction(cart.getInvestmentAccount(), orderNo, fundPackage, trxType);
        } else {
          logger.info("if statusPayment is not 'ORD'");
          emailService.sendSettlementTransaction(kyc, xMap);
        }
      }

    }
    logger.info("ini data yang bakal dibalikin : " + xMap);

    return xMap;
  }

  @Override
  public Map subscribeOrderByWallet(List<Map> orders, Kyc kyc) {
    Double netAmount = 0.0;
    for (Map order : orders) {
      netAmount += Double.valueOf(order.get("net_amount").toString());
    }

    Map bal = viseepayService.checkBalance(kyc);
    if (!bal.get("code").equals(0)) {
      return bal;
    } else {
      Map data = (Map) bal.get("data");
      double balance = (double) data.get("balance");
      if (balance < netAmount) {
        return errorResponse(12, "subscription", "balance saldo viseepay tidak mencukupi");
      }
      PaymentMethod paymentMethod = paymentMethodRepository.findByCode("WALL");
      return subscribeOrder(orders, kyc, "STL", paymentMethod);

    }
  }

  @Override
  public Map subscribeOrder(List<Map> maps, Kyc kyc, String statusPayment) {
    PaymentMethod paymentMethod = paymentMethodRepository.findByCode("CHAN");
    return subscribeOrder(maps, kyc, statusPayment, paymentMethod);
  }

  @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Map subscribeOrTopupOrder(List<Map> maps, Kyc kyc, String statusPayment) {
    PaymentMethod paymentMethod = paymentMethodRepository.findByCode("CHAN");
    return subscribeOrTopupOrder(maps, kyc, statusPayment, paymentMethod);
  }

  @Override
  public Map subscribeOrderByTCash(List<Map> maps, Kyc kyc) {
    PaymentMethod paymentMethod = paymentMethodRepository.findByCode("TCASH");
    Map mapSub = subscribeOrder(maps, kyc, "ORD", paymentMethod);

    if (mapSub.get("code").equals(0)) {
      try {
        List<Map> mapList = (List<Map>) mapSub.get("data");
        return sendToTcash(mapList);
      } catch (IOException e) {
    	  logger.error("[FATAL]" ,e);
        return errorResponse(50, "subscription tcash", null);
      }
    } else {
      return mapSub;
    }
  }

  @Override
  public Map subscribeOrderByTransfer(List<Map> maps, Kyc kyc) {
    PaymentMethod paymentMethod = paymentMethodRepository.findByCode("CTRAN");
    return subscribeOrder(maps, kyc, "ORD", paymentMethod);
  }

  @Override
  public Map subscribeOrderByFinpay(List<Map> orders, Kyc kyc) {
    PaymentMethod paymentMethod = paymentMethodRepository.findByCode("FINPAY");
    return subscribeOrder(orders, kyc, "ORD", paymentMethod);
  }

  private Map subscribeOrder(List<Map> maps, Kyc kyc, String statusPayment,
      PaymentMethod paymentMethod) {
    Map resultMap = this.checkIncompleteDataMandatory(maps, "SUBCR");
    if (!resultMap.isEmpty()) {
      return resultMap;
    }
    System.out.println("##############");
    UtTransactionType trxType = utTransactionTypeRepository.findByTrxCode("SUBCR");

    // TODO : Get request Order
    List<Map> dataMap = new ArrayList<>();

    for (Map map : maps) {
      System.out.println("FUNDPACKAGE CODE : " + map.get("package_code"));
      FundPackages fundPackage = fundPackagesRepository.findByPackageCode(String.valueOf(map.get("package_code")));
      if(fundPackage == null){
        return errorResponse(50, "package_code :"+String.valueOf(map.get("package_code")), null);
      }

      if(!fundPackage.getAllowedSubscription() && trxType.getTrxCode().equals("SUBCR")){
        return errorResponse(88, "package_code :"+String.valueOf(map.get("package_code")), "Transaksi tidak dapat dilakukan pada produk ini");
      }
      System.out.println("FUNDPACKAGE : " + fundPackage);

      Double netAmount = Double.valueOf(map.get("net_amount").toString());
      Double feeAmount = Double.valueOf(map.get("fee_amount").toString());
      Double totAmount = Double.valueOf(map.get("total_amount").toString());
      Date priceDate = DateTimeUtil
          .convertStringToDateCustomized(map.get("price_date").toString(), DateTimeUtil.API_MCW);
      String channelOrder = map.get("channel_order").toString();

      try {
        System.out.println("mapx");
        Map mapx = order(kyc, paymentMethod, fundPackage, null, trxType, netAmount, feeAmount,
            totAmount, priceDate, statusPayment, channelOrder);
        logger.trace("return from order : " + mapx);
        dataMap.add(mapx);
        logger.trace("current mapx : " + dataMap);
        if (paymentMethod.getCode().equalsIgnoreCase("WALL")) {
          viseepayService.trx(kyc, mapx.get("order_number").toString());
        }
        if (kyc.getAccount().getAgent().getEmailCustom()) {
          //do not save email scheduller
        } else {
          EmailJobScheduller emailJobScheduller = new EmailJobScheduller();
          emailJobScheduller.setOrderNo(mapx.get("order_number").toString());
          emailJobScheduller.setEmailType("MCW_SUBSCRIBE_SUMMARY");
          emailJobScheduller.setStatus("0");
          emailJobSchedullerRepository.save(emailJobScheduller);
        }

      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }

    resultMap.put("code", 0);
    resultMap.put("info", "Order successfully submitted");
    resultMap.put("data", dataMap);
    return resultMap;
  }

  private Map subscribeOrTopupOrder(List<Map> maps, Kyc kyc, String statusPayment, PaymentMethod paymentMethod) {
    Map resultMap = this.checkIncompleteDataSubsTopUp(maps, kyc.getAccount().getAgent());
    if (!resultMap.isEmpty()) {
      return resultMap;
    }

    List<Map> dataMap = new ArrayList<>();
    for (Map map : maps) {
      UtTransactionType trxType = null;
      InvestmentAccounts ia = null;
      FundPackages fundPackage = fundPackagesRepository.findByPackageCode(String.valueOf(map.get("package_code")));
      if(fundPackage == null){
        return errorResponse(50, "package_code :"+String.valueOf(map.get("package_code")), null);
      }

      String sql = "SELECT " +
              "cb.inv_account_id " +
              "FROM " +
              "customer_balance_latest cb " +
              "JOIN investment_accounts ia ON ( ia.investment_account_id = cb.inv_account_id AND cb.customer_id = ia.kycs_id ) " +
              "JOIN fund_packages fp ON ( ia.fund_packages_id = fp.fund_package_id ) " +
              "WHERE " +
              "fp.package_code=:packageCode " +
              "AND cb.customer_id=:customerId "+
              "AND cb.current_amount > 0";
      List<BigInteger> listInvAcctId = entityManager.createNativeQuery(sql).setParameter("packageCode", map.get("package_code")).setParameter("customerId", kyc.getId()).getResultList();
      if(listInvAcctId.size() > 0){
        trxType = utTransactionTypeRepository.findByTrxCode("TOPUP");
        if(!fundPackage.getAllowedTopup() && trxType.getTrxCode().equals("TOPUP")){
          return errorResponse(88, "package_code :"+String.valueOf(map.get("package_code")), "Transaksi tidak dapat dilakukan pada produk ini");
        }

        ia = investmentAccountsRepository.getOne(listInvAcctId.get(0).longValue());
      }else{
        Long invAccid = utTransactionsRepository.checkTransactionOrdered(kyc.getId());
        if(invAccid != null){
          trxType = utTransactionTypeRepository.findByTrxCode("TOPUP");
          if(!fundPackage.getAllowedTopup() && trxType.getTrxCode().equals("TOPUP")){
            return errorResponse(88, "package_code :"+String.valueOf(map.get("package_code")), "Transaksi tidak dapat dilakukan pada produk ini");
          }
          ia = investmentAccountsRepository.getOne(invAccid);
        }else{
          trxType = utTransactionTypeRepository.findByTrxCode("SUBCR");
          if(!fundPackage.getAllowedSubscription() && trxType.getTrxCode().equals("SUBCR")){
            return errorResponse(88, "package_code :"+String.valueOf(map.get("package_code")), "Transaksi tidak dapat dilakukan pada produk ini");
          }
        }
      }

      Double netAmount = Double.valueOf(map.get("net_amount").toString());
      Double feeAmount = Double.valueOf(map.get("fee_amount").toString());
      Double totAmount = Double.valueOf(map.get("total_amount").toString());
      Date priceDate = DateTimeUtil.clearTime(new Date());
      String channelOrder = map.get("channel_order").toString();

      try {
        Map mapx = orderWithoutTransactional(kyc, paymentMethod, fundPackage, ia, trxType, netAmount, feeAmount, totAmount, priceDate, statusPayment, channelOrder);
        logger.trace("return from order : " + mapx);
        dataMap.add(mapx);
        logger.trace("current mapx : " + dataMap);
        if (paymentMethod.getCode().equalsIgnoreCase("WALL")) {
          viseepayService.trx(kyc, mapx.get("order_number").toString());
        }
        if (kyc.getAccount().getAgent().getEmailCustom()) {
          //do not save email scheduller
        } else {
          EmailJobScheduller emailJobScheduller = new EmailJobScheduller();
          emailJobScheduller.setOrderNo(mapx.get("order_number").toString());
          emailJobScheduller.setEmailType("MCW_SUBSCRIBE_SUMMARY");
          emailJobScheduller.setStatus("0");
          emailJobSchedullerRepository.save(emailJobScheduller);
        }

      } catch (Exception e) {
        throw e;
      }
    }

    resultMap.put("code", 0);
    resultMap.put("info", "Order successfully submitted");
    resultMap.put("data", dataMap);
    return resultMap;
  }

  // TODO: TOPUP
  @Override
  public Map topupOrderByWallet(List<Map> maps, Kyc kyc) {
    Double netAmount = 0.0;
    for (Map order : maps) {
      netAmount += Double.valueOf(order.get("net_amount").toString());
    }

    Map bal = viseepayService.checkBalance(kyc);
    if (!bal.get("code").equals(0)) {
      return bal;
    } else {
      Map data = (Map) bal.get("data");
      double balance = (double) data.get("balance");
      if (balance < netAmount) {
        return errorResponse(12, "topup", "balance saldo viseepay tidak mencukupi");
      }
      PaymentMethod paymentMethod = paymentMethodRepository.findByCode("WALL");
      return topupOrder(maps, kyc, "STL", paymentMethod);
    }
  }

  @Override
  public Map topupOrder(List<Map> maps, Kyc kyc, String statusPayment) {
    PaymentMethod paymentMethod = paymentMethodRepository.findByCode("CHAN");
    return topupOrder(maps, kyc, statusPayment, paymentMethod);
  }

  @Override
  public Map topupOrderByTransfer(List<Map> maps, Kyc kyc) {
    PaymentMethod paymentMethod = paymentMethodRepository.findByCode("CTRAN");
    return topupOrder(maps, kyc, "ORD", paymentMethod);
  }

  @Override
  public Map topupOrderByFinpay(List<Map> maps, Kyc kyc) {
    PaymentMethod paymentMethod = paymentMethodRepository.findByCode("FINPAY");
    return topupOrder(maps, kyc, "ORD", paymentMethod);
  }

  @Override
  public Map topupOrderByTCash(List<Map> maps, Kyc kyc) {
    PaymentMethod paymentMethod = paymentMethodRepository.findByCode("TCASH");
    Map mapSub = topupOrder(maps, kyc, "ORD", paymentMethod);
    if (mapSub.get("code").equals(0)) {
      try {
        List<Map> mapList = (List<Map>) mapSub.get("data");
        return sendToTcash(mapList);
      } catch (IOException e) {
    	  logger.error("[FATAL]" ,e);
        return errorResponse(50, "subscription tcash", null);
      }
    } else {
      return mapSub;
    }
  }

  public Map topupOrder(List<Map> maps, Kyc kyc, String statusPayment,
      PaymentMethod paymentMethod) {
    // TODO: Cek Mandatory data order
    Map resultMap = this.checkIncompleteDataMandatory(maps, "TOPUP");
    if (!resultMap.isEmpty()) {
      return resultMap;
    }

    UtTransactionType trxType = utTransactionTypeRepository.findByTrxCode("TOPUP");

    // TODO : Get request Order
    List<Map> dataMap = new ArrayList<>();
    for (Map map : maps) {
      InvestmentAccounts investmentAccounts = investmentAccountsRepository.findByInvestmentAccountNo(String.valueOf(map.get("investment")));
      if (investmentAccounts == null) {
        return errorResponse(50, "invesment : " + map.get("investment"), null);
      } else if (!investmentAccounts.getKycs().getId().equals(kyc.getId())) {
        return errorResponse(14, "investment : " + map.get("investment"), null);
      }

      if(!investmentAccounts.getFundPackages().getAllowedTopup()){
        return errorResponse(88, "investment : " + map.get("investment"), "Transaksi tidak dapat dilakukan pada investment ini");
      }

      List<UtTransactions> listPendingTrxRedeem = utTransactionsRepository.getPendingTrxRedemp(investmentAccounts.getId());
//      if(listPendingTrxRedeem.size() > 0){
//        return errorResponse(12, investmentAccounts.getInvestmentAccountNo() + ", Investasi ini sedang dalam proses penjualan kembali", null);
//      }
    }

    for (Map map : maps) {
      InvestmentAccounts investmentAccounts = investmentAccountsRepository.findByInvestmentAccountNo(String.valueOf(map.get("investment")));
      Double netAmount = Double.valueOf(map.get("net_amount").toString());
      Double feeAmount = Double.valueOf(map.get("fee_amount").toString());
      Double totAmount = Double.valueOf(map.get("total_amount").toString());
      Date priceDate = DateTimeUtil
          .convertStringToDateCustomized(map.get("price_date").toString(), DateTimeUtil.API_MCW);
      String channelOrder = map.get("channel_order").toString();

      try {
        Map mapx = order(kyc, paymentMethod, investmentAccounts.getFundPackages(),
            investmentAccounts, trxType, netAmount, feeAmount, totAmount, priceDate, statusPayment,
            channelOrder);
        dataMap.add(mapx);
        if (paymentMethod.getCode().equalsIgnoreCase("WALL")) {
          viseepayService.trx(kyc, mapx.get("order_number").toString());
        }
        if (kyc.getAccount().getAgent().getEmailCustom()) {
          //do not save email scheduller
        } else {
          EmailJobScheduller emailJobScheduller = new EmailJobScheduller();
          emailJobScheduller.setOrderNo(mapx.get("order_number").toString());
          emailJobScheduller.setEmailType("MCW_SUBSCRIBE_SUMMARY");
          emailJobScheduller.setStatus("0");
          emailJobSchedullerRepository.save(emailJobScheduller);
        }
      } catch (Exception e) {
    	  logger.error("[FATAL]" ,e);
      }
    }

    resultMap.put("code", 0);
    resultMap.put("info", "Topup successfully submitted");
    resultMap.put("data", dataMap);
    return resultMap;
  }

  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Map uploadDocument(User user, MultipartFile uploadfile, String orderNo) throws Exception {
    Map result = new HashMap();

    if (!isExistingData(user) || !isExistingData(uploadfile) || !isExistingData(orderNo)) {
      result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
      result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, null, null));
      return result;
    }

    GlobalParameter globalPath = globalParameterRepository.findByName(ConstantUtil.GLOBAL_PARAM_CUSTOMER_FILE_PATH);
    if (globalPath == null) {
      result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
      result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, null, null));
      return result;
    }

    Kyc kyc = kycRepository.findByAccount(user);
    if (kyc == null) {
      result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
      result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, null, null));
      return result;
    }

    List<UtTransactions> list = utTransactionsRepository.findAllByOrderNoAndKycId(orderNo, kyc);
    if (list == null || list.isEmpty()) {
      result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
      result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "order number", null));
      return result;
    }

    UtTransactions ut = list.get(0);
    if (!ut.getTrxStatus().equalsIgnoreCase("ORD")) {
      result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
      result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "not order status", null));
      return result;
    }

    String filename = uploadfile.getOriginalFilename();
    Long fileSize = uploadfile.getSize();

    String fileNameToDb = System.currentTimeMillis() + "_" + filename;
    String tmpFilePath = System.getProperty("user.dir") + "/" +fileNameToDb;
    String filepath = Paths.get(globalPath.getValue(), fileNameToDb).toString();

    String contentType = uploadfile.getContentType();

    TransactionDocument td = transactionDocumentRepository.findByOrderNo(orderNo);
    CustomerDocument cd;

    if (td != null) {
      cd = td.getCustomerDocument();
    } else {
      cd = new CustomerDocument();
      cd.setUser(user);
      cd.setFileLocation(filepath);

      uploadfile.transferTo(new File(tmpFilePath));
      attachFileService.uploadToAwsS3(tmpFilePath, cd.getFileLocation());

      cd.setFileName(filename);
      cd.setFileType(contentType);
      cd.setFileSize(fileSize);
      cd.setFileKey(UUID.randomUUID().toString());
      cd.setDocumentType("CusTrans01");
      cd.setSourceType(CustomerEnum._CUSTOMER.getName());
      cd.setRowStatus(false);
      cd.setCreatedBy(user.getUsername());
      cd.setCreatedOn(new Date());
      cd.setEndedOn(DateTimeUtil.convertStringToDateCustomized("9999-12-31", DateTimeUtil.API_MCW));
      cd.setVersion(0);
      cd = customerDocumentRepository.saveAndFlush(cd);

      File temp = new File(tmpFilePath);
      if(temp.isFile()){
        temp.delete();
      }

      td = new TransactionDocument();
      td.setCustomerDocument(cd);
      td.setOrderNo(orderNo);
      transactionDocumentRepository.saveAndFlush(td);
    }

    Map data = new HashMap();
    data.put(ConstantUtil.KYC, kyc);
    data.put(ConstantUtil.DOCUMENT, td);

    result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
    result.put(ConstantUtil.DATA, data);
    return result;
  }

  @Override
  public Map getTransactionLists(Kyc kyc, Map map) {
    String where = " ";

    String queryPendingOrder = ""
        + "and ((ut_transaction_type.trx_name IN ('SUBSCRIPTION','TOPUP') AND lookup_line.value IN ('ORDERED','SETTLED')) OR "
        + "(ut_transaction_type.trx_name = 'REDEMPTION' AND lookup_line.value IN ('ORDERED')))";

    Query query = null;

    if (String.valueOf(map.get("account_no")) != "") {
      where += " and investment_accounts.investment_account_no = :accountNo ";
    }

    if (String.valueOf(map.get("package_name")) != "") {
      where += " and fund_packages.fund_package_name = :pkgname ";
    }

    if (String.valueOf(map.get("transaction_status")) != "") {
      if (String.valueOf(map.get("transaction_status")).equals("PENDINGORDER")) {
        where += queryPendingOrder;
      } else {
        where += " and lookup_line.value ilike :trxStatus ";
      }
    }

    if (String.valueOf(map.get("transaction_type")) != "") {
      where += " and ut_transaction_type.trx_name = :trxType ";
    }

    String sql = "SELECT " + "max (ut_transactions.transaction_date) as trans_date, "
        + "max (TO_CHAR (ut_transactions.created_date,'HH24:MI:SS')) as trans_time, "
        + "ut_transactions.order_no, "
        + "investment_accounts.investment_account_no as investment_number, "
        + "fund_packages.fund_package_name AS package_name, "
        + "ut_transaction_type.trx_name AS transactions_type, "
        + "lookup_line.value AS transaction_status, "
        + "sum(ut_transactions.order_amount) as Amount, "
        + "fund_packages.package_code AS package_code "
        + "FROM " + "ut_transactions " + "LEFT JOIN " + "fund_packages " + "ON "
        + "ut_transactions.fund_package_ref_id = fund_packages.fund_package_id " + "LEFT JOIN "
        + "ut_transaction_type " + "ON "
        + "ut_transactions.transaction_type_id = ut_transaction_type.trx_id "
        + "LEFT JOIN " + "lookup_line " + "ON " + "ut_transactions.trx_status = lookup_line.code "
        + "LEFT JOIN " + "investment_accounts " + "ON "
        + "ut_transactions.investement_account_id = investment_accounts.investment_account_id "
        + "WHERE "
        + " 1=1 and kyc_id_id = :kycid " + where + " " + "GROUP BY " + "ut_transactions.order_no, "
        + "investment_number, " + "fund_packages.fund_package_name, "
        + "ut_transactions.transaction_type_id, "
        + "ut_transaction_type.trx_name, " + "ut_transactions.trx_status, " + "lookup_line.value, "
        + "fund_packages.package_code " + "ORDER BY trans_date DESC, trans_time DESC "
        + "LIMIT :limit OFFSET :offset ";

    System.out.println("SQL : " + sql);

    query = entityManager.createNativeQuery(sql).setParameter("kycid", kyc.getId());

    Integer limit = 0;
    Integer offset = 0;
    if (map.get("limit") == null) {
      limit = 100;
    } else {
      limit = Integer.valueOf(String.valueOf(map.get("limit")));
    }
    query.setParameter("limit", limit);

    if (map.get("offset") == null) {
      offset = 0;
    } else {
      offset = Integer.valueOf(String.valueOf(map.get("offset")));
    }
    query.setParameter("offset", offset);

    if (String.valueOf(map.get("account_no")) != "") {
      query.setParameter("accountNo", String.valueOf(map.get("account_no")));
    }

    if (String.valueOf(map.get("package_name")) != "") {
      query.setParameter("pkgname", String.valueOf(map.get("package_name")));
    }

    if (String.valueOf(map.get("transaction_status")) != "") {
      query.setParameter("trxStatus", String.valueOf(map.get("transaction_status")));
    }

    if (String.valueOf(map.get("transaction_type")) != "") {
      query.setParameter("trxType", String.valueOf(map.get("transaction_type")));
    }

    List<Map> transactions = new ArrayList<>();

    List<Object[]> list = query.getResultList();
    for (Object[] obj : list) {
      InvestmentAccounts investmentAccounts = investmentAccountsRepository
          .findByInvestmentAccountNo(String.valueOf(obj[3]));
      List<UtTransactionsCart> utCart = utTransactionsCartRepository
          .findAllByOrderNoAndInvestmentAccount(String.valueOf(obj[2]), investmentAccounts);
      String paymentType = "-";

      if (utCart.size() > 0) {
        if (PaymentTypeEnumeration.TRANSFER_VA_PAYMENTTYPE.getKey()
            .equals(utCart.get(0).getPaymentType())) {
          paymentType = PaymentTypeEnumeration.TRANSFER_VA_PAYMENTTYPE.getStatus();
        } else if (PaymentTypeEnumeration.WALLET_PAYMENTTYPE.getKey()
            .equals(utCart.get(0).getPaymentType())) {
          paymentType = PaymentTypeEnumeration.WALLET_PAYMENTTYPE.getStatus();
        } else if (PaymentTypeEnumeration.TRANSFER_PAYMENTTYPE.getKey()
            .equals(utCart.get(0).getPaymentType())) {
          paymentType = PaymentTypeEnumeration.TRANSFER_PAYMENTTYPE.getStatus();
        }
      }

      Map trx = new HashMap<>();
      trx.put("account_no", obj[3]);
      trx.put("order_number", obj[2]);
      trx.put("amount", obj[7]);

      String[] strings = String.valueOf(obj[1]).split(":");
      Calendar calendar = Calendar.getInstance();
      calendar.setTime((Date) obj[0]);
      calendar.set(Calendar.HOUR, Integer.parseInt(strings[0]));
      calendar.set(Calendar.MINUTE, Integer.parseInt(strings[1]));
      calendar.set(Calendar.SECOND, Integer.parseInt(strings[2]));

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

      trx.put("date", sdf.format(calendar.getTime()));
      trx.put("type", paymentType);
      trx.put("status", obj[6]);
      trx.put("package_code", obj[8]);
      trx.put("package_name", obj[4]);
      transactions.add(trx);
    }
    Map maps = new HashMap<>();
    maps.put("transaction", transactions);

    Map resultMap = new HashMap<>();
    resultMap.put("code", 0);
    resultMap.put("info", "Transaction list successfully loaded");
    resultMap.put("data", maps);
    return resultMap;
  }

  private Map checkIncompleteDataMandatoryRedeem(Map map) {
    Map resultMap = new HashMap<>();
    if (String.valueOf(map.get("channel_order")).equals("")) {
      resultMap.put("code", 10);
      resultMap.put("info", "incomplete data channel order");
      return resultMap;
    }

    if (String.valueOf(map.get("investment")).equals("")) {
      resultMap.put("code", 10);
      resultMap.put("info", "incomplete data investment");
      return resultMap;
    }

    if (String.valueOf(map.get("amount_type")).equals("")) {
      resultMap.put("code", 10);
      resultMap.put("info", "incomplete data type of amount");
      return resultMap;
    }

    if (String.valueOf(map.get("amount_value")).equals("")) {
      resultMap.put("code", 10);
      resultMap.put("info", "incomplete data value of amount");
      return resultMap;
    }

    if (String.valueOf(map.get("fee_amount")).equals("")) {
      resultMap.put("code", 10);
      resultMap.put("info", "incomplete data fee amount");
      return resultMap;
    }
    if (String.valueOf(map.get("total_amount")).equals("")) {
      resultMap.put("code", 10);
      resultMap.put("info", "incomplete data total amount");
      return resultMap;
    }
    if (String.valueOf(map.get("price_date")).equals("")) {
      resultMap.put("code", 10);
      resultMap.put("info", "incomplete data price date");
      return resultMap;
    }
    return resultMap;
  }

  public Map transactionList(Map map, User user) {
    Map result = new HashMap();
    try {
      Kyc kyc = kycRepository.findByAccount(user);
      if (kyc == null) {
        result.put("code", 50);
        result.put("info", "Data not found : customer");
        return result;
      }

      String sql = "select trx.investementAccount.investmentAccountNo, "
          + "trx.orderNo, sum(trx.netAmount), max(trx.createdDate) as createdDate, "
          + "trx.transactionType.trxName, trx.trxStatus, trx.fundPackageRef.packageCode, "
          + "trx.fundPackageRef.fundPackageName, trx.orderUnit, trx.productId.id "
          + "from UtTransactions trx "
          + "where trx.kycId=:kyc ";

      /*String gropAndOrder = " group by trx.investementAccount.investmentAccountNo, trx.orderNo, "
          + "trx.transactionType.trxName, trx.trxStatus, "
          + "trx.fundPackageRef.packageCode, trx.fundPackageRef.fundPackageName "
          + "order by trx.orderNo, trx.investementAccount.investmentAccountNo asc";*/

      String gropAndOrder ;
      if ("BLANJA".equals((String) map.get("agent"))) {
        gropAndOrder = " group by trx.investementAccount.investmentAccountNo, trx.orderNo, "
                + "trx.transactionType.trxName, trx.trxStatus, trx.createdDate, "
                + "trx.fundPackageRef.packageCode, trx.fundPackageRef.fundPackageName,trx.orderUnit,trx.productId.id "
                + "order by trx.createdDate desc";
      } else if("IIMINVAPI".equals(user.getAgent().getCode())){
        gropAndOrder = " group by trx.investementAccount.investmentAccountNo, trx.orderNo, "
                + "trx.transactionType.trxName, trx.trxStatus, "
                + "trx.fundPackageRef.packageCode, trx.fundPackageRef.fundPackageName,trx.orderUnit,trx.productId.id "
                + "order by createdDate desc";
      }else {
        gropAndOrder = " group by trx.investementAccount.investmentAccountNo, trx.orderNo, "
                + "trx.transactionType.trxName, trx.trxStatus, "
                + "trx.fundPackageRef.packageCode, trx.fundPackageRef.fundPackageName,trx.orderUnit,trx.productId.id "
                + "order by trx.orderNo, trx.investementAccount.investmentAccountNo asc";
      }

      String where = "";
      if (map.get("investment") != null && !"".equals(map.get("investment").toString().trim())) {
        where =
            where + " and trx.investementAccount.investmentAccountNo like :investmentAccountNo ";
      }

      if (map.get("period_start") != null && !"".equals(map.get("period_start").toString().trim())
          && map.get("period_end") != null && !"".equals(map.get("period_end").toString().trim())) {
        where = where + " and trx.createdDate between :periodStart and :periodEnd";
      }

      if (map.get("package_name") != null && !""
          .equals(map.get("package_name").toString().trim())) {
        where = where + " and trx.fundPackageRef.fundPackageName like :packageName";
      }

      if (map.get("transaction_status") != null && !""
          .equals(map.get("transaction_status").toString().trim())) {
        String trxStatus = map.get("transaction_status").toString().trim();
        if (!"ORD".equalsIgnoreCase(trxStatus) && !"CAN".equalsIgnoreCase(trxStatus)
            && !"ALL".equalsIgnoreCase(trxStatus) && !"STL".equalsIgnoreCase(trxStatus)) {
          result.put("code", 11);
          result.put("info", "Invalid data format : transaction status");
          return result;
        }
        where = where + " and UPPER(trx.trxStatus)=:trxStatus";
      }

      if (map.get("transaction_type") != null && !""
          .equals(map.get("transaction_type").toString().trim())) {
        String trxType = map.get("transaction_type").toString().trim();
        if (!"SUBCR".equalsIgnoreCase(trxType) && !"TOPUP".equalsIgnoreCase(trxType)
            && !"REDMP".equalsIgnoreCase(trxType)) {
          result.put("code", 11);
          result.put("info", "Invalid data format : transaction type");
          return result;
        }
        where = where + " and UPPER(trx.transactionType.trxCode)=:trxType";
      }

      sql = sql + where + gropAndOrder;

      Query query = entityManager.createQuery(sql);

      if (map.get("investment") != null && !"".equals(map.get("investment").toString().trim())) {
        query.setParameter("investmentAccountNo",
            "%" + map.get("investment").toString().trim() + "%");
      }

      if (map.get("period_start") != null && !"".equals(map.get("period_start").toString().trim())
          && map.get("period_end") != null && !"".equals(map.get("period_end").toString().trim())) {
        Date periodStart = null;
        Date periodEnd = null;
        try {
          periodStart = new SimpleDateFormat("yyyy-MM-dd")
              .parse(map.get("period_start").toString().trim());
        } catch (Exception e) {
          result.put("code", 11);
          result.put("info", "Invalid data format : period start");
          return result;
        }

        try {
          periodEnd = new SimpleDateFormat("yyyy-MM-dd")
              .parse(map.get("period_end").toString().trim());
        } catch (Exception e) {
          result.put("code", 11);
          result.put("info", "Invalid data format : period end");
          return result;
        }
        query.setParameter("periodStart", periodStart);
        query.setParameter("periodEnd", periodEnd);
      }

      if (map.get("package_name") != null && !""
          .equals(map.get("package_name").toString().trim())) {
        query.setParameter("packageName", "%" + map.get("package_name").toString().trim() + "%");
      }

      if (map.get("transaction_status") != null && !""
          .equals(map.get("transaction_status").toString().trim())) {
        query.setParameter("trxStatus",
            map.get("transaction_status").toString().trim().toUpperCase());
      }

      if (map.get("transaction_type") != null && !""
          .equals(map.get("transaction_type").toString().trim())) {
        query.setParameter("trxType", map.get("transaction_type").toString().trim().toUpperCase());
      }

      // if (map.get("offset") != null && !"".equals(map.get("offset").toString()) &&
      // map.get("limit") != null
      // && !"".equals(map.get("limit").toString())) {
      // query.setFirstResult(Integer.parseInt(map.get("limit").toString().trim()))
      // .setMaxResults(Integer.parseInt(map.get("offset").toString().trim()));
      // }
      query.setParameter("kyc", kyc);
      List listTransaction = new ArrayList();
      Integer limit = 0;
      Integer offset = 0;
      if (map.get("offset") != null) {
        offset = Integer.parseInt(map.get("offset").toString().trim());
        query.setFirstResult(offset);
      }
      if (map.get("limit") != null) {
        limit = Integer.parseInt(map.get("limit").toString().trim());
        query.setMaxResults(limit);
        if (limit == 0) {
          result.put("code", 0);
          result.put("info", "transaction list successfully loaded but limit for showed is zero");
          result.put("data", listTransaction);
          return result;
        }
      }

      List listData = query.getResultList();
      for (int i = 0; i < listData.size(); i++) {
        Map data = new HashMap();
        Object[] object = (Object[]) listData.get(i);

        String orderNumber = object[1].toString();

        List<UtTransactionsCart> uCart = utTransactionsCartRepository.findAllByOrderNo(orderNumber);
        if (uCart != null && !uCart.isEmpty()) {
          UtTransactionsCart ut = uCart.get(0);
          PaymentMethod paymentMethod = paymentMethodRepository.findByCode(ut.getPaymentType());
          if (paymentMethod != null) {
            Map pay = new HashMap();
            pay.put("code", paymentMethod.getCode());
            pay.put("name", paymentMethod.getName());
            data.put("payment_method", pay);
          }
        }

        UtProductFundPrices upfp = utProductFundPricesRepository.findTop1ByUtProducts_idOrderByPriceDateDesc((Long) object[9]);

        data.put("account_no", object[0]);
        data.put("order_number", orderNumber);
        data.put("amount", object[2]);
        data.put("date", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,S'Z'").format(object[3]));
        data.put("type", object[4]);
        data.put("status", object[5]);
        data.put("package_code", object[6]);
        data.put("package_name", object[7]);
        data.put("order_unit", object[8]);
        data.put("prod_id", object[9]);
        if (upfp == null){
          data.put("bid_price", null);
        }else {
          data.put("bid_price", upfp.getBidPrice());
        }
        
        listTransaction.add(data);
      }

      result.put("code", 0);
      result.put("info", "transaction list successfully loaded");
      result.put("data", listTransaction);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      result.put("code", 99);
      result.put("info", "General error");
      return result;
    }
  }

  @Override
  public Map getRangeOfPartialByInvestment(String invNo, Kyc kyc) {
    InvestmentAccounts invest = investmentAccountsRepository
        .findByInvestmentAccountNoAndKycs(invNo, kyc);
    if (invest == null) {
      return errorResponse(12, "range_partial", null);
    }

    BigDecimal maxPartial = new BigDecimal(0);
    BigDecimal minPartial = new BigDecimal(0);

    List<FundPackageProducts> products = fundPackageProductsRepository
        .findAllByFundPackages(invest.getFundPackages());
    for (FundPackageProducts fundPackageProducts : products) {
      double currentAmount = 0.0;
      double currentUnit = 0.0;
      double minRedeemAmount = fundPackageProducts.getUtProducts().getMinRedemptionAmount();
      double minRedeemUnit = fundPackageProducts.getUtProducts().getMinRedemptionUnit();
      double minAfterRedeemUnit = fundPackageProducts.getUtProducts()
          .getMinBalAfterRedemptionUnit();

      List<CustomerBalance> customerBalances = customerBalanceRepository
          .findTop1ByUtProductAndInvAccountOrderByBalanceDateDesc(
              fundPackageProducts.getUtProducts(), invest);
      if (!customerBalances.isEmpty()) {
        currentAmount = customerBalances.get(0).getCurrentAmount() != null ? customerBalances.get(0)
            .getCurrentAmount() : 0.00;
        currentUnit = customerBalances.get(0).getCurrentUnit() != null ? customerBalances.get(0)
            .getCurrentUnit() : 0.00;
      }

      //----- GET MAX REDEEM PARTIAL ----//
      double minUnit = 0.00;
      if (currentUnit != 0.00) {
        minUnit = minAfterRedeemUnit / currentUnit;
      } else {
        return errorResponse(50, "currentUnit", null);
      }
      System.out.println("maxUnit : 100 - " + minUnit);
      BigDecimal newMaxPartial = BigDecimal.valueOf(minUnit).setScale(2, RoundingMode.UP);

      if (newMaxPartial.compareTo(maxPartial) == 1) {
        maxPartial = newMaxPartial;
      }

      //---- GET MIN REDEEM PARTIAL ----//
      minUnit = minRedeemUnit / currentUnit;
      System.out.println("minUnit : " + minUnit);
      BigDecimal newMinPartial = BigDecimal.valueOf(minUnit).setScale(2, RoundingMode.UP);

      if (newMinPartial.compareTo(minPartial) == 1) {
        minPartial = newMinPartial;
      }

      if (currentAmount != 0.00) {
        minUnit = minRedeemAmount / currentAmount;
      } else {
        return errorResponse(50, "currentAmount", null);
      }

      newMinPartial = BigDecimal.valueOf(minUnit).setScale(2, RoundingMode.UP);

      if (newMinPartial.compareTo(minPartial) == 1) {
        minPartial = newMinPartial;
      }
    }

    System.out.println("minPartial : " + minPartial);
    System.out.println("maxPartial : " + maxPartial);

    String dateLastRedeem = null;
    double minAfterRedeem = 0.0;

    Map mapRedeem = getRangePromotionRedeemtion(invNo, kyc);
    if (mapRedeem.get("code").equals(0)) {
      mapRedeem = (Map) mapRedeem.get("data");
      dateLastRedeem = mapRedeem.get("minimal_investment_redeem_date").toString();
      minAfterRedeem = (double) mapRedeem.get("minimal_investment_amount");
    }

    Map map = new HashMap();
    map.put("maxPartialRedemption", new BigDecimal(1).subtract(maxPartial));
    map.put("minPartialRedemption", minPartial);
    map.put("minimal_investment_amount", minAfterRedeem);
    map.put("minimal_investment_redeem_date", dateLastRedeem);
    return errorResponse(0, "data found", map);
  }

  private Map getRangePromotionRedeemtion(String invNo, Kyc kyc) {
    InvestmentAccounts invest = investmentAccountsRepository
        .findByInvestmentAccountNoAndKycs(invNo, kyc);
    List<InvestmentPromotion> invPromotions = investmentPromotionRepository
        .findAllByInvestmentAccountAndRowStatus(invest, true);
    if (invPromotions == null || invPromotions.isEmpty()) {
      return errorResponse(50, "promo", null);
    }

    Date date = null;
    double minAfterRedeem = 0.0;

    for (InvestmentPromotion invPromotion : invPromotions) {
      Date newDate = invPromotion.getMinimalInvestmentRedeemDate();
      if (date == null || newDate.after(date)) {
        date = newDate;
      }

      double newMinAfterRedeem = invPromotion.getMinimalInvestmentAmount();
      if (newMinAfterRedeem > minAfterRedeem) {
        minAfterRedeem = newMinAfterRedeem;
      }
    }

    System.out.println("promo minAfterRedeem : " + minAfterRedeem);
    System.out.println("promo minAfterRedeem : " + date);

    Map map = new HashMap();
    map.put("minimal_investment_amount", minAfterRedeem);
    map.put("minimal_investment_redeem_date",
        DateTimeUtil.convertDateToStringCustomized(date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    return errorResponse(0, "data found", map);
  }

  @Override
  public Map checkRedemptionTransaction(String investmentNumber) {
    Map resultMap = new HashMap<>();

    UtTransactionType type = utTransactionTypeRepository.findByTrxCode("REDMP");
    InvestmentAccounts invest = investmentAccountsRepository
        .findByInvestmentAccountNo(investmentNumber);
    List<UtTransactions> uts = utTransactionsRepository
        .findAllByInvestementAccountAndTransactionTypeAndTrxStatus(invest, type, "ORD");
    System.out.println("uts : " + uts);

    Boolean status = (uts == null || uts.isEmpty());

    System.out.println("checkRedemptionTransaction.investmentNumber : " + investmentNumber);
    System.out.println("checkRedemptionTransaction.kycs : " + invest.getKycs().getId());

    try {
      // TODO: MAKS < MIN
      Map rangePartial = this.getRangeOfPartialByInvestment(investmentNumber, invest.getKycs());
      Map dataMap = (Map) rangePartial.get("data");
      BigDecimal maxPartialRedemption = (BigDecimal) dataMap.get("maxPartialRedemption");
      BigDecimal minPartialRedemption = (BigDecimal) dataMap.get("minPartialRedemption");

      if (dataMap.get("minimal_investment_redeem_date") != null) {
        Calendar invPromotionDate = Calendar.getInstance();
        invPromotionDate.setTime((Date) dataMap.get("minimal_investment_redeem_date"));
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");

        if (maxPartialRedemption.compareTo(minPartialRedemption) < 0
            && dataMap.get("minimal_investment_redeem_date") != null && invPromotionDate.getTime()
            .after(currentDate.getTime())) {
          resultMap.put("code", 1);
          resultMap.put("info",
              "Investasi ini belum bisa dijual secara keseluruhan sampai tanggal " + sdf
                  .format(invPromotionDate.getTime())
                  + " dan belum mencukupi untuk penjualan sebagian. Ayo tambahkan lagi investasi anda.");
          resultMap.put("status", status);
          return resultMap;
        }
      }

      if (status) {
        resultMap.put("code", 0);
        resultMap.put("info", "Next");
        resultMap.put("status", status);
      }

      if (!status) {
        resultMap.put("code", 1);
        resultMap.put("info", "Investasi ini sedang dalam proses penjualan kembali");
        resultMap.put("status", status);
      }
      return resultMap;
    } catch (Exception e) {
    	logger.error("[FATAL]" ,e);
    	resultMap.put("code", 90);
    	resultMap.put("info", "General error");
    	resultMap.put("status", status);
    	return resultMap;
    }

  }

  //   @Override
//    public Map getRangeOfPartialByInvestment(String invNo, Kyc kyc) {
//        try {
//            Date tempDate = null;
//            InvestmentAccounts invest = investmentAccountsRepository.findByInvestmentAccountNoAndKycs(invNo, kyc);
//            if (invest == null) {
//                return errorResponse(12, "range_partial", null);
//            }
//            Map manualInvestment = this.getInvestmentIfNotInPromotion(invest, tempDate);
//            BigDecimal maxPartialRedemption = (BigDecimal) manualInvestment.get("max");
//            BigDecimal minPartialRedemption = (BigDecimal) manualInvestment.get("min");
//
//            //TODO: ADD ADDITIONAL CONDITION for PORTALI 1007
//            Double minimal_investment_amount = 0.0;
//            Date minimal_investment_redeem_date = null;
//            Double pcgInvestmentPromotion = 0.0;
//            InvestmentPromotion inv_promotion = investmentPromotionRepository.findByInvestmentAccountAndRowStatus(invest, true);
//            if (inv_promotion != null) {
//                Calendar invPromotionDate = Calendar.getInstance();
//                invPromotionDate.setTime(inv_promotion.getMinimalInvestmentRedeemDate());
//
//                Calendar currentDate = Calendar.getInstance();
//                if (invPromotionDate.getTime().after(currentDate.getTime())) {
//                    //GET ORIGINAL TOTAL MARKET VALUE
//                    List<FundPackageProducts> products = fundPackageProductsRepository.findAllByFundPackages(invest.getFundPackages());
//                    for (FundPackageProducts product : products) {
//
//                    }
//                    Double oriTotalMarketValue = investmentService.getTotalMarketValue(invest, products, kyc);
//                    if (invest.getInvestmentAccountNo().equalsIgnoreCase(inv_promotion.getInvestmentAccount().getInvestmentAccountNo())) {
//                        Double promotionMarketValue = oriTotalMarketValue - inv_promotion.getMinimalInvestmentAmount();
//                        pcgInvestmentPromotion = promotionMarketValue / oriTotalMarketValue;
//                    }
//                }
//
//                minimal_investment_amount = inv_promotion.getMinimalInvestmentAmount();
//                minimal_investment_redeem_date = inv_promotion.getMinimalInvestmentRedeemDate();
//            }
//
//            if (pcgInvestmentPromotion != 0) {
//                if (BigDecimal.valueOf(pcgInvestmentPromotion).compareTo(maxPartialRedemption) < 0) {
//                    maxPartialRedemption = BigDecimal.valueOf(pcgInvestmentPromotion).setScale(2, RoundingMode.DOWN);
//                }
//            }
//
//            Map map = new HashMap();
//            map.put("maxPartialRedemption", maxPartialRedemption);
//            map.put("minPartialRedemption", minPartialRedemption);
//            map.put("minimal_investment_amount", minimal_investment_amount);
//            map.put("minimal_investment_redeem_date", DateTimeUtil.convertDateToStringCustomized(minimal_investment_redeem_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
//            return errorResponse(0, "data found", map);
//        } catch (Exception e) {
//            logger.error(e);
//            return errorResponse(0, "range of partial", null);
//        }
//    }
//
//    // TODO: Max and Min Partial Redemption
//    @Override
//    public Map getInvestmentIfNotInPromotion(InvestmentAccounts invest, Date tempDate) {
//        List<FundPackageProducts> fproducts = fundPackageProductsRepository.findAllByFundPackages(invest.getFundPackages());
//        for (FundPackageProducts fpp : fproducts) {
//            UtProducts utProducts = fpp.getUtProducts();
//            List<CustomerBalance> balances = customerBalanceRepository.findTop1ByUtProductAndInvAccountOrderByBalanceDateDesc(utProducts, invest);
//            if (balances != null && !balances.isEmpty()) {
//                if (tempDate == null) {
//                    tempDate = balances.get(0).getBalanceDate();
//                } else if (!tempDate.equals(balances.get(0).getBalanceDate())) {
//                    if (tempDate.compareTo(balances.get(0).getBalanceDate()) > 0) {
//                        tempDate = balances.get(0).getBalanceDate();
//                    }
//                }
//            }
//        }
//
//        List<BigDecimal> result = new ArrayList<>();
//        List<BigDecimal> units = new ArrayList<>();
//        List<BigDecimal> amounts = new ArrayList<>();
//        List<BigDecimal> balanceUnits = new ArrayList<>();
//
//        for (FundPackageProducts product : fproducts) {
//            UtProductFundPrices utproductFundPrice = utProductFundPricesRepository.findByUtProductsAndPriceDate(product.getUtProducts(), tempDate);
//            CustomerBalance cb = customerBalanceRepository.findByInvAccountAndUtProductAndBalanceDate(invest, product.getUtProducts(), tempDate);
//
//            if (cb != null) {
//                units.add(BigDecimal.valueOf(product.getUtProducts().getMinRedemptionUnit()).divide(BigDecimal.valueOf(cb.getCurrentUnit()), 5, RoundingMode.UP));
//                amounts.add(BigDecimal.valueOf(product.getUtProducts().getMinRedemptionAmount()).divide(BigDecimal.valueOf(cb.getCurrentAmount()), 5, RoundingMode.UP));
//                balanceUnits.add(BigDecimal.valueOf(product.getUtProducts().getMinBalAfterRedemptionUnit()).multiply(BigDecimal.valueOf(utproductFundPrice.getBidPrice())).divide(BigDecimal.valueOf(cb.getCurrentAmount()), 5, RoundingMode.UP));
//            } else {
//                units.add(BigDecimal.ZERO);
//                amounts.add(BigDecimal.ZERO);
//                balanceUnits.add(BigDecimal.ZERO);
//            }
//        }
//
//        result.add(getMaxPercentage(units));
//        result.add(getMaxPercentage(amounts));
//
//        BigDecimal maxPartialRedemption = new BigDecimal(1).subtract(this.getMaxPercentage(balanceUnits).setScale(2, RoundingMode.DOWN));
//        BigDecimal minPartialRedemption = this.getMaxPercentage(result).setScale(2, RoundingMode.UP);
//
//        Map map = new HashMap();
//        map.put("min", minPartialRedemption);
//        map.put("max", maxPartialRedemption);
//        return map;
//    }
//
//    private BigDecimal getMaxPercentage(List<BigDecimal> temps) {
//        BigDecimal result = BigDecimal.ZERO;
//        for (BigDecimal o : temps) {
//            if (result.equals(BigDecimal.ZERO) || result == BigDecimal.ZERO) {
//                result = o;
//            }
//            if (result.compareTo(o) < 0) {
//                for (BigDecimal p : temps) {
//                    if (o.compareTo(p) > 0) {
//                        result = o;
//                    } else {
//                        result = p;
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//    private BigDecimal getMinPercentage(List<BigDecimal> temps) {
//        BigDecimal result = BigDecimal.ZERO;
//        for (BigDecimal o : temps) {
//            if (result.equals(BigDecimal.ZERO) || result == BigDecimal.ZERO) {
//                result = o;
//            }
//            if (result.compareTo(o) > 0) {
//                for (BigDecimal p : temps) {
//                    if (o.compareTo(p) < 0) {
//                        result = o;
//                    } else {
//                        result = p;
//                    }
//                }
//            }
//        }
//        return result;
//    } 
//@Override
//    public Map checkRedemptionTransaction(String investmentNumber) {
//        Map resultMap = new HashMap<>();
//
//        UtTransactionType type = utTransactionTypeRepository.findByTrxCode("REDMP");
//        InvestmentAccounts invest = investmentAccountsRepository.findByInvestmentAccountNo(investmentNumber);
//        List<UtTransactions> uts = utTransactionsRepository.findAllByInvestementAccountAndTransactionTypeAndTrxStatus(invest, type, "ORD");
//        System.out.println("uts : " + uts);
//
//        Boolean status = (uts == null || uts.isEmpty());
//
//        System.out.println("checkRedemptionTransaction.investmentNumber : " + investmentNumber);
//        System.out.println("checkRedemptionTransaction.kycs : " + invest.getKycs().getId());
//
//        try {
//            // TODO: MAKS < MIN
//            Map rangePartial = this.getRangeOfPartialByInvestment(investmentNumber, invest.getKycs());
//            Map dataMap = (Map) rangePartial.get("data");
//            BigDecimal maxPartialRedemption = (BigDecimal) dataMap.get("maxPartialRedemption");
//            BigDecimal minPartialRedemption = (BigDecimal) dataMap.get("minPartialRedemption");
//
//            if (dataMap.get("minimal_investment_redeem_date") != null) {
//                Calendar invPromotionDate = Calendar.getInstance();
//                invPromotionDate.setTime((Date) dataMap.get("minimal_investment_redeem_date"));
//                Calendar currentDate = Calendar.getInstance();
//                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
//
//                if (maxPartialRedemption.compareTo(minPartialRedemption) < 0
//                        && dataMap.get("minimal_investment_redeem_date") != null
//                        && invPromotionDate.getTime().after(currentDate.getTime())) {
//                    resultMap.put("code", 1);
//                    resultMap.put("info", "Investasi ini belum bisa dijual secara keseluruhan sampai tanggal "
//                            + sdf.format(invPromotionDate.getTime())
//                            + " dan belum mencukupi untuk penjualan sebagian. Ayo tambahkan lagi investasi anda.");
//                    resultMap.put("status", status);
//                    return resultMap;
//                }
//            }
//
//            if (status) {
//                resultMap.put("code", 0);
//                resultMap.put("info", "Next");
//                resultMap.put("status", status);
//            }
//
//            if (!status) {
//                resultMap.put("code", 1);
//                resultMap.put("info", "Investasi ini sedang dalam proses penjualan kembali");
//                resultMap.put("status", status);
//            }
//            return resultMap;
//        } catch (Exception e) {
//            e.printStackTrace();
//            resultMap.put("code", 90);
//            resultMap.put("info", "General error");
//            resultMap.put("status", status);
//            return resultMap;
//        }
//
//    }    
//    @Override
//    public Map checkRedemptionTransaction(String investmentNumber) {
//        Map resultMap = new HashMap<>();
//        Boolean status;
//        
//        UtTransactionType  type   = utTransactionTypeRepository.findByTrxCode("REDMP");
//        InvestmentAccounts invest = investmentAccountsRepository.findByInvestmentAccountNo(investmentNumber);
//        List<UtTransactions> uts  = utTransactionsRepository.findAllByInvestementAccountAndTrxStatus(invest, "ORD");
//        System.out.println("uts : " + uts);
//        
//        if (uts == null || uts.isEmpty()) {
//            status = true;
//        } else {
//            List<String> strings = new ArrayList<>();
//            for (UtTransactions ut : uts) {
//                if (ut.getTransactionType() == type) {
//                    strings.add(ut.getTrxStatus());
//                }
//            }
//
//            if (strings.contains("ORD")) {
//                status = false;
//            } else {
//                status = true;
//            }
//
//        }
//
//        try {
//            // TODO: MAKS < MIN
//            Map rangePartial = this.getRangeOfPartialByInvestment(investmentNumber, invest.getKycs());
//            Map dataMap = (Map) rangePartial.get("data");
//            BigDecimal maxPartialRedemption = (BigDecimal) dataMap.get("maxPartialRedemption");
//            BigDecimal minPartialRedemption = (BigDecimal) dataMap.get("minPartialRedemption");
//
//            if (dataMap.get("minimal_investment_redeem_date") != null) {
//                Calendar invPromotionDate = Calendar.getInstance();
//                invPromotionDate.setTime((Date) dataMap.get("minimal_investment_redeem_date"));
//                Calendar currentDate = Calendar.getInstance();
//                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
//
//                if (maxPartialRedemption.compareTo(minPartialRedemption) < 0
//                        && dataMap.get("minimal_investment_redeem_date") != null
//                        && invPromotionDate.getTime().after(currentDate.getTime())) {
//                    resultMap.put("code", 1);
//                    resultMap.put("info", "Investasi ini belum bisa dijual secara keseluruhan sampai tanggal "
//                            + sdf.format(invPromotionDate.getTime())
//                            + " dan belum mencukupi untuk penjualan sebagian. Ayo tambahkan lagi investasi anda.");
//                    resultMap.put("status", status);
//                    return resultMap;
//                }
//            }
//
//            if (status) {
//                resultMap.put("code", 0);
//                resultMap.put("info", "Next");
//                resultMap.put("status", status);
//            }
//
//            if (!status) {
//                resultMap.put("code", 1);
//                resultMap.put("info", "Investasi ini sedang dalam proses penjualan kembali");
//                resultMap.put("status", status);
//            }
//            return resultMap;
//        } catch (Exception e) {
//            e.printStackTrace();
//            resultMap.put("code", 90);
//            resultMap.put("info", "General error");
//            resultMap.put("status", status);
//            return resultMap;
//        }
//
//    }
  @Override
  public Double getRedemptionFee(InvestmentAccounts investment) {
    long diffRange = new Date().getTime() - investment.getCreatedDate().getTime();
    long daysRange = diffRange / (24 * 60 * 60 * 1000);

    System.out.println("diffRange : " + diffRange);
    System.out.println("daysRange : " + daysRange);

    UtTransactionType transactionType = utTransactionTypeRepository.findByTrxCode("REDMP");
    List<FundPackageFeeSetup> feeSetups = fundPackageFeeSetupRepository
        .findAllByFundPackagesAndTransactionTypeOrderByAmountMinDesc(investment.getFundPackages(),
            transactionType);
    System.out.println("feeSetups : " + feeSetups);

    try {
      Double feeAmount = Double.valueOf(0);
      for (FundPackageFeeSetup fee : feeSetups) {
        if (fee.getAmountMax() == 0) {
          if (daysRange >= fee.getAmountMax()) {
            feeAmount = fee.getFeeAmount();
          }
        } else if (daysRange <= fee.getAmountMax() && daysRange >= fee.getAmountMin()) {
          feeAmount = fee.getFeeAmount();
        }
      }
      return feeAmount;
    } catch (Exception e) {
    	logger.error("[FATAL] " ,e);
    	return Double.valueOf(0);
    }

  }

  public Object createRedemptionToAvantrade(String orderNo) {
    List<RedemptionDto> listData = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

    List<UtTransactions> transactions = utTransactionsRepository.findAllByOrderNo(orderNo);
    for (UtTransactions trx : transactions) {
      RedemptionDto dto = new RedemptionDto();
      dto.setProductId(trx.getProductId().getAtProductId());

      Double orderAmount = trx.getOrderAmount();
      Double feeRate = 0.0;
      if (orderAmount != 0 && orderAmount > 0) {
        feeRate = trx.getFeeAmount() / orderAmount;
      }
      dto.setFeeRate(feeRate.toString());
      dto.setFeeAmount(trx.getFeeAmount().toString());
      dto.setTransactionDate(sdf.format(trx.getPriceDate()));
      SettlementAccounts settlementAccounts = settlementAccountsRepository
          .findByKycs(trx.getKycId());
      dto.setSettlementAccountId(settlementAccounts.getAtSettlementAccountId());
      dto.setCustomerId(trx.getKycId().getAtCustomerId());
      dto.setInvestmentAccountId(trx.getInvestementAccount().getAtInvestmentAccountId());
      dto.setOrderNumber(trx.getOrderNo());
      dto.setTransactionNumber(trx.getTrxNo());
      dto.setTransactionId(trx.getAtTrxNo());

      System.out.println("dto : " + dto);

      listData.add(dto);
    }
    RestTemplate rest = new RestTemplate();
    GlobalParameter redirectUrl = globalParameterRepository
        .findByCategory("REDIRECT_URL_TO_AVANTRADE");
    String response = rest
        .postForObject(redirectUrl.getValue() + "/services/transaction/redemption-list", listData,
            String.class);
    System.out.println("Response from Redemption " + orderNo + " :" + response);
    return response;
  }

  // TODO: REDEMPTION
  @Override
  public Map redeemOrder(List<Map> maps, Kyc kyc) {
    Map resultMap = this.checkIncompleteDataMandatory(maps, "REDMP");
    if (!resultMap.isEmpty()) {
      return resultMap;
    }

    // TODO : Get request Order
    List<Map> dataMap = new ArrayList<>();

    for (Map map : maps) {
      InvestmentAccounts investmentAccounts = investmentAccountsRepository.findByInvestmentAccountNoAndKycs(String.valueOf(map.get("investment")), kyc);
      if (investmentAccounts == null) {
        return errorResponse(50, "investment", null);
      }

      List<UtTransactions> listPendingTrxRedemp = utTransactionsRepository.getPendingTrxRedemp(investmentAccounts.getId());
//      if(listPendingTrxRedemp.size() > 0){
//        return errorResponse(12, investmentAccounts.getInvestmentAccountNo() + ", Investasi ini sedang dalam proses penjualan kembali", null);
//      }

      if (map.get("channel_order") == null) {
        resultMap.put("code", 50);
        resultMap.put("info", "Channel Order not found");
        return resultMap;
      }

      logger.info("KYC : " + kyc.getId());
      logger.info("investmentAccounts.getInvestmentAccountNo() : " + investmentAccounts.getId());

      // TODO: Get range partial redemption
      Map rangePartial = this.getRangeOfPartialByInvestment(investmentAccounts.getInvestmentAccountNo(), kyc);
      logger.info("rangePartial : " + rangePartial);

      if (Integer.parseInt(rangePartial.get("code").toString()) == 50) {
        return rangePartial;
      }
      // TODO: Get Perkiraan Nilai Pasar (Maks total amount redeem)
      List<FundPackageProducts> products = fundPackageProductsRepository.findAllByFundPackages(investmentAccounts.getFundPackages());
      Double totalMarketValue = investmentService.getTotalMarketValue(investmentAccounts, products, kyc);
      totalMarketValue = new BigDecimal(totalMarketValue).setScale(2, RoundingMode.HALF_UP).doubleValue();

      // TODO: Get feeAmount Redeem in fundPackageFeeSetup
      Double feeAmount = 0.0;
      Double feePcg = this.getRedemptionFee(investmentAccounts);
      logger.info("feePcg : " + feePcg);

      if (feePcg > 0) {
        feeAmount = feePcg * totalMarketValue;
      }

      Map dataRangePartial = (Map) rangePartial.get("data");
      logger.info("dataRangePartial : " + dataRangePartial);
      logger.info("totalMarketValue : " + totalMarketValue);

      FundPackages fundPackage = investmentAccounts.getFundPackages();
      // TODO: Check price date cut off or no based on fundpackages

      Date priceDate = getPriceDate(fundPackage, new Date());

      boolean partial = true;
      double minPartialRedemption = Double.valueOf(dataRangePartial.get("minPartialRedemption").toString());
      double maxPartialRedemption = Double.valueOf(dataRangePartial.get("maxPartialRedemption").toString());
      double minPromoAmount = Double.valueOf(dataRangePartial.get("minimal_investment_amount").toString());
      Date lastPromoDate = null;
      if (minPromoAmount > 0.0) {
        lastPromoDate = DateTimeUtil.convertStringToDateCustomized(dataRangePartial.get("minimal_investment_redeem_date").toString(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      }

      double amount = Double.valueOf(String.valueOf(map.get("amount_value")));

      //replace string to double
      map.put("amount_value", amount);

      Double totalRedeem = amount;
      logger.info("####### amount_type  :  " + map.get("amount_type"));
      if (map.get("amount_type").equals("RAT002")) {
        totalRedeem = totalMarketValue * amount;
      }

      if (totalRedeem < totalMarketValue) {
        double minAmount = totalMarketValue * minPartialRedemption;
        double maxAmount = totalMarketValue * maxPartialRedemption;
        logger.info("minAmount : " + minAmount);
        logger.info("maxAmount : " + maxAmount);
        logger.info("amount : " + amount);
        logger.info("totalRedeem : " + totalRedeem);

        if (totalRedeem < minAmount || totalRedeem > maxAmount) {
          resultMap.put("code", 14);
          resultMap.put("info", "Invalid request for your amount value because more than total max can redeem, because min amount redeem is " + minAmount);
          return resultMap;
        }
      } else {
        List<UtTransactions> listPendingTrxTopup = utTransactionsRepository.getPendingTrxTopUp(investmentAccounts.getId());
        if(listPendingTrxTopup.size() > 0){
          return errorResponse(12, "Saat ini Anda tidak dapat melakukan penjualan seluruh nilai reksa dana karna masih memiliki transaksi top up yang belum teralokasi, " +
                  "investment :"+investmentAccounts.getInvestmentAccountNo(), null);
        }
        totalRedeem = totalMarketValue;
        partial = false;
      }

      logger.info("lastPromoDate : " + lastPromoDate);
      logger.info("priceDate     : " + priceDate);

      if (lastPromoDate != null && lastPromoDate.after(priceDate)) {
        logger.info("sisa           : " + (totalMarketValue - totalRedeem));
        logger.info("minPromoAmount : " + minPromoAmount);

        if ((totalMarketValue - totalRedeem) < minPromoAmount) {
          resultMap.put("code", 14);
          resultMap.put("info", "Investasi ini belum bisa dijual secara keseluruhan sampai tanggal " + DateTimeUtil.convertDateToStringCustomized(lastPromoDate, "dd MMM yyyy")
                  + " dan belum mencukupi untuk penjualan sebagian. Ayo tambahkan lagi investasi anda");
          return resultMap;
        }
      }

      SettlementAccounts sett = settlementAccountsRepository.findByKycs(kyc);
      UtTransactionType type = utTransactionTypeRepository.findByTrxCode("REDMP");

      // Get Settlement Account Customer
      // TODO: Save and Generate orderNo
      String channelName = kyc.getAccount().getAgent().getChannel().getName();
      String orderNo = null;

      try {
        orderNo = globalService.generateOrderNo(amount, channelName);
      } catch (InterruptedException e) {
        logger.error(e.getMessage(), e);
      }

      // TODO: Save into ut_transactions
      String orderNote = "";
      Double settlement_amount = new Double(0);
      for (FundPackageProducts product : products) {
        // TODO: Get last NAV
        UtProducts utProducts = product.getUtProducts();
        List<CustomerBalance> balances = customerBalanceRepository.findTop1ByUtProductAndInvAccountOrderByBalanceDateDesc(utProducts, investmentAccounts);
        Date tempDate = null;
        try {
          if (tempDate == null) {
            tempDate = balances.get(0).getBalanceDate();
          } else if (tempDate != balances.get(0).getBalanceDate()
              || !(tempDate.equals(balances.get(0).getBalanceDate()))) {
            if (tempDate.compareTo(balances.get(0).getBalanceDate()) > 0) {
              tempDate = balances.get(0).getBalanceDate();
            }
          }
        } catch (Exception e) {
          continue;
        }
        UtProductFundPrices utproductFundPrice = utProductFundPricesRepository.findByUtProductsAndPriceDate(product.getUtProducts(), tempDate);

        UtTransactions trx = new UtTransactions();
        trx.setCreatedBy(kyc.getAccount().getUsername());
        trx.setCreatedDate(new Date());
        trx.setOrderNo(orderNo);
        trx.setOrderAmount(totalRedeem * product.getCompositition());
        trx.setFeeAmount(Double.valueOf(String.valueOf(map.get("fee_amount"))) * product.getCompositition());
        if (!Double.valueOf(String.valueOf(map.get("fee_amount"))).equals(feeAmount)) {
          orderNote = orderNote + "fee amount is different with package’s fee;";
          trx.setFeeAmount(feeAmount * product.getCompositition());
        }
        trx.setNetAmount(amount * product.getCompositition());
        trx.setChannelOrderId(String.valueOf(map.get("channel_order")));

        trx.setFundPackageRef(fundPackage);
        trx.setPriceDate(priceDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!String.valueOf(map.get("price_date")).equals(sdf.format(priceDate))) {
          orderNote = orderNote + "price date has been changed to continue transaction;";
        }
        if (!orderNote.isEmpty()) {
          Map mapNote = new HashMap<>();
          mapNote.put("net_amount", map.get("net_amount"));
          mapNote.put("fee_amount", map.get("fee_amount"));
          mapNote.put("total_amount", map.get("total_amount"));
          mapNote.put("price_date", map.get("price_date"));

          trx.setTrxNotes(mapNote.toString());
        }
        trx.setTransactionType(type);
        trx.setKycId(kyc);
        trx.setProductId(product.getUtProducts());
        trx.setSettlementAmount(trx.getOrderAmount());
        settlement_amount = settlement_amount + trx.getSettlementAmount();
        trx.setSettlementStatus("YES");
        trx.setTrxDate(new Date());
        trx.setTrxNo(globalService.generateTrxNo(type, 2));
        trx.setTrxStatus("ORD");
        trx.setAtTrxNo(UUID.randomUUID().toString());
        trx.setTrxType(type.getId().intValue());
        FundEscrowAccount escrowAccount = fundEscrowAccountRepository.findByFundPackages(fundPackage);
        trx.setSettlementNoRef(escrowAccount);
        trx.setTransactionDate(new Date());
        trx.setOrderUnit(new BigDecimal(trx.getOrderAmount())
            .divide(new BigDecimal(utproductFundPrice.getBidPrice()), 2, RoundingMode.HALF_UP)
            .setScale(2, RoundingMode.HALF_UP).doubleValue());
        trx.setInvestementAccount(investmentAccounts);
        trx.setTaxAmount(new Double(0));
        trx = utTransactionsRepository.save(trx);
      }
      logger.info("######## 2 amount_type  +  " + map.get("amount_type"));
      // TODO: Send to Avantrade if full redeem/partial
      Object responseRedeem = new Object();

      if (map.get("amount_type").equals("RAT002")) {
        logger.info("#### data RAT002");
        if (!partial) {
          logger.info("RAT002 : ALL");
          responseRedeem = this.createRedemptionToAvantrade(orderNo);
          if (!responseRedeem.toString().contains("2000033")) {
            for (FundPackageProducts product : products) {
              List<UtTransactions> trxs = utTransactionsRepository.findAllByOrderNo(orderNo);
              for (UtTransactions trx : trxs) {
                trx.setTrxStatus("CAN");
                trx = utTransactionsRepository.save(trx);
              }
            }
          }
        } else {
          logger.info("RAT002 : PARTIAL");
          responseRedeem = investmentService.createTrxRedemptionToAvantradeNew(orderNo, 0);
          if (!responseRedeem.toString().contains("2000033")) {
            for (FundPackageProducts product : products) {
              List<UtTransactions> trxs = utTransactionsRepository.findAllByOrderNo(orderNo);
              for (UtTransactions trx : trxs) {
                trx.setTrxStatus("CAN");
                trx = utTransactionsRepository.save(trx);
              }
            }
          }
        }
      } else {
        logger.info("#### data RAT001");
        logger.info("totalMarketValue1: " + totalMarketValue);
        logger.info("partial : " + partial);
        logger.info("map.get(\"amount_value\") : " + map.get("amount_value"));
        if (!partial) {
          logger.info("RAT001 : ALL");

          responseRedeem = this.createRedemptionToAvantrade(orderNo);
          if (!responseRedeem.toString().contains("2000033")) {
            for (FundPackageProducts product : products) {
              List<UtTransactions> trxs = utTransactionsRepository.findAllByOrderNo(orderNo);
              for (UtTransactions trx : trxs) {
                trx.setTrxStatus("CAN");
                trx = utTransactionsRepository.save(trx);
              }
            }
          }
        } else {
          logger.info("RAT001 : PARTIAL");

          responseRedeem = investmentService.createTrxRedemptionToAvantradeNew(orderNo, 0);
          if (!responseRedeem.toString().contains("2000033")) {
            for (FundPackageProducts product : products) {
              List<UtTransactions> trxs = utTransactionsRepository.findAllByOrderNo(orderNo);
              for (UtTransactions trx : trxs) {
                trx.setTrxStatus("CAN");
                trx = utTransactionsRepository.save(trx);
              }
            }
          }
        }
      }

      if (responseRedeem != null && responseRedeem.equals("2000033")) {
        // TODO: Response if ava successfully
        List<Double> fee = fundPackageFeeSetupRepository.getFeeSetup(fundPackage, type, amount);
        Double feeTrx = 0.0;
        if (fee != null && !fee.isEmpty()) {
          feeTrx = fee.get(0);
        }
        logger.info("funPackage" + fundPackage);
        logger.info("type" + type);
        logger.info("amount" + amount);
        Map dataMaps = new HashMap<>();

        dataMaps.put("fee", feeTrx);
        dataMaps.put("settlement_period", fundPackage.getSettlementPeriod());
        dataMaps.put("order_number", orderNo);
        dataMaps.put("investment_account", investmentAccounts.getInvestmentAccountNo());
        dataMaps.put("package_name", investmentAccounts.getFundPackages().getFundPackageName());
        dataMaps.put("investment_market_value", totalMarketValue);
        dataMaps.put("settlement_amount", settlement_amount);
        SettlementAccounts accounts = settlementAccountsRepository.findByKycs(kyc);
        dataMaps.put("settlement_bank", accounts.getBankId().getBankName());
        dataMaps.put("settlement_account_number", accounts.getSettlementAccountNo());
        dataMaps.put("settlement_account_name", accounts.getSettlementAccountName());
        resultMap.put("code", 0);
        resultMap.put("info", "Order successfully submitted");
        resultMap.put("data", dataMaps);

        if (kyc.getAccount().getAgent().getEmailCustom()) {
          //do not save email scheduller
        } else {
          EmailJobScheduller emailJobScheduller = new EmailJobScheduller();
          emailJobScheduller.setOrderNo(orderNo);
          emailJobScheduller.setEmailType("MCW_REDEMPTION_SUMMARY");
          emailJobScheduller.setStatus("0");
          emailJobSchedullerRepository.save(emailJobScheduller);
        }
      } else if (responseRedeem != null && !responseRedeem.equals("2000033")) {
        resultMap.put("code", 1);
        resultMap.put("info", "Response Transaction Redemption from Avantrade :" + responseRedeem);
      } else {
        resultMap.put("code", 1);
        resultMap.put("info", "Transaction Redemption failed.");
      }

      if (kyc.getAccount().getAgent().getEmailCustom()) {
      } else {
        if (resultMap.get("code").equals(0)) {
          emailService.sendRedeem(kyc, orderNo);
        }
      }
    }

    return resultMap;
  }

  private Map sendToTcash(List<Map> maps) throws IOException {
    String url = globalParameterRepository.findByName("TCASH_REQ_TOKEN_URL").getValue();
    String terminalId = globalParameterRepository.findByName("TCASH_TERMINAL_ID").getValue();
    String userKey = globalParameterRepository.findByName("TCASH_USER_KEY").getValue();
    String password = globalParameterRepository.findByName("TCASH_PASSWORD").getValue();
    String signature = globalParameterRepository.findByName("TCASH_SIGNATURE").getValue();
    String successUrl = globalParameterRepository.findByName("TCASH_MCW_SUCCESS_URL").getValue();
    String failedUrl = globalParameterRepository.findByName("TCASH_MCW_FAIL_URL").getValue();
    String redirectUrl = globalParameterRepository.findByName("TCASH_REDIRECT_URL").getValue();

    /**
     * xMap.put("order_number", orderNo); xMap.put("investment_account",
     * cart.getInvestmentAccount().getInvestmentAccountNo());
     * xMap.put("channel_order", channelOrderId); xMap.put("package_code",
     * cart.getFundPackages().getPackageCode()); xMap.put("net_amount",
     * cart.getNetAmount()); xMap.put("fee_amount", cart.getFeeAmount());
     * xMap.put("total_amount", cart.getOrderAmount());
     * xMap.put("price_date",
     * DateTimeUtil.convertDateToStringCustomized(fixDatePrice,
     * "yyyy-MM-dd")); xMap.put("order_note", orderNote);
     */
    String orderNo = "";
    String values = "";
    Double total = 0.0;
    String trxId = null;

    for (Map map : maps) {
      FundPackages fp = fundPackagesRepository
          .findByPackageCode(map.get("package_code").toString());

      values +=
          ",[\"" + fp.getFundPackageName() + " - " + map.get("investment_account") + "\", \"" + map
              .get("total_amount") + "\", \"1\"]";
      total += Double.valueOf(map.get("total_amount").toString());

      if (trxId == null) {
        List<UtTransactionsCart> utTransactionsCarts = utTransactionsCartRepository
            .findAllByOrderNo(map.get("order_number").toString());

//                trxId = utTransactionsCarts.get(0).getSettlementRefNo();
        trxId = utTransactionsCarts.get(0).getSettlementRefNo();
//                System.out.println("########## trxId "+trxId);   

      }

      orderNo += "," + map.get("order_number").toString();
    }

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
    builder.queryParam("trxId", trxId);
    builder.queryParam("terminalId", terminalId);
    builder.queryParam("userKey", userKey);
    builder.queryParam("password", password);
    builder.queryParam("signature", signature);
    builder.queryParam("successUrl", successUrl + "?orders=" + orderNo.substring(1));
    builder.queryParam("failedUrl", failedUrl + "?orders=" + orderNo.substring(1));
    builder.queryParam("total", total);
    builder.queryParam("items", "[" + values.substring(1) + "]");

    logger.info("request : " + builder.build().toString());

    RestTemplate restTemplate = new RestTemplate();
    String response = restTemplate.postForObject(builder.build().toString(), null, String.class);
    logger.info("response : " + response);

    Map result = new ObjectMapper().readValue(response, Map.class);

    Map mapResult = new HashMap();
    mapResult.put("url", redirectUrl);
    mapResult.put("token", result.get("pgpToken"));
    return mapResult;
  }

  @Override
  @Transactional
  public Map executeOrderByTCash(List<String> orders, boolean success) {
    List<UtTransactionsCart> carts = new ArrayList();
    List<UtTransactions> trxs = new ArrayList();
    List<SubcriptionJobScheduller> jobs = new ArrayList();
    for (String order : orders) {
      List<UtTransactionsCart> cs = utTransactionsCartRepository.findAllByOrderNo(order);
      if (cs != null && !cs.isEmpty()) {
        carts.addAll(cs);
      }

      List<UtTransactions> trs = utTransactionsRepository.findAllByOrderNo(order);
      if (trs != null && !trs.isEmpty()) {
        trxs.addAll(trs);
      }

      SubcriptionJobScheduller job = subcriptionJobSchedullerRepository.findByOrderNo(order);
      if (job != null) {
        jobs.add(job);
      }
    }

    for (UtTransactionsCart cart : carts) {
      if (cart.getTrxStatus().equalsIgnoreCase("ACTIVE")) {
        cart.setTrxStatus(success ? "IN_ACTIVE" : "IN_ACTIVE");
        utTransactionsCartRepository.save(cart);
      }
    }

    for (UtTransactions trx : trxs) {
      if (trx.getTrxStatus().equalsIgnoreCase("ORD")) {
        trx.setSettlementStatus(success ? "STL" : "CAN");
        trx.setTrxStatus(success ? "STL" : "CAN");
        utTransactionsRepository.save(trx);
      }
    }

    for (SubcriptionJobScheduller job : jobs) {
      if (!job.getStatus().equalsIgnoreCase("1")) {
        job.setStatus(success ? "0" : "1");
        subcriptionJobSchedullerRepository.save(job);
      }
    }

    if (success) {
      return errorResponse(0, "transaction success", orders);
    }
    return errorResponse(88, "transaction failed", orders);
  }

  public Map checkRisk(String packageCode, Kyc kyc){
    FundPackages fp = fundPackagesRepository.findByPackageCode(packageCode);
    if(fp == null){
      return errorResponse(50, "package_code :"+packageCode, null);
    }

    if(kyc.getRiskProfile().getMaxScore() < fp.getRisk_Profile().getMaxScore()){
      Map result = new LinkedHashMap();
      result.put("code", 1);
      result.put("info", "Produk ini memiliki profil risiko yang lebih tinggi dari profil risiko Anda. Pastikan Anda sudah memahami betul isi Fund Fact Sheet & Prospektus produk ini. Lanjutkan transaksi?");
      return result;
    }

    return errorResponse(0, ConstantUtil.SUCCESS, null);
  }

// TODO: REDEMPTION
//    @Override
//    public Map redeemOrder(List<Map> maps, User user) {
//        Map resultMap = new HashMap<>();
//        // TODO: Cek Mandatory data order
//        resultMap = this.checkIncompleteDataMandatory(maps, "REDMP");
//        if (!resultMap.isEmpty()) {
//            return resultMap;
//        }
//        // TODO : Get request Order
//        List<Map> dataMap = new ArrayList<>();
//        // List<String> errs = new ArrayList<>();
//
//        Kyc kyc = kycRepository.findByAccount(user);
//
//        for (Map map : maps) {
//            InvestmentAccounts investmentAccounts = investmentAccountsRepository.findByInvestmentAccountNoAndKycs(String.valueOf(map.get("investment")), kyc);
//            if (investmentAccounts == null) {
//                resultMap.put("code", 50);
//                resultMap.put("info", "Invesment number not found");
//                return resultMap;
//            }
//
//            // TODO: Check Redemption Transaction
//            Map checkRedemption = this.checkRedemptionTransaction(investmentAccounts.getInvestmentAccountNo());
//            System.out.println("checkRedemption : " + checkRedemption);
//            if (checkRedemption.get("status").equals(false)) {
//                return checkRedemption;
//            }
//
//            if (map.get("channel_order") == null) {
//                resultMap.put("code", 50);
//                resultMap.put("info", "Channel Order not found");
//                return resultMap;
//            }
//
//            System.out.println("KYC : " + kyc.getId());
//            System.out.println("investmentAccounts.getInvestmentAccountNo() : " + investmentAccounts.getId());
//
//            // TODO: Get range partial redemption
//            Map rangePartial = this.getRangeOfPartialByInvestment(investmentAccounts.getInvestmentAccountNo(), kyc);
//            System.out.println("rangePartial : " + rangePartial);
//
//            // TODO: Get Perkiraan Nilai Pasar (Maks total amount redeem)
//            List<FundPackageProducts> products = fundPackageProductsRepository
//                    .findAllByFundPackages(investmentAccounts.getFundPackages());
//
//            Double totalMarketValue = new BigDecimal(
//                    investmentService.getTotalMarketValue(investmentAccounts, products, kyc).toString())
//                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
//
//            // TODO: Get feeAmount Redeem in fundPackageFeeSetup
//            Double feeAmount = Double.valueOf(0);
//            Double feePcg = this.getRedemptionFee(investmentAccounts);
//            System.out.println("feePcg : " + feePcg);
//
//            if (feePcg == -100) {
//                // errs.add("50["+investmentAccounts.getInvestmentAccountNo()+"]:Nilai fee belum
//                // terdaftar di fund package fee setup");
//                feePcg = 0.0;
//            } else {
//                feeAmount = feePcg * totalMarketValue;
//            }
//
//            // TODO: Perkiraan nilai penjualan
//            Double totalMaxRedeem = totalMarketValue - feeAmount;
//            Double totalAmount = Double.valueOf(0); // order_amount
//            Double netAmount = Double.valueOf(0);
//
//            Map dataRangePartial = (Map) rangePartial.get("data");
//            System.out.println("dataRangePartial : " + dataRangePartial);
//            System.out.println("totalMarketValue : " + totalMarketValue);
//
//            double minPartialRedemption = Double.valueOf(dataRangePartial.get("minPartialRedemption").toString());
//            double maxPartialRedemption = Double.valueOf(dataRangePartial.get("maxPartialRedemption").toString());
//
//            if (map.get("amount_type").equals("RAT001")) {
//                double amount = Double.valueOf(String.valueOf(map.get("amount_value")));
//                double minAmount = totalMaxRedeem * minPartialRedemption;
//                double maxAmount = totalMaxRedeem * maxPartialRedemption;
//
//                if (totalMaxRedeem < amount || (minAmount <= amount && amount <= maxAmount)) {
//                    if (amount > totalMaxRedeem) {
//                        amount = totalMaxRedeem;
//                    }
//                    totalAmount = amount;
//                    netAmount = totalAmount - Double.valueOf(String.valueOf(map.get("fee_amount")));
//                } else {
//                    resultMap.put("code", 14);
//                    resultMap.put("info", "Invalid request for your amount value because more than total max can redeem");
//                    return resultMap;
//                }
//            } else if (map.get("amount_type").equals("RAT002")) {
//                double amount = Double.valueOf(String.valueOf(map.get("amount_value")));
//                System.out.println("amount : " + amount);
//                if (amount < 0 || amount > 1) {
//                    resultMap.put("code", 14);
//                    resultMap.put("info", "Invalid request for your amount value is not in valid range");
//                    return resultMap;
//                } else if (amount >= 1 || (minPartialRedemption <= amount && amount <= maxPartialRedemption)) {
//                    if (amount > 1) {
//                        totalAmount = totalMaxRedeem;
//                    } else {
//                        totalAmount = totalMaxRedeem * amount;
//                    }
//                    netAmount = totalAmount - Double.valueOf(String.valueOf(map.get("fee_amount")));
//                } else {
//                    resultMap.put("code", 14);
//                    resultMap.put("info", "Invalid request for your amount value not in range of percentage partial");
//                    return resultMap;
//                }
//            }
//
////            if (map.get("amount_type").equals("RAT002")) {
////                if (Double.valueOf(String.valueOf(dataRangePartial.get("maxPartialRedemption")))
////                        .compareTo(Double.valueOf(String.valueOf(map.get("amount_value")))) >= 0
////                        && Double.valueOf(String.valueOf(dataRangePartial.get("minPartialRedemption")))
////                                .compareTo(Double.valueOf(String.valueOf(map.get("amount_value")))) <= 0) {
////                    totalAmount = Double.valueOf(String.valueOf(map.get("amount_value"))) * totalMarketValue;
////                    netAmount = totalAmount - Double.valueOf(String.valueOf(map.get("fee_amount")));
////                } else {
////                    resultMap.put("code", 14);
////                    resultMap.put("info", "Invalid request for your amount value not in range of percentage partial");
////                    return resultMap;
////                }
////            } else if (map.get("amount_type").equals("RAT001")) {
////                if (totalMaxRedeem.compareTo(Double.valueOf(String.valueOf(map.get("amount_value")))) >= 0) {
////                    totalAmount = Double.valueOf(String.valueOf(map.get("amount_value")));
////                    netAmount = totalAmount - Double.valueOf(String.valueOf(map.get("fee_amount")));
////                } else {
////                    resultMap.put("code", 14);
////                    resultMap.put("info",
////                            "Invalid request for your amount value because more than total max can redeem");
////                    return resultMap;
////                }
////            }
//            // Get Settlement Account Customer
//            SettlementAccounts sett = settlementAccountsRepository.findByKycs(kyc);
//            UtTransactionType type = utTransactionTypeRepository.findByTrxCode("REDMP");
//
//            FundPackages fundPackage = investmentAccounts.getFundPackages();
//            // TODO: Check price date cut off or no based on fundpackages
//            Date priceDate = new Date();
//            Calendar currentDate = Calendar.getInstance();
//            Calendar fundPackageDate = Calendar.getInstance();
//            fundPackageDate.setTime(fundPackage.getTransactionCutOff());
//            fundPackageDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
//            fundPackageDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
//            fundPackageDate.set(Calendar.DATE, currentDate.get(Calendar.DATE));
//            if (currentDate.before(fundPackageDate)) {
//                priceDate = currentDate.getTime();
//            } else if (currentDate.after(fundPackageDate)) {
//                priceDate = globalService.getNextWorkingDate(fundPackageDate.getTime());
//            }
//
//            // TODO: Save and Generate orderNo
//            String orderNo = globalService.generateOrderNo(1);
//            UtTransactionsGroup group = new UtTransactionsGroup();
//            group.setOrderNo(orderNo);
//            group.setVersion(0);
//            utTransactionsGroupRepository.save(group);
//
//            // TODO: Save into ut_transactions
//            String orderNote = "";
//            Double settlement_amount = new Double(0);
//            for (FundPackageProducts product : products) {
//                // TODO: Get last NAV
//                UtProducts utProducts = product.getUtProducts();
//                List<CustomerBalance> balances = customerBalanceRepository
//                        .findTop1ByUtProductAndInvAccountOrderByBalanceDateDesc(utProducts, investmentAccounts);
//                Date tempDate = null;
//                try {
//                    if (tempDate == null) {
//                        tempDate = balances.get(0).getBalanceDate();
//                    } else if (tempDate != balances.get(0).getBalanceDate()
//                            || !(tempDate.equals(balances.get(0).getBalanceDate()))) {
//                        if (tempDate.compareTo(balances.get(0).getBalanceDate()) > 0) {
//                            tempDate = balances.get(0).getBalanceDate();
//                        }
//                    }
//                } catch (Exception e) {
//                    continue;
//                }
//                UtProductFundPrices utproductFundPrice = utProductFundPricesRepository
//                        .findByUtProductsAndPriceDate(product.getUtProducts(), tempDate);
//
//                UtTransactions trx = new UtTransactions();
//                trx.setCreatedBy(user.getUsername());
//                trx.setCreatedDate(new Date());
//                trx.setOrderNo(orderNo);
//                trx.setOrderAmount(totalAmount * product.getCompositition());
//                trx.setFeeAmount(Double.valueOf(String.valueOf(map.get("fee_amount"))) * product.getCompositition());
//                if (!Double.valueOf(String.valueOf(map.get("fee_amount"))).equals(feeAmount)) {
//                    orderNote = orderNote + "fee amount is different with package’s fee;";
//                }
//                trx.setNetAmount(netAmount * product.getCompositition());
//                trx.setChannelOrderId(String.valueOf(map.get("channel_order")));
//
//                trx.setFundPackageRef(fundPackage);
//                trx.setPriceDate(priceDate);
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                if (!String.valueOf(map.get("price_date")).equals(sdf.format(priceDate))) {
//                    orderNote = orderNote + "price date has been changed to continue transaction;";
//                }
//                if (orderNote != "") {
//                    Map mapNote = new HashMap<>();
//                    mapNote.put("net_amount", map.get("net_amount"));
//                    mapNote.put("fee_amount", map.get("fee_amount"));
//                    mapNote.put("total_amount", map.get("total_amount"));
//                    mapNote.put("price_date", map.get("price_date"));
//
//                    trx.setTrxNotes(mapNote.toString());
//                }
//                trx.setTransactionType(type);
//                trx.setKycId(kyc);
//                trx.setProductId(product.getUtProducts());
//                trx.setSettlementAmount(trx.getOrderAmount());
//                settlement_amount = settlement_amount + trx.getSettlementAmount();
//                trx.setSettlementStatus("YES");
//                trx.setTrxDate(new Date());
//                trx.setTrxNo(globalService.generateTrxNo(type, 2));
//                trx.setTrxStatus("ORD");
//                trx.setAtTrxNo(UUID.randomUUID().toString());
//                trx.setTrxType(type.getId().intValue());
//                FundEscrowAccount escrowAccount = fundEscrowAccountRepository.findByFundPackages(fundPackage);
//                trx.setSettlementNoRef(escrowAccount);
//                trx.setTransactionDate(new Date());
//                trx.setOrderUnit(new BigDecimal(trx.getOrderAmount())
//                        .divide(new BigDecimal(utproductFundPrice.getBidPrice()), 2, RoundingMode.HALF_UP)
//                        .setScale(2, RoundingMode.HALF_UP).doubleValue());
//                trx.setInvestementAccount(investmentAccounts);
//                trx.setTaxAmount(new Double(0));
//                trx = utTransactionsRepository.save(trx);
//            }
//
//            // TODO: Send to Avantrade if full redeem/partial
//            Object responseRedeem = new Object();
//            if (map.get("amount_type").equals("RAT002")) {
//                if (Double.valueOf(String.valueOf(map.get("amount_value"))) >= 1) {
//                    System.out.println("RAT002 : ALL");
//
//                    responseRedeem = this.createRedemptionToAvantrade(orderNo);
//                    if (!responseRedeem.toString().contains("2000033")) {
//                        for (FundPackageProducts product : products) {
//                            List<UtTransactions> trxs = utTransactionsRepository.findAllByOrderNo(orderNo);
//                            for (UtTransactions trx : trxs) {
//                                trx.setTrxStatus("CAN");
//                                trx = utTransactionsRepository.save(trx);
//                            }
//                        }
//                    }
//                } else {
//                    System.out.println("RAT002 : PARTIAL");
//
//                    responseRedeem = investmentService.createTrxRedemptionToAvantradeNew(orderNo, 0);
//                    if (!responseRedeem.toString().contains("2000033")) {
//                        for (FundPackageProducts product : products) {
//                            List<UtTransactions> trxs = utTransactionsRepository.findAllByOrderNo(orderNo);
//                            for (UtTransactions trx : trxs) {
//                                trx.setTrxStatus("CAN");
//                                trx = utTransactionsRepository.save(trx);
//                            }
//                        }
//                    }
//                }
//            } else if (map.get("amount_type").equals("RAT001")) {
//                System.out.println("totalMarketValue : " + totalMarketValue);
//                System.out.println("map.get(\"amount_value\") : " + map.get("amount_value"));
//                if (totalMarketValue < Double.valueOf(String.valueOf(map.get("amount_value")))) {
//                    System.out.println("RAT001 : ALL");
//
//                    responseRedeem = this.createRedemptionToAvantrade(orderNo);
//                    if (!responseRedeem.toString().contains("2000033")) {
//                        for (FundPackageProducts product : products) {
//                            List<UtTransactions> trxs = utTransactionsRepository.findAllByOrderNo(orderNo);
//                            for (UtTransactions trx : trxs) {
//                                trx.setTrxStatus("CAN");
//                                trx = utTransactionsRepository.save(trx);
//                            }
//                        }
//                    }
//                } else {
//                    System.out.println("RAT001 : PARTIAL");
//
//                    responseRedeem = investmentService.createTrxRedemptionToAvantradeNew(orderNo, 0);
//                    if (!responseRedeem.toString().contains("2000033")) {
//                        for (FundPackageProducts product : products) {
//                            List<UtTransactions> trxs = utTransactionsRepository.findAllByOrderNo(orderNo);
//                            for (UtTransactions trx : trxs) {
//                                trx.setTrxStatus("CAN");
//                                trx = utTransactionsRepository.save(trx);
//                            }
//                        }
//                    }
//                }
//            }
//            if (responseRedeem != null && responseRedeem.equals("2000033")) {
//                // TODO: Response if ava successfully
//                Map dataMaps = new HashMap<>();
//                dataMaps.put("order_number", orderNo);
//                dataMaps.put("investment_account", investmentAccounts.getInvestmentAccountNo());
//                dataMaps.put("package_name", investmentAccounts.getFundPackages().getFundPackageName());
//                dataMaps.put("investment_market_value", totalMarketValue);
//                dataMaps.put("settlement_amount", settlement_amount);
//                SettlementAccounts accounts = settlementAccountsRepository.findByKycs(kyc);
//                dataMaps.put("settlement_bank", accounts.getBankId().getBankName());
//                dataMaps.put("settlement_account_number", accounts.getSettlementAccountNo());
//                dataMaps.put("settlement_account_name", accounts.getSettlementAccountName());
//                resultMap.put("code", 0);
//                resultMap.put("info", "Order successfully submitted");
//                resultMap.put("data", dataMaps);
//            } else if (responseRedeem != null && !responseRedeem.equals("2000033")) {
//                resultMap.put("code", 1);
//                resultMap.put("info", "Response Transaction Redemption from Avantrade :" + responseRedeem);
//            } else {
//                resultMap.put("code", 1);
//                resultMap.put("info", "Transaction Redemption failed.");
//            }
//        }
//        return resultMap;
//    }
//@Override
//    public Map topupOrder(List<Map> maps, Kyc kyc, String statusPayment) {
//        // TODO: Cek Mandatory data order
//        Map resultMap = this.checkIncompleteDataMandatory(maps, "TOPUP");
//        if (!resultMap.isEmpty()) {
//            return resultMap;
//        }
//
//        // TODO : Get request Order
//        List<Map> dataMap = new ArrayList<>();
//        for (Map map : maps) {
//            InvestmentAccounts investmentAccounts = investmentAccountsRepository.findByInvestmentAccountNo(String.valueOf(map.get("investment")));
//            FundPackages fundPackage = investmentAccounts.getFundPackages();
//
//            // TODO: Check price date cut off or no based on fundpackages
//            Date priceDate = new Date();
//            Calendar currentDate = Calendar.getInstance();
//            Calendar fundPackageDate = Calendar.getInstance();
//            fundPackageDate.setTime(fundPackage.getTransactionCutOff());
//            fundPackageDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
//            fundPackageDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
//            fundPackageDate.set(Calendar.DATE, currentDate.get(Calendar.DATE));
//            if (currentDate.before(fundPackageDate)) {
//                priceDate = currentDate.getTime();
//            } else if (currentDate.after(fundPackageDate)) {
//                priceDate = globalService.getNextWorkingDate(fundPackageDate.getTime());
//            }
//
//            // TODO: Save and Generate orderNo
//            String orderNo = globalService.generateOrderNo(1);
//
//            UtTransactionsGroup group = new UtTransactionsGroup();
//            group.setOrderNo(orderNo);
//            group.setVersion(0);
//            utTransactionsGroupRepository.save(group);
//
//            // TODO: Insert UT Cart
//            UtTransactionsCart cart = new UtTransactionsCart();
//            cart.setInvestmentAccount(investmentAccounts);
//            UtTransactionType trxType = utTransactionTypeRepository.findByTrxCode("TOPUP");
//            cart = this.insertUTCart(map, fundPackage, cart, kyc, trxType, orderNo);
//
//            // TODO: Insert to JOB
//            subscriptionJobSchedullerService.saveJob(orderNo, "0", null, null, null, paymentMethodRepository.findByCode("CHAN").getCode());
//
//            // TODO: Insert UT Transaction
//            Map dataInsertTrx = this.saveUtTransactions(cart, map, kyc, trxType, priceDate, statusPayment);
//            List<UtTransactions> transactions = (List<UtTransactions>) dataInsertTrx.get("data");
//            if (!transactions.isEmpty()) {
//                cart.setTrxStatus("IN_ACTIVE");
//                utTransactionsCartRepository.save(cart);
//                utTransactionsRepository.markEntryAsRead(orderNo);
//            }
//
//            // TODO: Response
//            Map xMap = new HashMap<>();
//            xMap.put("order_number", orderNo);
//            xMap.put("investment_account", cart.getInvestmentAccount().getInvestmentAccountNo());
//            xMap.put("channel_order", String.valueOf(map.get("channel_order")));
//            xMap.put("package_code", cart.getFundPackages().getPackageCode());
//            xMap.put("net_amount", cart.getNetAmount());
//            xMap.put("fee_amount", cart.getFeeAmount());
//            xMap.put("total_amount", cart.getOrderAmount());
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            xMap.put("price_date", sdf.format(priceDate));
//            xMap.put("order_note", String.valueOf(dataInsertTrx.get("order_note")));
//            dataMap.add(xMap);
//        }
//
//        resultMap.put("code", 0);
//        resultMap.put("info", "Order successfully submitted");
//        resultMap.put("data", dataMap);
//        return resultMap;
//    }    
//@Override
//    public Map subscribeOrder(List<Map> maps, Kyc kyc, String statusPayment) {
//        Map resultMap = this.checkIncompleteDataMandatory(maps, "SUBCR");
//        if (!resultMap.isEmpty()) {
//            return resultMap;
//        }
//
//        // TODO : Get request Order
//        List<Map> dataMap = new ArrayList<>();
//        for (Map map : maps) {
//            FundPackages fundPackage = fundPackagesRepository.findByPackageCode(String.valueOf(map.get("package_code")));
//            System.out.println("fundPackage : " + map.get("package_code") + " : " + fundPackage);
//
//            Date priceDate = new Date();
//            Calendar currentDate = Calendar.getInstance();
//            Calendar fundPackageDate = Calendar.getInstance();
//            fundPackageDate.setTime(fundPackage.getTransactionCutOff());
//            fundPackageDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
//            fundPackageDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
//            fundPackageDate.set(Calendar.DATE, currentDate.get(Calendar.DATE));
//            if (currentDate.before(fundPackageDate)) {
//                priceDate = currentDate.getTime();
//            } else if (currentDate.after(fundPackageDate)) {
//                priceDate = globalService.getNextWorkingDate(fundPackageDate.getTime());
//            }
//
//            // TODO: Generate orderNo
//            String orderNo = globalService.generateOrderNo(1);
//
//            // TODO: Save orderNo
////            UtTransactionsGroup group = new UtTransactionsGroup();
////            group.setOrderNo(orderNo);
////            group.setVersion(0);
////            utTransactionsGroupRepository.save(group);
//            // TODO: Insert UT Cart
//            UtTransactionType trxType = utTransactionTypeRepository.findByTrxCode("SUBCR");
//
//            UtTransactionsCart cart = new UtTransactionsCart();
//            cart = this.insertUTCart(map, fundPackage, cart, kyc, trxType, orderNo);
//
//            // TODO: Insert to JOB
//            subscriptionJobSchedullerService.saveJob(orderNo, "0", null, null, null, paymentMethodRepository.findByCode("CHAN").getCode());
//
//            // TODO: Insert UT Transaction
//            Map dataInsertTrx = this.saveUtTransactions(cart, map, kyc, trxType, priceDate, statusPayment);
//            List<UtTransactions> transactions = (List<UtTransactions>) dataInsertTrx.get("data");
//            if (!transactions.isEmpty()) {
//                cart.setTrxStatus("IN_ACTIVE");
//                utTransactionsCartRepository.save(cart);
//                utTransactionsRepository.markEntryAsRead(orderNo);
//            }
//
//            JSONObject jSONObject = new JSONObject();
//            jSONObject.put("kyc", kyc);
//            jSONObject.put("details", transactions);
//
//            System.out.println("DATA JSON : " + jSONObject.toString());
//
//            // TODO: Response
//            Map xMap = new HashMap<>();
//            xMap.put("order_number", orderNo);
//            xMap.put("investment_account", cart.getInvestmentAccount().getInvestmentAccountNo());
//            xMap.put("channel_order", String.valueOf(map.get("channel_order")));
//            xMap.put("package_code", cart.getFundPackages().getPackageCode());
//            xMap.put("net_amount", cart.getNetAmount());
//            xMap.put("fee_amount", cart.getFeeAmount());
//            xMap.put("total_amount", cart.getOrderAmount());
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            xMap.put("price_date", sdf.format(priceDate));
//            xMap.put("order_note", String.valueOf(dataInsertTrx.get("order_note")));
//            dataMap.add(xMap);
//        }
//
//        resultMap.put("code", 0);
//        resultMap.put("info", "Order successfully submitted");
//        resultMap.put("data", dataMap);
//        return resultMap;
//    }
// TODO: INSERT UT_TRANSACTION
//    private Map saveUtTransactions(UtTransactionsCart cart, Map map, Kyc kyc, UtTransactionType trxType, Date priceDate, String statusPayment) {
//        List<UtTransactions> utTransactions = new ArrayList<>();
//        List<FundPackageProducts> packageProducts = fundPackageProductsRepository.findAllByFundPackages(cart.getFundPackages());
//        FundEscrowAccount escrowAccount = fundEscrowAccountRepository.findByFundPackages(cart.getFundPackages());
//
//        String orderNote = "";
//        for (FundPackageProducts packageProduct : packageProducts) {
//
//            UtTransactions utTransaction = new UtTransactions();
//            utTransaction.setOrderNo(cart.getOrderNo());
//            utTransaction.setCreatedBy(kyc.getAccount().getUsername());
//            utTransaction.setCreatedDate(new Date());
//            utTransaction.setFeeAmount(cart.getFeeAmount() * packageProduct.getCompositition());
//            if (!Double.valueOf(String.valueOf(map.get("fee_amount"))).equals(cart.getFeeAmount())) {
//                orderNote = orderNote + "fee amount is different with package’s fee;";
//            }
//            if (map.get("channel_order") != null && !String.valueOf(map.get("channel_order")).isEmpty()) {
//                utTransaction.setChannelOrderId(String.valueOf(map.get("channel_order")));
//            }
//            utTransaction.setNetAmount(cart.getNetAmount() * packageProduct.getCompositition());
//            utTransaction.setOrderAmount(utTransaction.getFeeAmount() + utTransaction.getNetAmount());
//            utTransaction.setFundPackageRef(cart.getFundPackages());
//            utTransaction.setPriceDate(priceDate);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            if (!String.valueOf(map.get("price_date")).equals(sdf.format(priceDate))) {
//                orderNote = orderNote + "price date has been changed to continue transaction;";
//            }
//            if (orderNote != null && !orderNote.isEmpty()) {
//                Map mapNote = new HashMap<>();
//                mapNote.put("net_amount", map.get("net_amount"));
//                mapNote.put("fee_amount", map.get("fee_amount"));
//                mapNote.put("total_amount", map.get("total_amount"));
//                mapNote.put("price_date", map.get("price_date"));
//
//                utTransaction.setTrxNotes(mapNote.toString());
//            }
//
//            utTransaction.setTransactionType(trxType);
//            utTransaction.setKycId(kyc);
//            utTransaction.setProductId(packageProduct.getUtProducts());
//            utTransaction.setSettlementAmount(utTransaction.getOrderAmount());
//            utTransaction.setTrxDate(cart.getTrxDate());
//            utTransaction.setTrxNo(globalService.generateTrxNo(cart.getTransactionType(), 1));
//
//            //utTransaction.setSettlementStatus("STL");
//            //utTransaction.setTrxStatus("STL");
//            utTransaction.setSettlementStatus(statusPayment);
//            utTransaction.setTrxStatus(statusPayment);
//
//            utTransaction.setAtTrxNo(UUID.randomUUID().toString());
//            utTransaction.setTrxType(cart.getTransactionType().getId().intValue());
//            utTransaction.setSettlementNoRef(escrowAccount);
//            utTransaction.setTransactionDate(cart.getTrxDate());
//
//            InvestmentAccounts investmentAccounts;
//            if (cart.getTransactionType().getTrxCode().equalsIgnoreCase("SUBCR")) {
//                investmentAccounts = investmentAccountsService.saveInvestmentAccount(cart.getFundPackages(), cart.getKyc());
//                cart.setInvestmentAccount(investmentAccounts);
//                cart = utTransactionsCartRepository.save(cart);
//            } else {
//                investmentAccounts = cart.getInvestmentAccount();
//            }
//
//            utTransaction.setInvestementAccount(investmentAccounts);
//            utTransaction.setTaxAmount(new Double(0));
//            utTransaction = utTransactionsRepository.save(utTransaction);
//            utTransactions.add(utTransaction);
//        }
//        Map trxMap = new HashMap<>();
//        trxMap.put("order_note", orderNote);
//        trxMap.put("data", utTransactions);
//        return trxMap;
//    }    
//    TODO: Cek Fee Amount
//    private Double checkFeeAmount(Double netAmount, FundPackages fp, Double feeAmountOrder, UtTransactionType trxType) {
//        List<FundPackageFeeSetup> feeSetups = fundPackageFeeSetupRepository.findAllByFundPackagesAndTransactionTypeOrderByIdAsc(fp, trxType);
//        for (FundPackageFeeSetup feeSetup : feeSetups) {
//            if ((feeSetup.getAmountMin() >= netAmount && netAmount < feeSetup.getAmountMax())
//                    || (feeSetup.getAmountMin() >= netAmount && feeSetup.getAmountMax() == new Double(0))) {
//                Double feeAmountFeeSetup = feeSetup.getFeeAmount() * netAmount;
//                if (feeAmountFeeSetup != feeAmount) {
//                    feeAmount = feeAmountFeeSetup;
//                }
//            }
//        }
//
//        return feeAmount;
//    }
//    private UtTransactionsCart insertUTCart(Map map, FundPackages fundPackage, UtTransactionsCart cart, Kyc kyc, UtTransactionType trxType, String orderNo) {
//        PaymentMethod paymentMethod = paymentMethodRepository.findByCode("CHAN");
//        FundEscrowAccount account = fundEscrowAccountRepository.findByFundPackages(fundPackage);
//
//        cart.setFundPackages(fundPackage);
//        cart.setCreatedBy(kyc.getAccount().getUsername());
//        cart.setCreatedDate(new Date());
//        cart.setKyc(kyc);
//        cart.setNetAmount(Double.valueOf(String.valueOf(map.get("net_amount"))));
//        cart.setFeeAmount(this.checkFeeAmount(Double.valueOf(String.valueOf(map.get("net_amount"))), fundPackage, Double.valueOf(String.valueOf(map.get("fee_amount"))), trxType));
//        cart.setOrderAmount(cart.getNetAmount() + cart.getFeeAmount());
//        cart.setPaymentType(paymentMethod.getCode());
//        cart.setTrxDate(new Date());
//        cart.setTransactionType(trxType);
//        cart.setTrxStatus("ACTIVE");
//        cart.setOrderNo(orderNo);
//        cart.setSettlementRefNo(account.getVaCode() + " " + account.getEscrowNumber());
//        return utTransactionsCartRepository.save(cart);
//    }
//    @Override
//    public Map getInvestmentIfNotInPromotion(InvestmentAccounts invest, Date tempDate) {
//        List<FundPackageProducts> fproducts = fundPackageProductsRepository.findAllByFundPackages(invest.getFundPackages());
//        System.out.println("fproducts : " + fproducts);
//        System.out.println("tempDate : " + tempDate);
//        for (FundPackageProducts fpp : fproducts) {
//            UtProducts utProducts = fpp.getUtProducts();
//            List<CustomerBalance> balances = customerBalanceRepository
//                    .findTop1ByUtProductAndInvAccountOrderByBalanceDateDesc(utProducts, invest);
//
//            try {
//                if (tempDate == null) {
//                    tempDate = balances.get(0).getBalanceDate();
//                } else if (tempDate != balances.get(0).getBalanceDate()
//                        || !(tempDate.equals(balances.get(0).getBalanceDate()))) {
//                    if (tempDate.compareTo(balances.get(0).getBalanceDate()) > 0) {
//                        tempDate = balances.get(0).getBalanceDate();
//                    }
//                }
//            } catch (Exception e) {
//                continue;
//            }
//        }
//
//        List<BigDecimal> result = new ArrayList<BigDecimal>();
//        List<BigDecimal> units = new ArrayList<BigDecimal>();
//        List<BigDecimal> amounts = new ArrayList<BigDecimal>();
//        List<BigDecimal> balanceUnits = new ArrayList<BigDecimal>();
//        for (FundPackageProducts product : fproducts) {
//            UtProductFundPrices utproductFundPrice = utProductFundPricesRepository.findByUtProductsAndPriceDate(product.getUtProducts(), tempDate);
//            System.out.println("utproductFundPrice : " + utproductFundPrice);
//
//            CustomerBalance cb = customerBalanceRepository.findByInvAccountAndUtProductAndBalanceDate(invest, product.getUtProducts(), tempDate);
//            System.out.println("cb : " + cb);
//            try {
//                units.add(BigDecimal.valueOf(product.getUtProducts().getMinRedemptionUnit())
//                        .divide(BigDecimal.valueOf(cb.getCurrentUnit()), 5, RoundingMode.UP));
//            } catch (Exception e) {
//                units.add(new BigDecimal(0));
//            }
//            try {
//                amounts.add(BigDecimal.valueOf(product.getUtProducts().getMinRedemptionAmount())
//                        .divide(BigDecimal.valueOf(cb.getCurrentAmount()), 5, RoundingMode.UP));
//            } catch (Exception e) {
//                amounts.add(new BigDecimal(0));
//            }
//            try {
//                balanceUnits.add(BigDecimal.valueOf(product.getUtProducts().getMinBalAfterRedemptionUnit())
//                        .multiply(BigDecimal.valueOf(utproductFundPrice.getBidPrice()))
//                        .divide(BigDecimal.valueOf(cb.getCurrentAmount()), 5, RoundingMode.UP));
//            } catch (Exception e) {
//                balanceUnits.add(new BigDecimal(0));
//            }
//        }
//
//        result.add(this.getMaxPercentage(units));
//        result.add(this.getMaxPercentage(amounts));
//        BigDecimal maxPartialRedemption = new BigDecimal(1)
//                .subtract(this.getMaxPercentage(balanceUnits)).setScale(2, RoundingMode.DOWN);
//        BigDecimal minPartialRedemption = this.getMaxPercentage(result).setScale(2, RoundingMode.UP);
//
//        Map map = new HashMap();
//        map.put("min", minPartialRedemption);
//        map.put("max", maxPartialRedemption);
//        return map;
//    }    
//    @Override
//    public Map getRangeOfPartialByInvestment(String invNo, Kyc kyc) {
//        Map resultMap = new HashMap<>();
//        BigDecimal minPartialRedemption = BigDecimal.ZERO;
//            BigDecimal maxPartialRedemption = BigDecimal.ZERO;
//
//        InvestmentAccounts invest = investmentAccountsRepository.findByInvestmentAccountNoAndKycs(invNo, kyc);
//        if (invest == null) {
//            resultMap.put("code", 0);
//            resultMap.put("info", "Data not found");
//            return errorResponse(0, "partial_investment", null);
//        }
//
//        Map manualInvestment = getInvestmentIfNotInPromotion(invest, invest.getCreatedDate());
//
//        try {
//            
//            Date tempDate = null;
//
//            InvestmentAccounts invest = investmentAccountsRepository.findByInvestmentAccountNoAndKycs(invNo, kyc);
//            System.out.println("invest : " + invest);
//            System.out.println("invNo : " + invNo);
//            System.out.println("kyc : " + kyc.getId());
//
//            if (invest == null) {
//                resultMap.put("code", 0);
//                resultMap.put("info", "Data not found");
//                return resultMap;
//            }
//
//            Map manualInvestment = this.getInvestmentIfNotInPromotion(invest, tempDate);
//            maxPartialRedemption = (BigDecimal) manualInvestment.get("max");
//            minPartialRedemption = (BigDecimal) manualInvestment.get("min");
//
//            // TODO: ADD ADDITIONAL CONDITION for PORTALI 1007
//            Double minimal_investment_amount = 0.00;
//            Date minimal_investment_redeem_date = null;
//            Double pcgInvestmentPromotion = 0.00;
//            InvestmentPromotion inv_promotion = investmentPromotionRepository
//                    .findByInvestmentAccountAndRowStatus(invest, true);
//            if (inv_promotion != null) {
//                Calendar invPromotionDate = Calendar.getInstance();
//                invPromotionDate.setTime(inv_promotion.getMinimalInvestmentRedeemDate());
//
//                Calendar currentDate = Calendar.getInstance();
//                if (invPromotionDate.getTime().after(currentDate.getTime())) {
//                    // GET ORIGINAL TOTAL MARKET VALUE
//                    List<FundPackageProducts> products = fundPackageProductsRepository
//                            .findAllByFundPackages(invest.getFundPackages());
//                    Double oriTotalMarketValue = investmentService.getTotalMarketValue(invest, products, kyc);
//                    if (invest.getInvestmentAccountNo() == inv_promotion.getInvestmentAccount()
//                            .getInvestmentAccountNo()) {
//                        Double promotionMarketValue = oriTotalMarketValue - inv_promotion.getMinimalInvestmentAmount();
//                        pcgInvestmentPromotion = promotionMarketValue / oriTotalMarketValue;
//                    }
//
//                }
//
//                minimal_investment_amount = inv_promotion.getMinimalInvestmentAmount();
//                minimal_investment_redeem_date = inv_promotion.getMinimalInvestmentRedeemDate();
//            }
//
//            if (pcgInvestmentPromotion != 0) {
//                if (BigDecimal.valueOf(pcgInvestmentPromotion).compareTo(maxPartialRedemption) < 0) {
//                    maxPartialRedemption = BigDecimal.valueOf(pcgInvestmentPromotion).setScale(2, RoundingMode.DOWN);
//                }
//            }
//
//            Map map = new HashMap();
//            map.put("maxPartialRedemption", maxPartialRedemption);
//            map.put("minPartialRedemption", minPartialRedemption);
//            map.put("minimal_investment_amount", minimal_investment_amount);
//            map.put("minimal_investment_redeem_date", minimal_investment_redeem_date);
//
//            resultMap.put("code", 0);
//            resultMap.put("info", "Data found");
//            resultMap.put("data", map);
//            return resultMap;
//        } catch (Exception e) {
//            resultMap.put("code", 1);
//            resultMap.put("info", "Failed, please try again!");
//            return resultMap;
//        }
//    }
}
