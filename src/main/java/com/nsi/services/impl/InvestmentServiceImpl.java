package com.nsi.services.impl;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.CustomerBalance;
import com.nsi.domain.core.FundEscrowAccount;
import com.nsi.domain.core.FundPackageFeeSetup;
import com.nsi.domain.core.FundPackageProducts;
import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.GlobalParameter;
import com.nsi.domain.core.GoalPlanner;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.InvestmentManagers;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.SettlementAccounts;
import com.nsi.domain.core.User;
import com.nsi.domain.core.UtProductFundPrices;
import com.nsi.domain.core.UtProducts;
import com.nsi.domain.core.UtTransactionType;
import com.nsi.domain.core.UtTransactions;
import com.nsi.domain.core.UtTransactionsCart;
import com.nsi.domain.core.UtTransactionsGroup;
import com.nsi.dto.AgentDto;
import com.nsi.dto.RedemptionPartialDto;
import com.nsi.dto.request.investment.DetailRequest;
import com.nsi.enumeration.TrxStatusEnum;
import com.nsi.repositories.core.CustomerBalanceRepository;
import com.nsi.repositories.core.FundEscrowAccountRepository;
import com.nsi.repositories.core.FundPackageFeeSetupRepository;
import com.nsi.repositories.core.FundPackageProductsRepository;
import com.nsi.repositories.core.FundPackagesRepository;
import com.nsi.repositories.core.GlobalParameterRepository;
import com.nsi.repositories.core.InvestmentAccountsRepository;
import com.nsi.repositories.core.KycRepository;
import com.nsi.repositories.core.LookupLineRepository;
import com.nsi.repositories.core.SettlementAccountsRepository;
import com.nsi.repositories.core.UtProductFundPricesRepository;
import com.nsi.repositories.core.UtTransactionTypeRepository;
import com.nsi.repositories.core.UtTransactionsCartRepository;
import com.nsi.repositories.core.UtTransactionsGroupRepository;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.services.GlobalService;
import com.nsi.services.InvestmentService;
import com.nsi.services.UtilService;
import com.nsi.util.DateTimeUtil;
import com.nsi.util.ConstantUtil;
import com.nsi.util.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InvestmentServiceImpl extends BaseService implements InvestmentService {
  @PersistenceContext
  EntityManager entityManager;
  @Autowired
  GlobalService globalService;
  @Autowired
  FundPackagesRepository packageService;
  @Autowired
  FundEscrowAccountRepository fundEscrowAccountService;
  @Autowired
  UtTransactionsCartRepository utTransactionCartService;
  @Autowired
  KycRepository kycRepository;
  @Autowired
  UtTransactionTypeRepository utTransactionTypeService;
  @Autowired
  FundPackageFeeSetupRepository fundPackageFeeSetupService;
  @Autowired
  FundPackageProductsRepository fundPackageProductsService;
  @Autowired
  UtTransactionsRepository utTransactionsService;
  @Autowired
  SettlementAccountsRepository settlementAccountsService;
  @Autowired
  InvestmentAccountsRepository investmentAccountsRepository;
  @Autowired
  CustomerBalanceRepository customerBalanceRepository;
  @Autowired
  UtProductFundPricesRepository utProductFundPricesRepository;
  @Autowired
  GlobalParameterRepository globalParameterRepository;
  @Autowired
  UtTransactionsGroupRepository utTransactionsGroupRespository;
  @Autowired
  UtilService utilService;
  @Autowired
  LookupLineRepository lookupLineRepository;
  @Autowired
  UtTransactionsRepository utTransactionsRepository;
  @Autowired
  FundPackageProductsRepository fundPackageProductsRepository;

  private Logger logger = Logger.getLogger(this.getClass());

  //TODO: SUBSCRIPTION
  @Override
  public Map subscribeTransfer(Map map, User user) {
    List<Map> orders = (List<Map>) map.get("order");

    List<Map> maps = new ArrayList<>();
    for (Map ord : orders) {
      FundPackages fundPackages = packageService
          .findByPackageCode(String.valueOf(ord.get("package_code")));
      FundEscrowAccount escrowAccount = fundEscrowAccountService.findByFundPackages(fundPackages);
      String orderNo = globalService.generateOrderNoTransfer(1, escrowAccount.getVaCode());

      Kyc kyc = kycRepository.findByAccount(user);
      UtTransactionType transactionType = utTransactionTypeService.findByTrxCode("SUBCR");
      List<FundPackageFeeSetup> feeSetups = fundPackageFeeSetupService
          .findAllByFundPackagesAndTransactionTypeOrderByIdAsc(fundPackages, transactionType);

      //UPDATE TRANSACTION CART
      UtTransactionsCart cart = utTransactionCartService
          .findByFundPackagesAndKycAndTransactionTypeAndTrxStatus(fundPackages, kyc,
              transactionType, TrxStatusEnum.ACTIVE.getStatus());
      cart.setOrderNo(orderNo);
      cart.setPaymentType(String.valueOf(map.get("payment_method")));
      cart.setNetAmount(new Double(String.valueOf(ord.get("subscribe_amount"))));
      cart.setTransactionType(transactionType);
      cart.setUpdatedDate(new Date());
      cart.setFeeAmount(globalService.getFeeAmountTransCart(feeSetups,
          new Double(String.valueOf(ord.get("subscribe_amount")))));
      cart.setOrderAmount(cart.getNetAmount() + cart.getFeeAmount());
      cart = utTransactionCartService.save(cart);

      //INSERT TO UT_TRANSACTIONS
      List<UtTransactions> transactions = this.saveTransactionPayment(cart);
      cart.setTrxStatus(TrxStatusEnum.IN_ACTIVE.getStatus());
      cart = utTransactionCartService.save(cart);
      utTransactionsService.markEntryAsRead(orderNo);

      Map submap = new HashMap<>();
      submap.put("order_number", orderNo);
      submap.put("package_code", String.valueOf(ord.get("package_code")));
      submap.put("currency", String.valueOf(ord.get("currency")));
      submap.put("subscribe_amount", new Double(ord.get("subscribe_amount").toString()));
      submap.put("fee", cart.getFeeAmount() / cart.getNetAmount());
      submap.put("settlement_price", cart.getOrderAmount());

      SettlementAccounts settlementAccount = settlementAccountsService.findByKycs(kyc);
      submap.put("settlement_bank", settlementAccount.getBankId().getBankName());
      submap.put("settlement_account_name", settlementAccount.getSettlementAccountName());
      submap.put("settlement_account_number", settlementAccount.getSettlementAccountNo());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
      submap.put("settlement_valid_period", sdf.format(new Date()) + " " + sdf2
          .format(cart.getFundPackages().getTransactionCutOff()));

      maps.add(submap);
    }
    Map resultMap = new HashMap<>();
    resultMap.put("subscription", maps);
    return resultMap;
  }

  @Override
  public List<UtTransactions> saveTransactionPayment(UtTransactionsCart cart) {
    InvestmentAccounts investmentAccounts = null;
    if (cart.getTransactionType().getTrxCode().equals("SUBCR")
        || cart.getTransactionType().getTrxCode() == "SUBCR") {
      investmentAccounts = globalService
          .saveInvestmentAccount(cart.getFundPackages(), cart.getKyc());
      cart.setInvestmentAccount(investmentAccounts);
      cart = utTransactionCartService.save(cart);
    } else {
      investmentAccounts = cart.getInvestmentAccount();
    }

    Date newPriceDate = cart.getUpdatedDate();
    Calendar newPriceDatecal = Calendar.getInstance();
    newPriceDatecal.setTime(newPriceDate);

    Calendar cal = Calendar.getInstance();
    cal.setTime(cart.getFundPackages().getSettlementCutOff());

    newPriceDatecal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
    newPriceDatecal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
    newPriceDatecal.set(Calendar.SECOND, cal.get(Calendar.SECOND));
    newPriceDatecal.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));

    newPriceDate = newPriceDatecal.getTime();
    if (globalService.checkOverTransactionCutOffOrNot(cart.getFundPackages().getTransactionCutOff(),
        cart.getUpdatedDate())) {
      newPriceDate = globalService.getWorkingDate(newPriceDate);
    }

    List<UtTransactions> utTransactionsList = new ArrayList<>();
    List<FundPackageProducts> products = fundPackageProductsService
        .findAllByFundPackages(cart.getFundPackages());
    for (FundPackageProducts product : products) {
      UtTransactions utTransactions = new UtTransactions();
      utTransactions.setProductId(product.getUtProducts());
      utTransactions.setTrxNo(globalService.generateTrxNo(cart.getTransactionType(), 1));
      utTransactions.setCreatedBy(cart.getKyc().getEmail());
      utTransactions.setCreatedDate(new Date());
      try {
        utTransactions.setPriceDate(newPriceDate);
      } catch (Exception e) {
        utTransactions.setPriceDate(new Date());
      }
      utTransactions.setTrxDate(new Date());
      utTransactions.setTransactionDate(new Date());
      utTransactions.setFundPackageRef(product.getFundPackages());
      FundEscrowAccount escrowAccount = fundEscrowAccountService
          .findByFundPackages(product.getFundPackages());
      utTransactions.setSettlementNoRef(escrowAccount);
      utTransactions.setFeeAmount(cart.getFeeAmount() * product.getCompositition());
      utTransactions.setNetAmount(cart.getNetAmount() * product.getCompositition());
      utTransactions.setOrderAmount(cart.getOrderAmount() * product.getCompositition());
      utTransactions.setSettlementAmount(cart.getOrderAmount() * product.getCompositition());
      utTransactions.setTaxAmount(new Double(0));
      utTransactions.setSettlementStatus("ORD");
      utTransactions.setTrxStatus("ORD");
      utTransactions.setAtTrxNo(UUID.randomUUID().toString());
      utTransactions.setTrxType(cart.getTransactionType().getId().intValue());
      utTransactions.setKycId(cart.getKyc());
      utTransactions.setOrderNo(cart.getOrderNo());
      try {
        utTransactions.setTransactionType(cart.getTransactionType());
      } catch (Exception e) {
        utTransactions.setTransactionType(null);
      }
      try {
        utTransactions.setInvestementAccount(investmentAccounts);
      } catch (Exception e) {
        utTransactions.setInvestementAccount(null);
      }
      utTransactions = utTransactionsService.save(utTransactions);
      utTransactionsList.add(utTransactions);
    }
    return utTransactionsList;
  }

  //TODO: TOPUP
  @Override
  public Map topupTransfer(Map map, User user) {
    List<Map> orders = (List<Map>) map.get("order");
    Map resultMap = new HashMap<>();
    List<Map> maps = new ArrayList<>();
    for (Map ord : orders) {
      InvestmentAccounts investmentAccount = investmentAccountsRepository
          .findByInvestmentAccountNo(String.valueOf(ord.get("investment_code")));
      if (investmentAccount != null) {
        FundPackages fundPackage = investmentAccount.getFundPackages();
        if (new Double(String.valueOf(ord.get("topup_amount"))) < fundPackage.getMinTopupAmount()) {
          Map submap = new HashMap<>();
          submap.put("info",
              "Your topup for investmen number :" + ord.get("investment_code")
                  + " less than default minimum topup amount");
          maps.add(submap);
          continue;
        }
        FundEscrowAccount escrowAccount = fundEscrowAccountService.findByFundPackages(fundPackage);
        String orderNo = globalService.generateOrderNoTransfer(1, escrowAccount.getVaCode());

        Kyc kyc = kycRepository.findByAccount(user);
        UtTransactionType transactionType = utTransactionTypeService.findByTrxCode("TOPUP");
        List<FundPackageFeeSetup> feeSetups = fundPackageFeeSetupService
            .findAllByFundPackagesAndTransactionTypeOrderByIdAsc(fundPackage,
                utTransactionTypeService.findByTrxCode("SUBCR"));

        //UPDATE TRANSACTION CART
        UtTransactionsCart cart = utTransactionCartService
            .findByInvestmentAccountAndKycAndTransactionTypeAndTrxStatus(investmentAccount, kyc,
                transactionType, TrxStatusEnum.ACTIVE.getStatus());
        cart.setOrderNo(orderNo);
        cart.setPaymentType(String.valueOf(map.get("payment_method")));
        cart.setNetAmount(new Double(String.valueOf(ord.get("topup_amount"))));
        cart.setTransactionType(transactionType);
        cart.setUpdatedDate(new Date());
        cart.setFeeAmount(globalService
            .getFeeAmountTransCart(feeSetups, new Double(String.valueOf(ord.get("topup_amount")))));
        cart.setOrderAmount(cart.getNetAmount() + cart.getFeeAmount());
        cart = utTransactionCartService.save(cart);

        //INSERT TO UT_TRANSACTIONS
        List<UtTransactions> transactions = this.saveTransactionPayment(cart);
        cart.setTrxStatus(TrxStatusEnum.IN_ACTIVE.getStatus());
        cart = utTransactionCartService.save(cart);
        utTransactionsService.markEntryAsRead(orderNo);

        Map submap = new HashMap<>();
        submap.put("order_number", orderNo);
        submap.put("investment_code", String.valueOf(ord.get("investment_code")));
        submap.put("currency", String.valueOf(ord.get("currency")));
        submap.put("topup_amount", new Double(ord.get("topup_amount").toString()));
        submap.put("fee", cart.getFeeAmount() / cart.getNetAmount());
        submap.put("settlement_price", cart.getOrderAmount());

        SettlementAccounts settlementAccount = settlementAccountsService.findByKycs(kyc);
        submap.put("settlement_bank", settlementAccount.getBankId().getBankName());
        submap.put("settlement_account_name", settlementAccount.getSettlementAccountName());
        submap.put("settlement_account_number", settlementAccount.getSettlementAccountNo());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
        submap.put("settlement_valid_period", sdf.format(new Date()) + " " + sdf2
            .format(cart.getFundPackages().getTransactionCutOff()));

        maps.add(submap);
      }
    }
    resultMap.put("code", 0);
    resultMap.put("info", "successfully topup");
    resultMap.put("topup", maps);
    return resultMap;
  }

  //TODO: REDEMPTION
  @Override
  public Map redeemTransaction(Map map, User user) {
    List<Map> orders = (List<Map>) map.get("order");
    Map resultMap = new HashMap<>();
    List<Map> maps = new ArrayList<>();
    List statusList = new ArrayList<>();

    try {
      for (Map ord : orders) {
        InvestmentAccounts investmentAccount = investmentAccountsRepository
            .findByInvestmentAccountNo(String.valueOf(ord.get("investment_code")));
        Kyc kyc = kycRepository.findByAccount(user);

        //TODO: Validation for day
        SettlementAccounts settlementAccount = settlementAccountsService.findByKycs(kyc);
        List<CustomerBalance> customerBalances = customerBalanceRepository
            .findAllByInvAccountOrderByBalanceDateAsc(investmentAccount);
        CustomerBalance customerBalance = customerBalances.get(0);
        Double fee = this.getFeeCurrentAccount(customerBalance.getBalanceDate(), new Date(),
            investmentAccount.getFundPackages());

        List<FundPackageProducts> fundPackageProducts = fundPackageProductsService
            .findAllByFundPackages(investmentAccount.getFundPackages());
        Double totalMarketValue = this
            .getTotalMarketValue(investmentAccount, fundPackageProducts, kyc);
        Date tempDate = null;
        Double total = new Double(0);
        List<UtTransactions> utTransactions = new ArrayList<UtTransactions>();

        // TODO: Save and Generate orderNo
        Double amount = Double.parseDouble(map.get("net_amount").toString()) + Double
            .parseDouble(map.get("fee_amount").toString());
        String channelName = user.getAgent().getChannel().getName();
        String orderNo = globalService.generateOrderNo(amount, channelName);

        //String orderNo = globalService.generateOrderNo(1);
        UtTransactionsGroup transactionsGroup = new UtTransactionsGroup();
        transactionsGroup.setOrderNo(orderNo);
        utTransactionsGroupRespository.save(transactionsGroup);

        Double redeemAmount = (totalMarketValue - (totalMarketValue * fee));

        for (FundPackageProducts ut : fundPackageProducts) {
          List<CustomerBalance> balances = customerBalanceRepository
              .findTop1ByUtProductAndInvAccountOrderByBalanceDateDesc(ut.getUtProducts(),
                  investmentAccount);
          if (!balances.isEmpty()) {
            if (tempDate == null) {
              tempDate = balances.get(0).getBalanceDate();
            } else if (tempDate != balances.get(0).getBalanceDate() || !(tempDate
                .equals(balances.get(0).getBalanceDate()))) {
              if (tempDate.compareTo(balances.get(0).getBalanceDate()) > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tempDate);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                tempDate = calendar.getTime();
              }
            }
          }

          UtProductFundPrices utproductFundPrice = utProductFundPricesRepository
              .findByUtProductsAndPriceDate(ut.getUtProducts(), tempDate);
          CustomerBalance balance = customerBalanceRepository
              .findCustomerBalanceWithCustomQuery(kyc.getId(), tempDate, investmentAccount.getId(),
                  ut.getUtProducts().getId());

          BigDecimal currAmount = BigDecimal.valueOf(balance.getCurrentAmount());
          total += currAmount.doubleValue();

          UtTransactionType transactionType = utTransactionTypeService.findByTrxCode("REDMP");

          //TODO: SAVE to Ut_transactions
          UtTransactions utTransaction = this
              .saveUtTransactionsRedemption(ut.getUtProducts(), orderNo, kyc,
                  investmentAccount.getFundPackages(), balance, fee, transactionType,
                  new Double(String.valueOf(ord.get("redeem_percentage"))),
                  utproductFundPrice.getBidPrice(), investmentAccount);
          utTransactions.add(utTransaction);
        }

        //TODO: Send to AVANTRADE
        Map sendToAva = this.createTrxRedemptionToAvantrade(orderNo, 0);

        if (sendToAva.get("res").equals(2000033)) {
          Map data = new HashMap<>();
          data.put("order_number", orderNo);
          data.put("package_name", investmentAccount.getFundPackages().getFundPackageName());
          data.put("investment_number", investmentAccount.getInvestmentAccountNo());
          data.put("investment_market_value", totalMarketValue);
          data.put("redemption_fee", fee);
          data.put("settlement_amount", redeemAmount);
          data.put("settlement_bank", settlementAccount.getBankId().getBankName());
          data.put("settlement_account_number", settlementAccount.getSettlementAccountNo());
          data.put("settlement_account_name", settlementAccount.getSettlementAccountName());
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          List<UtTransactions> transaction = utTransactionsService.findAllByOrderNo(orderNo);
          data.put("settlement_date", sdf.format(transaction.get(0).getPriceDate()));
          maps.add(data);
        } else {
          List<UtTransactions> transaction = utTransactionsService.findAllByOrderNo(orderNo);
          for (UtTransactions trx : transaction) {
            trx.setTrxStatus("CAN");
            utTransactionsService.save(trx);
          }
          Map data = new HashMap<>();
          data.put("order_number", orderNo);
          data.put("package_name", investmentAccount.getFundPackages().getFundPackageName());
          data.put("investment_number", investmentAccount.getInvestmentAccountNo());
          data.put("info", "Failed to redeem");
          maps.add(data);
        }
      }
      Map order = new HashMap<>();
      order.put("order", maps);
      resultMap.put("code", 0);
      resultMap.put("info", "Redemption successfully ordered");
      resultMap.put("data", order);

      return resultMap;
    } catch (Exception e) {

      logger.error(e.getMessage(), e);
      return errorResponse(99, "redeem transaction ", null);
    }

  }

  //TODO: GET FEE CURRENT ACCOUNT
  @Override
  public Double getFeeCurrentAccount(Date minBalanceDate, Date currentDate, FundPackages fp) {
    Long devitiation = currentDate.getTime() - minBalanceDate.getTime();
    Double dev = new Double(TimeUnit.DAYS.convert(devitiation, TimeUnit.MILLISECONDS));

    Double fee = new Double(0);
    UtTransactionType transactionType = utTransactionTypeService.findByTrxCode("REDMP");

    List<FundPackageFeeSetup> feeSetups = fundPackageFeeSetupService
        .findAllByFundPackagesAndTransactionTypeOrderByIdAsc(fp, transactionType);
    for (FundPackageFeeSetup feeSetup : feeSetups) {
      if (feeSetup.getAmountMax().equals(new Double("0.0"))) {
        fee = feeSetup.getFeeAmount();
      }

      if (dev >= feeSetup.getAmountMin() && dev <= feeSetup.getAmountMax()) {
        fee = feeSetup.getFeeAmount();
      }
    }
    return fee;
  }

  //TODO: GET TOTAL MARKET VALUE
  @Override
  public Double getTotalMarketValue(InvestmentAccounts invest, List<FundPackageProducts> products,
      Kyc kyc) {
    Double total = new Double(0);
    Date tempDate = null;
    for (FundPackageProducts fpp : products) {
      System.out.println("fpp.getUtProducts() : " + fpp.getUtProducts().getId());
      System.out.println("invest : " + invest.getId());

      List<CustomerBalance> balances = customerBalanceRepository
          .findTop1ByUtProductAndInvAccountOrderByBalanceDateDesc(fpp.getUtProducts(), invest);
      System.out.println("balances : " + balances);

      if (balances != null && !balances.isEmpty()) {
        if (tempDate == null) {
          tempDate = balances.get(0).getBalanceDate();
        } else if (tempDate != balances.get(0).getBalanceDate() || !(tempDate
            .equals(balances.get(0).getBalanceDate()))) {
          if (tempDate.compareTo(balances.get(0).getBalanceDate()) > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tempDate);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            tempDate = calendar.getTime();
          }
        }
      }

      CustomerBalance balance = customerBalanceRepository
          .findCustomerBalanceWithCustomQuery(kyc.getId(), tempDate, invest.getId(),
              fpp.getUtProducts().getId());
      System.out.println("balance : " + balance);
      if (balance != null) {
        total = total + balance.getCurrentAmount();
      }
    }
    return total;
  }

  @Override
  public UtTransactions saveUtTransactionsRedemption(UtProducts utProducts, String orderNo, Kyc kyc,
      FundPackages fundPackages, CustomerBalance customerBalance, Double fee,
      UtTransactionType transactionType,
      Double percentage, Double lastNav, InvestmentAccounts investementAccount) {

    Date newPriceDate = new Date();
    Calendar newPriceDatecal = Calendar.getInstance();
    newPriceDatecal.setTime(newPriceDate);

    Calendar cal = Calendar.getInstance();
    cal.setTime(fundPackages.getTransactionCutOff());

    if (cal.get(Calendar.HOUR_OF_DAY) < (newPriceDatecal.get(Calendar.HOUR_OF_DAY))) {
      newPriceDatecal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
      newPriceDatecal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
      newPriceDatecal.set(Calendar.SECOND, cal.get(Calendar.SECOND));
      newPriceDatecal.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));
    }
    newPriceDate = newPriceDatecal.getTime();
    if (globalService
        .checkOverTransactionCutOffOrNot(fundPackages.getTransactionCutOff(), newPriceDate)) {
      newPriceDate = globalService.getWorkingDate(newPriceDate);
    }

    //TODO: Save to table Ut_transactions
    UtTransactions ut = new UtTransactions();
    ut.setProductId(utProducts);
    ut.setTrxNo(globalService.generateTrxNo(transactionType, 1));
    ut.setOrderNo(orderNo);
    ut.setCreatedBy(kyc.getEmail());
    ut.setCreatedDate(new Date());
    try {
      ut.setPriceDate(newPriceDate);
    } catch (Exception e) {
      ut.setPriceDate(new Date());
    }
    ut.setTrxDate(new Date());
    ut.setTransactionDate(new Date());
    ut.setFundPackageRef(fundPackages);
    FundEscrowAccount escrowAccount = fundEscrowAccountService.findByFundPackages(fundPackages);
    ut.setSettlementNoRef(escrowAccount);
    ut.setOrderAmount(customerBalance.getCurrentAmount() * (percentage / 100));
    ut.setFeeAmount(fee * customerBalance.getCurrentAmount() * (percentage / 100));
    ut.setNetAmount(ut.getOrderAmount() - ut.getFeeAmount());
    ut.setSettlementAmount(ut.getOrderAmount());
    ut.setOrderUnit(
        new BigDecimal(ut.getOrderAmount()).divide(new BigDecimal(lastNav), 2, RoundingMode.HALF_UP)
            .doubleValue());
    ut.setTaxAmount(new Double(0));
    ut.setSettlementStatus("YES");
    ut.setTrxStatus("ORD");
    ut.setNote("REDEMPTION");
    ut.setTrxType(transactionType.getId().intValue());
    ut.setKycId(kyc);
    ut.setTransactionType(transactionType);
    ut.setInvestementAccount(investementAccount);
    ut.setAtTrxNo(UUID.randomUUID().toString());
    ut.setUpdatedDate(new Date());
    ut.setUpdatedBy(kyc.getEmail());

    ut = utTransactionsService.save(ut);
    return ut;
  }

  @Override
  public Map createTrxRedemptionToAvantrade(String orderNo, Integer partialType) {
    List<UtTransactions> utTransactions = utTransactionsService.findAllByOrderNo(orderNo);
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    List<JSONObject> jsonObjects = new ArrayList<>();

    for (UtTransactions ut : utTransactions) {
      Double orderAmount = new Double(0);
      if (ut.getOrderAmount() == 0) {
        orderAmount = new Double(1);
      } else {
        orderAmount = ut.getOrderAmount();
      }

      Double feeRates = ut.getFeeAmount() / orderAmount;
      SettlementAccounts settlementAccount = settlementAccountsService.findByKycs(ut.getKycId());

      JSONObject redemption = new JSONObject();
      redemption.put("productId", ut.getProductId().getAtProductId());
      redemption.put("feeRate", feeRates.toString());
      redemption.put("feeAmount", ut.getFeeAmount().toString());
      redemption.put("transactionDate", sdf.format(ut.getPriceDate()));
      redemption.put("settlementAccountId", settlementAccount.getAtSettlementAccountId());
      redemption.put("customerId", ut.getKycId().getAtCustomerId());
      redemption.put("investmentAccountId", ut.getInvestementAccount().getAtInvestmentAccountId());
      redemption.put("orderNumber", ut.getOrderNo());
      redemption.put("transactionNumber", ut.getTrxNo());
      redemption.put("transactionId", ut.getAtTrxNo());
      redemption.put("partialType", partialType.toString());
      redemption.put("unitValue", ut.getOrderUnit().toString());
      redemption.put("amountValue", ut.getOrderAmount().toString());
      jsonObjects.add(redemption);
    }

    RestTemplate rest = new RestTemplate();
    GlobalParameter globalParameter = globalParameterRepository
        .findByCategory("REDIRECT_URL_TO_AVANTRADE");
    Object response = rest
        .postForObject(globalParameter.getValue() + "/services/transaction/redemption-list-partial",
            jsonObjects, Integer.class);
    Map resultMap = new HashMap<>();
    resultMap.put("res", response);
    return resultMap;
  }

  @Override
  public Object createTrxRedemptionToAvantradeNew(String orderNo, Integer partialType) {
    List<UtTransactions> utTransactions = utTransactionsService.findAllByOrderNo(orderNo);
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    List<RedemptionPartialDto> listData = new ArrayList<>();

    for (UtTransactions ut : utTransactions) {
      RedemptionPartialDto dto = new RedemptionPartialDto();
      Double orderAmount = new Double(0);
      if (ut.getOrderAmount() == 0) {
        orderAmount = new Double(1);
      } else {
        orderAmount = ut.getOrderAmount();
      }

      Double feeRates = ut.getFeeAmount() / orderAmount;
      SettlementAccounts settlementAccount = settlementAccountsService.findByKycs(ut.getKycId());

      dto.setProductId(ut.getProductId().getAtProductId());
      dto.setFeeRate(feeRates.toString());
      dto.setFeeAmount(ut.getFeeAmount().toString());
      dto.setTransactionDate(sdf.format(ut.getPriceDate()));
      dto.setSettlementAccountId(settlementAccount.getAtSettlementAccountId());
      dto.setCustomerId(ut.getKycId().getAtCustomerId());
      dto.setInvestmentAccountId(ut.getInvestementAccount().getAtInvestmentAccountId());
      dto.setOrderNumber(ut.getOrderNo());
      dto.setTransactionNumber(ut.getTrxNo());
      dto.setTransactionId(ut.getAtTrxNo());
      dto.setPartialType(partialType.toString());
      dto.setUnitValue(ut.getOrderUnit().toString());
      dto.setAmountValue(ut.getOrderAmount().toString());
      listData.add(dto);
    }

    RestTemplate rest = new RestTemplate();
    GlobalParameter globalParameter = globalParameterRepository
        .findByCategory("REDIRECT_URL_TO_AVANTRADE");
    String response = rest
        .postForObject(globalParameter.getValue() + "/services/transaction/redemption-list-partial",
            listData, String.class);
    logger.info("response avantrade for order No '" + orderNo + "' : " + response);
    return response;
  }

  @Override
  public ResponseEntity<Map> detail(String invNo, DetailRequest request,
      HttpServletRequest httpServletRequest) {
    Map response = new LinkedHashMap();

    Map checkToken = utilService.checkToken(request.getToken(), getIpAddress(httpServletRequest));
    if (Integer.parseInt(checkToken.get("code").toString()) == 100) {
      response.put("code", 401);
      response.put("info", "UNAUTHORIZED: Your token is invalid.");
      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    User user = (User) checkToken.get("user");
    CustomerBalance customerBalance = customerBalanceRepository
        .findFirstByInvAccount_InvestmentAccountNoOrderByBalanceDateDesc(invNo);
    if (customerBalance != null) {
      if (customerBalance.getCustomer().getAccount() != user) {
        response.put("code", 403);
        response.put("info", "FORBIDDEN: Your account is not allowed.");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
      }
      InvestmentAccounts investmentAccounts = customerBalance.getInvAccount();
      GoalPlanner goalPlanner = investmentAccounts.getGoalPlanner();
      FundPackages fundPackages = investmentAccounts.getFundPackages();
      UtProducts utProducts = customerBalance.getUtProduct();
      InvestmentManagers investmentManagers = utProducts.getInvestmentManagers();

      String sql = "select SUM(subscription_amount) - SUM(redemption_amount) from customer_balance where inv_account_id=:invAccountId";
      Number singleResult = ((Number) entityManager.createNativeQuery(sql).setParameter("invAccountId", investmentAccounts.getId()).getSingleResult());
      BigDecimal investAmount = new BigDecimal(singleResult.toString());

      String goalName = null;
      if (goalPlanner != null) {
        goalName = goalPlanner.getGoalName();
      }

      Map investmentComposition = new LinkedHashMap();
      investmentComposition.put("investment_manager", investmentManagers.getDisplayName());
      investmentComposition.put("fund_name", utProducts.getProductName());
      investmentComposition.put("fund_type",
          lookupLineRepository.findById(Long.parseLong(utProducts.getProductType()))
              .getDescription());
      investmentComposition.put("type_color", investmentManagers.getDisplayName());
      investmentComposition.put("unit", customerBalance.getCurrentUnit());
      investmentComposition.put("market_value", customerBalance.getCurrentAmount());
      investmentComposition.put("nav_value", utProductFundPricesRepository
          .findByUtProductsAndPriceDate(utProducts, customerBalance.getBalanceDate())
          .getBidPrice());
      investmentComposition.put("nav_date", customerBalance.getBalanceDate());

      Map data = new LinkedHashMap();
      data.put("account_no", investmentAccounts.getInvestmentAccountNo());
      data.put("invest_amount", investAmount);
      data.put("goal", goalName);
      data.put("package_code", fundPackages.getPackageCode());
      data.put("package_name", fundPackages.getFundPackageName());
      data.put("package_image", fundPackages.getPackageImage());
      data.put("investmentComposition", investmentComposition);

      response.put("code", 200);
      response.put("info", "OK: Resource successfully retrieved.");
      response.put("data", data);
    } else {
      response.put("code", 404);
      response.put("info", "NOT_FOUND: Resource " + invNo + " not found.");
    }
    return ResponseEntity.ok(response);
  }

  @Override
  public Map investmentList(Integer offset, Integer limit, Map map, Kyc kyc) throws ParseException {
    List<InvestmentAccounts> investmentAccounts = new ArrayList<>();
    if (map.get("package_code") != null) {
      investmentAccounts = customerBalanceRepository
          .findAllByCustomerAndCurrentAmountAndPackageCodeWithCustomQuery(kyc.getId(),
              map.get("package_code").toString());
    } else {
      investmentAccounts = customerBalanceRepository
          .findAllByCustomerAndCurrentAmountWithCustomQuery(kyc.getId());
    }
    List<Map> resultsList = new ArrayList<>();

    for (InvestmentAccounts i : investmentAccounts) {
      if (resultsList.size() >= limit) {
        continue;
      } else {
        List<UtProducts> products = fundPackageProductsService
            .findUtProductsByFundPackages(i.getFundPackages());
        Date tempDate = null;

        Map lastDatePerInvestment = new HashMap();
        for (UtProducts ut : products) {
          List<CustomerBalance> balances = customerBalanceRepository
              .findTop1ByUtProductAndInvAccountOrderByBalanceDateDesc(ut, i);
          if (balances.isEmpty()) {
            continue;
          }

          if (tempDate == null) {
            tempDate = balances.get(0).getBalanceDate();
          } else if (tempDate != balances.get(0).getBalanceDate() || !(tempDate
              .equals(balances.get(0).getBalanceDate()))) {
            if (tempDate.compareTo(balances.get(0).getBalanceDate()) > 0) {
              Calendar calendar = Calendar.getInstance();
              calendar.setTime(tempDate);
              calendar.add(Calendar.DAY_OF_MONTH, -1);
              tempDate = calendar.getTime();
            }
          }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        lastDatePerInvestment.put(i.getId(), sdf.format(tempDate));

        String sql = "SELECT i.investment_account_id, "
            + "i.investment_account_no, "
            + "CASE WHEN (sum(c.subscription_amount)-sum(c.redemption_amount))<0 THEN 0 ELSE sum(c.subscription_amount)-sum(c.redemption_amount) END as inv_amount,  "
            + "fp.fund_package_id, "
            + "fp.fund_package_name, "
            + "fp.package_image, "
            + "subcus.curr_amount as total_invest_market_value,"
            + "g.goal_name, "
            + "subcus.curr_unit, "
            + "fp.package_code "
            + "FROM customer_balance c "
            + "INNER JOIN investment_accounts i ON c.inv_account_id=i.investment_account_id "
            + "INNER JOIN fund_packages fp ON fp.fund_package_id=i.fund_packages_id "
            + "LEFT JOIN goal_planner g ON g.goal_planner_id=i.goal_planner_id "
            + "LEFT JOIN (SELECT sum(current_amount) as curr_amount,inv_account_id,SUM(current_unit) as curr_unit FROM customer_balance GROUP BY inv_account_id,balance_date HAVING balance_date=:balanceDate AND inv_account_id=:id) subcus on subcus.inv_account_id = i.investment_account_id "
            + "WHERE i.investment_account_id=:id AND c.balance_date<=:balanceDate and subcus.curr_unit > 0 "
            + "GROUP BY i.investment_account_id,i.investment_account_no,fp.fund_package_id, fp.fund_package_id, fp.package_image,g.goal_name,subcus.curr_unit,subcus.curr_amount,fp.package_code ";

        Query query = entityManager.createNativeQuery(sql).setParameter("id", i.getId())
            .setParameter("balanceDate", tempDate);
        List<Object[]> objects = query.getResultList();
        if (!objects.isEmpty()) {
          for (Object[] obj : objects) {
            if (Double.valueOf(obj[8].toString()) > 0) {
              String subDetailQuery = "select c.balance_date,"
                  + "c.ut_product_id,"
                  + "c.current_unit,"
                  + "p.product_name,"
                  + "lo.description,"
                  + "up.bid_price as last_nav,"
                  + "c.current_amount as market_value, "
                  + "im.display_name "
                  + "from customer_balance c "
                  + "inner join ut_products p on p.product_id=c.ut_product_id "
                  + "inner join lookup_line lo on lo.lookup_id = cast(p.product_type as Integer) "
                  + "inner join ut_product_fund_prices up on up.products_id=p.product_id and up.price_date = c.balance_date  "
                  + "inner join investment_managers im on im.inv_manager_id=p.investment_managers_id "
                  + "where c.inv_account_id= :id and c.balance_date between :startDate and :endDate";

              Date startDate = new SimpleDateFormat("yyyy-MM-dd")
                  .parse(new SimpleDateFormat("yyyy-MM-dd").format(tempDate));
              Calendar calendar = Calendar.getInstance();
              calendar.setTime(startDate);
              calendar.add(Calendar.DAY_OF_MONTH, 1);
              Date endDate = calendar.getTime();
              Query subQuery = entityManager.createNativeQuery(subDetailQuery)
                  .setParameter("id", Long.parseLong(obj[0].toString()))
                  .setParameter("startDate", startDate)
                  .setParameter("endDate", endDate);
              List<Object[]> objectsSubQuery = subQuery.getResultList();
              List<Map> details = new ArrayList<>();
              for (Object[] objSub : objectsSubQuery) {
                Map mapObjSub = new HashMap<>();
                mapObjSub.put("investment_manager", objSub[7]);
                mapObjSub.put("fund_name", objSub[3]);
                mapObjSub.put("fund_type", objSub[4]);
                mapObjSub.put("type_color", objSub[7]);
                mapObjSub.put("unit", objSub[2]);
                mapObjSub.put("market_value", objSub[6]);
                mapObjSub.put("nav_value", objSub[5]);
                mapObjSub.put("nav_date", objSub[0]);
                details.add(mapObjSub);
              }
              Map investmentMap = new HashMap<>();
              investmentMap.put("investmentComposition", details);
              investmentMap.put("account_no", String.valueOf(obj[1]));
              investmentMap.put("invest_amount", new BigDecimal(String.valueOf(obj[2])));
              investmentMap.put("goal", String.valueOf(obj[7]));
              investmentMap.put("package_code", String.valueOf(obj[9]));
              investmentMap.put("package_image", String.valueOf(obj[5]));
              investmentMap.put("package_name", String.valueOf(obj[4]));
              resultsList.add(investmentMap);
            }
          }

        }
      }

    }
    Map investment = new HashMap<>();
    investment.put("investment", resultsList);
    Map result = new HashMap<>();
    result.put("code", 0);
    if (resultsList.isEmpty()) {
      result.put("info", "investment list successfully loaded, but data is empty");
      result.put("data", null);
    } else {
      result.put("info", "investment list successfully loaded");
      result.put("data", investment);
    }

    return result;
  }
  
  @Override
  public Map<String, Object> getAumByCustomer(Long customerId){
	  Map<String, Object> map = new LinkedHashMap <>();
	  String sql = "SELECT " + 
		  		"	kyc.first_name, " + 
		  		"	kyc.middle_name, " + 
		  		"	kyc.last_name, " + 
		  		"	_user.created_date, " + 
		  		"	_user.email, " + 
		  		"	cb.total_aum, " + 
		  		"	kyc.portalcif " + 
		  		"FROM " + 
		  		"	_user " + 
		  		"	INNER JOIN kyc ON kyc.account_id = _user.id " + 
		  		"	JOIN (SELECT " + 
		  		"	SUM(cb.current_amount) as total_aum, " + 
		  		"	cb.customer_id " + 
		  		"FROM customer_balance cb " + 
		  		"JOIN (SELECT MAX(price_date) as price_date, products_id FROM ut_product_fund_prices GROUP BY products_id) fp ON (fp.price_date=cb.balance_date AND fp.products_id=cb.ut_product_id) " + 
		  		"WHERE cb.customer_id= :customerId " + 
		  		"GROUP BY cb.customer_id) cb ON (cb.customer_id=kyc.customer_id) " + 
		  		"WHERE " + 
		  		"	kyc.customer_id = :customerId ";
	  Query query = entityManager.createNativeQuery(sql).setParameter("customerId", customerId).setMaxResults(1);
	  Object[] obj =  (Object[]) query.getResultList().stream().findFirst().orElse(null);
	  if(Validator.isNotNullOrEmpty(obj)) {
		  map.put("totalAum", new BigDecimal(obj[5].toString()).setScale(2, RoundingMode.DOWN));
	  }else {
		  map.put("totalAum", 0d);
	  }
	  return map;
  }

  @Override
  public List<AgentDto> getAumByAgent(List<Agent> agentList) throws Exception {
	  List<AgentDto> agentDtoList = new ArrayList<>();
	  String sql = 
			  "SELECT SUM(AUM.total_aum) AS total_aum, agent.id as agent_id FROM ( " + 
					  "	SELECT  kyc.first_name,kyc.middle_name,kyc.last_name, _user.created_date, _user.email, _user.agent_id, " +
					  "	SUM(cb.current_amount) as total_aum, kyc.portalcif " + 
					  "	FROM _user " + 
					  "	INNER JOIN kyc on kyc.account_id = _user.id " + 
					  "	INNER JOIN investment_accounts inv on inv.kycs_id = kyc.customer_id " + 
					  "	INNER JOIN ( " + 
					  "		SELECT MAX(balance_date) as max_bdate, inv_account_id " + 
					  "		FROM customer_balance group by inv_account_id) tbldate on tbldate.inv_account_id = inv.investment_account_id " + 
					  "	INNER JOIN customer_balance cb on cb.inv_account_id = inv.investment_account_id and cb.balance_date in (tbldate.max_bdate) " + 
					  "	GROUP BY kyc.first_name,kyc.middle_name,kyc.last_name, _user.created_date, _user.email, _user.agent_id,kyc.portalcif " + 
					  "	ORDER BY  _user.email) AS AUM " + 
					  "INNER JOIN agent ON AUM.agent_id = agent.id " +
					  "WHERE  agent.id = :agentId " + 
					  "GROUP BY agent.id ";
	  logger.error(sql);
	  for (Agent agent : agentList) {
		  AgentDto agentDto = new AgentDto();
		  BeanUtils.copyProperties(agentDto, agent);
		  Query query = entityManager.createNativeQuery(sql).setParameter("agentId", agent.getId()).setMaxResults(1);
		  Object[] obj =  (Object[]) query.getResultList().stream().findFirst().orElse(null);
		  if(Validator.isNotNullOrEmpty(obj)) {
			  agentDto.setTotalAumAgent(new BigDecimal(Double.valueOf(obj[0].toString())));
		  }
		  agent.setToken(null);
		  agentDtoList.add(agentDto);
	  }
	  return agentDtoList;
  }

  public ResponseEntity<Map> detailNewVersion(String invNo, DetailRequest request, HttpServletRequest httpServletRequest) {
    Map response = new LinkedHashMap();
    Map checkToken = utilService.checkToken(request.getToken(), getIpAddress(httpServletRequest));
    if (Integer.parseInt(checkToken.get("code").toString()) == 100) {
      response.put("code", 401);
      response.put("info", "UNAUTHORIZED: Your token is invalid.");
      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    User user = (User) checkToken.get("user");
    Kyc kyc = kycRepository.findByAccount(user);
    InvestmentAccounts investmentAccounts = investmentAccountsRepository.findByInvestmentAccountNoAndKycs(invNo, kyc);
    if(investmentAccounts == null){
      return new ResponseEntity<>(errorResponse(50, "Investment account not found", null), HttpStatus.NOT_FOUND);
    }

    List<CustomerBalance> listCb = customerBalanceRepository.getLatestBalance(investmentAccounts.getId());
    if(listCb.size() == 0){
      return new ResponseEntity<>(errorResponse(14, "Investasi sudah dijual", null), HttpStatus.BAD_REQUEST);
    }

    GoalPlanner goalPlanner = investmentAccounts.getGoalPlanner();
    String goalName = null;
    if (goalPlanner != null) {
      goalName = goalPlanner.getGoalName();
    }

    List<Map> listInvestmentComposition = new ArrayList();
    BigDecimal investAmount = new BigDecimal(0);

    for(CustomerBalance cb: listCb){
      String sql = "SELECT SUM(subscription_amount) - SUM(redemption_amount) FROM customer_balance WHERE inv_account_id=:invAccountId AND ut_product_id=:productId";
      Number singleResult = ((Number) entityManager.createNativeQuery(sql)
              .setParameter("invAccountId", cb.getInvAccount().getId())
              .setParameter("productId", cb.getUtProduct()).getSingleResult());
      investAmount = investAmount.add(new BigDecimal(singleResult.toString()));

      Map investmentComposition = new LinkedHashMap();
      investmentComposition.put("product_id", cb.getUtProduct().getId());
      investmentComposition.put("product_name", cb.getUtProduct().getProductName());
      investmentComposition.put("unit", cb.getCurrentUnit());
      investmentComposition.put("market_value", cb.getCurrentAmount());
      investmentComposition.put("nav_value", utProductFundPricesRepository.findByUtProductsAndPriceDate(cb.getUtProduct(), cb.getBalanceDate()).getBidPrice());
      investmentComposition.put("nav_date", cb.getBalanceDate());
      investmentComposition.put("investment_manager", cb.getUtProduct().getInvestmentManagers().getDisplayName());
      investmentComposition.put("fund_name", cb.getUtProduct().getProductName());
      investmentComposition.put("fund_type", lookupLineRepository.findById(Long.parseLong(cb.getUtProduct().getProductType())).getDescription());
      investmentComposition.put("type_color", cb.getUtProduct().getInvestmentManagers().getDisplayName());
      listInvestmentComposition.add(investmentComposition);
    }

    Map data = new LinkedHashMap();
    data.put("account_no", investmentAccounts.getInvestmentAccountNo());
    data.put("invest_amount", investAmount);
    data.put("goal", goalName);
    data.put("package_code", investmentAccounts.getFundPackages().getPackageCode());
    data.put("package_name", investmentAccounts.getFundPackages().getFundPackageName());
    data.put("package_image", investmentAccounts.getFundPackages().getPackageImage());
    data.put("investmentComposition", listInvestmentComposition);
    data.put("enable_full_redeem", utTransactionsRepository.getPendingTrxTopUp(investmentAccounts.getId()).size() > 0 ? false : true);

    response.put("code", 200);
    response.put("info", "OK: Resource successfully retrieved.");
    response.put("data", data);
    return ResponseEntity.ok(response);
  }
  
  public Map listBalance(String invNo, Date balanceDate, User user) {
    Map response = new LinkedHashMap();
    Kyc kyc = kycRepository.findByAccount(user);
    InvestmentAccounts investmentAccounts = investmentAccountsRepository.findByInvestmentAccountNoAndKycs(invNo, kyc);
    if(investmentAccounts == null){
      return errorResponse(404, "Investment account not found", null);
    }

    GoalPlanner goalPlanner = investmentAccounts.getGoalPlanner();
    FundPackages fundPackages = investmentAccounts.getFundPackages();

    String goalName = null;
    if (goalPlanner != null) {
      goalName = goalPlanner.getGoalName();
    }

    String sql = "select SUM(subscription_amount) - SUM(redemption_amount) from customer_balance where inv_account_id=:invAccountId";
    Number singleResult = ((Number) entityManager.createNativeQuery(sql).setParameter("invAccountId", investmentAccounts.getId()).getSingleResult());
    BigDecimal investAmount = new BigDecimal(singleResult.toString());

    List<CustomerBalance> listCb = customerBalanceRepository.findAllByInvAccount_InvestmentAccountNoAndCustomerAndBalanceDate(investmentAccounts.getId(), kyc.getId(), DateTimeUtil.getCustomDate(balanceDate, -1));
    if (listCb.size() > 0) {
      List listComp = new ArrayList();
      for(CustomerBalance customerBalance: listCb){
        UtProducts utProducts = customerBalance.getUtProduct();
        InvestmentManagers investmentManagers = utProducts.getInvestmentManagers();

        Map investmentComposition = new LinkedHashMap();
        UtProductFundPrices utfp = utProductFundPricesRepository.findByUtProductsAndPriceDateWithCustomQuery(utProducts.getId(), customerBalance.getBalanceDate());
        investmentComposition.put("product_id", customerBalance.getUtProduct().getId());
        investmentComposition.put("product_name", customerBalance.getUtProduct().getProductName());
        investmentComposition.put("investment_manager", investmentManagers.getDisplayName());
        investmentComposition.put("fund_name", utProducts.getProductName());
        investmentComposition.put("fund_type", lookupLineRepository.findById(Long.parseLong(utProducts.getProductType())).getDescription());
        investmentComposition.put("type_color", investmentManagers.getDisplayName());
        investmentComposition.put("unit", customerBalance.getBroughtForwardUnit());
        investmentComposition.put("market_value", customerBalance.getBroughtForwardUnit() * utfp.getBidPrice());
        investmentComposition.put("nav_value", utfp.getBidPrice());
        investmentComposition.put("balance_date", new SimpleDateFormat("yyyy-MM-dd").format(utfp.getPriceDate()));
        listComp.add(investmentComposition);
      }

      Map data = new LinkedHashMap();
      data.put("account_no", investmentAccounts.getInvestmentAccountNo());
      data.put("invest_amount", investAmount);
      data.put("goal", goalName);
      data.put("package_code", fundPackages.getPackageCode());
      data.put("package_name", fundPackages.getFundPackageName());
      data.put("package_image", fundPackages.getPackageImage());
      data.put("investmentComposition", listComp);

      response.put("code", 200);
      response.put("info", "OK: Resource successfully retrieved.");
      response.put("data", data);
    } else {
      response.put("code", 404);
      response.put("info", "NOT_FOUND: Resource " + invNo + " not found.");
      return response;
    }

    return response;
  }
  
  public ResponseEntity<Map> performance(String invNo, DetailRequest request, HttpServletRequest httpServletRequest) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    Map response = new LinkedHashMap();
    Map checkToken = utilService.checkToken(request.getToken(), getIpAddress(httpServletRequest));
    if (Integer.parseInt(checkToken.get("code").toString()) == 100) {
      response.put("code", 401);
      response.put("info", "UNAUTHORIZED: Your token is invalid.");
      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    User user = (User) checkToken.get("user");
    Kyc kyc = kycRepository.findByAccount(user);
    InvestmentAccounts ia = investmentAccountsRepository.findByInvestmentAccountNoAndKycs(invNo, kyc);
    if(ia == null){
      return new ResponseEntity<>(errorResponse(50, "Investment not found", null), HttpStatus.NOT_FOUND);
    }
    Date endDate = null;
    Date startDate = null;
    List<FundPackageProducts> listFpp = fundPackageProductsRepository.findAllByFundPackages(ia.getFundPackages());
    for(FundPackageProducts fpp: listFpp){
      List<CustomerBalance> listCb = customerBalanceRepository.findAllByInvAccountAndUtProductOrderByBalanceDateDesc(ia, fpp.getUtProducts());
      if(listCb.size() == 0){
        continue;
      }

      if(endDate == null) {
        endDate = listCb.get(0).getBalanceDate();
      }else if(endDate.compareTo(listCb.get(0).getBalanceDate()) != 0) {
        if (endDate.compareTo(listCb.get(0).getBalanceDate()) > 0) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(endDate);
          cal.add(Calendar.DAY_OF_MONTH, -1);
          endDate = cal.getTime();
        }
      }

      startDate = listCb.get(listCb.size() - 1).getBalanceDate();
    }

    Integer countTotal = customerBalanceRepository.countDistinctBalanceDateByInvAccount(ia.getId());
    List<Object[]> listData = customerBalanceRepository.getDataPerformanceInvestment(startDate, endDate, ia.getId());
    BigDecimal startingAmount = new BigDecimal(0);
    List listResult = new ArrayList();
    if(countTotal < 40){
      for(Object[] data: listData){
        Long hasil = Math.round((Double) data[1]) - Math.round((Double) data[3]);
        startingAmount = startingAmount.add(BigDecimal.valueOf(hasil));

        if(startingAmount.compareTo(BigDecimal.ZERO) == 0){
          break;
        }

        Map mapData = new LinkedHashMap();
        mapData.put("date", sdf.format(data[2]));
        mapData.put("value", calculatePerformance(BigDecimal.valueOf((Double) data[0]), startingAmount));
        listResult.add(mapData);
      }
    }else{
      Integer rangeDay = new BigDecimal(countTotal / 20).setScale(0, BigDecimal.ROUND_DOWN).intValue();
      for (int i = 0; i < countTotal; i = i + rangeDay) {
        if (i < listData.size()) {
          Object[] iab = listData.get(i);
          startingAmount = new BigDecimal(0);
          for (int y = 0; y <= i; y++) {
            Object[] data = listData.get(y);
            if ((Double) data[1] > 0 || (Double) data[3] > 0) {
              Long hasil = Math.round((Double) data[1]) - Math.round((Double) data[3]);
              startingAmount = startingAmount.add(BigDecimal.valueOf(hasil));
            }
          }

          if(startingAmount.compareTo(BigDecimal.ZERO) == 0){
            break;
          }

          Map mapData = new LinkedHashMap();
          mapData.put("date", sdf.format(iab[2]));
          mapData.put("value", calculatePerformance(BigDecimal.valueOf((Double) iab[0]), startingAmount));
          listResult.add(mapData);
        }
      }

      if (!sdf.format(listData.get(listData.size() - 1)[2]).equals(((Map) listResult.get(listResult.size() - 1)).get("date"))) {
        startingAmount = new BigDecimal(0);
        int i = listData.size() - 1;
        Object[] iab = listData.get(i);
        for (int y = 0; y <= i; y++) {
          Object[] data = listData.get(y);
          if ((Double) data[1] > 0 || (Double) data[3] > 0) {
            Long hasil = Math.round((Double) data[1]) - Math.round((Double) data[3]);
            startingAmount = startingAmount.add(BigDecimal.valueOf(hasil));
          }
        }

        if(startingAmount.compareTo(BigDecimal.ZERO) > 0){
          Map mapData = new LinkedHashMap();
          mapData.put("date", sdf.format(iab[2]));
          mapData.put("value", calculatePerformance(BigDecimal.valueOf((Double) iab[0]), startingAmount));
          listResult.add(mapData);
        }
      }

      if ((countTotal % rangeDay) == 0) {
        startingAmount = new BigDecimal(0);
        int i = listData.size() - 1;
        Object[] iab = listData.get(i);
        for (int y = 0; y <= i; y++) {
          Object[] data = listData.get(y);
          if ((Double) data[1] > 0 || (Double) data[3] > 0) {
            Long hasil = Math.round((Double) data[1]) - Math.round((Double) data[3]);
            startingAmount = startingAmount.add(BigDecimal.valueOf(hasil));
          }
        }

        if(startingAmount.compareTo(BigDecimal.ZERO) > 0){
          Map mapData = new LinkedHashMap();
          mapData.put("date", sdf.format(iab[2]));
          mapData.put("value", calculatePerformance(BigDecimal.valueOf((Double) iab[0]), startingAmount));
          listResult.add(mapData);
        }
      }
    }

    response.put("code", ConstantUtil.STATUS_SUCCESS);
    response.put("info", ConstantUtil.SUCCESS);
    response.put("data", listResult);
    return ResponseEntity.ok(response);
  }

  BigDecimal calculatePerformance(BigDecimal currentAmount, BigDecimal startingAmount) {
    BigDecimal result = currentAmount.subtract(startingAmount);
    result = result.divide(startingAmount, 4, BigDecimal.ROUND_HALF_UP);
    return result;
  }

}