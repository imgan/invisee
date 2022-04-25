package com.nsi.repositories.core;

import com.nsi.domain.core.SbnSubregistry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SbnSubregistryRepository extends JpaRepository<SbnSubregistry, Long> {
    public SbnSubregistry findBySubregId(String subregId);
}
