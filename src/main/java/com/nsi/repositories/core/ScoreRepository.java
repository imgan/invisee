package com.nsi.repositories.core;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nsi.domain.core.Score;

public interface ScoreRepository extends JpaRepository<Score, Long>{
	@Query("from Score where ?1 between minScore and maxScore and ?2 between effectiveDateFrom AND effectiveDateTo")
	public Score getScore(Long score, Date currentTime);
}
