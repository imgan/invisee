package com.nsi.services;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.CustomerBalance;
import com.nsi.domain.core.FundPackageProducts;
import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import com.nsi.domain.core.UtProducts;
import com.nsi.domain.core.UtTransactionType;
import com.nsi.domain.core.UtTransactions;
import com.nsi.domain.core.UtTransactionsCart;
import com.nsi.dto.AgentDto;
import com.nsi.dto.request.investment.DetailRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface InvestmentService {

	Map subscribeTransfer(Map map, User user);

	Map topupTransfer(Map map, User user);

	Map redeemTransaction(Map map, User user);

	Object createTrxRedemptionToAvantrade(String string, Integer integer);

	List<UtTransactions> saveTransactionPayment(UtTransactionsCart cart);

	Double getFeeCurrentAccount(Date minBalanceDate, Date currentDate, FundPackages fp);

	UtTransactions saveUtTransactionsRedemption(
			UtProducts utProducts, String orderNo, Kyc kyc, FundPackages fundPackages,
			CustomerBalance customerBalance, Double fee, UtTransactionType transactionType,
			Double percentage, Double lastNav, InvestmentAccounts investementAccount);

	Map investmentList(Integer offset, Integer limit, Map map, Kyc kyc) throws ParseException;

	Double getTotalMarketValue(InvestmentAccounts invest, List<FundPackageProducts> products,
			Kyc kyc);

	Object createTrxRedemptionToAvantradeNew(String orderNo, Integer partialType);

	ResponseEntity<Map> detail(String invNo, DetailRequest request,
			HttpServletRequest httpServletRequest);

	List<AgentDto> getAumByAgent(List<Agent> agentList) throws Exception;

	Map<String, Object> getAumByCustomer(Long customerId);
	ResponseEntity<Map> performance(String invNo, DetailRequest request, HttpServletRequest httpServletRequest);

	ResponseEntity<Map> detailNewVersion(String invNo, DetailRequest request, HttpServletRequest httpServletRequest);
	Map listBalance(String invNo, Date balanceDate, User user);
}
