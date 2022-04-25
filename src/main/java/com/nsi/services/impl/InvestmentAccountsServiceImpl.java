package com.nsi.services.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.nsi.services.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;
import com.nsi.repositories.core.InvestmentAccountsRepository;
import com.nsi.services.InvestmentAccountsService;

@Service
public class InvestmentAccountsServiceImpl implements InvestmentAccountsService {

    @Autowired
    private InvestmentAccountsRepository investmentAccountsRepository;
    @Autowired
    private GlobalService globalService;

    @Override
    public InvestmentAccounts saveInvestmentAccount(FundPackages fundPackages, Kyc kyc) {
        InvestmentAccounts investmentAccounts = new InvestmentAccounts();
        String name = "";
        if (kyc.getMiddleName() == null || kyc.getMiddleName().isEmpty()) {
            name = kyc.getFirstName() + " " + kyc.getLastName();
        } else {
            name = kyc.getFirstName() + " " + kyc.getMiddleName() + " " + kyc.getLastName();
        }
        investmentAccounts.setInvestmentAccountNo(globalService.generateInvestmentNo(1));
        investmentAccounts.setInvestmentAccountName(name);
        investmentAccounts.setKycs(kyc);
        investmentAccounts.setCreatedBy(kyc.getEmail());
        investmentAccounts.setCreatedDate(new Date());
        investmentAccounts.setFundPackages(fundPackages);
        investmentAccounts.setAtInvestmentAccountId(UUID.randomUUID().toString());
        investmentAccounts = investmentAccountsRepository.save(investmentAccounts);
        return investmentAccounts;
    }

    private String generateInvestmentNo(int counting) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYMM");
        String pref = "I" + sdf.format(new Date());
        String n = "000000000000000000" + (investmentAccountsRepository.countByInvestmentAccountNoLike(pref + "%") + counting);
        return pref + n.substring(n.length() - 6);
    }

}
