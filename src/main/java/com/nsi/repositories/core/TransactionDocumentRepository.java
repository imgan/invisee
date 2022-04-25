package com.nsi.repositories.core;


import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.TransactionDocument;

public interface TransactionDocumentRepository extends JpaRepository<TransactionDocument, Long> {
    public TransactionDocument findByOrderNo(String orderNo);
}
