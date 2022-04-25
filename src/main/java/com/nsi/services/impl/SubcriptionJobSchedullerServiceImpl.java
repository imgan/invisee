package com.nsi.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nsi.domain.avantrade.MstInvestmentAccounts;
import com.nsi.repositories.avantrade.MstInvestmentAccountsRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nsi.domain.core.GlobalParameter;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.LookupLine;
import com.nsi.domain.core.SettlementAccounts;
import com.nsi.domain.core.SubcriptionJobScheduller;
import com.nsi.domain.core.UtTransactionType;
import com.nsi.domain.core.UtTransactions;
import com.nsi.dto.InvestmentAccountDto;
import com.nsi.dto.SubscriptionDto;
import com.nsi.enumeration.AvantradeIntegrationReturnCodeEnumeration;
import com.nsi.repositories.core.GlobalParameterRepository;
import com.nsi.repositories.core.LookupLineRepository;
import com.nsi.repositories.core.SettlementAccountsRepository;
import com.nsi.repositories.core.SubcriptionJobSchedullerRepository;
import com.nsi.repositories.core.UtTransactionTypeRepository;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.services.SubcriptionJobSchedullerService;

@Service
public class SubcriptionJobSchedullerServiceImpl implements SubcriptionJobSchedullerService{
	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	SubcriptionJobSchedullerRepository subscriptionJobSchedullerRepository;
	@Autowired
	UtTransactionTypeRepository utTransactionTypeRepository;
	@Autowired
	UtTransactionsRepository utTransactionsRepository;
	@Autowired
	GlobalParameterRepository globalParameterRepository;
	@Autowired
	LookupLineRepository lookupLineRepository;
	@Autowired
	SettlementAccountsRepository settlementAccountsRepository;
	@Autowired
	MstInvestmentAccountsRepository mstInvestmentAccountsRepository;

	@Override
	public Map saveJob(String orderNo, String status, String dateCreated, String executeDate,
			String message, String payType) {
		Map resultMap = new HashMap<>();
		SubcriptionJobScheduller job = subscriptionJobSchedullerRepository.findByOrderNo(orderNo);
		if(job != null){
			resultMap.put("code", 13);
			resultMap.put("info", "Data existing");
			resultMap.put("data", job);
			return resultMap;
		}

		job = new SubcriptionJobScheduller();
		job.setOrderNo(orderNo);
		job.setStatus(status);
		try{ job.setDateCreated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,S").parse(dateCreated));   }catch(Exception e){ job.setDateCreated(new Date()); }
		job.setMessage(message);
		job.setPayType(payType);
		job = subscriptionJobSchedullerRepository.save(job);
		resultMap.put("code", 0);
		resultMap.put("info", "Insert data successfully");
		resultMap.put("data", job);
		return resultMap;
	}

	@Override
	public Object createSubscriptionIntegrationAvantrade(String orderNo) {
		List<SubscriptionDto> listData = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

		String invAcc = this.createInvestmentAccount(orderNo);
		if(invAcc!=null){
			if(AvantradeIntegrationReturnCodeEnumeration.DATA_SUCCESSFULLY_UPDATE.getCode().equals(Integer.parseInt(invAcc))){
				List<UtTransactions> transactions = utTransactionsRepository.findAllByOrderNo(orderNo);
				for(UtTransactions trx : transactions){
					LookupLine income = lookupLineRepository.findByCode("TRF");
					SubscriptionDto dto = new SubscriptionDto();
					dto.setCustomerId(trx.getKycId().getAtCustomerId());
					dto.setTransactionDate(sdf.format(trx.getPriceDate()));
					dto.setInvestmentAccountId(trx.getInvestementAccount().getAtInvestmentAccountId());
					dto.setProductId(trx.getProductId().getAtProductId());
					dto.setTransactionAmount(trx.getNetAmount().toString());
					dto.setOrderNumber(trx.getOrderNo());
					dto.setFundSourceId(income.getAtLookupId());

					//Double feeRates =  (trx.getFeeAmount()/trx.getOrderAmount());
					SettlementAccounts settlementAccount = settlementAccountsRepository.findByKycs(trx.getKycId());

					/*dto.setFeeRate(feeRates.toString());
					dto.setFeeAmount(trx.getFeeAmount().toString());*/
					dto.setFeeRate("0");
					dto.setFeeAmount("0");
					dto.setSettlementAccountId(settlementAccount.getAtSettlementAccountId());
					dto.setTransactionId(trx.getAtTrxNo());
					dto.setTransactionNumber(trx.getTrxNo());
					listData.add(dto);

				}

				RestTemplate rest = new RestTemplate();
				GlobalParameter redirectUrl = globalParameterRepository.findByCategory("REDIRECT_URL_TO_AVANTRADE");
				String response = rest.postForObject(redirectUrl.getValue()+"/services/transaction/subscription-list", listData, String.class);
				System.out.println("Response from Subscription "+orderNo+" :"+response);
				return response;
			}else if(AvantradeIntegrationReturnCodeEnumeration.DATA_ALREADY_EXISTS.getCode().equals(Integer.parseInt(invAcc))){
				System.out.println("Investment with orderNo "+orderNo+" already exist :"+invAcc);
				return invAcc;
			}
		}
		return null;
	}

	@Override
	public String createInvestmentAccount(String orderNo) {
		List<InvestmentAccountDto> invObject = new ArrayList<>();
		UtTransactionType transactionType = utTransactionTypeRepository.findByTrxCode("SUBCR");
		UtTransactionType topupTransactionType = utTransactionTypeRepository.findByTrxCode("TOPUP");
		List<InvestmentAccounts> investmentAccounts = utTransactionsRepository.getInvestAccountByOrderNoAndTrxTypeWithQuery(orderNo, transactionType,topupTransactionType);
		for(InvestmentAccounts accounts : investmentAccounts){
			InvestmentAccountDto dto = new InvestmentAccountDto();
			dto.setCustomerId(accounts.getKycs().getAtCustomerId());
			dto.setInvestmentAccountId(accounts.getAtInvestmentAccountId());
			dto.setInvestmentAccountName(accounts.getInvestmentAccountName());
			dto.setInvestmentAccountNo(accounts.getInvestmentAccountNo());
			invObject.add(dto);
		}


		RestTemplate restTemplate = new RestTemplate();
		GlobalParameter redirectUrl = globalParameterRepository.findByCategory("REDIRECT_URL_TO_AVANTRADE");
		String response = restTemplate.postForObject(redirectUrl.getValue()+"/services/account/investment-list", invObject, String.class);
		log.info("Response Create Investment Account in Avantrade : "+response);
		if(AvantradeIntegrationReturnCodeEnumeration.DATA_SUCCESSFULLY_UPDATE.getCode().equals(Integer.parseInt(response))){
			for(InvestmentAccounts ia : investmentAccounts){
				MstInvestmentAccounts mstInvestmentAccounts = mstInvestmentAccountsRepository.findByInvAccountId(ia.getAtInvestmentAccountId().toUpperCase().replaceAll("-", ""));
				mstInvestmentAccounts.setInvAccountIdPortal(ia.getId());
				mstInvestmentAccountsRepository.saveAndFlush(mstInvestmentAccounts);
			}

		}
		return response;
	}


}
