package com.nsi.repositories.core;

import com.nsi.domain.core.AllowedPackagesAgent;
import com.nsi.domain.core.FundPackages;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllowedPackagesAgentRepository extends JpaRepository<AllowedPackagesAgent, Long> {
  AllowedPackagesAgent findByAgent_CodeAndPackages(String agentCode, FundPackages packages);
  List<AllowedPackagesAgent> findAllByAgent_Code(String agentCode);
}
