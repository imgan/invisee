package com.nsi.repositories.avantrade;

import com.nsi.domain.avantrade.MstInvestmentAccounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MstInvestmentAccountsRepository extends JpaRepository<MstInvestmentAccounts, String>{
	public MstInvestmentAccounts findByInvAccountId(String invAccountId);
}
