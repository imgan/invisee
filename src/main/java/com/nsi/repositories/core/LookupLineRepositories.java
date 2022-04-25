package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.LookupHeader;
import com.nsi.domain.core.LookupLine;

public interface LookupLineRepositories extends JpaRepository<LookupLine, Long>{

	public LookupLine findByCategoryAndPublishStatus(LookupHeader category, Boolean pubStatus);
}
