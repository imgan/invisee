package com.nsi.repositories.core;

import com.nsi.domain.core.Submidis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmidisRepository extends JpaRepository<Submidis, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM submidis WHERE sbn_packages_id=:sbnPackagesId ORDER BY created_date DESC limit 1")
    public Submidis findBySbnPackagesWithCustomQuery(@Param("sbnPackagesId") Long sbnPackagesId);
    public List<Submidis> findAllBySbnPackages_Id(Long sbnPackageId);
}
