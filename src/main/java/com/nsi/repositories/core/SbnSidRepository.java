package com.nsi.repositories.core;

import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.SbnSid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SbnSidRepository extends JpaRepository<SbnSid, Long> {
    public SbnSid findByKyc(Kyc kyc);
}
