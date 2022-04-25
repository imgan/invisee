package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.FundPackageFeeSetup;
import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.UtTransactionType;
import org.springframework.data.jpa.repository.Query;

public interface FundPackageFeeSetupRepository extends JpaRepository<FundPackageFeeSetup, Long> {

    @Query("SELECT a.feeAmount FROM FundPackageFeeSetup a WHERE a.transactionType = ?2 and a.fundPackages = ?1 AND a.amountMin <= ?3 AND (?3 <= a.amountMax OR a.amountMax = 0) ORDER BY a.amountMin DESC")
    public List<Double> getFeeSetup(FundPackages packages, UtTransactionType transactionType, Double amount);
    
    public List<FundPackageFeeSetup> findAllByFundPackagesAndTransactionTypeOrderByIdAsc(FundPackages packages, UtTransactionType transactionType);
    
    public List<FundPackageFeeSetup> findAllByTransactionTypeId(FundPackages fundPackages);

    public List<FundPackageFeeSetup> findAllByFundPackagesAndTransactionTypeOrderByAmountMinDesc(FundPackages packages, UtTransactionType transactionType);
}
