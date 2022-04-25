package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.UtTransactionType;

public interface UtTransactionTypeRepository extends JpaRepository<UtTransactionType, Long> {

	public UtTransactionType findByTrxCode(String code);
}
