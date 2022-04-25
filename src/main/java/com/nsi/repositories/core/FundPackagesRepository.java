package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.Score;

public interface FundPackagesRepository extends JpaRepository<FundPackages, Long> {

	public List<FundPackages> findAllByOrderByFundPackageName(Pageable pageable);
	public FundPackages findByPackageCode(String code);
	@Query("select fp from FundPackages fp where cast(fp.effectiveDate as date) <= CURRENT_DATE and fp.publishStatus = true")
	public List<FundPackages> findByCurrentDateAndPublishStatusWithQuery();
	@Query("select fp.id, fp.fundPackageName, fp.packageDesc, fp.packageImage, 'false' from FundPackages fp where cast(fp.effectiveDate as date) <= CURRENT_DATE and fp.publishStatus = true ORDER BY fp.risk_Profile.maxScore, fp.createdDate, fp.fundPackageName asc")
	public List<Object[]> findByFundWithQuery();
	@Query("select fp.id, fp.fundPackageName, fp.packageDesc, fp.packageImage, 'true' from FundPackages fp where cast(fp.effectiveDate as date) <= CURRENT_DATE and fp.publishStatus = true and fp.risk_Profile=?1 ORDER BY fp.risk_Profile.maxScore, fp.createdDate, fp.fundPackageName asc")
	public List<Object[]> listFpWithScore(Score score);
}
