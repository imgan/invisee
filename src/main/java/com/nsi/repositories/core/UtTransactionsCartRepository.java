package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.UtTransactionType;
import com.nsi.domain.core.UtTransactionsCart;

public interface UtTransactionsCartRepository extends JpaRepository<UtTransactionsCart, Long>{

	public int countByOrderNoLike(String oldOrderNo);
	public UtTransactionsCart findByFundPackagesAndKycAndTransactionTypeAndTrxStatus(FundPackages fundPackages,Kyc kyc, UtTransactionType transactionType, String trxStatus);
	public UtTransactionsCart findByInvestmentAccountAndKycAndTransactionTypeAndTrxStatus(InvestmentAccounts investmentAccount, Kyc kyc, UtTransactionType transactionType, String trxStatus);
	public List<UtTransactionsCart> findAllByOrderNoAndInvestmentAccount(String orderNo, InvestmentAccounts investmentAccounts);
        public List<UtTransactionsCart> findAllByOrderNo(String orderNo);
}
