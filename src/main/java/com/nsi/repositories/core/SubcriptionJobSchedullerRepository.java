package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.SubcriptionJobScheduller;

public interface SubcriptionJobSchedullerRepository extends JpaRepository<SubcriptionJobScheduller, Long>{

	public SubcriptionJobScheduller findByOrderNo(String orderNo);
	public List<SubcriptionJobScheduller> findAllByPayTypeAndStatus(String payType, String status);
}
