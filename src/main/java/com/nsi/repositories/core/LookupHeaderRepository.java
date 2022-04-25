package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.LookupHeader;

public interface LookupHeaderRepository extends JpaRepository<LookupHeader, Long> {

	public LookupHeader findByCategory(String category);
}
