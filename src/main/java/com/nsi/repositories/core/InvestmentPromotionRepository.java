package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.InvestmentPromotion;
import java.util.List;

public interface InvestmentPromotionRepository extends JpaRepository<InvestmentPromotion, Long>{

	public InvestmentPromotion findByInvestmentAccountAndRowStatus(InvestmentAccounts invest, Boolean rowStatus);
        public List<InvestmentPromotion> findAllByInvestmentAccountAndRowStatus(InvestmentAccounts invest, Boolean rowStatus);
}
