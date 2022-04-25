package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.LookupHeader;
import com.nsi.domain.core.LookupLine;

public interface LookupLineRepository extends JpaRepository<LookupLine, Long> {

	public List<LookupLine> findAllByCategoryOrderBySequenceLookupAsc(LookupHeader header);
	public List<LookupLine> findAllByCodeAndCategoryOrderBySequenceLookupAsc(String code, LookupHeader header);
	public LookupLine findByCategoryAndCode(LookupHeader header, String code);
	public LookupLine findByCategoryAndCodeAndPublishStatus(LookupHeader header, String code, Boolean publish);
	public LookupLine findByCategory_CategoryAndCode(String categoryHeader, String code);
	public LookupLine findByCode(String code);
	public LookupLine findById(Long id);
}
