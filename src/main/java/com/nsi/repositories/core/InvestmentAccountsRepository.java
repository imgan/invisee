package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;
import org.springframework.data.jpa.repository.Query;

public interface InvestmentAccountsRepository extends JpaRepository<InvestmentAccounts, Long> {

	public int countByInvestmentAccountNoLike(String pref);
	public InvestmentAccounts findByInvestmentAccountNo(String invAccountNo);
	public InvestmentAccounts findByInvestmentAccountNoAndKycs(String invNo, Kyc kyc);

	@Query(value = "SELECT NEXTVAl(?1)", nativeQuery = true)
	public Long getNextSeriesId(String seqInvestAcctNo);
}
