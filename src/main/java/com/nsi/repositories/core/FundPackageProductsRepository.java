package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nsi.domain.core.FundPackageProducts;
import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.UtProducts;

public interface FundPackageProductsRepository extends JpaRepository<FundPackageProducts, Long> {

  public List<FundPackageProducts> findAllByFundPackages(FundPackages fundPackages);

  public FundPackageProducts findByFundPackagesAndUtProducts(FundPackages fundPackages,
      UtProducts utProducts);

  @Query("select distinct pro.utProducts from FundPackageProducts pro where pro.fundPackages = ?1 ")
  public List<UtProducts> findUtProductsByFundPackages(FundPackages fundPackages);

  FundPackageProducts findByFundPackages(FundPackages fundPackages);

}
