package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.FundEscrowAccount;
import com.nsi.domain.core.FundPackages;

public interface FundEscrowAccountRepository extends JpaRepository<FundEscrowAccount, Long> {

	public FundEscrowAccount findByFundPackages(FundPackages fundPackages);
}
