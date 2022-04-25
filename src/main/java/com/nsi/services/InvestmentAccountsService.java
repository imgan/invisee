package com.nsi.services;

import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;

public interface InvestmentAccountsService {

	public InvestmentAccounts saveInvestmentAccount(FundPackages fundPackages, Kyc kyc);
}
