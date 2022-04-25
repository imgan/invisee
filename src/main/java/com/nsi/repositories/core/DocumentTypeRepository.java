package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.DocumentType;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

	public List<DocumentType> findAllByRowStatusAndCodeIn(Boolean rowStatus, String[] codes);
	public DocumentType findByCodeAndRowStatus(String code, Boolean rowStatus);
}
