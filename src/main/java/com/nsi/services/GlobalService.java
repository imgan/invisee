package com.nsi.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.nsi.domain.core.FundPackageFeeSetup;
import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import com.nsi.domain.core.UtTransactionType;

public interface GlobalService {

	public Boolean checkpinValid(Map map, User user);
	public Map checkToken(String token, HttpServletRequest request);
	public String generateOrderNoTransfer(int counting, String vaCode);
	public Double getFeeAmountTransCart(List<FundPackageFeeSetup> feeSetups, Double orderAmount);
	public InvestmentAccounts saveInvestmentAccount(FundPackages fundPackages, Kyc kyc);
	public String generateInvestmentNo(int counting);
	public Boolean checkOverTransactionCutOffOrNot(Date transactionCutOff, Date currentTime);
	public Boolean isHoliday(Date currentDate);
	public Date getWorkingDate(Date updatedDate);
	public Date getPrevWorkingDay(Date date);
	public Date getNextWorkingDate(Date updatedDate);
	public String generateTrxNo(UtTransactionType type, int counting);
	public String generateOrderNo(Double amount, String channelName) throws InterruptedException;
}
