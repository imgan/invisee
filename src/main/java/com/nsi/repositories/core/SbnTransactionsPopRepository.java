package com.nsi.repositories.core;

import com.nsi.domain.core.SbnTransactionsPop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SbnTransactionsPopRepository extends JpaRepository<SbnTransactionsPop, Long> {
    public SbnTransactionsPop findByFileKey(String fileKey);
}
