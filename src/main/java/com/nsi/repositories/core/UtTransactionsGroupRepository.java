package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.UtTransactionsGroup;
import org.springframework.data.jpa.repository.Query;

public interface UtTransactionsGroupRepository extends JpaRepository<UtTransactionsGroup, Long>{

	public int countByOrderNoLike(String pref);
	UtTransactionsGroup findFirstByOrderNoLikeOrderByOrderNoDesc(String format);

	@Query(value = "SELECT NEXTVAl(?1)", nativeQuery = true)
	Long getNextSeriesId(String seq_orderno);
}
