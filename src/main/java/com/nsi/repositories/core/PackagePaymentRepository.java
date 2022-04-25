package com.nsi.repositories.core;

import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.PackagePayment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackagePaymentRepository extends JpaRepository<PackagePayment, Long> {
    public List<PackagePayment> findAllByFundPackages(FundPackages fundPackages);
}
