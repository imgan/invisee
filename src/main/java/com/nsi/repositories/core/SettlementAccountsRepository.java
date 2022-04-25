package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.SettlementAccounts;

public interface SettlementAccountsRepository extends JpaRepository<SettlementAccounts, Long> {

	public SettlementAccounts findByKycs(Kyc kyc);
	public Integer countByKycs(Kyc kyc);
}
