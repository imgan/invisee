package com.nsi.repositories.core;

import com.nsi.domain.core.SbnPackages;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SbnPackagesRepository extends JpaRepository<SbnPackages, Long> {
    public SbnPackages findByIdSeri(Long idSeri);
    public SbnPackages findByIdAndDeleted(Long id, Boolean isDeleted);
    public List<SbnPackages> findAllByDeletedAndActivatedOrderByCreatedDateDesc(Boolean isDeleted, Boolean isActive);
}
